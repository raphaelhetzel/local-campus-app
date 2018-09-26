package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;

/**
    This Post Repository contains the methods to interact with Posts, Votes and PostsExtensions
    that should be used from the GUI/Applications layer.

    Some notes about the decision to share the Repository of Post, Vote and PostExtension:
    As posts and votes are tightly coupled for performance reasons (You can query the score
    via the <code>getPost()</code> and <code>getPostsForTopic()</code> methods) they share a repository.

    The Post extensions could be extracted into their own repository, however this would require
    the Scampi Service connection to be extracted out of the posts repository, which will load to more complex
    lifetime handling. Also, the Extracted PostExtension Repository would need
    a way to be informed about an inserted Post, which would lead to a bidirectional binding.
    If in there are more entities coupled to a Post in the future this should be extracted, in the
    current form it does not make sense to extract it.
 */
public interface PostRepository {

    /// Post

    LiveData<Post> getPost(long id);

    LiveData<List<Post>> getPostsforTopic(long topicId);

    /**
        Add a new Post. As this will most likely run in a background thread,
        it won't provide any feedback whether the operation was successful.

        It is expected that the post used in this function uses the Constructor that only sets
        <code>topicId</code>, <code>typeId</code> and <code>data</code>. Furthermore the Topic referenced by
        <code>topicID</code> MUST exist.
     */
    void addPost(Post post);


    /// Vote

    /**
        Upvote a Post. As this will most likely run in a background thread,
        it won't provide any feedback whether the operation was successful.
        Won't allow duplicate Votes.

        The post referenced by <code>postId</code> MUST exist.
     */
    void upVote(long postId);

    /**
     Downvote a Post. As this will most likely run in a background thread,
     it won't provide any feedback whether the operation was successful.
     Won't allow duplicate Votes.

     The post referenced by <code>postId</code> MUST exist.
     */
    void downVote(long postId);


    /// PostExtension

    /**
     Add a new PostExtension. As this will most likely run in a background thread,
     it won't provide any feedback whether the operation was successful.
     Won't filter for duplicates.

     It is expected that the post used in this function uses the Constructor that only sets
     <code>postId</code> and <code>data</code>. Furthermore the Post referenced by
     <code>postID</code> MUST exist.
     */
    void addPostExtension(PostExtension postExtension);

    LiveData<List<PostExtension>> getPostExtensionsForPost(long postId);
}
