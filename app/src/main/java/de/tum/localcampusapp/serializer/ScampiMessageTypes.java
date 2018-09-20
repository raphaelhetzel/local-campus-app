package de.tum.localcampusapp.serializer;

import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class ScampiMessageTypes {

    public static final String MESSAGE_TYPE_FIELD = "message_type";

    public static final String MESSAGE_TYPE_POST_EXTENSION = "post_extension";
    public static final String MESSAGE_TYPE_VOTE = "vote";
    public static final String MESSAGE_TYPE_POST = "post";
    public static final String MESSAGE_TYPE_EXTENSION = "extension";

    public static String messageTypeOf(SCAMPIMessage scampiMessage) {
        if (ScampiPostSerializer.messageIsPost(scampiMessage)) return MESSAGE_TYPE_POST;
        if (ScampiVoteSerializer.messageIsVote(scampiMessage)) return MESSAGE_TYPE_VOTE;
        if (ScampiPostExtensionSerializer.messageIsPostExtension(scampiMessage))
            return MESSAGE_TYPE_POST_EXTENSION;
        return "";
    }
}
