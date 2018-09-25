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

    private static final Container<UserRepository> userRepository = new Container<>();
    private static final Container<LocationRepository> locationRepository = new Container<>();
    private static final Container<TopicRepository> topicRepository = new Container<>();
    private static final Container<ExtensionRepository> extensionRepository  = new Container<>();
    private static final Container<PostRepository> postRepository  = new Container<>();
    private static final Container<NetworkLayerPostRepository> networkLayerPostRepository= new Container<>();
    private static final Container<ExtensionLoader> extensionLoader = new Container<>();
    private static final Container<ExtensionPublisher> extensionPublisher = new Container<>();
    private static final Object lock = new Object();

    private static class Container<T> {
        private T content;
        private boolean initialized = false;
        public synchronized void init(T newContent) {
            content = newContent;
            initialized = true;
        }

        public synchronized T getValue() {
            if(! initialized)
                // catch potential dependency issues early on.
                throw new RuntimeException("Unitialized");
            return content;
        }

        public synchronized void reset() {
            this.content = null;
            this.initialized = false;
        }
    }


    // Needs to be called before any Repository is used.
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

        // Repositories the service might depend on (implicit, explicit impossible due to the limited
        // data you can pass to the service start.
        AppDatabase appDatabase = AppDatabase.buildDatabase(applicationContext);
        userRepository.init(new UserRepository(applicationContext));
        locationRepository.init(new PersistentLocationRepository(applicationContext));
        extensionRepository.init(new ExtensionRepository());
        extensionLoader.init(new ExtensionLoader(applicationContext, getExtensionRepository()));
        topicRepository.init(new RealTopicRepository(getLocationRepository(), appDatabase.getTopicDao(), appDatabase.getLocationTopicMappingDao()));

        RealPostRepository realPostRepository = new RealPostRepository(applicationContext,
                appDatabase.getPostDao(),
                appDatabase.getVoteDao(),
                appDatabase.getPostExtensionDao(),
                getTopicRepository(),
                getUserRepository());
        networkLayerPostRepository.init(realPostRepository);

        // The Service depends on RealPostRepository for inserting and other database activities,
        // but RealPostRepository depends on the service for the application layer functionality,
        // thats why the interfaces are split. TODO split implementation
        applicationContext.startService(new Intent(applicationContext, AppLibService.class));

        // Repositories that depend on the Service
        realPostRepository.bindService();
        postRepository.init(realPostRepository);

        RealExtensionPublisher realExtensionPublisher = new RealExtensionPublisher(applicationContext, getExtensionRepository());
        realExtensionPublisher.bindService();
        extensionPublisher.init(realExtensionPublisher);


        initialized = true;
    }

    public static void reInitInMemory(Context applicationContext) {
        userRepository.init( new UserRepository(applicationContext));
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

    // Warning: the real post repository currently depends on the real topic repository
    public static void reInitCustom(UserRepository newUserRepository,
                                    TopicRepository newTopicRepository,
                                    PostRepository newPostRepository,
                                    ExtensionRepository newExtensionRepository,
                                    ExtensionLoader newExtensionLoader,
                                    ExtensionPublisher newExtensionPublisher,
                                    PersistentLocationRepository newLocationRepository,
                                    NetworkLayerPostRepository newNetworkLayerPostRepository) {
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

    public static void reset() {
        synchronized (lock) {
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
