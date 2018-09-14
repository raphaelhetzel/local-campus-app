package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;

public class InMemoryPostRepository implements PostRepository {

    private final Handler handler;
    private volatile Object lock = new Object();

    private MutableLiveData<List<Post>> posts;
    private MutableLiveData<List<Vote>> votes;
    private MutableLiveData<List<PostExtension>> postExtensions;

    private volatile long postId = 1L;
    private volatile long voteId = 1L;
    private volatile long extensionId = 1L;


    public InMemoryPostRepository() {
        this(new Handler());
    }

    public InMemoryPostRepository(Handler handler) {
        this.handler = handler;

        this.posts = new MutableLiveData<>();
        this.posts.setValue(new ArrayList<>());

        this.votes = new MutableLiveData<>();
        this.votes.setValue(new ArrayList<>());

        this.postExtensions = new MutableLiveData<>();
        this.postExtensions.setValue(new ArrayList<>());
    }

    /// Post

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

    /// Vote

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
        String userId = "MOCKCREATOR";
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

    /// PostExtension

    @Override
    public void addPostExtension(PostExtension postExtension) {
        Post relatedPost = posts.getValue().stream()
                .filter(post -> post.getId() == postExtension.getPostId())
                .reduce(null, (acc, current) -> current);
        if(relatedPost == null) throw new DatabaseException();
        postExtension.setPostUuid(relatedPost.getUuid());
        postExtension.setUuid(UUID.randomUUID().toString());
        postExtension.setCreatedAt(new Date());
        postExtension.setCreatorId("MOCKCREATOR");
        insertPostExtension(postExtension);
    }

    @Override
    public LiveData<List<PostExtension>> getPostExtensionsForPost(long postId) {
        return Transformations.map(postExtensions, newExtensions -> {
            return newExtensions.stream().filter(postExtension -> postExtension.getPostId() == postId)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public void insertPostExtension(PostExtension postExtension) {
        handler.post(new InsertPostExtensionTask(postExtension));
    }


    /// Runners & Helpers

    private long calculateScore(long postId, List<Vote> votes) {
        return votes.stream().filter(vote -> vote.getPostId() == postId)
                .map(vote -> vote.getScoreInfluence()).reduce(0L, (concat, influence) -> concat + influence);
    }

    // Helper to update LiveData from main Thread
    private class InsertTask implements Runnable {
        Post post;

        public InsertTask(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            synchronized (lock) {
                ArrayList<Post> temp_posts = new ArrayList<Post>(posts.getValue());

                if (post.getId() == 0) {
                    post.setId(postId++);
                } else {
                    if (temp_posts.stream().anyMatch(p -> p.getId() == post.getId()))
                        throw new DatabaseException();
                }
                temp_posts.add(post);
                posts.setValue(temp_posts);

                updateRelatedVotes();
                updateRelatedPostExtensions();
            }
        }

        private void updateRelatedVotes() {
            ArrayList<Vote> allVotes = new ArrayList<Vote>(votes.getValue());
            List<Vote> related_votes = allVotes.stream()
                    .filter(vote -> vote.getPostUuid().equals(post.getUuid()))
                    .collect(Collectors.toList());
            for (Vote vote : related_votes) {
                vote.setPostId(post.getId());
            }
            votes.setValue(allVotes);
        }

        private void updateRelatedPostExtensions() {
            ArrayList<PostExtension> allPostExtensions = new ArrayList<>(postExtensions.getValue());
            List<PostExtension> relatedPostExtensions = allPostExtensions.stream()
                    .filter(postExtension -> postExtension.getPostUuid().equals(post.getUuid()))
                    .collect(Collectors.toList());
            for (PostExtension postExtension : relatedPostExtensions) {
                postExtension.setPostId(post.getId());
            }
            postExtensions.setValue(allPostExtensions);
        }
    }

    // Helper to update LiveData from main Thread
    private class InsertVoteTask implements Runnable {
        Vote vote;

        public InsertVoteTask(Vote vote) {
            this.vote = vote;
        }

        @Override
        public void run() {
            synchronized (lock) {
                ArrayList<Vote> temp_votes = new ArrayList<Vote>(votes.getValue());
                ArrayList<Post> temp_posts = new ArrayList<>(posts.getValue());


                if (vote.getId() == 0) {
                    vote.setId(voteId++);
                } else {
                    if (temp_votes.stream().anyMatch(v -> v.getId() == vote.getId()))
                        throw new DatabaseException();
                }

                Post related_post = temp_posts.stream().filter(p -> p.getUuid().equals(vote.getPostUuid()))
                        .reduce(null, (acc, current_post) -> current_post);
                if (related_post == null) vote.setPostId(0);
                else vote.setPostId(related_post.getId());
                temp_votes.add(vote);
                votes.setValue(temp_votes);
            }
        }
    }

    // Helper to update LiveData from main Thread
    private class InsertPostExtensionTask implements Runnable {
        PostExtension postExtension;

        public InsertPostExtensionTask(PostExtension postExtension) {
            this.postExtension = postExtension;
        }

        @Override
        public void run() {
            synchronized (lock) {
                ArrayList<PostExtension> allPostExtensions = new ArrayList<>(postExtensions.getValue());
                ArrayList<Post> allPosts = new ArrayList<>(posts.getValue());

                if (postExtension.getId() == 0) {
                    postExtension.setId(extensionId++);
                } else {
                    if (allPostExtensions.stream().anyMatch(postExtension -> postExtension.getId() == postExtension.getId()))
                        throw new DatabaseException();
                }

                Post related_post = allPosts.stream().filter(p -> p.getUuid().equals(postExtension.getPostUuid()))
                        .reduce(null, (acc, current_post) -> current_post);
                if (related_post == null) postExtension.setPostId(0);
                else postExtension.setPostId(related_post.getId());
                allPostExtensions.add(postExtension);
                postExtensions.setValue(allPostExtensions);
            }
        }
    }
}
