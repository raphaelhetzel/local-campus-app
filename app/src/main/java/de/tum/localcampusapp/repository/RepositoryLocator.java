package de.tum.localcampusapp.repository;

import android.content.Context;

import java.util.concurrent.Executors;

import de.tum.localcampusapp.database.AppDatabase;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;

public class RepositoryLocator {

    private static volatile boolean initialized = false;
    private static volatile TopicRepository topicRepository;
    private static volatile ScampiPostSerializer scampiPostSerializer;
    private static volatile PostRepository postRepository;
    private static final Object lock = new Object();


    // Needs to be called before any Repository is used.
    // Service should be explicitly started before.
    public static void init(Context applicationContext) {
        synchronized (lock) {
            if (initialized == false) reInit(applicationContext);

        }
    }

    public static void initInMemory(Context applicationContext) {
        synchronized (lock) {
            if (initialized == false) reInitInMemory(applicationContext);
        }
    }

    public static void reInit(Context applicationContext) {
        AppDatabase appDatabase = AppDatabase.buildDatabase(applicationContext);
        topicRepository = new RealTopicRepository(appDatabase.getTopicDao());
        scampiPostSerializer = new ScampiPostSerializer(topicRepository);
        postRepository = new RealPostRepository(applicationContext,
                appDatabase.getPostDao(),
                topicRepository,
                Executors.newSingleThreadExecutor(),
                scampiPostSerializer,
                appDatabase.getVoteDao());
        initialized = true;
    }

    public static void reInitInMemory(Context applicationContext) {
        topicRepository = new InMemoryTopicRepository();
        scampiPostSerializer = new ScampiPostSerializer(topicRepository);
        postRepository = new InMemoryPostRepository();
        initialized = true;
    }

    // Warning: the real post repository currently depends on the real topic repository
    public static void reInitCustom(TopicRepository newTopicRepository, PostRepository newPostRepository, ScampiPostSerializer newScampiPostSerializer) {
        topicRepository = newTopicRepository;
        scampiPostSerializer = newScampiPostSerializer;
        postRepository = newPostRepository;
        initialized = true;
    }

    public static TopicRepository getTopicRepository() {
        synchronized (lock) {
            if (initialized) {
                return topicRepository;
            }
            throw new RuntimeException("Not initialized");
        }
    }

    public static PostRepository getPostRepository() {
        synchronized (lock) {
            if (initialized) {
                return postRepository;
            }
            throw new RuntimeException("Not initialized");
        }
    }

    public static ScampiPostSerializer getScampiPostSerializer() {
        synchronized (lock) {
            if (initialized) {
                return scampiPostSerializer;
            }
            throw new RuntimeException("Not initialized");
        }
    }

    public static void reset() {
        synchronized (lock) {
            if (initialized) {
                topicRepository = null;
                postRepository = null;
                scampiPostSerializer = null;
                initialized = false;
            }
        }
    }
}
