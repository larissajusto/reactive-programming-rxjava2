package com.larissa.reactiveprogrammingrxjava2.utility.subjects;

import io.reactivex.Observable;
import io.reactivex.Observer;

import java.util.HashMap;

public class SubjectManager {

    // A named subject is just a simple class that wraps a subject of any type
    // But forces you to provide a name for that subject.
    // This HashMap will be used to map subject names, to the actual named subjects.
    private HashMap<String, NamedSubject> subjectMap;

    public SubjectManager() { this.subjectMap = new HashMap<>(); }

    public void registerSubject( NamedSubject namedSubject ) {
        subjectMap.put(namedSubject.getSubjectName(), namedSubject);
    }

    public void deRegisterSubject( NamedSubject namedSubject ) {
        subjectMap.remove(namedSubject.getSubjectName());
    }

    public void addEventProducer(String subjectName, Observable observable) {

        if( subjectMap.containsKey(subjectName)) {
            subjectMap.get(subjectName).addEventProducer(observable);
        }
    }

    public void addEventConsumer(String subjectName, Observer observer) {

        if( subjectMap.containsKey(subjectName)) {
            subjectMap.get(subjectName).addEventConsumer(observer);
        }
    }
}
