package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ColdVsHot_HotExample {

    private static final Logger log = LoggerFactory.getLogger(ColdVsHot_HotExample.class);

    public static void main(String[] args) {

        // Let's keep this thread from exiting until all of our test code has executed.
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Create a "hot" observable that emits greek letters at a furious pace.
        // We only take the first 49 events to keep things understandable.
        // GreekAlphabet.greekAlphabetInEnglishHotObservable is a special method created that will construct a hot
        // Observable that is sending out Greek letters whether anybody is listening or not
        Observable<String> hotGreekAlphabet = GreekAlphabet.greekAlphabetInEnglishHotObservable(true)
                .take(49);

        // sleep for 2 seconds to give the hot observable a chance to run
        ThreadHelper.sleep(2, TimeUnit.SECONDS);

        // Set up a demo subscriber
        DemoSubscriber<String> subscriber = new DemoSubscriber<>(gate);

        // Subscribe to the hot stream of greek letters
        log.info("Subscribing now...");
        hotGreekAlphabet.subscribe(subscriber);

        // Wait for 2 seconds, or until one of the gates is opened.
        log.info("Wait for subscriber to signal that it is finished.");
        gate.waitForAny("onComplete", "onError");

        System.exit(0);

    }
}