package de.tum.localcampusapp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import de.tum.localcampusapp.PermissionManager;
import de.tum.localcampusapp.extensioninterface.ExtensionLoader;
import de.tum.localcampusapp.repository.ExtensionRepository;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiExtensionSerializer.BINARY_FIELD;
import static de.tum.localcampusapp.serializer.ScampiExtensionSerializer.UUID_FIELD;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_EXTENSION;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExtensionHandlerTest {

    private ExtensionRepository mExtensionRepository;
    private ExtensionLoader mExtensionLoader;
    private PermissionManager mPermissionManager;
    private File mExtensionFolder;

    @Before
    public void initMocks() {
        mExtensionRepository = mock(ExtensionRepository.class);
        mExtensionLoader = mock(ExtensionLoader.class);
        mPermissionManager = mock(PermissionManager.class);
        mExtensionFolder = new File("/asdf");
    }

    @Test
    public void newExtension() throws IOException {
        ExtensionHandler extensionHandler = new ExtensionHandler(mExtensionRepository, mExtensionLoader, mPermissionManager, mExtensionFolder);

        SCAMPIMessage scampiMessage = mock(SCAMPIMessage.class);
        when(scampiMessage.getString(MESSAGE_TYPE_FIELD)).thenReturn(MESSAGE_TYPE_EXTENSION);
        when(scampiMessage.hasString(MESSAGE_TYPE_FIELD)).thenReturn(true);
        when(scampiMessage.hasString(UUID_FIELD)).thenReturn(true);
        when(scampiMessage.hasBinary(BINARY_FIELD)).thenReturn(true);
        when(scampiMessage.getString(UUID_FIELD)).thenReturn("UUID");
        doNothing().when(scampiMessage).copyBinary(eq(BINARY_FIELD), any(File.class));

        when(mPermissionManager.hasStoragePermission()).thenReturn(true);
        when(mExtensionRepository.extensionExists("UUID")).thenReturn(false);

        extensionHandler.messageReceived(scampiMessage, "extensions");

        verify(mPermissionManager).hasStoragePermission();
        verify(mExtensionRepository).extensionExists("UUID");
        verify(mExtensionLoader).loadAPK(argThat(argument -> {
            return argument.getAbsolutePath().equals("/asdf/UUID.apk");
        }));
        verify(scampiMessage).copyBinary(eq(BINARY_FIELD), argThat(argument -> {
            return argument.getAbsolutePath().equals("/asdf/UUID.apk");
        }));
        verify(scampiMessage).close();

    }

    @Test
    public void exstingExtension() throws IOException {
        ExtensionHandler extensionHandler = new ExtensionHandler(mExtensionRepository, mExtensionLoader, mPermissionManager, mExtensionFolder);

        SCAMPIMessage scampiMessage = mock(SCAMPIMessage.class);
        when(scampiMessage.getString(MESSAGE_TYPE_FIELD)).thenReturn(MESSAGE_TYPE_EXTENSION);
        when(scampiMessage.hasString(MESSAGE_TYPE_FIELD)).thenReturn(true);
        when(scampiMessage.hasString(UUID_FIELD)).thenReturn(true);
        when(scampiMessage.getString(UUID_FIELD)).thenReturn("UUID");
        when(scampiMessage.hasBinary(BINARY_FIELD)).thenReturn(true);

        when(mPermissionManager.hasStoragePermission()).thenReturn(true);
        when(mExtensionRepository.extensionExists("UUID")).thenReturn(true);

        extensionHandler.messageReceived(scampiMessage, "extensions");

        verify(mPermissionManager).hasStoragePermission();
        verify(mExtensionRepository).extensionExists("UUID");
        verify(mExtensionLoader, never()).loadAPK(any());
        verify(scampiMessage, never()).copyBinary(any(), any());
        verify(scampiMessage, never()).close();

    }

    @Test
    public void noStoragePermission() throws IOException {
        ExtensionHandler extensionHandler = new ExtensionHandler(mExtensionRepository, mExtensionLoader, mPermissionManager, mExtensionFolder);

        SCAMPIMessage scampiMessage = mock(SCAMPIMessage.class);
        when(scampiMessage.getString(MESSAGE_TYPE_FIELD)).thenReturn(MESSAGE_TYPE_EXTENSION);
        when(scampiMessage.hasString(MESSAGE_TYPE_FIELD)).thenReturn(true);
        when(scampiMessage.hasString(UUID_FIELD)).thenReturn(true);
        when(scampiMessage.hasBinary(BINARY_FIELD)).thenReturn(true);

        when(mPermissionManager.hasStoragePermission()).thenReturn(false);
        try {
            extensionHandler.messageReceived(scampiMessage, "extensions");
        } catch (RuntimeException e) {
            if(!e.getMessage().contains("Method d in android.util.Log not mocked")) throw e;
        }

        verify(mPermissionManager).hasStoragePermission();
        verify(mExtensionRepository, never()).extensionExists("UUID");
        verify(mExtensionLoader, never()).loadAPK(any());
        verify(scampiMessage, never()).copyBinary(any(), any());
        verify(scampiMessage, never()).close();

    }

    @Test
    public void wrongMessageType() throws IOException {
        ExtensionHandler extensionHandler = new ExtensionHandler(mExtensionRepository, mExtensionLoader, mPermissionManager, mExtensionFolder);

        SCAMPIMessage scampiMessage = mock(SCAMPIMessage.class);
        when(scampiMessage.getString(MESSAGE_TYPE_FIELD)).thenReturn("FOO");
        when(scampiMessage.hasString(MESSAGE_TYPE_FIELD)).thenReturn(true);

        extensionHandler.messageReceived(scampiMessage, "extensions");

        verify(mPermissionManager, never()).hasStoragePermission();
        verify(mExtensionRepository, never()).extensionExists("UUID");
        verify(mExtensionLoader, never()).loadAPK(any());
        verify(scampiMessage, never()).copyBinary(any(), any());
        verify(scampiMessage, never()).close();

    }

    @Test
    public void messageIsMissingFields() throws IOException {
        ExtensionHandler extensionHandler = new ExtensionHandler(mExtensionRepository, mExtensionLoader, mPermissionManager, mExtensionFolder);

        SCAMPIMessage scampiMessage = mock(SCAMPIMessage.class);
        when(scampiMessage.getString(MESSAGE_TYPE_FIELD)).thenReturn(MESSAGE_TYPE_EXTENSION);
        when(scampiMessage.hasString(MESSAGE_TYPE_FIELD)).thenReturn(true);
        when(scampiMessage.hasBinary(BINARY_FIELD)).thenReturn(true);
            extensionHandler.messageReceived(scampiMessage, "extensions");

        verify(mPermissionManager, never()).hasStoragePermission();
        verify(mExtensionRepository, never()).extensionExists("UUID");
        verify(mExtensionLoader, never()).loadAPK(any());
        verify(scampiMessage, never()).copyBinary(any(), any());
        verify(scampiMessage, never()).close();

    }
}
