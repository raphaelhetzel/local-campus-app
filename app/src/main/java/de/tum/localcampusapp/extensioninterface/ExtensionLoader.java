package de.tum.localcampusapp.extensioninterface;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import de.tum.localcampusapp.PermissionManager;
import de.tum.localcampusapp.repository.ExtensionRepository;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ShowPostFragment;

/**
    Dynamically loads an Extension/PostType from local storage.
    (Currently, the extensions are stored in the Downloads directory for easier adding of extensions.
    This is a security risk as they could be replaced by malicious code by any other application)

    This should be modified to only allow extensions signed by developers explicitly allowed by the user.

    Expects the loaded apks to contain a class <code>Registry</code> in the package <code>de.tum.localcampusextension</code>,
    which contains the static fields
    <code>addPostFragmentClass</code> (the class of a Fragment to add a new Post based on the class AddPostFragment),
    <code>showPostFragmentClass</code> (the class of a Fragment to show a new Post based on the class AddPostFragment),
    <code>typeDescription</code> (A short, human-readable description of the Posts created by the extension, e.g. "Text Post")
    and <code>typeId</code> (The UUID of one version of the extension).
 */
public class ExtensionLoader {

    static final String TAG = ExtensionLoader.class.getSimpleName();
    private ClassLoader systemClassLoader;
    private final File codeCacheDir;
    private ExtensionRepository extensionRepository;
    private boolean loaded = false;
    private PermissionManager permissionManager;

    public ExtensionLoader(Context context, ExtensionRepository extensionRepository) {
        this.systemClassLoader = context.getClassLoader();
        this.extensionRepository = extensionRepository;
        this.codeCacheDir = context.getCodeCacheDir();
        this.permissionManager = new PermissionManager(context);
    }

    /**
        (Try to) load one Extension apk and register it in the ExtensionRepository.
        Should be used when a new extension has been added while the App is running.
     */
    public void loadAPK(File apkFile) {
        if(! permissionManager.hasStoragePermission()) {
            Log.d(TAG,"Tried to load apk but the app does not have the permission to do so!");
            return;
        }
        if (!apkFile.exists()) return;

        final DexClassLoader apkClassLoader = new DexClassLoader(
                apkFile.getAbsolutePath(), codeCacheDir.getAbsolutePath(),
                "data/local/tmp/natives/",
                systemClassLoader);

        try {
            Class<?> registryClass = (Class<?>) apkClassLoader
                    .loadClass("de.tum.localcampusextension.Registry");

            Field typeIdField = registryClass.getDeclaredField("typeId");
            String apkUUID = (String) typeIdField.get(null);

            Field typeDescriptionField = registryClass.getDeclaredField("typeDescription");
            String typeDescription = (String) typeDescriptionField.get(null);

            Field addPostFragmentClassField = registryClass.getDeclaredField("addPostFragmentClass");
            Class<? extends AddPostFragment> addPostFragmentClass = (Class<? extends AddPostFragment>) addPostFragmentClassField.get(null);

            Field showPostFragmentClassField = registryClass.getDeclaredField("showPostFragmentClass");
            Class<? extends ShowPostFragment> showPostFragmentClass = (Class<? extends ShowPostFragment>) showPostFragmentClassField.get(null);

            extensionRepository.registerExtension(apkUUID, typeDescription, showPostFragmentClass, addPostFragmentClass, apkFile.getAbsolutePath());

        } catch (ClassNotFoundException e) {
            Log.d(TAG, "Extension is missing Mandatory Class" + apkFile.getAbsolutePath());
            return;
        } catch (IllegalAccessException e) {
            Log.d(TAG, "Extension is missing Mandatory Field in the Registry");
            return;
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "Extension is missing Mandatory Field in the Registry");
            return;
        }
    }

    /**
        Load all Extensions in the Extensions folder.
        Should only be used once (when the application is started).
     */
    public void loadAPKFiles() {
        File extensionDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "localcampusjars");
        if (loaded == true) return;
        File[] apkFiles = extensionDirectory.listFiles(filename -> filename.getName().endsWith(".apk"));
        if (apkFiles == null) return;
        for (File apkFile : apkFiles) {
            loadAPK(apkFile);
        }
        loaded = true;
    }
}
