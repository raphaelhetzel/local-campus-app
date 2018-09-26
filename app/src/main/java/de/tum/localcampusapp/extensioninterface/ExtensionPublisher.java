package de.tum.localcampusapp.extensioninterface;

/**
    Interface to enable sharing of extensions over the network.

    This is done here compared to the service as the user might
    not allow the app to access local storage.
 */
public interface ExtensionPublisher {
    void enableSharing();
}
