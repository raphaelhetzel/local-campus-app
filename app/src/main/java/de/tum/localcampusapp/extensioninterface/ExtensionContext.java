package de.tum.localcampusapp.extensioninterface;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
    Context for dynamically loaded apk extensions.
    This context contains the android system resources and the resources present in the
    apk, the resources of the host extension are not present in this context. This is to prevent
    conflicting resource ids.
 */
public class ExtensionContext extends ContextWrapper {

    private AssetManager assetManager;
    private Resources resources;

    public ExtensionContext(Context base, String apkPath) {
        super(base);
        try {
            Constructor<AssetManager> assetManagerConstructor = AssetManager.class.getConstructor();
            assetManager = assetManagerConstructor.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, apkPath);
            Resources superRes = super.getResources();
            Resources childResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
            this.resources = childResources;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        return this.resources;
    }

    @Override
    public AssetManager getAssets() {
        return this.assetManager;
    }
}
