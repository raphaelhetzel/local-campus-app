package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.generator.JSONParser;
import de.tum.localcampusapp.postTypes.PostMapper;
import de.tum.localcampusapp.postTypes.PostMapperHelper;
import de.tum.localcampusapp.postTypes.Comment;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostCommentViewModel extends ViewModel {

    private LiveData<List<Comment>> liveDataComments;
    private LiveData<List<Comment>> liveDataCommentsFiltered;

    private LiveData<PostMapper> livePostMapper;
    private long postId;
    PostMapperHelper postMapperHelper;
    private PostRepository postRepository;

    private static final String ATTR_DATA = "text";


    public PostCommentViewModel(long postId, Context context) throws DatabaseException{

        this.postId = postId;
        this.postRepository = RepositoryLocator.getPostRepository();

        liveDataComments = Transformations.map(postRepository.getPostExtensionsForPost(postId), postExtensions -> {
            return postExtensions.stream().map(postExtension -> {
                return Comment.getWorkingComment(postId,
                        1,
                        postExtension.getData(),
                        postExtension.getCreatedAt());
            }).collect(Collectors.toList());
        });

        liveDataCommentsFiltered = getFilteredLiveComments();     //filtered comments

        postMapperHelper = new PostMapperHelper(postId, true);
        livePostMapper = postMapperHelper.tranformPost();

        //FakeDataGenerator.getInstance().createSeveralFakeComments(3, commentHelper, postId, 1);
    }


    public static String makeJsonCommentOutput(String textInput) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(ATTR_DATA, textInput);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public void addComment(String commentData) throws DatabaseException {
        String jsonText = JSONParser.makeJsonCommentOutput(commentData);
        postRepository.addPostExtension(new PostExtension(getPostId(), jsonText));
    }

    public LiveData<List<Comment>> getFilteredLiveComments(){
        return Transformations.map(liveDataComments, (List<Comment> listComments) -> {
            List<Comment> workingComments = new ArrayList<>();
            for (Comment comment : listComments) {
                if(comment!=null){
                    workingComments.add(comment);
                }
            }
            return workingComments;
        });
    }

    public LiveData<PostMapper> getLiveDataPost() {
        return livePostMapper;
    }

    public int getBackColor(Post post) throws JSONException {
        PostMapper postMapper = PostMapper.getWorkingPostMapper(post);
        return postMapper.getColor();
    }

    public long getPostId(){
        return postId;
    }

    public void upVote(){
        postRepository.upVote(postId);
    }

    public void downVote(){
        postRepository.downVote(postId);
    }

}