package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;

public class InMemoryPostRepository implements PostRepository {

    private MutableLiveData<List<Post>> posts;
    private MutableLiveData<List<Vote>> votes;
    private final Handler handler;

    private volatile long vote_id = 1L;
    private volatile long post_id = 1L;
    private volatile Object lock = new Object();

    public InMemoryPostRepository() {
        this(new Handler());
    }

    public InMemoryPostRepository(Handler handler) {
        this.posts = new MutableLiveData<>();
        this.posts.setValue(new ArrayList<>());
        this.votes = new MutableLiveData<>();
        this.votes.setValue(new ArrayList<>());
        this.handler = handler;
    }

    @Override
    public LiveData<Post> getPost(long id) throws DatabaseException {
        MediatorLiveData<Post> liveData = new MediatorLiveData<>();
        liveData.addSource(posts, posts -> {
            List<Post> items = posts.stream().filter(p -> p.getId() == id).collect(Collectors.toList());
            if (items.size() == 1) {
                Post post = items.get(0);
                post.setScore(calculateScore(post.getId(), votes.getValue()));
                liveData.setValue(post);
            }
        });
        liveData.addSource(votes, vote -> {
            if (liveData.getValue() != null) {
                Post post = liveData.getValue();
                post.setScore(calculateScore(post.getId(), votes.getValue()));
                liveData.setValue(post);
            }
        });
        return liveData;
    }

    @Override
    public LiveData<Post> getPostByUUID(String uuid) {
        MediatorLiveData<Post> liveData = new MediatorLiveData<>();
        liveData.addSource(posts, posts -> {
            List<Post> items = posts.stream().filter(p -> p.getUuid().equals(uuid)).collect(Collectors.toList());
            if (items.size() == 1) {
                Post post = items.get(0);
                post.setScore(calculateScore(post.getId(), votes.getValue()));
                liveData.setValue(post);
            }
        });
        liveData.addSource(votes, vote -> {
            if (liveData.getValue() != null) {
                Post post = liveData.getValue();
                post.setScore(calculateScore(post.getId(), votes.getValue()));
                liveData.setValue(post);
            }
        });
        return liveData;
    }

    @Override
    public Post getFinalPostByUUID(String uuid) {
        Post result_post = posts.getValue().stream().filter(p -> p.getUuid().equals(uuid)).reduce(null, (concat, post) -> post);
        if (result_post == null) return null;
        result_post.setScore(calculateScore(result_post.getId(), votes.getValue()));
        return result_post;
    }

    @Override
    public void addPost(Post post) throws DatabaseException {
        insertPost(post);
    }

    @Override
    public LiveData<List<Post>> getPostsforTopic(long topicId) throws DatabaseException {
        MediatorLiveData<List<Post>> liveData = new MediatorLiveData<>();
        liveData.addSource(posts, posts -> {
            List<Post> items = posts.stream().filter(p -> p.getTopicId() == topicId).collect(Collectors.toList());
            for (Post post : items) {
                post.setScore(calculateScore(post.getId(), votes.getValue()));
            }
            liveData.setValue(items);
        });
        liveData.addSource(votes, vote -> {
            List<Post> posts = liveData.getValue();
            for (Post post : posts) {
                post.setScore(calculateScore(post.getId(), votes.getValue()));
            }
            liveData.setValue(posts);
        });
        return liveData;
    }

    public void insertPost(Post post) throws DatabaseException {
        handler.post(new InsertTask(post));
    }

    @Override
    public void upVote(long postId) {
        vote(postId, 1L);
    }

    @Override
    public void downVote(long postId) {
        vote(postId, -1L);
    }

    @Override
    public void insertVote(Vote vote) throws DatabaseException {
        handler.post(new InsertVoteTask(vote));
    }

    private void vote(long postId, long scoreInflunce) {
        String userId = "TODOCREATOR";
        Post post = posts.getValue().stream().filter(p -> p.getId() == postId).reduce(null, (cancat, post1) -> post1);
        if (post == null) return;
        if (!votes.getValue().stream().anyMatch(v -> v.getPostId() == postId && v.getCreatorId().equals(userId))) {
            try {
                insertVote(new Vote(UUID.randomUUID().toString(), post.getUuid(), userId, new Date(), scoreInflunce));
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    private long calculateScore(long postId, List<Vote> votes) {
        return votes.stream().filter(vote -> vote.getPostId() == postId)
                .map(vote -> vote.getScoreInfluence()).reduce(0L, (concat, influence) -> concat + influence);
    }

    // Helper to update LiveData from main Thread
    private class InsertVoteTask implements Runnable {
        Vote vote;

        public InsertVoteTask(Vote vote) {
            this.vote = vote;
        }

        @Override
        public void run() {
            ArrayList<Vote> temp_votes = new ArrayList<Vote>(votes.getValue());
            ArrayList<Post> temp_posts = new ArrayList<>(posts.getValue());


            if (vote.getId() == 0) {
                synchronized (lock) {
                    vote.setId(vote_id++);
                }
            } else {
                if (temp_votes.stream().anyMatch(v -> v.getId() == vote.getId()))
                    throw new DatabaseException();
            }

            Post related_post = temp_posts.stream().filter(p -> p.getUuid().equals(vote.getPostUuid())).reduce(null, (acc, current_post) -> current_post);
            if (related_post == null) vote.setPostId(0);
            else vote.setPostId(related_post.getId());
            temp_votes.add(vote);
            votes.setValue(temp_votes);
        }
    }

    // Helper to update LiveData from main Thread
    private class InsertTask implements Runnable {
        Post post;

        public InsertTask(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            ArrayList<Vote> temp_votes = new ArrayList<Vote>(votes.getValue());
            ArrayList<Post> temp_posts = new ArrayList<>(posts.getValue());

            if (post.getId() == 0) {
                synchronized (lock) {
                    post.setId(post_id++);
                }
            } else {
                if (temp_posts.stream().anyMatch(p -> p.getId() == post.getId()))
                    throw new DatabaseException();
            }

            List<Vote> related_votes = temp_votes.stream().filter(vote -> vote.getPostUuid().equals(post.getUuid())).collect(Collectors.toList());
            for (Vote vote : related_votes) {
                vote.setPostId(post.getId());
            }
            temp_posts.add(post);
            posts.setValue(temp_posts);
            votes.setValue(temp_votes);
        }
    }
}
