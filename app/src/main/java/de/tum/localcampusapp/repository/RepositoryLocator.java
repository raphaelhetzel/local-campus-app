package de.tum.localcampusapp.repository;

import android.content.Context;
import android.content.Intent;

import de.tum.localcampusapp.database.AppDatabase;
import de.tum.localcampusapp.extensioninterface.ExtensionLoader;
import de.tum.localcampusapp.extensioninterface.ExtensionPublisher;
import de.tum.localcampusapp.extensioninterface.RealExtensionPublisher;
import de.tum.localcampusapp.extensioninterface.StubExtensionPublisher;
import de.tum.localcampusapp.service.AppLibService;

/**
    Simple Implementation of the Locator Pattern for managing instances of commonly used components and
    repositories (that might have other components as dependencies).

    Every Repository needs to be initialized before it is used. (This should be done in your entry activity)
    It is also responsible for ensuring in which order the repositories are initialized,
    which is important as some repositories depend on other repositories.

    The init methods can safely be called from multiple activities as they prevent reinitializing
    already initialized Repositories. Once initialized, the Repositories should not be changed as
    they are often cached in local variables.
*/

public class RepositoryLocator {

    private static volatile boolean initialized = false;

    private static final Container<UserRepository> userRepository = new Container<>();
    private static final Container<LocationRepository> locationRepository = new Container<>();
    private static final Container<TopicRepository> topicRepository = new Container<>();
    private static final Container<ExtensionRepository> extensionRepository = new Container<>();
    private static final Container<PostRepository> postRepository = new Container<>();
    private static final Container<NetworkLayerPostRepository> networkLayerPostRepository = new Container<>();
    private static final Container<ExtensionLoader> extensionLoader = new Container<>();
    private static final Container<ExtensionPublisher> extensionPublisher = new Container<>();

    private static final Object initLock = new Object();

    /**
        Helper class to keep track of the initialization state of a Repository / Component
     */
    private static class Container<T> {
        private T content;
        private boolean initialized = false;

        public synchronized void init(T newContent) {
            content = newContent;
            initialized = true;
        }

        public synchronized T getValue() {
            if (!initialized)
                // catch potential dependency issues early on.
                throw new RuntimeException("Unitialized");
            return content;
        }

        public synchronized void reset() {
            this.content = null;
            this.initialized = false;
        }
    }


    /**
        Initialize with the real implementations of the repositories (which use Scampi).
        Ensures the repositories and components are initialized in a way in which every dependency on other
        components is satisfied.
     */
    public static void init(Context applicationContext) {
        synchronized (initLock) {
            if (initialized == false) {
                reset();
                // Repositories the service implicitly depends on (an explicit dependency is impossible due to the limited
                // data you can pass to the service on startup)
                AppDatabase appDatabase = AppDatabase.buildDatabase(applicationContext);
                userRepository.init(new UserRepository(
                        applicationContext
                ));
                locationRepository.init(new PersistentLocationRepository(
                        applicationContext
                ));
                extensionRepository.init(new ExtensionRepository());
                extensionLoader.init(new ExtensionLoader(
                        applicationContext,
                        getExtensionRepository()
                ));
                topicRepository.init(new RealTopicRepository(
                        getLocationRepository(),
                        appDatabase.getTopicDao(),
                        appDatabase.getLocationTopicMappingDao())
                );
                networkLayerPostRepository.init(new RealNetworkLayerPostRepository(
                        getTopicRepository(),
                        appDatabase.getPostDao(),
                        appDatabase.getVoteDao(),
                        appDatabase.getPostExtensionDao()
                ));

                // The Service depends on NetworkLayerRealPostRepository for inserting,
                // but RealPostRepository depends on the service for the application layer functionality (adding),
                // that's why the Repository is Split
                applicationContext.startService(new Intent(applicationContext, AppLibService.class));

                // Repositories that depend on the Service
                postRepository.init(new RealPostRepository(applicationContext,
                        appDatabase.getPostDao(),
                        appDatabase.getVoteDao(),
                        appDatabase.getPostExtensionDao(),
                        getTopicRepository(),
                        getUserRepository())
                );
                extensionPublisher.init(new RealExtensionPublisher(
                        applicationContext,
                        getExtensionRepository()
                ));

                initialized = true;
            }
        }
    }

    /**
        Initialize with InMemory mock implementations of the repositories which are useful for testing GUI and
        application layer code. This method ensures a valid initialization order.
        As there a few android dependencies, this repositories should easily be usable in unit tests
     */
    public static void initInMemory(Context applicationContext) {
        synchronized (initLock) {
            if (initialized == false) {
                reset();
                userRepository.init(new UserRepository(applicationContext));
                locationRepository.init(new InMemoryLocationRepository());
                topicRepository.init(new InMemoryTopicRepository(getLocationRepository()));
                InMemoryPostRepository newPostRepository = new InMemoryPostRepository(getTopicRepository());
                postRepository.init(newPostRepository);
                networkLayerPostRepository.init(newPostRepository);
                extensionRepository.init(new ExtensionRepository());
                extensionLoader.init(new ExtensionLoader(applicationContext, getExtensionRepository()));
                extensionPublisher.init(new StubExtensionPublisher());

                initialized = true;
            }
        }
    }

    /**
        Initialize with custom repository implementations.
        Warning: the existing real* repositories contain some Data Level dependencies/constraints
     */
    public static void initCustom(UserRepository newUserRepository,
                                  TopicRepository newTopicRepository,
                                  PostRepository newPostRepository,
                                  ExtensionRepository newExtensionRepository,
                                  ExtensionLoader newExtensionLoader,
                                  ExtensionPublisher newExtensionPublisher,
                                  PersistentLocationRepository newLocationRepository,
                                  NetworkLayerPostRepository newNetworkLayerPostRepository) {
        synchronized (initLock) {
            if (initialized == false) {
                reset();
                userRepository.init(newUserRepository);
                topicRepository.init(newTopicRepository);
                postRepository.init(newPostRepository);
                extensionRepository.init(newExtensionRepository);
                extensionLoader.init(newExtensionLoader);
                extensionPublisher.init(newExtensionPublisher);
                locationRepository.init(newLocationRepository);
                networkLayerPostRepository.init(newNetworkLayerPostRepository);
                initialized = true;
            }
        }
    }

    public static UserRepository getUserRepository() {
        return userRepository.getValue();
    }

    public static TopicRepository getTopicRepository() {
        return topicRepository.getValue();
    }

    public static PostRepository getPostRepository() {
        return postRepository.getValue();
    }

    public static ExtensionRepository getExtensionRepository() {
        return extensionRepository.getValue();
    }

    public static ExtensionLoader getExtensionLoader() {
        return extensionLoader.getValue();
    }

    public static ExtensionPublisher getExtensionPublisher() {
        return extensionPublisher.getValue();
    }

    public static LocationRepository getLocationRepository() {
        return locationRepository.getValue();
    }

    public static NetworkLayerPostRepository getNetworkLayerPostRepository() {
        return networkLayerPostRepository.getValue();
    }


    /**
        Reset all Repositories. Due to the instances commonly being stored in local variables,
        this should only be used in special circumstances, e.g. test.
     */
    public static void reset() {
        synchronized (initLock) {
            if (initialized) {
                topicRepository.reset();
                postRepository.reset();
                userRepository.reset();
                extensionRepository.reset();
                extensionLoader.reset();
                extensionPublisher.reset();
                initialized = false;
            }
        }
    }
}
