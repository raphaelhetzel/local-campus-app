package de.tum.localcampusapp.Activities;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.entity.ExtensionInfo;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostSpinnerViewModel {

    //ViewModel for getting the needed extensions data
    //which will be shown in the spinner for creating a extensions post

    private ArrayList<String> extensionsDescription;
    private List<ExtensionInfo> extensions;
    private int position = 0;

    public PostSpinnerViewModel() {
        extensions = RepositoryLocator.getExtensionRepository().getExtensions();
        extensionsDescription = new ArrayList<>();
        for (ExtensionInfo e : extensions) {
            extensionsDescription.add(e.getDescription());
        }
    }

    public ArrayList<String> getExtensionDescriptions() {
        return extensionsDescription;
    }

    public String getUIID() {
        return extensions.get(position).getExtensionUUID();
    }

    public void setChosenPosition(int pos) {
        this.position = pos;
    }

}
