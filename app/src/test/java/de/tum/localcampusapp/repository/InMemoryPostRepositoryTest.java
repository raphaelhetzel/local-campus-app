package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.os.Handler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.List;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
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
        assertEquals (repository.getFinalPostByUUID("foo"), null);
        assertEquals (repository.getFinalPostByUUID(uuid), post1);
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
    public void add_update() throws DatabaseException, InterruptedException {
        Post post1 = new Post();
        post1.setId(1);
        post1.setData("Before");
        repository.addPost(post1);
        LiveData<Post> single_post = repository.getPost(1);
        assert (LiveDataHelper.getValue(single_post).getData().equals("Before"));
        Post post2 = new Post();
        post1.setId(1);
        post1.setData("After");
        assert (LiveDataHelper.getValue(single_post).getData().equals("After"));
    }
}
