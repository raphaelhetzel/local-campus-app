package de.tum.localcampusapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;

@Database(version = 4, entities = {Topic.class, Post.class, Vote.class})
@TypeConverters({Converters.class})
abstract public class AppDatabase extends RoomDatabase {
    abstract public TopicDao getTopicDao();

    abstract public PostDao getPostDao();

    abstract public VoteDao getVoteDao();

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
