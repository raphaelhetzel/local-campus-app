package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.os.Handler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.testhelper.HandlerInstantRun;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertEquals;

public class InMemoryPostRepositoryTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    Handler mockHandler = HandlerInstantRun.getMockHandler();

    PostRepository repository;

    @Before
    public void setupRepository() {
        repository = new InMemoryPostRepository(mockHandler);
    }

    @Test
    public void add_getId() throws DatabaseException, InterruptedException {
        Post post1 = new Post();
        post1.setId(1);
        repository.addPost(post1);
        Post post2 = new Post();
        post2.setId(2);
        repository.addPost(post2);
        LiveData<Post> single_result = repository.getPost(1);
        assert (LiveDataHelper.getValue(single_result).equals(post1));
    }

    @Test
    public void add_getByUUID() throws DatabaseException, InterruptedException {
        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        Post post1 = new Post();
        post1.setId(1);
        post1.setUuid(uuid);
        repository.addPost(post1);
        Post post2 = new Post();
        post2.setId(2);
        post2.setUuid(uuid2);
        repository.addPost(post2);
        LiveData<Post> single_result = repository.getPostByUUID(uuid);
        assert (LiveDataHelper.getValue(single_result).equals(post1));
    }

    @Test
    public void add_getFinalByUUID() throws DatabaseException, InterruptedException {
        String uuid = UUID.randomUUID().toString();
        Post post1 = new Post();
        post1.setId(1);
        post1.setUuid(uuid);
        repository.addPost(post1);
        assertEquals(repository.getFinalPostByUUID("foo"), null);
        assertEquals(repository.getFinalPostByUUID(uuid), post1);
    }

    @Test
    public void add_getPostsForTopics() throws DatabaseException, InterruptedException {
        Post post1 = new Post();
        post1.setId(1);
        post1.setTopicId(1);
        repository.addPost(post1);
        Post post2 = new Post();
        post2.setId(2);
        post2.setTopicId(2);
        repository.addPost(post2);
        LiveData<List<Post>> posts = repository.getPostsforTopic(1);
        assert (LiveDataHelper.getValue(posts).get(0).equals(post1));
    }

    @Test
    public void upVote() throws DatabaseException {
        String uuid = UUID.randomUUID().toString();
        Post post1 = new Post();
        post1.setId(1);
        post1.setUuid(uuid);
        repository.addPost(post1);
        repository.upVote(1);
        assertEquals(repository.getFinalPostByUUID(uuid).getScore(), 1);
    }

    @Test
    public void upVoteLiveDataChangesOnlyOnce() throws DatabaseException, InterruptedException {
        String uuid = UUID.randomUUID().toString();
        Post post1 = new Post();
        post1.setId(1);
        post1.setUuid(uuid);
        repository.addPost(post1);

        LiveData<Post> resultPost = repository.getPostByUUID(uuid);
        assertEquals(LiveDataHelper.getValue(resultPost).getScore(), 0);
        repository.upVote(1);
        assertEquals(LiveDataHelper.getValue(resultPost).getScore(), 1);
        repository.upVote(1);
        assertEquals(LiveDataHelper.getValue(resultPost).getScore(), 1);
    }

    @Test
    public void upVoteLiveDataMultiple() throws DatabaseException, InterruptedException {
        Post post1 = new Post();
        post1.setId(1);
        post1.setTopicId(1);
        post1.setUuid(UUID.randomUUID().toString());
        repository.addPost(post1);

        LiveData<List<Post>> resultPost = repository.getPostsforTopic(1);
        assertEquals(LiveDataHelper.getValue(resultPost).size(), 1);

        Post post2 = new Post();
        post2.setId(2);
        post2.setTopicId(1);
        post2.setUuid(UUID.randomUUID().toString());
        repository.addPost(post2);
        assertEquals(LiveDataHelper.getValue(resultPost).size(), 2);

        assertEquals(LiveDataHelper.getValue(resultPost).get(0).getScore(), 0);
        repository.upVote(LiveDataHelper.getValue(resultPost).get(0).getId());
        assertEquals(LiveDataHelper.getValue(resultPost).get(0).getScore(), 1);
    }

    @Test
    public void downVote() throws DatabaseException {
        String uuid = UUID.randomUUID().toString();
        Post post1 = new Post();
        post1.setId(1);
        post1.setUuid(uuid);
        repository.addPost(post1);
        repository.downVote(1);
        assertEquals(repository.getFinalPostByUUID(uuid).getScore(), -1);
    }

    @Test
    public void insertPostUsesPreviouslyCreatedVotes() throws DatabaseException {
        String post_uuid = UUID.randomUUID().toString();
        String vote_uuid = UUID.randomUUID().toString();

        Post post1 = new Post();
        post1.setId(1);
        post1.setUuid(post_uuid);

        Vote vote1 = new Vote(vote_uuid, post_uuid, "Creator", new Date(), 10);
        repository.insertVote(vote1);
        repository.insertPost(post1);

        assertEquals(repository.getFinalPostByUUID(post_uuid).getScore(), 10);
    }

    @Test
    public void insertPostLinksPostExtensions() throws DatabaseException, InterruptedException {
        String postUUID = UUID.randomUUID().toString();
        String extensionUUID = UUID.randomUUID().toString();

        Post post1 = new Post();
        post1.setId(1);
        post1.setUuid(postUUID);

        PostExtension postExtension = new PostExtension(extensionUUID, postUUID, "Creator", new Date(), "Data");
        repository.insertPostExtension(postExtension);
        repository.insertPost(post1);

        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).size(), 1);
        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).get(0).getData(), "Data");
    }

    @Test
    public void addPostExtension_getPostExtensionsForPost() throws DatabaseException, InterruptedException {
        String postUUID = UUID.randomUUID().toString();

        Post post1 = new Post();
        post1.setId(1);
        post1.setUuid(postUUID);

        PostExtension postExtension = new PostExtension(1, "Data");
        repository.insertPost(post1);
        repository.addPostExtension(postExtension);

        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).size(), 1);
        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).get(0).getData(), "Data");
        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).get(0).getPostUuid(), postUUID);
    }
}
