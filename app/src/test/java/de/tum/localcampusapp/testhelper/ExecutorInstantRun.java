package de.tum.localcampusapp.testhelper;

import android.os.Handler;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ExecutorInstantRun {
    public static Executor getMockExecutor() {
        Executor mockExecutor = mock(Executor.class);
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
