package de.tum.localcampusapp.service;

import de.tum.localcampusapp.repository.LocationRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.LocationUpdateCallback;
import fi.tkk.netlab.dtn.scampi.applib.impl.parser.Protocol;

public class LocationHandler implements LocationUpdateCallback {

    private final LocationRepository locationRepository;

    public LocationHandler() {
        this(RepositoryLocator.getLocationRepository());
    }

    public LocationHandler(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void locationUpdated(AppLib appLib, double v, double v1, double v2, double v3, long l) {
        // Unused
    }

    @Override
    public void gpsLocationUpdated(Protocol.GpsLocation gpsLocation) {
        locationRepository.setCurrentLocation(buildLocationId(gpsLocation));
    }

    private String buildLocationId(Protocol.GpsLocation gpsLocation) {
        return "LAT:"+gpsLocation.latitude+",LON:"+gpsLocation.longitude;
    }
}
