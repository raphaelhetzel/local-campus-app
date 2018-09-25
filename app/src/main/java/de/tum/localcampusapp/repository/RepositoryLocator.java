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
    private static volatile LocationRepository locationRepository;
    private static volatile TopicRepository topicRepository;
    private static volatile ExtensionRepository extensionRepository;
    private static volatile PostRepository postRepository;

    private static volatile ExtensionLoader extensionLoader;
    private static volatile ExtensionPublisher extensionPublisher;
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
        locationRepository = new LocationRepository(applicationContext);
        extensionRepository = new ExtensionRepository();
        topicRepository = new RealTopicRepository(locationRepository, appDatabase.getTopicDao(), appDatabase.getLocationTopicMappingDao());
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
        extensionLoader = new ExtensionLoader(applicationContext, extensionRepository);

        RealExtensionPublisher realExtensionPublisher = new RealExtensionPublisher(applicationContext, extensionRepository);
        realExtensionPublisher.bindService();
        extensionPublisher = realExtensionPublisher;


        initialized = true;
    }

    public static void reInitInMemory(Context applicationContext) {
        userRepository = new UserRepository(applicationContext);
        locationRepository = new LocationRepository(applicationContext);

        topicRepository = new InMemoryTopicRepository(locationRepository);
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
            return userRepository;
        }
    }

    public static TopicRepository getTopicRepository() {
        synchronized (lock) {
            return topicRepository;
        }
    }

    public static PostRepository getPostRepository() {
        synchronized (lock) {
            return postRepository;
        }
    }

    public static ExtensionRepository getExtensionRepository() {
        synchronized (lock) {
            return extensionRepository;
        }
    }

    public static ExtensionLoader getExtensionLoader() {
        synchronized (lock) {
            return extensionLoader;
        }
    }

    public static ExtensionPublisher getExtensionPublisher() {
        synchronized (lock) {
            return extensionPublisher;
        }
    }

    public static LocationRepository getLocationRepository() {
        synchronized (lock) {
            return locationRepository;
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
