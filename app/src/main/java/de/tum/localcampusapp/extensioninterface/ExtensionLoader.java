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

            Log.d("RAH", "Classfound");

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
