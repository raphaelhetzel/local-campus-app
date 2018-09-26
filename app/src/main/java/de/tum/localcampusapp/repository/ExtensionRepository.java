package de.tum.localcampusapp.repository;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.ExtensionInfo;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampusapp.extensioninterface.ExtensionContext;
import de.tum.localcampuslib.ShowPostFragment;

/**
    Repository to manage the various post Types available to the System.
    The Extension could either be bundled with the application and registed by the application itself,
    or loaded from an Extension apk by the {@link de.tum.localcampusapp.extensioninterface.ExtensionLoader}.

    As the users of this repositories should not care about the instantiation of the Fragments there is no Extension entity.
    Instead, this repository provides the users with methods to directly receive relevant parts of the Extensions,
    e,g. a new instance of the Fragments and a Context depending on the source of the Extension(included vs. loaded).
 */
public class ExtensionRepository {


    public static volatile Map<String, Extension> extensionStorage;

    public ExtensionRepository() {
        extensionStorage = new HashMap<>();
    }

    /**
        Register an Extension, MUST also be called for local PostTypes.

        Will allow duplicate inserts by only using the first registration of an Extension.

        Local Extensions MUST provide <code>null</code> as their resourceApkPath.
     */
    public void registerExtension(String extensionUUID,
                                   String description,
                                   Class<? extends ShowPostFragment> showPostFragmentClass,
                                   Class<? extends AddPostFragment> addPostFragmentClass,
                                   String resourceApkPath) {

        if (extensionStorage.containsKey(extensionUUID)) return;
        extensionStorage.put(extensionUUID, new Extension(addPostFragmentClass, showPostFragmentClass, description, resourceApkPath));
    }

    /**
        Get Information about all registered Extensions.
        Will return a simple Object containing the extensions <code>uuid</code>, <code>description</code> and apkPath. In case of local extensions,
        this apkPath will be set to <code>null</code>.
     */
    public List<ExtensionInfo> getExtensions() {
        return extensionStorage.entrySet().stream().map((entry -> {
            File extensionFile = (entry.getValue().apkPath == null || entry.getValue().apkPath.isEmpty()) ? null : new File(entry.getValue().apkPath);
            return new ExtensionInfo(entry.getKey(), entry.getValue().typeDescription, extensionFile);
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

    /**
        Provides a Context for the Fragments of an Extension.
        For dynamically loaded extensions, this context will contain the resources bundled
        with the apk. Currently, loaded extensions don't have acess to the resources of the host application.
     */
    public Context getContextFor(String extensionUUID, Context activityContext) {
        if(!extensionStorage.containsKey(extensionUUID)) return activityContext;
        String apkPath = extensionStorage.get(extensionUUID).apkPath;
        if(apkPath == null || apkPath.isEmpty()) {
            return activityContext;
        } else {
            return new ExtensionContext(activityContext, apkPath);
        }
    }

    /**
        Instantiate the Fragment to show a Post of a certain Type.
        This Fragment MUST only be used by a Activity extending {@link de.tum.localcampuslib.ShowPostHostActivity}
     */
    public ShowPostFragment getShowPostFragmentFor(String extensionUUID) {
        if(!extensionStorage.containsKey(extensionUUID)) return null;
        try {
            return (ShowPostFragment) extensionStorage.get(extensionUUID).showPostFragmentClass.newInstance();
        }   catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
        Instantiate the Fragment to add a Post of a certain Type.
        This Fragment MUST only be used by a Activity extending {@link de.tum.localcampuslib.AddPostHostActivity}
     */
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
