package de.tum.localcampusapp.viewModel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.generator.ColorGenerator;
import de.tum.localcampusapp.postTypes.PostMapper;
import de.tum.localcampusapp.postTypes.PostMapperHelper;
import de.tum.localcampusapp.repository.InMemoryPostRepository;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.TopicRepository;
import de.tum.localcampusapp.testhelper.HandlerInstantRun;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostMapperTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    Handler mockHandler = HandlerInstantRun.getMockHandler();
    TopicRepository mTopicRepository;
    PostRepository repository;

    Context context;
    Random mRandom;
    long topicId;


    @Before
    public void setupMocks() {
        mTopicRepository = mock(TopicRepository.class);
        repository = new InMemoryPostRepository(mockHandler, mTopicRepository);

        Topic topic = new Topic(1, "/tum");
        when(mTopicRepository.getFinalTopic(1)).thenReturn(topic);
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);

        topicId = 1;
        int [] elem = {123, 234, 345, 456};
        context = mock(Context.class);
        mRandom = mock(Random.class);
        Resources mResources = mock(Resources.class);
        when(context.getResources()).thenReturn(mResources);
        when(context.getResources().getIntArray(R.array.CampusAppColors)).thenReturn(elem);
    }


    @Test
    public void generateColor(){
        int color = ColorGenerator.getColor(context);
        Assert.assertNotNull(color);
    }


    @Test
    public void notValidPostMapper() {
        String uuid = UUID.randomUUID().toString();
        String typeId = "typeId";
        String topicName = "topicName";
        String creator = "creator";
        Date createdAt = new Date();
        String sampleData = "Not Json data";
        Post post = new Post(uuid, typeId, topicName, creator, createdAt, sampleData);

        PostMapper postMapper = PostMapper.getValidPostMapper(post);
        assert(Objects.equals(postMapper, null));
    }

    @Test
    public void validPostMapper() {
        String uuid = UUID.randomUUID().toString();
        String typeId = "typeId";
        String topicName = "topicName";
        String creator = "creator";
        Date createdAt = new Date();
        String sampleData = "not Json Data";

        String jsonText = PostMapper.makeJsonPostOutput(sampleData, context);

        Post post = new Post(uuid, typeId, topicName, creator, createdAt, jsonText);
        PostMapper postMapper = PostMapper.getValidPostMapper(post);
        assertFalse(Objects.equals(postMapper, null));
    }

    @Test
    public void jsonDataParsing(){
        String uuid = UUID.randomUUID().toString();
        String typeId = "typeId";
        String topicName = "topicName";
        String creator = "creator";
        Date createdAt = new Date();
        String sampleData = "not Json Data";

        String jsonText = PostMapper.makeJsonPostOutput(sampleData, context);

        Post post = new Post(uuid, typeId, topicName, creator, createdAt, jsonText);
        PostMapper postMapper = PostMapper.getValidPostMapper(post);
        assertEquals(postMapper.getTextComment(), sampleData);

    }

    @Test
    public void liveDataTransformation() throws InterruptedException {

        String text = "Data";
        String jsonText = PostMapper.makeJsonPostOutput(text, context);

        Post post1 = new Post(topicId, "Type", jsonText);
        long postId = 11;
        post1.setId(postId);

        repository.addPost(post1);

        PostMapperHelper postMapperHelper = new PostMapperHelper(repository,postId, true);
        LiveData<PostMapper> liveData = postMapperHelper.tranformPost();
        PostMapper postMapper = LiveDataHelper.getValue(liveData);

        assertFalse(Objects.equals(postMapper, null));
        assertEquals(postId, postMapper.getId());
        assertEquals(postMapper.getTextComment(), text);
        assertEquals(postMapper.getId(), postId, post1.getId());
    }

    @Test
    public void validLiveData() throws InterruptedException{

        int postsNumberValid = 5;
        int postsNumberNotValid = 3;

        for(int i=1; i<=postsNumberValid; i++){
            long postId = i;
            String text = "Data No: " + postId;
            String jsonText = PostMapper.makeJsonPostOutput(text, context);
            Post post = new Post(topicId, "Type", jsonText);
            post.setId(postId);
            repository.addPost(post);
        }

        PostMapperHelper postMapperHelper = new PostMapperHelper(repository, topicId);
        LiveData<List<PostMapper>> liveData = postMapperHelper.transformPosts();
        List<PostMapper> postMappers = LiveDataHelper.getValue(liveData);

        assertEquals(postMappers.size(), postsNumberValid);


        for(int i=1; i<=postsNumberNotValid; i++){
            long postId = postsNumberValid + i;
            String text = "Data No: " + postId;
            Post post = new Post(topicId, "Type", text);
            post.setId(postId);
            repository.addPost(post);
        }

        PostMapperHelper postMapperHelper2 = new PostMapperHelper(repository, topicId);
        LiveData<List<PostMapper>> liveData2 = postMapperHelper2.transformPosts();
        List<PostMapper> postMappers2 = LiveDataHelper.getValue(liveData);

        assertEquals(postMappers2.size(), postsNumberValid, postMappers.size());


        String text = "Data No: " + ( ++postsNumberValid );
        String jsonText = PostMapper.makeJsonPostOutput(text, context);
        Post post = new Post(topicId, "Type", jsonText);
        post.setId(postsNumberValid + postsNumberNotValid);
        repository.addPost(post);

        PostMapperHelper postMapperHelper3 = new PostMapperHelper(repository, topicId);
        LiveData<List<PostMapper>> liveData3 = postMapperHelper3.transformPosts();
        List<PostMapper> postMappers3 = LiveDataHelper.getValue(liveData);

        assertEquals(postsNumberValid, postMappers.size(), postMappers3.size());
    }

    @Test
    public void internalRatingComparison(){
        String uuid = UUID.randomUUID().toString();
        String typeId = "typeId";
        String topicName = "topicName";
        String creator = "creator";
        Date createdAt = new Date();
        String sampleData = "Not Json data";
        String jsonText = PostMapper.makeJsonPostOutput(sampleData, context);

        Post post = new Post(uuid, typeId, topicName, creator, createdAt, jsonText);
        PostMapper postMapper = PostMapper.getValidPostMapper(post);


        Calendar calendar = Calendar.getInstance();

        Date dateNow = new Date();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        Date dateDayAgo = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -4);
        Date date4DaysAgo = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date dateMonthAgo = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date dateYearAgo = calendar.getTime();


        final long score5 = 5;

        float ratingNow = postMapper.calculateRating(dateNow, score5);
        float rating1DayAgo = postMapper.calculateRating(dateDayAgo, score5);
        float rating4DaysAgo = postMapper.calculateRating(date4DaysAgo, score5);
        float ratingMonthAgo = postMapper.calculateRating(dateMonthAgo, score5);
        float ratingYearAgo = postMapper.calculateRating(dateYearAgo, score5);

        System.out.println("Score: "+score5+ ", 0 days ago: Rating: "+ratingNow);
        System.out.println("Score: "+score5+ ", 1 days ago: Rating: "+rating1DayAgo);
        System.out.println("Score: "+score5+ ", 4 days ago: Rating: "+rating4DaysAgo);
        System.out.println("Score: "+score5+ ", Month ago: Rating: "+ratingMonthAgo);
        System.out.println("Score: "+score5+ ", Year ago: Rating: "+ratingYearAgo);


        assertTrue(ratingYearAgo < ratingMonthAgo && ratingMonthAgo < rating4DaysAgo
                && rating4DaysAgo < rating1DayAgo && rating1DayAgo < ratingNow);


        final long scoreMinus5 = -5;

        float ratingNowMinus5 = postMapper.calculateRating(dateNow, scoreMinus5);
        float rating1DayAgoMinus5  = postMapper.calculateRating(dateDayAgo, scoreMinus5);
        float rating4DaysAgoMinus5  = postMapper.calculateRating(date4DaysAgo, scoreMinus5);
        float ratingYearAgoMinus5  = postMapper.calculateRating(dateYearAgo, scoreMinus5);

        System.out.println("Score: "+scoreMinus5+ ", 0 days ago: Rating: "+ratingNowMinus5);
        System.out.println("Score: "+scoreMinus5+ ", 1 days ago: Rating: "+rating1DayAgoMinus5);
        System.out.println("Score: "+scoreMinus5+ ", 4 days ago: Rating: "+rating4DaysAgoMinus5);
        System.out.println("Score: "+scoreMinus5+ ", Year ago: Rating: "+ratingYearAgoMinus5);


        assertTrue(ratingYearAgoMinus5 < rating4DaysAgoMinus5
                && rating4DaysAgoMinus5 < rating1DayAgoMinus5 && rating1DayAgoMinus5 < ratingNowMinus5);


        final long score0 = 0;

        float ratingNow0 = postMapper.calculateRating(dateNow, score0);
        float rating1DayAgo0  = postMapper.calculateRating(dateDayAgo, score0);
        float rating4DaysAgo0  = postMapper.calculateRating(date4DaysAgo, score0);
        float ratingYearAgo0  = postMapper.calculateRating(dateYearAgo, score0);

        System.out.println("Score: "+score0+ ", 0 days ago: Rating: "+ratingNow0);
        System.out.println("Score: "+score0+ ", 1 days ago: Rating: "+rating1DayAgo0);
        System.out.println("Score: "+score0+ ", 4 days ago: Rating: "+rating4DaysAgo0);
        System.out.println("Score: "+score0+ ", Year ago: Rating: "+ratingYearAgo0);

        assertTrue(ratingYearAgo0 < rating4DaysAgo0
                && rating4DaysAgo0 < rating1DayAgo0 && rating1DayAgo0 < ratingNow0);


        assertTrue(ratingYearAgo>ratingYearAgo0 && ratingYearAgo0>ratingYearAgoMinus5
                &&rating4DaysAgo>rating4DaysAgo0 && rating4DaysAgo0>rating4DaysAgoMinus5 &&
                rating1DayAgo > rating1DayAgo0 && rating1DayAgo0 > rating1DayAgoMinus5);
    }

    @Test
    public void comparisonMethod(){

        // methods which could be also tested
        //public List<PostMapper> comparison(List<PostMapper> pm)

        //tranformPost()
        //transformPosts()

    /*
     public List<PostMapper> comparison(List<PostMapper> pm){
        Comparator<PostMapper> pmComparator = Comparator.comparingDouble(PostMapper::getInternalRating);
        pm.sort(pmComparator);
        Collections.reverse(pm);
        return pm;
    }
     */
    }

}
