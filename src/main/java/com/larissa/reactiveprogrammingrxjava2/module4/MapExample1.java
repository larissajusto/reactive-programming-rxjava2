package com.larissa.reactiveprogrammingrxjava2.module4;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;

// The map operator provides an easy one-to-one transformation of incoming to outgoing events.
public class MapExample1 {

    public static void main(String[] args) {

        // Take each english representation and return a string that contains the string length of each one ...
        Observable<Integer> lengthStream =
                GreekAlphabet.greekAlphabetInEnglishObservable()
                        .map(nextRepresentation -> nextRepresentation.length() );

        lengthStream.subscribe(new DemoSubscriber<>());

        System.exit(0);
    }
}
