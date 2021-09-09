package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;

public class CreationJustExample1 {

    public static void main(String[] args) {

        // "just" allows for the creation of Observables out of a single value or up to 10 other single values.
        Observable justObservable = Observable.just(545, 45, 89, 2548);

        // Output the single value
        justObservable.subscribe(new DemoSubscriber());

        System.exit(0);
    }
}
