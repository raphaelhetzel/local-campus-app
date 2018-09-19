package de.tum.localcampuslib.entity;

import java.util.Date;

public interface IPost {
    public long getId();

    public String getUuid();

    public String getTypeId();

    public long getTopicId();

    public String getCreator();

    public Date getCreatedAt();

    public String getData();

    public long getScore();

    public String getTopicName();
}
