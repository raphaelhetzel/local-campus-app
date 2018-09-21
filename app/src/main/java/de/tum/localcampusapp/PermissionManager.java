package de.tum.localcampusapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

public class PermissionManager {
    public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private Context applicationContext;

    public PermissionManager(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean hasStoragePermission() {
        return applicationContext.checkSelfPermission( PERMISSION_STORAGE ) == PackageManager.PERMISSION_GRANTED;
    }


}
