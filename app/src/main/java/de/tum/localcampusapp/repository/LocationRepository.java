package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

/**
    Repository to manage the location information received from the network (e.g. current room).
 */
public interface LocationRepository {
    public LiveData<String> getCurrentLocation();
    /**
        Directly return the current location identifier.
        As this could be blocking, it MUST not be called from the UI thread!
     */
    public String getFinalCurrentLocation() ;

    /**
        Set the current Location.
        As this will most likely run on a Background Thread, this won't
        provide any feedback about the operations success.
     */
    public void setCurrentLocation(String locationId);
}
