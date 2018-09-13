package de.tum.localcampusapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.AppLibLifecycleListener;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class AppLibService extends Service implements AppLibLifecycleListener {

    public static final String DISCOVERY_SERVICE = "discovery";

    public static final long RECONNECT_PERIOD = 8000;

    public static final String TAG = AppLibService.class.getSimpleName();

    private volatile AppLib appLib;

    private ScheduledExecutorService scheduledExecutor;

    private DiscoveryHandler discoveryHandler;

    private Binder binder;

    private volatile String scampiId;
    private volatile boolean connected;
    private volatile List<Message> preconnect_buffer = new ArrayList<>();

    public void publish(SCAMPIMessage message, String service) throws InterruptedException {
        if (this.connected) {
            publish_now(message, service);
        } else {
            preconnect_buffer.add(new Message(message, service));
            Log.d(TAG, "Buffered Message");
        }

    }

    private class Message {
        public SCAMPIMessage scampiMessage;
        public String targetService;

        public Message(SCAMPIMessage scampiMessage, String targetService) {
            this.scampiMessage = scampiMessage;
            this.targetService = targetService;
        }
    }

    // Service Lifecycle
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // Restart if it gets killed
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        RepositoryLocator.init(getApplicationContext());

        super.onCreate();
        Log.d(TAG, "onCreate");

        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

        this.binder = new ScampiBinder();


        appLib = AppLib.builder().build();
        this.discoveryHandler = new DiscoveryHandler(appLib);
        appLib.addLifecycleListener(this);
        try {
            appLib.subscribe(DISCOVERY_SERVICE, this.discoveryHandler);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        appLib.start();
        this.scheduleConnect(0, TimeUnit.MILLISECONDS);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        appLib.stop();
        Log.d(TAG, "onDestroy");

    }

    // Scampi Lifecycle
    @Override
    public void onConnected(String scampiId) {
        this.connected = true;
        this.scampiId = scampiId;
        Log.d(TAG, "AppLib connected: " + scampiId);
        clear_preconnect_buffer();
    }

    @Override
    public void onDisconnected() {
        this.connected = false;
        this.scampiId = null;
        Log.d(TAG, "AppLib disconnected");
        this.scheduleConnect(RECONNECT_PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onConnectFailed() {
        Log.d(TAG, "AppLib connect failed");
        this.scheduleConnect(RECONNECT_PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onStopped() {
        this.connected = false;
        this.scampiId = null;
        Log.d(TAG, "AppLib stopped");
    }


    private void scheduleConnect(long delay, TimeUnit unit) {
        Log.d(TAG, "Scheduling applib connect in: " + delay + " " + unit);
        this.scheduledExecutor.schedule(() -> {
            AppLib.State state = this.appLib.getLifecycleState();
            if (state == AppLib.State.IDLE || state == AppLib.State.NEW) {
                Log.d(TAG, "Trying to connect AppLib");
                this.appLib.connect();
            } else {
                Log.d(TAG, "Can't connect, lifecycle state: " + state);
            }
        }, delay, unit);
    }

    private void publish_now(SCAMPIMessage message, String service) throws InterruptedException {
        message.putString(ScampiPostSerializer.CREATOR_FIELD, "TODOCREATOR");
        this.appLib.publish(message, service, (appLib, scampiMessage) -> {
            Log.d(TAG, "Message: " + scampiMessage.getAppTag() + " published");
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

    //Bind

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ScampiBinder extends Binder {
        public void publish(SCAMPIMessage scampiMessage, String service) throws InterruptedException {
            AppLibService.this.publish(scampiMessage, service);
        }
    }
}
