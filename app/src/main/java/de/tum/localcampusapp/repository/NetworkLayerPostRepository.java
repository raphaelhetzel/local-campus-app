package de.tum.localcampusapp.repository;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;

/**
    This Repository contains the methods to interact with Posts, Votes and PostsExtensions
    that should be only be used from the Network layer. This is to ensure a consisten data flow
    in which everything shown in the UI Layer (even the posts created by the application itself)
    was processed by the networking layer.

    Please also refer to the discussion about the coupling of Posts, Votes and PostExtensions in
    {@link PostRepository}.
 */
public interface NetworkLayerPostRepository {

    /**
        Insert a Post to the Database. The linked Topic MUST be present in the Database, otherwise the insert
        is ignored. Will handle duplicate posts by ignoring them.

        Expectes the postExtension to exactly contain the fields <code>uuid</code>,
        <code>postUuid</code>, <code>creatorId</code>, <code>createdAt</code> and <code>data</code>.
     */
    void insertPost(Post post) throws DatabaseException, MissingRelatedDataException;

    /**
        Insert a PostExtension to the Database. Does't need the linked post to be present.
        Will handle duplicated by ignoring them.

        Expectes the postExtension to exactly contain the fields <code>uuid</code>,
        <code>postUuid</code>, <code>creatorId</code>, <code>createdAt</code> and <code>data</code>.
     */
    void insertPostExtension(PostExtension postExtension) throws DatabaseException;

    /**
        Insert a Vote to the Database. Does't need the linked post to be present.
        Will handle duplicated by ignoring them.

        Expectes the Vote to exactly contain the fields <code>uuid</code>,
        <code>postUuid</code>, <code>creatorId</code>, <code>createdAt</code> and <code>scoreInfluence</code>.
     */
    void insertVote(Vote vote) throws DatabaseException;
}
