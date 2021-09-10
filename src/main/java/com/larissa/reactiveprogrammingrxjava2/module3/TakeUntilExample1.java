package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public class TakeUntilExample1 {

    public static void main(String[] args) {
        // Get the usual Greek alphabet and repeat it FOREVER!
        Observable<String> greekAlphabet = GreekAlphabet.greekAlphabetInEnglishObservable()
                .repeat();

        // We want to take for 2 seconds
        greekAlphabet
                // this will cause our subscriber to get 2 seconds' worth of output and then stop.
                .takeUntil(Observable.interval(2,10, TimeUnit.SECONDS))
                .subscribe(new DemoSubscriber<>());

        System.exit(0);
    }
}
