package de.tum.localcampusapp.testhelper;

import android.os.Handler;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HandlerInstantRun {
    public static Handler getMockHandler() {
        Handler mockHandler = mock(Handler.class);
        when(mockHandler.post(any(Runnable.class))).thenAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }

        });
        return mockHandler;
    }
}
