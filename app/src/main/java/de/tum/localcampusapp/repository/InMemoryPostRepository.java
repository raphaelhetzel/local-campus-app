package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;

public class InMemoryPostRepository implements PostRepository {

    private MutableLiveData<List<Post>> posts;
    private final Handler handler;

    public InMemoryPostRepository() {
        this(new Handler());
    }

    public InMemoryPostRepository(Handler handler) {
        this.posts = new MutableLiveData<>();
        this.posts.setValue(new ArrayList<>());
        this.handler = handler;
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
            List<Post> items = posts.stream().filter(p -> p.getUuid().equals(uuid)).collect(Collectors.toList());
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
    }

    public void insertPost(Post post) throws DatabaseException {
        handler.post(new InsertTask(post));
    }

    // Helper to update LiveData from main Thread
    private class InsertTask implements Runnable {
        Post post;

        public InsertTask(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            List<Post> temp = new ArrayList<>(posts.getValue());
            temp.add(post);
            posts.setValue(temp);
        }
    }

    // Helper to update LiveData from main Thread
    private class UpdateTask implements Runnable {
        Post post;

        public UpdateTask(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            ArrayList<Post> temp_posts = new ArrayList<Post>(posts.getValue());
            for (int i = 0; i < temp_posts.size(); i++) {
                if (temp_posts.get(i).getId() == post.getId()) {
                    temp_posts.set(i, post);
                }
            }
            posts.setValue(temp_posts);
        }
    }
}
