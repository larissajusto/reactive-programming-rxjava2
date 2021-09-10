package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ColdVsHot_ColdExample {

    private static final Logger log = LoggerFactory.getLogger(ColdVsHot_ColdExample.class);

    public static void main(String[] args) {

        // Let's keep this thread from exiting until all of our test code has executed.
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Create a "cold" observable that emits the Greek alphabet using English words.
        // We want the stream to keep repeating until we unsubscribe, but not more than
        // 49 events since we don't want to overflow our output window for illustration purposes.
        Observable<String> coldGreekAlphabet = GreekAlphabet.greekAlphabetInEnglishObservable()
                .repeat()
                .take(49)
                .subscribeOn(Schedulers.newThread());

        // Sleep for 2 seconds to give the Observable time to run if it's going to...
        // but it's not since it's a cold observable
        ThreadHelper.sleep(2, TimeUnit.SECONDS);

        // Setup a subscriber
        log.info("Subscribing...");
        DemoSubscriber<String> subscriber = new DemoSubscriber<>(gate);

        // Subscribe to the stream of greek letters
        coldGreekAlphabet.subscribe(subscriber);

        // Let it run for 2 seconds, or until it's taken 49 items and opens the onComplete gate.
        log.info("Wait for subscriber to signal that it has finished.");
        gate.waitForAny("onComplete", "onError");

        System.exit(0);
    }
}
