package de.tum.localcampusapp.serializer;

import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.WrongParserException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class ScampiPostExtensionSerializer {

    public SCAMPIMessage postExtensionToMessage(PostExtension postExtension) throws MissingFieldsException {
        return null;
    }

    public PostExtension messageToPostExtension(SCAMPIMessage scampiMessage) throws WrongParserException, MissingFieldsException {
        return null;
    }

    public static boolean messageIsPostExtension(SCAMPIMessage scampiMessage) {
        return false;
    }
}
