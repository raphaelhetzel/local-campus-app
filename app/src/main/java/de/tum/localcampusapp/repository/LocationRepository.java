package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

public class LocationRepository {

    private static String CURRENT_LOCATION = "current_location";
    private static String NO_LOCATION = "no_location";

    private final MutableLiveData<String> current_location;
    private final SharedPreferences sharedPreferences;

    private final Handler handler;
    private final Object lock = new Object();


    public LocationRepository(Context applicationContext) {
        this(applicationContext.getSharedPreferences(
                "de.tum.localcampusapp.CURRENT_LOCATION_FILE",
                Context.MODE_PRIVATE),
                new Handler()
        );
    }

    public LocationRepository(SharedPreferences sharedPreferences, Handler handler) {
        this.sharedPreferences = sharedPreferences;
        current_location = new MutableLiveData<>();
        current_location.setValue(loadValue());
        this.handler = handler;
    }

    public LiveData<String> getCurrentLocation() {
        synchronized (lock) {
            return this.current_location;
        }
    }

    public void setCurrentLocation(String locationId) {
        this.handler.post(() -> {
            synchronized (lock) {
                storeValue(locationId);
                current_location.setValue(locationId);
            }
        });
    }

    private String loadValue() {
        return sharedPreferences.getString(CURRENT_LOCATION, NO_LOCATION);
    }

    private void storeValue(String locationId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_LOCATION, locationId);
        editor.commit();
    }
}
