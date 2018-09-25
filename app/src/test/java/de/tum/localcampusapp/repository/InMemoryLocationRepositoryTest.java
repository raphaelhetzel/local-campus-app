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
public class InMemoryLocationRepositoryTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    Handler mHandler;

    @Before
    public void initializeMocks() {
        mHandler = HandlerInstantRun.getMockHandler();
    }

    @Test
    public void DefaultLocation() throws InterruptedException {
        InMemoryLocationRepository inMemoryLocationRepository = new InMemoryLocationRepository(mHandler);
        assertEquals(LiveDataHelper.getValue(inMemoryLocationRepository.getCurrentLocation()), "no_location");
    }

    @Test
    public void SetGetLocation() throws InterruptedException {
        InMemoryLocationRepository inMemoryLocationRepository = new InMemoryLocationRepository(mHandler);
        inMemoryLocationRepository.setCurrentLocation("newLocation");

        assertEquals(LiveDataHelper.getValue(inMemoryLocationRepository.getCurrentLocation()), "newLocation");
    }

    @Test
    public void GetFinalLocation() throws InterruptedException {
        InMemoryLocationRepository inMemoryLocationRepository = new InMemoryLocationRepository(mHandler);
        inMemoryLocationRepository.setCurrentLocation("newLocation");

        assertEquals(inMemoryLocationRepository.getFinalCurrentLocation(), "newLocation");
    }
}
