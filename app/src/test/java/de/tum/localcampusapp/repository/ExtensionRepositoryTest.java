package de.tum.localcampusapp.repository;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ExtensionContext;
import de.tum.localcampuslib.ShowPostFragment;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ExtensionRepositoryTest {

    public static class MockShowPostFagment extends ShowPostFragment {}
    public static class MockAddPostFagment extends AddPostFragment {}

    private Context mContext;

    @Before
    public void initializeMocks() {
        mContext = mock(Context.class);
    }

    @Test
    public void registerExtension_getFields() {
        ExtensionRepository extensionRepository = new ExtensionRepository();
        extensionRepository.registerExtension("UUID",
                "SampleExtension",
                MockShowPostFagment.class,
                MockAddPostFagment.class,
                "/foo/bar");

        assertEquals(extensionRepository.getAddPostFragmentFor("UUID").getClass(), MockAddPostFagment.class);
        assertEquals(extensionRepository.getShowPostFragmentFor("UUID").getClass(), MockShowPostFagment.class);
        assertEquals(extensionRepository.getContextFor("UUID", mContext).getClass(), ExtensionContext.class);
        assertEquals(extensionRepository.getDescriptionFor("UUID"), "SampleExtension");
    }

    @Test
    public void extensionExists() {
        ExtensionRepository extensionRepository = new ExtensionRepository();
        extensionRepository.registerExtension("UUID",
                "SampleExtension",
                MockShowPostFagment.class,
                MockAddPostFagment.class,
                "/foo/bar");

        assertEquals(extensionRepository.extensionExists("UUID"), true);
    }

    @Test
    public void getExtensions() {
        ExtensionRepository extensionRepository = new ExtensionRepository();
        extensionRepository.registerExtension("UUID",
                "SampleExtension",
                MockShowPostFagment.class,
                MockAddPostFagment.class,
                "/foo/bar");
        extensionRepository.registerExtension("UUID2",
                "SampleExtension2",
                MockShowPostFagment.class,
                MockAddPostFagment.class,
                "/foo/bar2");

        assertEquals(extensionRepository.getExtensions().size(), 2);
        assertEquals(extensionRepository.getExtensions().get(0).getExtensionUUID(), "UUID2");
        assertEquals(extensionRepository.getExtensions().get(1).getExtensionUUID(), "UUID");
    }

    @Test
    public void getFieldsMissingExtension() {
        ExtensionRepository extensionRepository = new ExtensionRepository();

        assertEquals(extensionRepository.getAddPostFragmentFor("UUID"), null);
        assertEquals(extensionRepository.getShowPostFragmentFor("UUID"), null);
        assertEquals(extensionRepository.getContextFor("UUID", mContext), mContext);
        assertEquals(extensionRepository.getDescriptionFor("UUID"), "Unknown PostType");
    }

    @Test
    public void getFieldsMissingDescription() {
        ExtensionRepository extensionRepository = new ExtensionRepository();
        extensionRepository.registerExtension("UUID",
                null,
                MockShowPostFagment.class,
                MockAddPostFagment.class,
                "/foo/bar");

        assertEquals(extensionRepository.getAddPostFragmentFor("UUID").getClass(), MockAddPostFagment.class);
        assertEquals(extensionRepository.getShowPostFragmentFor("UUID").getClass(), MockShowPostFagment.class);
        assertEquals(extensionRepository.getContextFor("UUID", mContext).getClass(), ExtensionContext.class);
        assertEquals(extensionRepository.getDescriptionFor("UUID"), "No Post Description");
    }

    @Test
    public void getFieldsNoAPKPath() {
        ExtensionRepository extensionRepository = new ExtensionRepository();
        extensionRepository.registerExtension("UUID",
                "Description",
                MockShowPostFagment.class,
                MockAddPostFagment.class,
                null);

        assertEquals(extensionRepository.getAddPostFragmentFor("UUID").getClass(), MockAddPostFagment.class);
        assertEquals(extensionRepository.getShowPostFragmentFor("UUID").getClass(), MockShowPostFagment.class);
        assertEquals(extensionRepository.getContextFor("UUID", mContext), mContext);
        assertEquals(extensionRepository.getDescriptionFor("UUID"), "Description");
    }
}
