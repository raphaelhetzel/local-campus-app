package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

public interface LocationRepository {
    public LiveData<String> getCurrentLocation();
    public String getFinalCurrentLocation() ;
    public void setCurrentLocation(String locationId);
}
