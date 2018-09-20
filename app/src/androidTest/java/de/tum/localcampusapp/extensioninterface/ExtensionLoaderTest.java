package de.tum.localcampusapp.extensioninterface;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import de.tum.localcampusapp.repository.ExtensionRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ExtensionContext;
import de.tum.localcampuslib.ShowPostFragment;

import static junit.framework.TestCase.assertEquals;


// Expects the following file to be present on the testdevice (not really possible to install from the test)
// /data/local/tmp/testjars/test.apk
// this file should contain the testextension bundled with the project

@RunWith(AndroidJUnit4.class)
public class ExtensionLoaderTest {

    @Test
    public void testAPKloading() {
        Context context = InstrumentationRegistry.getTargetContext();
        RepositoryLocator.init(context);

        assertEquals(0, RepositoryLocator.getExtensionRepository().getExtensions().size());
        RepositoryLocator.getExtensionLoader().loadAPK(new File("/data/local/tmp/testjars/test.apk"));
        assertEquals(1, RepositoryLocator.getExtensionRepository().getExtensions().size());
        assertEquals("Sample Extension", RepositoryLocator.getExtensionRepository().getDescriptionFor("ee5afd62-6e72-4728-8404-e91d7ea2c303"));
        assertEquals(ExtensionContext.class, RepositoryLocator.getExtensionRepository().getContextFor("ee5afd62-6e72-4728-8404-e91d7ea2c303" , context).getClass());
        assertEquals(AddPostFragment.class, RepositoryLocator.getExtensionRepository().getAddPostFragmentFor("ee5afd62-6e72-4728-8404-e91d7ea2c303").getClass().getSuperclass());
        assertEquals(ShowPostFragment.class, RepositoryLocator.getExtensionRepository().getShowPostFragmentFor("ee5afd62-6e72-4728-8404-e91d7ea2c303").getClass().getSuperclass());
    }
}
