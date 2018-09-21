package de.tum.localcampusapp.service;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import de.tum.localcampusapp.PermissionManager;
import de.tum.localcampusapp.entity.ExtensionInfo;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.extensioninterface.ExtensionLoader;
import de.tum.localcampusapp.repository.ExtensionRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.serializer.ScampiExtensionSerializer;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiExtensionSerializer.BINARY_FIELD;
import static de.tum.localcampusapp.serializer.ScampiExtensionSerializer.UUID_FIELD;

public class ExtensionHandler implements MessageReceivedCallback {

    public static final String TAG = ExtensionHandler.class.getSimpleName();

    public static String EXTENSION_SERVICE = "extensions";

    private File extensionFolder;
    private ExtensionRepository extensionRepository;
    private ExtensionLoader extensionLoader;
    private PermissionManager permissionManager;

    public ExtensionHandler(AppLibService appLibService) {
                this(RepositoryLocator.getExtensionRepository(),
                RepositoryLocator.getExtensionLoader(),
                new PermissionManager(appLibService.getApplicationContext()),
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS ), "localcampusjars"));
    }

    public ExtensionHandler(ExtensionRepository extensionRepository, ExtensionLoader extensionLoader, PermissionManager permissionManager, File extensionFolder) {
        this.extensionRepository = extensionRepository;
        this.extensionLoader = extensionLoader;
        this.permissionManager = permissionManager;
        this.extensionFolder = extensionFolder;
        extensionFolder.mkdirs();
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String service) {
        if (!ScampiExtensionSerializer.messageIsExtension(scampiMessage)) return;
        if (!ScampiExtensionSerializer.messageHasRequiredFields(scampiMessage)) return;
        if(! permissionManager.hasStoragePermission()) {
            Log.d(TAG,"Received Extension Message, but the app has no permission to store it.");
            scampiMessage.close();
            return;
        }

        String extensionUUID = scampiMessage.getString(UUID_FIELD);


        if (extensionRepository.extensionExists(extensionUUID)) return;

        String targetPath = extensionUUID+".apk";

        File targetFile = new File(extensionFolder, targetPath);

        try {
            scampiMessage.copyBinary(BINARY_FIELD, targetFile);
            extensionLoader.loadAPK(targetFile);
        } catch (IOException e) {
            Log.d(TAG, "Issue while moving the File to the extension directory");
            e.printStackTrace();
        } finally {
            scampiMessage.close();
        }
    }

}
