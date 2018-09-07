package de.tum.localcampusapp.testhelper;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.InMemoryTopicRepository;

public class FakeDataGenerator {

    private static final int ID_MAX = 100000;

    private int fakeDataCount;
    private String elementsName;
    private InMemoryTopicRepository inMemoryTopicRepository;

    private ArrayList<Long> idList;
    private LiveData<List<Topic>> liveDataTopics;


    public FakeDataGenerator(String elementsName, int fakeDataCount){
        this.elementsName = elementsName;
        this.fakeDataCount = fakeDataCount;
        inMemoryTopicRepository = new InMemoryTopicRepository();
        idList = new ArrayList<Long>();
    }

    public FakeDataGenerator(String elementsName, int fakeDataCount, InMemoryTopicRepository inMemoryTopicRepository){
        this.elementsName = elementsName;
        this.fakeDataCount = fakeDataCount;
        this.inMemoryTopicRepository = inMemoryTopicRepository;
        idList = new ArrayList<Long>();
    }

    public long getId(){
        long num = (long) (Math.random()*ID_MAX) +1;
        while(idList.contains(num)){
            num = (long) (Math.random()*ID_MAX) +1;
        }
        idList.add(num);
        return num;
    }

    public String getNameWithId(long id){
        return elementsName + Long.toString(id);
    }

    public void insertSeveralTopics(){
        for(int i=0; i<fakeDataCount; i++){
            insertNewTopic();
        }
    }

    public void insertNewTopic() {
        long id = getId();
        try {
            Log.d("FakeDataGenerator", "insert: "+ getNameWithId(id));
            inMemoryTopicRepository.insertTopic(new Topic(id, getNameWithId(id)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public InMemoryTopicRepository getInMemoryTopicRepository() {
        return inMemoryTopicRepository;
    }

    public LiveData<List<Topic>> getLiveData() throws DatabaseException {
        return inMemoryTopicRepository.getTopics();
    }

}
