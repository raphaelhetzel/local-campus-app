package de.tum.localcampusapp.extensioninterface;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ShowPostFragment;

public class ExtensionLoader {
    static final String TAG = ExtensionLoader.class.getSimpleName();

    public static volatile Map<String, Class<? extends AddPostFragment>> addPostFragmentClasses = new HashMap<>();
    public static volatile Map<String, Class<? extends ShowPostFragment>> showPostFragmentClasses = new HashMap<>();
    public static volatile Map<String, String> typeDescriptions = new HashMap< >();
    public static volatile Map<String, String> paths = new HashMap< >();
    public static ClassLoader systemClassLoader;

    public static void init(Context context) {
        systemClassLoader = context.getClassLoader();
        loadFiles();
        Log.d("RAH", "Packages: "+ addPostFragmentClasses.size());
    }

    public static ShowPostFragment getShowPostFragmentFor(String uuid) {
        if(!addPostFragmentClasses.containsKey(uuid)) {
            Log.d(TAG, "showPostFragmentClass not found!");
            return null;
        }
        try {
            return (ShowPostFragment) showPostFragmentClasses.get(uuid).newInstance();
        }   catch (InstantiationException | IllegalAccessException e) {
            Log.d(TAG, "Could not instantiate show Fragment class");
            e.printStackTrace();
            return null;
        }
    }

    public static AddPostFragment getaddPostFragmentFor(String uuid) {
        if(!addPostFragmentClasses.containsKey(uuid)) {
            Log.d(TAG, "addPostFragmentClass not found!");
            return null;
        }
        try {
            return (AddPostFragment) addPostFragmentClasses.get(uuid).newInstance();
        }   catch (InstantiationException | IllegalAccessException e) {
            Log.d(TAG, "Could not instantiate show Fragment class");
            e.printStackTrace();
            return null;
        }
    }

    public static String getDescriptionFor(String uuid) {
        if(!typeDescriptions.containsKey(uuid)) {
            Log.d(TAG, "Description not found!");
            return null;
        }
        return typeDescriptions.get(uuid);
    }

    public static String getPathFor(String uuid) {
        if(!typeDescriptions.containsKey(uuid)) {
            Log.d(TAG, "Path not found!");
            return null;
        }
        return paths.get(uuid);
    }

    private static boolean loadFiles() {

        //TODO: move to app storage to make it more secure
        File[] apkFiles = new File("data/local/tmp/testjars/").listFiles();

        boolean newFilesLoaded = false;
        for(File apkFile : apkFiles) {
            if(loadAPK(apkFile)) {
                newFilesLoaded = true;
            }
        }
        return newFilesLoaded;
    }

    private static boolean loadAPK(File apkFile) {

        //File apkFile = new File("/data/local/tmp/testjars/", filename+".apk");
        if(!apkFile.exists()) return false;

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

            if(showPostFragmentClasses.containsKey(apkUUID)) return false;
            showPostFragmentClasses.put(apkUUID, showPostFragmentClass);
            addPostFragmentClasses.put(apkUUID, addPostFragmentClass);
            typeDescriptions.put(apkUUID, typeDescription);
            paths.put(apkUUID,  apkFile.getAbsolutePath());
            return true;

        } catch (ClassNotFoundException e) {
            Log.d(TAG, "Extension is missing Mandatory Class");
            return false;
        } catch (IllegalAccessException e) {
            Log.d(TAG, "Extension is missing Mandatory Field in the Registry");
            return false;
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "Extension is missing Mandatory Field in the Registry");
            return false;
        }
    }
}
