package de.tum.localcampusapp.serializer;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.PostRepository;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.CREATED_AT_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.CREATOR_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.MESSAGE_TYPE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.MESSAGE_TYPE_VOTE;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.POST_UUID_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.SCORE_INFLUENCE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.UUID_FIELD;

public class ScampiVoteDeserializer {

    private final PostRepository postRepository;

    public ScampiVoteDeserializer(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public Vote messageToVote(SCAMPIMessage scampiMessage) throws WrongParserException, MissingFieldsException, DatabaseException, MissingRelatedDataException {
        if (!messageIsVote(scampiMessage)) throw new WrongParserException();
        if (!messageHasRequiredFields(scampiMessage)) throw new MissingFieldsException();
        Post related_post = postRepository.getFinalPostByUUID(scampiMessage.getString(POST_UUID_FIELD));
        if (related_post == null) throw new MissingRelatedDataException();

        Vote vote = new Vote();
        vote.setUuid(scampiMessage.getString(UUID_FIELD));
        vote.setPostId(related_post.getId());
        vote.setCreatedAt(Converters.fromTimestamp(scampiMessage.getInteger(CREATED_AT_FIELD)));
        vote.setCreatorId(scampiMessage.getString(CREATOR_FIELD));
        vote.setScoreInfluence(scampiMessage.getInteger(SCORE_INFLUENCE_FIELD));
        return vote;
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
