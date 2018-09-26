package de.tum.localcampusapp.serializer;

import java.io.File;

import de.tum.localcampusapp.exception.MissingFieldsException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_EXTENSION;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;

public class ScampiExtensionSerializer {

    public static String BINARY_FIELD = "binary";
    public static String UUID_FIELD = "uuid";

    /**
        Serialize a Extension (as there is no entity, an Extension consists is the  Extension File and the extensionUUID)
        into a Scampi Message. Raises a {@link MissingFieldsException} if the file does not exist.

        Currently does not copy the file to a safe location as there is no official way of deleting extensions from
        the device anyway (the file needs to exist until the message has been sent)
     */
    public SCAMPIMessage extensionToMessage(File extensionFile, String extensionUUID) throws MissingFieldsException {
        if(!extensionFile.exists() || !extensionFile.isFile()) throw new MissingFieldsException();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().appTag(extensionUUID).build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_EXTENSION);
        scampiMessage.putBinary(BINARY_FIELD, extensionFile);
        scampiMessage.putString(UUID_FIELD, extensionUUID);
        return scampiMessage;
    }

    public static boolean messageHasRequiredFields(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasBinary(BINARY_FIELD) &&
                scampiMessage.hasString(UUID_FIELD)) return true;
        return false;
    }

    public static boolean messageIsExtension(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasString(MESSAGE_TYPE_FIELD) && scampiMessage.getString(MESSAGE_TYPE_FIELD).equals(MESSAGE_TYPE_EXTENSION))
            return true;
        return false;
    }
}
