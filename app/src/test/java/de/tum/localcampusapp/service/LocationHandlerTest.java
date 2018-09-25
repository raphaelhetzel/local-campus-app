package de.tum.localcampusapp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.tum.localcampusapp.repository.LocationRepository;
import fi.tkk.netlab.dtn.scampi.applib.impl.parser.Protocol;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocationHandlerTest {
    private LocationRepository mLocationRepository;

    @Before
    public void initializeMocks() {
        mLocationRepository = mock(LocationRepository.class);
    }

    @Test
    public void updatesLocation() throws IOException {
        LocationHandler locationHandler = new LocationHandler(mLocationRepository);
        byte[] testLocation = generateGpsLocation(1.0, 1.0);
        locationHandler.gpsLocationUpdated(new Protocol.GpsLocation(testLocation));
        verify(mLocationRepository).setCurrentLocation("LAT:+001.000000,LON:+001.000000");
    }

    @Test
    public void updatesLocationNegativCoordinates() throws IOException {
        LocationHandler locationHandler = new LocationHandler(mLocationRepository);
        byte[] testLocation = generateGpsLocation(-1.0, 1.0);
        locationHandler.gpsLocationUpdated(new Protocol.GpsLocation(testLocation));
        verify(mLocationRepository).setCurrentLocation("LAT:-001.000000,LON:+001.000000");
    }

    @Test
    public void updatesLocationPreciseCoordinates() throws IOException {
        LocationHandler locationHandler = new LocationHandler(mLocationRepository);
        byte[] testLocation = generateGpsLocation(1.999999999, 1.0);
        locationHandler.gpsLocationUpdated(new Protocol.GpsLocation(testLocation));
        verify(mLocationRepository).setCurrentLocation("LAT:+001.999999,LON:+001.000000");
    }

    private byte[] generateGpsLocation(double latitude, double longitude) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
        outputStream.writeDouble(longitude); // longitude
        outputStream.writeDouble(latitude); // latitude
        outputStream.writeDouble(1.0); // error
        outputStream.writeDouble(1.0); // elevation
        outputStream.writeLong(1); // timestamp
        outputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
