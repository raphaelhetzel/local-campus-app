package de.tum.localcampusapp.repository;

import android.content.Context;

import de.tum.localcampusapp.database.AppDatabase;
import de.tum.localcampusapp.extensioninterface.ExtensionLoader;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;

public class RepositoryLocator {

    private static volatile boolean initialized = false;
    private static volatile UserRepository userRepository;
    private static volatile TopicRepository topicRepository;
    private static volatile PostRepository postRepository;
    private static volatile ExtensionRepository extensionRepository;
    private static volatile ExtensionLoader extensionLoader;
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
        userRepository = new UserRepository(applicationContext);
        topicRepository = new RealTopicRepository(appDatabase.getTopicDao());
        postRepository = new RealPostRepository(applicationContext,
                appDatabase.getPostDao(),
                appDatabase.getVoteDao(),
                appDatabase.getPostExtensionDao(),
                topicRepository,
                userRepository);
        extensionRepository = new ExtensionRepository();
        extensionLoader = new ExtensionLoader(applicationContext, extensionRepository);
        initialized = true;
    }

    public static void reInitInMemory(Context applicationContext) {
        userRepository = new UserRepository(applicationContext);
        topicRepository = new InMemoryTopicRepository();
        postRepository = new InMemoryPostRepository(topicRepository);
        extensionRepository = new ExtensionRepository();
        extensionLoader = new ExtensionLoader(applicationContext, extensionRepository);
        initialized = true;
    }

    // Warning: the real post repository currently depends on the real topic repository
    public static void reInitCustom(UserRepository newUserRepository, TopicRepository newTopicRepository, PostRepository newPostRepository, ExtensionRepository newExtensionRepository, ExtensionLoader newExtensionLoader) {
        userRepository = newUserRepository;
        topicRepository = newTopicRepository;
        postRepository = newPostRepository;
        extensionRepository = newExtensionRepository;
        extensionLoader = newExtensionLoader;
        initialized = true;
    }

    public static UserRepository getUserRepository() {
        synchronized (lock) {
            if (initialized) {
                return userRepository;
            }
            throw new RuntimeException("Not initialized");
        }
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

    public static ExtensionRepository getExtensionRepository() {
        synchronized (lock) {
            if (initialized) {
                return extensionRepository;
            }
            throw new RuntimeException("Not initialized");
        }
    }

    public static ExtensionLoader getExtensionLoader() {
        synchronized (lock) {
            if (initialized) {
                return extensionLoader;
            }
            throw new RuntimeException("Not initialized");
        }
    }

    public static void reset() {
        synchronized (lock) {
            if (initialized) {
                topicRepository = null;
                postRepository = null;
                userRepository = null;
                extensionRepository = null;
                extensionLoader = null;
                initialized = false;
            }
        }
    }
}
