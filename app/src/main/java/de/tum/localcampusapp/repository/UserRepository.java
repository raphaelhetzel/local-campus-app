package de.tum.localcampusapp.repository;

import android.content.Context;
import android.provider.Settings;

/**
    Simple Repository for User management.
    Due to it's simplicity, this is currently implemented directly as a class.

    Could be refactored similar to the other repositories in the future as this repository grows
    (e.g. if the username will be made selectable)
    and a Second In Memory Implementation makes sense.
 */
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
