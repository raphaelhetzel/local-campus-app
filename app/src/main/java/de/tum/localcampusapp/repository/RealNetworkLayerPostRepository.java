package de.tum.localcampusapp.repository;

import java.util.List;

import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.database.PostExtensionDao;
import de.tum.localcampusapp.database.VoteDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;

public class RealNetworkLayerPostRepository implements NetworkLayerPostRepository {


    static final String TAG = RealNetworkLayerPostRepository.class.getSimpleName();

    private final TopicRepository topicRepository;

    private final PostDao postDao;
    private final PostExtensionDao postExtensionDao;
    private final VoteDao voteDao;

    private Object insertLock = new Object();

    public RealNetworkLayerPostRepository(TopicRepository topicRepository,
                                          PostDao postDao,
                                          VoteDao voteDao,
                                          PostExtensionDao postExtensionDao) {
        this.postDao = postDao;
        this.postExtensionDao = postExtensionDao;
        this.voteDao = voteDao;
        this.topicRepository = topicRepository;

    }

    @Override
    public void insertPost(Post post) throws DatabaseException, MissingRelatedDataException {
        try {
            Topic relatedTopic = topicRepository.getFinalTopicByName(post.getTopicName());
            if (relatedTopic == null) throw new MissingRelatedDataException();

            post.setScore(0);
            post.setTopicId(relatedTopic.getId());

            synchronized (insertLock) {
                long postId = postDao.insert(post);
                post.setId(postId);
                updateRelatedVotes(post);
                updateRelatedPostExtensions(post);
            }

        } catch (android.database.sqlite.SQLiteConstraintException e) {
            // Ignore duplicate inserts
            if (e.getMessage().contains("posts.uuid")) {
                return;
            }
            throw new DatabaseException();
        }
    }

    @Override
    public void insertVote(Vote vote) throws DatabaseException {
        try {
            synchronized (insertLock) {

                Vote existing_user_vote = voteDao.getUserVoteByUUID(vote.getPostUuid(), vote.getCreatorId());
                if (existing_user_vote != null) return;

                Post post = postDao.getFinalPostByUUID(vote.getPostUuid());
                if (post == null) vote.setPostId(0L);
                else vote.setPostId(post.getId());
                voteDao.insert(vote);
            }
        }
        // Catches both the case where the id and the uuid are duplicate
        catch (android.database.sqlite.SQLiteConstraintException e) {
            /*
                Ignore duplicate votes, while matching a String isn't ideal
                this should be fine as the string is verified by a test
             */
            if (e.getMessage().contains("votes.uuid")) {
                return;
            }
            throw new DatabaseException();
        }
    }

    @Override
    public void insertPostExtension(PostExtension postExtension) throws DatabaseException {
        try {
            synchronized (insertLock) {
                Post relatedPost = postDao.getFinalPostByUUID(postExtension.getPostUuid());
                if (relatedPost == null) postExtension.setPostId(0);
                else postExtension.setPostId(relatedPost.getId());
                postExtensionDao.insert(postExtension);
            }
        }
        // Catches both the case where the id and the uuid are duplicate
        catch (android.database.sqlite.SQLiteConstraintException e) {
            // ignore duplicate inserts
            if (e.getMessage().contains("post_extensions.uuid")) {
                return;
            }
            throw new DatabaseException();
        }
    }

    private void updateRelatedVotes(Post post) {
        List<Vote> unassignedVotes = voteDao.getVotesByPostUUID(post.getUuid());
        for (Vote vote : unassignedVotes) {
            vote.setPostId(post.getId());
            voteDao.update(vote);
        }
    }

    private void updateRelatedPostExtensions(Post post) {
        List<PostExtension> unassignedPosts = postExtensionDao.getFinalPostExtensionsByPostUUID(post.getUuid());
        for (PostExtension postExtension : unassignedPosts) {
            postExtension.setPostId(post.getId());
            postExtensionDao.update(postExtension);
        }
    }
}
