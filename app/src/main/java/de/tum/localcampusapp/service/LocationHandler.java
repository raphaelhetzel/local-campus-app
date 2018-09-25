package de.tum.localcampusapp.service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

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
        // Unused / Deprecated
    }

    @Override
    public void gpsLocationUpdated(Protocol.GpsLocation gpsLocation) {
       locationRepository.setCurrentLocation(buildLocationId(gpsLocation));
    }

    private String buildLocationId(Protocol.GpsLocation gpsLocation) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        formatter.applyPattern("+000.000000;-000.000000");
        formatter.setRoundingMode(RoundingMode.DOWN);
        String formatedLatitude = formatter.format(gpsLocation.latitude);
        String formatedLongitude = formatter.format(gpsLocation.longitude);
        return "LAT:"+formatedLatitude+",LON:"+formatedLongitude;
    }
}
