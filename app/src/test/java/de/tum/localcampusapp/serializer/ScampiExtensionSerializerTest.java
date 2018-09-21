package de.tum.localcampusapp.serializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import de.tum.localcampusapp.exception.MissingFieldsException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiExtensionSerializer.BINARY_FIELD;
import static de.tum.localcampusapp.serializer.ScampiExtensionSerializer.UUID_FIELD;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_EXTENSION;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScampiExtensionSerializerTest {

    @Test
    public void extensionToMessage() throws MissingFieldsException {
        File mFile = mock(File.class);
        when(mFile.exists()).thenReturn(true);
        when(mFile.isFile()).thenReturn(true);
        ScampiExtensionSerializer scampiExtensionSerializer = new ScampiExtensionSerializer();
        SCAMPIMessage scampiMessage = scampiExtensionSerializer.extensionToMessage(mFile, "UUID");

        assertEquals(scampiMessage.getString(MESSAGE_TYPE_FIELD), MESSAGE_TYPE_EXTENSION);
        assertEquals(scampiMessage.getAppTag(), "UUID");
        assertEquals(scampiMessage.getString(UUID_FIELD), "UUID");
        assertEquals(scampiMessage.hasBinary(BINARY_FIELD), true);
    }

    @Test(expected = MissingFieldsException.class)
    public void missingFile() throws MissingFieldsException {
        File mFile = mock(File.class);
        when(mFile.exists()).thenReturn(false);
        ScampiExtensionSerializer scampiExtensionSerializer = new ScampiExtensionSerializer();
        scampiExtensionSerializer.extensionToMessage(mFile, "UUID");
    }

    @Test(expected = MissingFieldsException.class)
    public void fileIsFolder() throws MissingFieldsException {
        File mFile = mock(File.class);
        when(mFile.exists()).thenReturn(true);
        when(mFile.isFile()).thenReturn(false);
        ScampiExtensionSerializer scampiExtensionSerializer = new ScampiExtensionSerializer();
        scampiExtensionSerializer.extensionToMessage(mFile, "UUID");
    }
}
