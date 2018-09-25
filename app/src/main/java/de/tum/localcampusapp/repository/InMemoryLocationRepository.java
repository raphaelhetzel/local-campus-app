package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

public class InMemoryLocationRepository implements LocationRepository {

    private static String NO_LOCATION = "no_location";

    private final MutableLiveData<String> current_location;

    private final Handler handler;


    public InMemoryLocationRepository() {
        this(new Handler());
    }

    public InMemoryLocationRepository(Handler handler) {
        current_location = new MutableLiveData<>();
        current_location.setValue(NO_LOCATION);
        this.handler = handler;
    }

    public LiveData<String> getCurrentLocation() {
        return this.current_location;
    }

    public String getFinalCurrentLocation() {
        return this.current_location.getValue();
    }

    public void setCurrentLocation(String locationId) {
        this.handler.post(() -> {
            current_location.setValue(locationId);
        });
    }
}
