package de.tum.localcampusapp.extensioninteface;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import de.tum.localcampusapp.PermissionManager;
import de.tum.localcampusapp.entity.ExtensionInfo;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.extensioninterface.RealExtensionPublisher;
import de.tum.localcampusapp.repository.ExtensionRepository;
import de.tum.localcampusapp.serializer.ScampiExtensionSerializer;
import de.tum.localcampusapp.service.AppLibService;
import de.tum.localcampusapp.testhelper.ScheduledExecutorInstantRun;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RealExtensionPublisherTest {

    private ExtensionRepository mExtensionRepository;
    private PermissionManager mPermissionManager;
    private ScampiExtensionSerializer mExtensionSerializer;

    private ScheduledExecutorService mExecutorService;

    private Context mContext;
    private AppLibService.ScampiBinder mScampiBinder;
    private ComponentName mComponentName;


    @Before
    public void initializeMocks() {
        mExtensionRepository = mock(ExtensionRepository.class);
        mPermissionManager = mock(PermissionManager.class);
        mExtensionSerializer = mock(ScampiExtensionSerializer.class);

        mExecutorService = ScheduledExecutorInstantRun.getMockExecutor();

        mContext = mock(Context.class);
        mScampiBinder = mock(AppLibService.ScampiBinder.class);
        mComponentName = mock(ComponentName.class);

        when(mContext.bindService(any(Intent.class), any(ServiceConnection.class), anyInt())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ServiceConnection serviceConnection = invocation.getArgument(1);
                serviceConnection.onServiceConnected(mComponentName, mScampiBinder);
                return null;
            }
        });
    }

    @Test
    public void sharingEnabled() throws MissingFieldsException, InterruptedException {
        RealExtensionPublisher realExtensionPublisher = new RealExtensionPublisher(mExtensionRepository,
                mContext,
                mPermissionManager,
                mExtensionSerializer,
                mExecutorService
        );

        File mExtensionFile = new File("/foo/bar");
        File mExtensionFile2 = new File("/foo/bar2");
        SCAMPIMessage mScampiMessage = SCAMPIMessage.builder().build();
        SCAMPIMessage mScampiMessage2 = SCAMPIMessage.builder().build();

        when(mPermissionManager.hasStoragePermission()).thenReturn(true);
        ExtensionInfo extensionInfo = new ExtensionInfo("UUID", "Description", mExtensionFile);
        ExtensionInfo extensionInfo2 = new ExtensionInfo("UUID2", "Description", null);
        ExtensionInfo extensionInfo3 = new ExtensionInfo("UUID3", "Description", mExtensionFile2);
        List<ExtensionInfo> extensionInfos = new ArrayList<>();
        extensionInfos.add(extensionInfo);
        extensionInfos.add(extensionInfo2);
        extensionInfos.add(extensionInfo3);
        when(mExtensionRepository.getExtensions()).thenReturn(extensionInfos);
        when(mExtensionSerializer.extensionToMessage(mExtensionFile, "UUID")).thenReturn(mScampiMessage);
        when(mExtensionSerializer.extensionToMessage(mExtensionFile2, "UUID3")).thenReturn(mScampiMessage2);

        realExtensionPublisher.enableSharing();

        verify(mScampiBinder).subscribeToExtensionService();
        verify(mExtensionSerializer).extensionToMessage(mExtensionFile, "UUID");
        verify(mExtensionSerializer).extensionToMessage(mExtensionFile2, "UUID3");
        verify(mExtensionSerializer, never()).extensionToMessage(any(), eq("UUID2"));
        verify(mScampiBinder).publish(mScampiMessage, "extensions");
        verify(mScampiBinder).publish(mScampiMessage2, "extensions");
    }
}
