package de.tum.localcampusapp.repository;

import android.content.Context;
import android.provider.Settings;

public class UserRepository {

    private Context context;

    public UserRepository(Context context) {
        this.context = context;
    }

    public String getId() {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
