package de.tum.localcampusapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RepositoryLocator;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.AppLibLifecycleListener;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class AppLibService extends Service implements AppLibLifecycleListener, MessageReceivedCallback {

    public static final String DISCOVERY_SERVICE = "discovery";

    public static final long RECONNECT_PERIOD = 8000;

    public static final String TAG = AppLibService.class.getSimpleName();

    private volatile AppLib appLib;

    private ScheduledExecutorService scheduledExecutor;

    private Handler handler;

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

        appLib = AppLib.builder().build();
        appLib.addLifecycleListener(this);
        appLib.addMessageReceivedCallback(DISCOVERY_SERVICE,this);
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

    // Message Receiver


    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String service) {
        Log.d(TAG, "Message received on Service: "+service);
        if (scampiMessage.hasString("topicName") && scampiMessage.hasString("deviceId")) {
            Log.d(TAG, "Received Topic: "+scampiMessage.getString("topicName")+" from location "+scampiMessage.getString("deviceId"));
            Topic topic  = new Topic();
            topic.setTopicName(scampiMessage.getString("topicName"));
            handler.post(new InsertTask(this.getApplicationContext(), topic));
        }
        scampiMessage.close();
    }
    // TODO: Helper for the Fake repo, move to that repo
    private static class InsertTask implements Runnable {
        private Topic topic;
        private Context applicationContext;
        public InsertTask( Context applicationContext, Topic topic ) {
            this.applicationContext = applicationContext;
            this.topic = topic;
        }

        @Override
        public void run() {
            try {
                RepositoryLocator.getTopicRepository(applicationContext).insertTopic(topic);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }

    //Bind not Implemented yet
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
