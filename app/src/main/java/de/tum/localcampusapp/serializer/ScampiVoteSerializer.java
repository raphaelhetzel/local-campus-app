package de.tum.localcampusapp.serializer;

import java.util.UUID;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.WrongParserException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class ScampiVoteSerializer {

    public static final String UUID_FIELD = "uuid";
    public static final String POST_UUID_FIELD = "post_uuid";
    public static final String CREATOR_FIELD = "creator";
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String SCORE_INFLUENCE_FIELD = "score_influence";

    //TODO: Unify with other serializers
    public static final String MESSAGE_TYPE_FIELD = "message_type";
    public static final String MESSAGE_TYPE_VOTE = "vote";

    public SCAMPIMessage voteToMessage(Vote vote) throws MissingFieldsException {
        if (!voteHasRequiredFields(vote)) throw new MissingFieldsException();

        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().appTag(vote.getUuid()).build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_VOTE);
        scampiMessage.putString(UUID_FIELD, vote.getUuid());
        scampiMessage.putString(POST_UUID_FIELD, vote.getPostUuid());
        scampiMessage.putString(CREATOR_FIELD, vote.getCreatorId());
        scampiMessage.putInteger(CREATED_AT_FIELD, Converters.dateToTimestamp(vote.getCreatedAt()));
        scampiMessage.putInteger(SCORE_INFLUENCE_FIELD, vote.getScoreInfluence());
        return scampiMessage;
    }

    public Vote messageToVote(SCAMPIMessage scampiMessage) throws WrongParserException, MissingFieldsException {
        if (!messageIsVote(scampiMessage)) throw new WrongParserException();
        if (!messageHasRequiredFields(scampiMessage)) throw new MissingFieldsException();

        Vote vote = new Vote();
        vote.setUuid(scampiMessage.getString(UUID_FIELD));
        vote.setPostUuid(scampiMessage.getString(POST_UUID_FIELD));
        vote.setCreatedAt(Converters.fromTimestamp(scampiMessage.getInteger(CREATED_AT_FIELD)));
        vote.setCreatorId(scampiMessage.getString(CREATOR_FIELD));
        vote.setScoreInfluence(scampiMessage.getInteger(SCORE_INFLUENCE_FIELD));
        return vote;
    }

    private boolean voteHasRequiredFields(Vote vote) {
        if (!(vote.getUuid() == null ||
                vote.getUuid().isEmpty() ||
                vote.getPostUuid() == null ||
                vote.getPostUuid().isEmpty() ||
                vote.getCreatedAt() == null ||
                vote.getCreatorId() == null ||
                vote.getCreatorId().isEmpty() ||
                vote.getScoreInfluence() == 0)) return true;
        return false;
    }

    private boolean messageHasRequiredFields(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasString(POST_UUID_FIELD) &&
                scampiMessage.hasString(UUID_FIELD) &&
                scampiMessage.hasString(CREATOR_FIELD) &&
                scampiMessage.hasInteger(CREATED_AT_FIELD) &&
                scampiMessage.hasInteger(SCORE_INFLUENCE_FIELD)) return true;
        return false;
    }


    public static boolean messageIsVote(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasString(MESSAGE_TYPE_FIELD) && scampiMessage.getString(MESSAGE_TYPE_FIELD).equals(MESSAGE_TYPE_VOTE))
            return true;
        return false;
    }
}
