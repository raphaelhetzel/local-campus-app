package de.tum.localcampusapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private String scampiId;

    public void publish(SCAMPIMessage message, String service) throws InterruptedException {
        this.appLib.publish(message, service, (appLib, scampiMessage) -> {
            Log.d(TAG, "Message: " + scampiMessage.getAppTag() + " published");
        });

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
        public void publish(SCAMPIMessage scampiMessage, String service) throws InterruptedException {
            AppLibService.this.publish(scampiMessage, service);
        }
    }
}
