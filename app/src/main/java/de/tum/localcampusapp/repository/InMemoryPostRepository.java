package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;

// TODO: Should be Singleton, but this is most likely part of the DI method
public class InMemoryPostRepository implements PostRepository {
    private MutableLiveData<List<Post>> posts;

    public InMemoryPostRepository() {
        this.posts = new MutableLiveData<>();
        this.posts.setValue(new ArrayList<>());
    }

    @Override
    public LiveData<Post> getPost(long id) throws DatabaseException {
        return Transformations.map(posts, posts -> {
            List<Post> items = posts.stream().filter(p -> p.getId() == id).collect(Collectors.toList());
            if (items.size() == 1) {
                return items.get(0);
            }
            return null;
        });
    }

    @Override
    public LiveData<Post> getPostByUUID(String uuid) throws DatabaseException {
        return Transformations.map(posts, posts -> {
            List<Post> items = posts.stream().filter(p -> p.getUUID().equals(uuid)).collect(Collectors.toList());
            if (items.size() == 1) {
                return items.get(0);
            }
            return null;
        });
    }

    @Override
    public void addPost(Post post) throws DatabaseException {
        insertPost(post);
    }

    @Override
    public LiveData<List<Post>> getPostsforTopic(long topicId) throws DatabaseException {
        return Transformations.switchMap(posts, posts -> {
            MutableLiveData<List<Post>> topic_posts = new MutableLiveData<>();
            List<Post> items = posts.stream().filter(p -> p.getTopicId() == topicId).collect(Collectors.toList());
            topic_posts.setValue(items);
            return topic_posts;
        });
    }

    @Override
    public void updatePost(Post post) throws DatabaseException {
        ArrayList<Post> posts = new ArrayList<>(this.posts.getValue());
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getId() == post.getId()) {
                posts.set(i, post);
                return;
            }
        }
    }

    public void insertPost(Post post) throws DatabaseException {
        List<Post> temp = new ArrayList<>(posts.getValue());
        temp.add(post);
        posts.setValue(temp);
    }
}
