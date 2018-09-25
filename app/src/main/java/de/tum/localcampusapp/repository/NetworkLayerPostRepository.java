package de.tum.localcampusapp.repository;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;

public interface NetworkLayerPostRepository {

    void insertPostExtension(PostExtension postExtension) throws DatabaseException;

    void insertVote(Vote vote) throws DatabaseException;

    void insertPost(Post post) throws DatabaseException, MissingRelatedDataException;
}
