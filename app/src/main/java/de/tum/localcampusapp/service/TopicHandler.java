package de.tum.localcampusapp.service;

import android.content.Context;
import android.util.Log;

import de.tum.localcampusapp.repository.PostRepository;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class TopicHandler implements MessageReceivedCallback {

    public static final String TAG = DiscoveryHandler.class.getSimpleName();

    private final PostRepository postRepository;

    public TopicHandler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String s) {
        Log.d(TAG, "reiceived Message for Topic");
    }
}
