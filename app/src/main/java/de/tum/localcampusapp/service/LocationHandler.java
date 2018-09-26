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

/**
    Handles the location updates provided by AppLib.
    Uses the Latidude and Longitude to generate a Location identifier of the form
    <code>Lat:+000.000000,LON:+000.000000</code>, normalizing the length of the coordinates to create
    a Location identifier that is then used as the current location.
    (While they are cut here, they MUST be defined with exactly this precession in the Scampi Service).

    This is fine as a LocationIdentifier if the locations are statically configured as they are now
    (if configured with the right precession, as it is currently expected, there is no processing that
    would lead to floating point precession problems). The System is designed around distinct locations
    (e.g. a room), however this is impossible with Scampi, so we use static coordinates to identify a room.
    This static identifier is also published by the service responsible for informing about the topics
    available at a Location (currently, the field is called deviceId, needs to have the same form as
    the locationId described above).

    If the locations would be used as actual locations, this needs to be changed to allow some
    degree of imprecision (which would require a different approach to querying and storing the topic
    to location mapping, e.g. storing them as multiple REAL values in the Database, querying them with an
    allowed error).

    A discrete value for the location would be preferable to easily identify the location, e.g the
    cafeteria or a room.
 */
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
