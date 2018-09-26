package de.tum.localcampusapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleService;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.tum.localcampusapp.Activities.TopicsActivity;
import de.tum.localcampusapp.R;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.AppLibLifecycleListener;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.service.ExtensionHandler.EXTENSION_SERVICE;

/**
    Service responsible for managing the AppLib connection.
    Starts a persistent/automatically restarting AppLib instance that automatically subscribes to
    the discovery services and the locations updates via the Handlers.

    It allows Clients to Bind to it, which allows them to publish messages to Scampi and subscribe to
    the extensions service (this is needed as the permissions are not immediately granted, but the service
    is immediately started). The Service handles messages sent while it is not connected to scampi by caching
    them inMemory.

    The service is run as a Foreground Service as it needs to run and android is too aggressive
    in killing background services. In a production version of this app, this could also be handled by restarting
    the service if it got killed but the user is active again. However, this would result in reprocessing all messages.
 */
public class AppLibService extends LifecycleService implements AppLibLifecycleListener {

    public static final String TAG = AppLibService.class.getSimpleName();



    public static final String DISCOVERY_SERVICE = "discovery";

    public static final long RECONNECT_PERIOD = 8000;


    private volatile AppLib appLib;

    private ScheduledExecutorService scheduledExecutor;

    private DiscoveryHandler discoveryHandler;

    private ExtensionHandler extensionHandler;

    private LocationHandler locationHandler;

    private Binder binder;
    private volatile boolean connected;
    private volatile List<Message> preconnect_buffer = new ArrayList<>();

    private volatile boolean subscribedToExtensions = false;
    private final Object extensionLock = new Object();

    /// API, called via the Binder

    /**
        Called by the Binder, immediately publishes the message while connecte to the router,
        caches it (inMemory!) otherwise.
     */
    public void publish(SCAMPIMessage message, String service) throws InterruptedException {
        if (this.connected) {
            publish_now(message, service);
        } else {
            preconnect_buffer.add(new Message(message, service));
            Log.v(TAG, "Buffered Message");
        }

    }

    /**
        Called by the Binder, attaches the extensionHandler to the <code>extensions</code> Service.
        Ignores duplicate calls.
     */
    public void subscribeToExtensionService() {
        try {
            synchronized (extensionLock) {
                if(subscribedToExtensions) return;
                appLib.subscribe(EXTENSION_SERVICE, this.extensionHandler);
                subscribedToExtensions = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /// Service Lifecycle
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // Restart if it gets killed
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        super.onCreate();

        moveToForeGround();

        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

        this.binder = new ScampiBinder();


        appLib = AppLib.builder().build();
        this.discoveryHandler = new DiscoveryHandler(appLib, (LifecycleOwner) this);
        this.extensionHandler = new ExtensionHandler(this);
        this.locationHandler = new LocationHandler();

        appLib.addLifecycleListener(this);
        try {
            appLib.startLocationUpdates(locationHandler);
            appLib.subscribe(DISCOVERY_SERVICE, this.discoveryHandler);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        appLib.start();
        this.scheduleConnect(0, TimeUnit.MILLISECONDS);

    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();

        appLib.stop();
    }

    /// Scampi Lifecycle
    @Override
    public void onConnected(String scampiId) {
        Log.v(TAG, "AppLib connected: " + scampiId);
        this.connected = true;
        clear_preconnect_buffer();
    }

    @Override
    public void onDisconnected() {
        Log.v(TAG, "AppLib disconnected");
        this.connected = false;
        this.scheduleConnect(RECONNECT_PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onConnectFailed() {
        Log.v(TAG, "AppLib connect failed");
        this.scheduleConnect(RECONNECT_PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onStopped() {
        Log.v(TAG, "AppLib stopped");
        this.connected = false;
    }

    // Helper class for buffering received Messages while Scampi is not connected
    private class Message {
        public SCAMPIMessage scampiMessage;
        public String targetService;

        public Message(SCAMPIMessage scampiMessage, String targetService) {
            this.scampiMessage = scampiMessage;
            this.targetService = targetService;
        }
    }


    private void scheduleConnect(long delay, TimeUnit unit) {
        Log.d(TAG, "Scheduling applib connect in: " + delay + " " + unit);
        this.scheduledExecutor.schedule(() -> {
            AppLib.State state = this.appLib.getLifecycleState();
            if (state == AppLib.State.IDLE || state == AppLib.State.NEW) {
                Log.v(TAG, "Trying to connect AppLib");
                this.appLib.connect();
            } else {
                Log.v(TAG, "Can't connect, lifecycle state: " + state);
            }
        }, delay, unit);
    }

    private void publish_now(SCAMPIMessage message, String service) throws InterruptedException {
        this.appLib.publish(message, service, (appLib, scampiMessage) -> {
            Log.v(TAG, "Message: " + scampiMessage.getAppTag() + " published");
        });
    }

    private void clear_preconnect_buffer() {
        scheduledExecutor.execute(() -> {
            while (preconnect_buffer.size() > 0) {
                if (connected == false) return;
                Message message = preconnect_buffer.remove(0);
                try {
                    publish(message.scampiMessage, message.targetService);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void moveToForeGround() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, TopicsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "LCA_CHANNEL_DEFAULT")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("LocalCampusApp")
            .setContentText("Scampi Running")
            .setContentIntent(pendingIntent)
            .setTicker("Scampi Running")
            .build();


        startForeground(1337, notification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Local Campus Channel";
            String description = "Local Campus Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("LCA_CHANNEL_DEFAULT", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    // Service Binding

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
    }

    public class ScampiBinder extends Binder {
        /**
            Immediately publishes the message while connecte to the router,
            caches it (inMemory!) otherwise.
         */
        public void publish(SCAMPIMessage scampiMessage, String service) throws InterruptedException {
            AppLibService.this.publish(scampiMessage, service);
        }

        /**
            Attaches the extensionHandler to the <code>extensions</code> Service.
            Ignores duplicate calls.
         */
        public void subscribeToExtensionService() {
            AppLibService.this.subscribeToExtensionService();
        }
    }
}
