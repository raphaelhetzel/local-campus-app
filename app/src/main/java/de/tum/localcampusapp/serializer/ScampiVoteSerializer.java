package de.tum.localcampusapp.serializer;

import java.util.UUID;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.PostRepository;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class ScampiVoteSerializer {

    public static final String UUID_FIELD = "uuid";
    public static final String POST_UUID_FIELD = "post_uuid";
    public static final String CREATOR_FIELD = "creator";
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String SCORE_INFLUENCE_FIELD = "score_influence";
    //TODO: Unify with other serialziers
    public static final String MESSAGE_TYPE_FIELD = "message_type";
    public static final String MESSAGE_TYPE_VOTE = "vote";

    public SCAMPIMessage voteToMessage(Vote vote, Post post) throws MissingFieldsException {
        if (!voteHasRequiredFields(vote)) throw new MissingFieldsException();

        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().appTag(vote.getUuid()).build();
        scampiMessage.putString(UUID_FIELD, vote.getUuid());
        scampiMessage.putString(POST_UUID_FIELD, post.getUuid());
        scampiMessage.putString(CREATOR_FIELD, vote.getCreatorId());
        scampiMessage.putInteger(CREATED_AT_FIELD, Converters.dateToTimestamp(vote.getCreatedAt()));
        scampiMessage.putInteger(SCORE_INFLUENCE_FIELD, vote.getScoreInfluence());
        return scampiMessage;
    }

    private boolean voteHasRequiredFields(Vote vote) {
        if (!(vote.getUuid() == null ||
                vote.getUuid().isEmpty() ||
                vote.getCreatedAt() == null ||
                vote.getCreatorId() == null ||
                vote.getCreatorId().isEmpty() ||
                vote.getScoreInfluence() == 0)) return true;
        return false;
    }
}
