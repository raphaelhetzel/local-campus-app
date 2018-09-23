package de.tum.localcampusapp.repository;

import android.content.Context;
import android.content.Intent;

import de.tum.localcampusapp.database.AppDatabase;
import de.tum.localcampusapp.extensioninterface.ExtensionLoader;
import de.tum.localcampusapp.extensioninterface.ExtensionPublisher;
import de.tum.localcampusapp.extensioninterface.RealExtensionPublisher;
import de.tum.localcampusapp.extensioninterface.StubExtensionPublisher;
import de.tum.localcampusapp.service.AppLibService;

public class RepositoryLocator {

    private static volatile boolean initialized = false;
    private static volatile UserRepository userRepository;
    private static volatile TopicRepository topicRepository;
    private static volatile PostRepository postRepository;
    private static volatile ExtensionRepository extensionRepository;
    private static volatile ExtensionLoader extensionLoader;
    private static volatile ExtensionPublisher extensionPublisher;
    private static volatile LocationRepository locationRepository;
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
        LocationRepository locationRepository = new LocationRepository(applicationContext);
        topicRepository = new RealTopicRepository(appDatabase.getTopicDao());
        RealPostRepository realPostRepository = new RealPostRepository(applicationContext,
                appDatabase.getPostDao(),
                appDatabase.getVoteDao(),
                appDatabase.getPostExtensionDao(),
                topicRepository,
                userRepository);

        // Service depends on RealPostRepository for inserting, but RealPostRepository
        // depends on the service for application layer (not a problem if initialized correctly,
        // just not as nice and clean as possible)
        // TODO refactor this by splitting service and app layer repositories. (Big change)
        applicationContext.startService(new Intent(applicationContext, AppLibService.class));
        realPostRepository.bindService();
        postRepository = realPostRepository;

        extensionRepository = new ExtensionRepository();
        extensionLoader = new ExtensionLoader(applicationContext, extensionRepository);

        RealExtensionPublisher realExtensionPublisher = new RealExtensionPublisher(applicationContext, extensionRepository);
        realExtensionPublisher.bindService();
        extensionPublisher = realExtensionPublisher;


        initialized = true;
    }

    public static void reInitInMemory(Context applicationContext) {
        userRepository = new UserRepository(applicationContext);
        LocationRepository locationRepository = new LocationRepository(applicationContext);

        topicRepository = new InMemoryTopicRepository();
        postRepository = new InMemoryPostRepository(topicRepository);
        extensionRepository = new ExtensionRepository();
        extensionLoader = new ExtensionLoader(applicationContext, extensionRepository);
        extensionPublisher = new StubExtensionPublisher();
        initialized = true;
    }

    // Warning: the real post repository currently depends on the real topic repository
    public static void reInitCustom(UserRepository newUserRepository,
                                    TopicRepository newTopicRepository,
                                    PostRepository newPostRepository,
                                    ExtensionRepository newExtensionRepository,
                                    ExtensionLoader newExtensionLoader,
                                    ExtensionPublisher newExtensionPublisher,
                                    LocationRepository newLocationRepository) {
        userRepository = newUserRepository;
        topicRepository = newTopicRepository;
        postRepository = newPostRepository;
        extensionRepository = newExtensionRepository;
        extensionLoader = newExtensionLoader;
        extensionPublisher = newExtensionPublisher;
        locationRepository = newLocationRepository;
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

    public static ExtensionPublisher getExtensionPublisher() {
        synchronized (lock) {
            if (initialized) {
                return extensionPublisher;
            }
            throw new RuntimeException("Not initialized");
        }
    }

    public static LocationRepository getLocationRepository() {
        synchronized (lock) {
            if (initialized) {
                return locationRepository;
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
                extensionPublisher = null;
                initialized = false;
            }
        }
    }
}
