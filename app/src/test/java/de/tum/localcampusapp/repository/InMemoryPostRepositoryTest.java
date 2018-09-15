package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.os.Handler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;
import de.tum.localcampusapp.testhelper.HandlerInstantRun;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryPostRepositoryTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    Handler mockHandler = HandlerInstantRun.getMockHandler();
    TopicRepository mTopicRepository;

    PostRepository repository;

    @Before
    public void setupRepositoryandMock() {
        mTopicRepository = mock(TopicRepository.class);
        repository = new InMemoryPostRepository(mockHandler, mTopicRepository);

        Topic topic = new Topic(1, "/tum");
        when(mTopicRepository.getFinalTopic(1)).thenReturn(topic);
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);
    }

    @Test
    public void addPost_getId_hasDefaults() throws DatabaseException, InterruptedException {
        Topic topic = new Topic(1, "/tum");
        Date date = new Date();

        when(mTopicRepository.getFinalTopic(1)).thenReturn(topic);
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);

        Post post1 = new Post(1, "Type", "Data");
        post1.setId(1);
        repository.addPost(post1);
        Post post2 = new Post(1, "Type", "Data2");
        post2.setId(2);
        post2.setUuid("UUID");
        post2.setCreatedAt(date);
        post2.setCreator("Creator");
        repository.addPost(post2);

        Post postResult1 = LiveDataHelper.getValue(repository.getPost(1));
        Post postResult2 = LiveDataHelper.getValue(repository.getPost(2));

        assertEquals("Data", post1.getData());
        assertEquals("Type", post1.getTypeId());
        assertEquals("MOCKUSER", post1.getCreator());
        assert(!post1.getUuid().isEmpty());
        assert(!(post1.getCreatedAt() == null));
        assertEquals("/tum", post1.getTopicName());

        assertEquals("Data2", post2.getData());
        assertEquals("Type", post2.getTypeId());
        assertEquals("UUID", post2.getUuid());
        assertEquals("Creator", post2.getCreator());
        assertEquals(date, post2.getCreatedAt());
        assertEquals("/tum", post2.getTopicName());
    }

    @Test
    public void add_getByUUID() throws DatabaseException, InterruptedException {
        String uuid = UUID.randomUUID().toString();

        Post post1 = new Post(1, "Type", "Data");
        post1.setUuid(uuid);

        repository.addPost(post1);

        LiveData<Post> single_result = repository.getPostByUUID(uuid);
        assert (LiveDataHelper.getValue(single_result).equals(post1));
    }

    @Test
    public void add_getFinalByUUID() throws DatabaseException, InterruptedException {

        String uuid = UUID.randomUUID().toString();
        Post post1 = new Post(1, "Type", "Data");
        post1.setId(1);
        post1.setUuid(uuid);
        repository.addPost(post1);
        assertEquals(repository.getFinalPostByUUID("foo"), null);
        assertEquals(repository.getFinalPostByUUID(uuid), post1);
    }

    @Test
    public void add_getPostsForTopics() throws DatabaseException, InterruptedException {

        Topic topic1 = new Topic(3, "/t1");
        Topic topic2 = new Topic(4, "/t2");
        when(mTopicRepository.getFinalTopic(3)).thenReturn(topic1);
        when(mTopicRepository.getFinalTopicByName("/t1")).thenReturn(topic1);
        when(mTopicRepository.getFinalTopic(4)).thenReturn(topic2);
        when(mTopicRepository.getFinalTopicByName("/t2")).thenReturn(topic2);

        Post post1 = new Post(3, "Type", "Test");
        repository.addPost(post1);
        Post post2 = new Post(4, "Type", "Test2");
        repository.addPost(post2);
        LiveData<List<Post>> posts = repository.getPostsforTopic(3);
        assertEquals ("Test", LiveDataHelper.getValue(posts).get(0).getData());
    }

    @Test
    public void upVote() throws DatabaseException {

        String uuid = UUID.randomUUID().toString();
        Post post1 = new Post(1, "Type", "Data");
        post1.setId(1);
        post1.setUuid(uuid);
        repository.addPost(post1);
        repository.upVote(1);
        assertEquals(repository.getFinalPostByUUID(uuid).getScore(), 1);
    }

    @Test
    public void upVoteLiveDataChangesOnlyOnce() throws DatabaseException, InterruptedException {

        String uuid = UUID.randomUUID().toString();
        Post post1 = new Post(1, "Type", "Data");
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
    public void testMediator() throws DatabaseException, InterruptedException {

        Post post1 = new Post(1, "Type", "Data1");
        post1.setId(1);
        repository.addPost(post1);

        LiveData<List<Post>> resultPost = repository.getPostsforTopic(1);
        assertEquals(LiveDataHelper.getValue(resultPost).size(), 1);

        Post post2 = new Post(1, "Type", "Data2");
        post2.setId(2);
        repository.addPost(post2);
        assertEquals(LiveDataHelper.getValue(resultPost).size(), 2);

        assertEquals(LiveDataHelper.getValue(resultPost).get(0).getScore(), 0);
        repository.upVote(LiveDataHelper.getValue(resultPost).get(0).getId());
        assertEquals(LiveDataHelper.getValue(resultPost).get(0).getScore(), 1);
    }

    @Test
    public void downVote() throws DatabaseException {

        String uuid = UUID.randomUUID().toString();
        Post post1= new Post(1, "Type", "Data1");
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

        Post post1 = new Post(1, "Type", "Data1");
        post1.setId(1);
        post1.setUuid(post_uuid);

        Vote vote1 = new Vote(vote_uuid, post_uuid, "Creator", new Date(), 10);
        repository.insertVote(vote1);
        repository.addPost(post1);

        assertEquals(repository.getFinalPostByUUID(post_uuid).getScore(), 10);
    }

    @Test
    public void insertPostLinksPostExtensions() throws DatabaseException, InterruptedException {

        String postUUID = UUID.randomUUID().toString();
        String extensionUUID = UUID.randomUUID().toString();

        Post post1 = new Post(1, "Type", "Data1");
        post1.setId(1);
        post1.setUuid(postUUID);

        PostExtension postExtension = new PostExtension(extensionUUID, postUUID, "Creator", new Date(), "Data");
        repository.insertPostExtension(postExtension);
        repository.addPost(post1);

        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).size(), 1);
        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).get(0).getData(), "Data");
    }

    @Test
    public void addPostExtension_getPostExtensionsForPost() throws DatabaseException, InterruptedException, MissingRelatedDataException {

        String postUUID = UUID.randomUUID().toString();

        Post post1 = new Post(1, "Type", "Data1");
        post1.setId(1);
        post1.setUuid(postUUID);

        PostExtension postExtension = new PostExtension(1, "Data");
        repository.addPost(post1);
        repository.addPostExtension(postExtension);

        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).size(), 1);
        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).get(0).getData(), "Data");
        assertEquals(LiveDataHelper.getValue(repository.getPostExtensionsForPost(1)).get(0).getPostUuid(), postUUID);
    }
}
