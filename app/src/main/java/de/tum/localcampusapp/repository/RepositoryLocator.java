package de.tum.localcampusapp.repository;

import android.content.Context;

import de.tum.localcampusapp.database.AppDatabase;

public class RepositoryLocator {

    private static volatile AppDatabase appDatabase;
    private static volatile TopicRepository topicRepository;
    private static volatile PostRepository postRepository;
    private static final Object lock = new Object();

    public static void reset() {
        topicRepository = null;
        postRepository = null;
    }
    //TODO: This kind of initialization is too lazy, add an init method
    public static TopicRepository getTopicRepository(Context applicationContext) {
        if (topicRepository != null) {
            return topicRepository;
        }
        synchronized (lock) {
            if (topicRepository == null) {
                topicRepository = new RealTopicRepository(getAppDatabase(applicationContext).getTopicDao());
            }
            return topicRepository;
        }
    }

    public static void setCustomTopicRepository(TopicRepository newTopicRepository) {
        synchronized (lock) {
            topicRepository = newTopicRepository;
        }
    }

    public static PostRepository getPostRepository(Context applicationContext) {
        if (postRepository != null) {
            return postRepository;
        }
        synchronized (lock) {
            if (postRepository == null) {
                postRepository = new RealPostRepository(applicationContext);
            }
            return postRepository;
        }
    }

    public static void setCustomPostRepository(PostRepository newPostRepository) {
        synchronized (lock) {
            postRepository = newPostRepository;
        }
    }

    public static AppDatabase getAppDatabase(Context applicationContext) {
        if (appDatabase != null) {
            return appDatabase;
        }
        synchronized (lock) {
            if (appDatabase == null) {
                appDatabase = AppDatabase.buildDatabase(applicationContext);
            }
            return appDatabase;
        }
    }
}
