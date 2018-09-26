package de.tum.localcampusapp.extensioninterface;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.tum.localcampusapp.PermissionManager;
import de.tum.localcampusapp.entity.ExtensionInfo;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.repository.ExtensionRepository;
import de.tum.localcampusapp.serializer.ScampiExtensionSerializer;
import de.tum.localcampusapp.service.AppLibService;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.service.ExtensionHandler.EXTENSION_SERVICE;

public class RealExtensionPublisher implements ExtensionPublisher {

    static final String TAG = RealExtensionPublisher.class.getSimpleName();

    private AppLibService.ScampiBinder scampiBinder;
    private Boolean serviceBound = false;
    private ExtensionRepository extensionRepository;
    private Context applicationContext;
    private PermissionManager permissionManager;
    private ScampiExtensionSerializer extensionSerializer;
    private ScheduledExecutorService executorService;

    public RealExtensionPublisher(Context applicationContext, ExtensionRepository extensionRepository) {
        this(extensionRepository,
                applicationContext,
                new PermissionManager(applicationContext),
                new ScampiExtensionSerializer(),
                Executors.newSingleThreadScheduledExecutor()
        );

    }

    // Testing Constructor
    public RealExtensionPublisher(ExtensionRepository extensionRepository,
                                  Context applicationContext,
                                  PermissionManager permissionManager,
                                  ScampiExtensionSerializer extensionSerializer,
                                  ScheduledExecutorService executorService) {
        this.extensionRepository = extensionRepository;
        this.applicationContext = applicationContext;
        this.permissionManager = permissionManager;
        this.extensionSerializer = extensionSerializer;
        this.executorService = executorService;

        this.bindService();
    }

    @Override
    public void enableSharing() {
        executorService.execute(() -> {
            if (!serviceBound) {
                executorService.schedule(() -> enableSharing(), 1, TimeUnit.SECONDS);
                return;
            }
            subScribeToExtensionService();
            publishLocalAPKFiles();
        });
    }

    private void subScribeToExtensionService() {
        scampiBinder.subscribeToExtensionService();
    }

    private void publishLocalAPKFiles() {
        if (!permissionManager.hasStoragePermission()) {
            Log.d(TAG, "Tried to publish the local APKs but the app does not have the permission to do so!");
            return;
        }
        for (ExtensionInfo extensionInfo : extensionRepository.getExtensions()) {
            if (extensionInfo.getExtensionFile() == null) return;
            try {
                SCAMPIMessage scampiMessage = extensionSerializer.extensionToMessage(extensionInfo.getExtensionFile(), extensionInfo.getExtensionUUID());
                scampiBinder.publish(scampiMessage, EXTENSION_SERVICE);
            } catch (MissingFieldsException | InterruptedException e) {
                // This should not happen...
                e.printStackTrace();
            }
        }
    }

    /// Service Connection

    private void bindService() {
        Intent intent = new Intent(applicationContext.getApplicationContext(), AppLibService.class);
        applicationContext.bindService(intent, serviceConnection, Context.BIND_IMPORTANT);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AppLibService.ScampiBinder scampi = (AppLibService.ScampiBinder) service;
            scampiBinder = scampi;
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };
}
