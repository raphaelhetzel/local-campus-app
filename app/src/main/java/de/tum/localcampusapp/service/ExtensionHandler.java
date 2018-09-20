package de.tum.localcampusapp.service;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

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
    private ScampiExtensionSerializer extensionSerialier;
    private AppLibService appLibService;

    public ExtensionHandler(AppLibService appLibService) {
        this(appLibService,
                RepositoryLocator.getExtensionRepository(),
                RepositoryLocator.getExtensionLoader(),
                new ScampiExtensionSerializer());
    }

    public ExtensionHandler(AppLibService appLibService, ExtensionRepository extensionRepository, ExtensionLoader extensionLoader, ScampiExtensionSerializer extensionSerializer) {
        this.extensionRepository = extensionRepository;
        this.extensionLoader = extensionLoader;
        this.extensionSerialier = extensionSerializer;
        this.appLibService = appLibService;

        File dlDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS );

        this.extensionFolder = new File( dlDir, "localcampusjars" );
        extensionFolder.mkdirs();
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String service) {

        if (!ScampiExtensionSerializer.messageIsExtension(scampiMessage)) return;
        if (!ScampiExtensionSerializer.messageHasRequiredFields(scampiMessage)) return;

        String extensionUUID = scampiMessage.getString(UUID_FIELD);


        if (extensionRepository.extensionExists(extensionUUID)) return;

        File targetFile = new File(extensionFolder, extensionUUID + ".apk");

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

    public void publishLocalAPKFiles() {
        for (ExtensionInfo extensionInfo : extensionRepository.getExtensions()) {
            if (extensionInfo.getExtensionFile() == null) return;
            try {
                SCAMPIMessage scampiMessage = extensionSerialier.extensionToMessage(extensionInfo.getExtensionFile(), extensionInfo.getExtensionUUID());
                appLibService.publish(scampiMessage, EXTENSION_SERVICE);
            } catch (MissingFieldsException | InterruptedException e) {
                // This should not happen...
                e.printStackTrace();
            }
        }
    }

}
