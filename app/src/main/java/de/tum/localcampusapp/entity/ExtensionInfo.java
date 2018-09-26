package de.tum.localcampusapp.entity;

import java.io.File;
import java.util.Objects;

/**
    Simple Object to Store Information about an Extension (e.g. to show a list of extensions
    or publish a list of Extensions). See the Extension Repository documentation for a Discussion on
    why there is no Extension Entity.
 */
public class ExtensionInfo {
    private String extensionUUID;
    private String description;
    private File extensionFile;

    public ExtensionInfo(String extensionUUID, String description, File extensionFile) {
        this.description = description;
        this.extensionUUID = extensionUUID;
        this.extensionFile = extensionFile;
    }

    public String getDescription() {
        return description;
    }

    public String getExtensionUUID() {
        return extensionUUID;
    }

    public File getExtensionFile() {
        return extensionFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtensionInfo that = (ExtensionInfo) o;
        return Objects.equals(getExtensionUUID(), that.getExtensionUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExtensionUUID());
    }
}
