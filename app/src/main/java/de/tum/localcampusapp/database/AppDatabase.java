package de.tum.localcampusapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import de.tum.localcampusapp.entity.LocationTopicMapping;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;

@Database(version = 7, entities = {Topic.class, Post.class, Vote.class, PostExtension.class, LocationTopicMapping.class})
@TypeConverters({Converters.class})
abstract public class AppDatabase extends RoomDatabase {
    abstract public TopicDao getTopicDao();

    abstract public PostDao getPostDao();

    abstract public VoteDao getVoteDao();

    abstract public PostExtensionDao getPostExtensionDao();

    abstract public LocationTopicMappingDao getLocationTopicMappingDao();

//    public static AppDatabase buildDatabase(Context applicationContext) {
//        return Room.databaseBuilder(applicationContext, AppDatabase.class, "local_campus_db")
//                .fallbackToDestructiveMigration()
//                .build();
//    }
    public static AppDatabase buildDatabase(Context applicationContext) {
        return Room.inMemoryDatabaseBuilder(applicationContext, AppDatabase.class)
                .fallbackToDestructiveMigration()
                .build();
    }
}
