package de.tum.localcampusapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RepositoryLocator;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.AppLibLifecycleListener;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class AppLibService extends Service implements AppLibLifecycleListener {

    public static final String DISCOVERY_SERVICE = "discovery";

    public static final long RECONNECT_PERIOD = 8000;

    public static final String TAG = AppLibService.class.getSimpleName();

    private volatile AppLib appLib;

    private ScheduledExecutorService scheduledExecutor;

    public Handler handler;

    private DiscoveryHandler discoveryHandler;

    private Binder binder;

    private String scampiId;

    public void publish(SCAMPIMessage message, String service) {
        // TODO: Propagate error to the Repository
        try {
            this.appLib.publish(message, service, (appLib, scampiMessage) -> {
                Log.d( TAG, "Message: "+scampiMessage.getAppTag()+" published");
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        super.onCreate();
        Log.d(TAG, "onCreate");

        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        handler = new Handler();

        this.discoveryHandler = new DiscoveryHandler(this.getApplicationContext());

        this.binder = new ScampiBinder();


        appLib = AppLib.builder().build();
        appLib.addLifecycleListener(this);
        appLib.addMessageReceivedCallback(DISCOVERY_SERVICE, this.discoveryHandler);
        try {
            appLib.subscribe(DISCOVERY_SERVICE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        appLib.start();
        this.scheduleConnect(0, TimeUnit.MILLISECONDS);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }

    // Scampi Lifecycle
    @Override
    public void onConnected(String scampiId) {
        this.scampiId = scampiId;
        Log.d(TAG, "AppLib connected: " + scampiId);
    }

    @Override
    public void onDisconnected() {
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

    //Bind

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ScampiBinder extends Binder {

        // Messages Livetime set to 30 Minutes to Limit the deletion needed while testing
        public static final long MSG_LIFETIME = 60 * 30;
        public static final String TYPE_ID_FIELD = "type_id";
        public static final String CREATOR_FIELD = "creator";
        public static final String CREATED_AT_FIELD = "created_at";
        public static final String UPDATED_AT_FIELD = "updated_at";
        public static final String DATA_FIELD = "data";
        public static final String SCORE_FIELD = "score";
        // UUID already sent as the app tag

        public void publishPost(Post post) {
            SCAMPIMessage message = SCAMPIMessage.builder()
                    .appTag(post.getUuid())
                    .build();
            message.putString(TYPE_ID_FIELD, Long.toString(post.getTypeId()));
            message.putString(CREATOR_FIELD, scampiId);
            //Sent as String to prevent Sizing issues
            message.putString(CREATED_AT_FIELD, Converters.dateToTimestamp(post.getCreatedAt()).toString());
            //Sent as String to prevent Sizing issues
            message.putString(UPDATED_AT_FIELD, Converters.dateToTimestamp(post.getCreatedAt()).toString());
            message.putString(DATA_FIELD, post.getData());
            //Sent as String to prevent Sizing issues
            message.putString(SCORE_FIELD, Integer.toString(post.getScore()));
            message.setLifetime(MSG_LIFETIME);
            publish(message, "/testservice");
        }
    }
}
