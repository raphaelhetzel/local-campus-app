package de.tum.localcampusapp.extensioninterface;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;
import de.tum.localcampusapp.repository.ExtensionRepository;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ExtensionContext;
import de.tum.localcampuslib.ShowPostFragment;

public class ExtensionLoader {
    static final String TAG = ExtensionLoader.class.getSimpleName();

    private ClassLoader systemClassLoader;
    private ExtensionRepository extensionRepository;

    public ExtensionLoader(Context context, ExtensionRepository extensionRepository) {
        this.systemClassLoader = context.getClassLoader();
        this.extensionRepository = extensionRepository;
    }

    public void loadAPK(File apkFile) {

        //File apkFile = new File("/data/local/tmp/testjars/", filename+".apk");
        if(!apkFile.exists()) return;

        final DexClassLoader apkClassLoader = new DexClassLoader(
                apkFile.getAbsolutePath(), "",
                "data/local/tmp/natives/",
                systemClassLoader);

        try {
            Class<?> registryClass = (Class<?>) apkClassLoader
                    .loadClass("de.tum.localcampusextension.Registry");

            Field typeIdField = registryClass.getDeclaredField("typeId");
            String apkUUID = (String) typeIdField.get(null);

            Field typeDescriptionField = registryClass.getDeclaredField("typeDescription");
            String typeDescription  = (String) typeDescriptionField.get(null);

            Field addPostFragmentClassField = registryClass.getDeclaredField("addPostFragmentClass");
            Class<? extends AddPostFragment> addPostFragmentClass = (Class<? extends AddPostFragment>) addPostFragmentClassField.get(null);

            Field showPostFragmentClassField = registryClass.getDeclaredField("showPostFragmentClass");
            Class<? extends ShowPostFragment> showPostFragmentClass = (Class<? extends ShowPostFragment>) showPostFragmentClassField.get(null);

            extensionRepository.registerExtension(apkUUID, typeDescription,showPostFragmentClass, addPostFragmentClass, apkFile.getAbsolutePath());

        } catch (ClassNotFoundException e) {
            Log.d(TAG, "Extension is missing Mandatory Class");
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

        //TODO: move to app storage to make it more secure
        File[] apkFiles = new File("data/local/tmp/testjars/").listFiles();
        if (apkFiles == null) return;
        for(File apkFile : apkFiles) {
            loadAPK(apkFile);
        }
    }
}
