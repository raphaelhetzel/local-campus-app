package de.tum.localcampusapp.testhelper;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ScheduledExecutorInstantRun {
    public static ScheduledExecutorService getMockExecutor() {
        ScheduledExecutorService mockExecutor = mock(ScheduledExecutorService.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }

        }).when(mockExecutor).execute(any(Runnable.class));

        return mockExecutor;
    }
}
