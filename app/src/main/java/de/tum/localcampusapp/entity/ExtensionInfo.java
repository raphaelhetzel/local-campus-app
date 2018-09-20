package de.tum.localcampusapp.entity;

import java.util.Objects;

import de.tum.localcampusapp.repository.ExtensionRepository;

public class ExtensionInfo {
    private String extensionUUID;
    private String description;

    public ExtensionInfo(String extensionUUID, String description) {
        this.description = description;
        this.extensionUUID = extensionUUID;
    }

    public String getDescription() {
        return description;
    }

    public String getExtensionUUID() {
        return extensionUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtensionInfo that = (ExtensionInfo) o;
        return Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getExtensionUUID(), that.getExtensionUUID());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getDescription(), getExtensionUUID());
    }
}
