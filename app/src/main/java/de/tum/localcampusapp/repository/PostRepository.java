package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;

public interface PostRepository {

    /*
        As posts and votes are tightly coupled for performance reasons, they share a repository.
        The Post extensions could be extracted into their own repository, however this would require
        the Scampi Service connection to be extracted out of the posts repository, which makes it harder
        to handle the lifetime in the future. Also, the Extracted PostExtension Repository would need
        a way to be informed about an inserted Post, which would lead to a bidirectional binding.
        If in there are more entities coupled to a post in the future, this should be extracted, in the
        current form it does not make sense to extract it.

        The insert methods should only be used from scampi to ensure a consistent data flow, the application
        logic should use add instead.
     */

    /// Post

    LiveData<Post> getPost(long id);

    // could be removed
    LiveData<Post> getPostByUUID(String uuid);

    // could be removed by preventing duplicated in the insert method
    Post getFinalPostByUUID(String uuid);

    //Won't raise on missing related data as it will most likely run in the background
    void addPost(Post post);

    LiveData<List<Post>> getPostsforTopic(long topicId);

    void insertPost(Post post) throws DatabaseException, MissingRelatedDataException;


    /// Vote

    //Won't raise on missing related data as it will most likely run in the background
    void upVote(long postId);

    //Won't raise on missing related data as it will most likely run in the background
    void downVote(long postId);

    void insertVote(Vote vote) throws DatabaseException;


    /// PostExtension

    //Won't raise on missing related data as it will most likely run in the background
    void addPostExtension(PostExtension postExtension);

    LiveData<List<PostExtension>> getPostExtensionsForPost(long postId);

    void insertPostExtension(PostExtension postExtension) throws DatabaseException;
}
