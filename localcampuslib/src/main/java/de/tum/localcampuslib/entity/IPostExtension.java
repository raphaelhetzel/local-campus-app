package de.tum.localcampuslib.entity;

import java.util.Date;

public interface IPostExtension {
    public long getId();

    public String getUuid();

    public long getPostId();

    public String getPostUuid();

    public String getCreatorId();

    public Date getCreatedAt();

    public String getData();
}
