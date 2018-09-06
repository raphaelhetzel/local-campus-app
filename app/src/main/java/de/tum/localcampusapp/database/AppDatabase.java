package de.tum.localcampusapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;

@Database(version = 1, entities = {Topic.class, Post.class})
@TypeConverters({Converters.class})
abstract class AppDatabase extends RoomDatabase {
    abstract public TopicDao getTopicDao();

    abstract public PostDao getPostDao();
}
