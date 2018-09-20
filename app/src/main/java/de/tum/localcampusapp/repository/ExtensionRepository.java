package de.tum.localcampusapp.repository;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.ExtensionInfo;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ExtensionContext;
import de.tum.localcampuslib.ShowPostFragment;

public class ExtensionRepository {


    public static volatile Map<String, Extension> extensionStorage;

    public ExtensionRepository() {
        extensionStorage = new HashMap<>();
    }

    // Accepts duplicate inserts
    public void registerExtension(String extensionUUID,
                                   String description,
                                   Class<? extends ShowPostFragment> showPostFragmentClass,
                                   Class<? extends AddPostFragment> addPostFragmentClass,
                                   String resourceApkPath) {

        if (extensionStorage.containsKey(extensionUUID)) return;
        extensionStorage.put(extensionUUID, new Extension(addPostFragmentClass, showPostFragmentClass, description, resourceApkPath));
    }

    // To be used with the create post extension type selection
    public List<ExtensionInfo> getExtensions() {
        return extensionStorage.entrySet().stream().map((entry -> {
            return new ExtensionInfo(entry.getKey(), entry.getValue().typeDescription);
        })).collect(Collectors.toList());
    }

    public boolean extensionExists(String extensionUUID) {
        return extensionStorage.containsKey(extensionUUID);
    }

    public String getDescriptionFor(String extensionUUID) {
        if(!extensionStorage.containsKey(extensionUUID)) return "Unknown PostType";
        String description = extensionStorage.get(extensionUUID).typeDescription;
        if(description ==null || description.isEmpty()) return "No Post Description";
        return description;
    }

    public Context getContextFor(String extensionUUID, Context activityContext) {
        if(!extensionStorage.containsKey(extensionUUID)) return activityContext;
        String apkPath = extensionStorage.get(extensionUUID).apkPath;
        if(apkPath == null || apkPath.isEmpty()) {
            return activityContext;
        } else {
            return new ExtensionContext(activityContext, apkPath);
        }
    }

    public ShowPostFragment getShowPostFragmentFor(String extensionUUID) {
        if(!extensionStorage.containsKey(extensionUUID)) return null;
        try {
            return (ShowPostFragment) extensionStorage.get(extensionUUID).showPostFragmentClass.newInstance();
        }   catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AddPostFragment getAddPostFragmentFor(String extensionUUID) {
        if(!extensionStorage.containsKey(extensionUUID)) return null;
        try {
            return (AddPostFragment) extensionStorage.get(extensionUUID).addPostFragmentClass.newInstance();
        }   catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class Extension {
        public Class<? extends AddPostFragment> addPostFragmentClass;
        public Class<? extends ShowPostFragment> showPostFragmentClass;
        public String typeDescription;
        public String apkPath;

        public Extension(Class<? extends AddPostFragment> addPostFragmentClass, Class<? extends ShowPostFragment> showPostFragmentClass, String typeDescription, String apkPath) {
            this.addPostFragmentClass = addPostFragmentClass;
            this.showPostFragmentClass = showPostFragmentClass;
            this.typeDescription = typeDescription;
            this.apkPath = apkPath;
        }
    }
}
