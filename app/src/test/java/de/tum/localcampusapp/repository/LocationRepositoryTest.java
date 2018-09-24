package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.SharedPreferences;
import android.os.Handler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.tum.localcampusapp.testhelper.HandlerInstantRun;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationRepositoryTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    Handler mHandler;
    SharedPreferences mSharedPreferences;

    @Before
    public void initializeMocks() {
        mHandler = HandlerInstantRun.getMockHandler();
        mSharedPreferences = mock(SharedPreferences.class);
    }

    @Test
    public void DefaultLocation() throws InterruptedException {
        when(mSharedPreferences.getString("current_location", "no_location")).thenReturn("no_location");
        LocationRepository locationRepository = new LocationRepository(mSharedPreferences, mHandler);
        assertEquals(LiveDataHelper.getValue(locationRepository.getCurrentLocation()), "no_location");
    }

    @Test
    public void SetGetLocation() throws InterruptedException {
        SharedPreferences.Editor mEditor = mock(SharedPreferences.Editor.class);
        when(mSharedPreferences.edit()).thenReturn(mEditor);
        LocationRepository locationRepository = new LocationRepository(mSharedPreferences, mHandler);
        locationRepository.setCurrentLocation("newLocation");

        assertEquals(LiveDataHelper.getValue(locationRepository.getCurrentLocation()), "newLocation");

        verify(mEditor).putString("current_location", "newLocation");
        verify(mEditor).commit();
    }

    @Test
    public void GetFinalLocation() throws InterruptedException {
        SharedPreferences.Editor mEditor = mock(SharedPreferences.Editor.class);
        when(mSharedPreferences.edit()).thenReturn(mEditor);
        LocationRepository locationRepository = new LocationRepository(mSharedPreferences, mHandler);
        locationRepository.setCurrentLocation("newLocation");

        assertEquals(locationRepository.getFinalCurrentLocation(), "newLocation");
    }

    @Test
    public void LoadSavedLocation() throws InterruptedException {
        when(mSharedPreferences.getString("current_location", "no_location")).thenReturn("oldLocation");
        LocationRepository locationRepository = new LocationRepository(mSharedPreferences, mHandler);
        assertEquals(LiveDataHelper.getValue(locationRepository.getCurrentLocation()), "oldLocation");
    }
}
