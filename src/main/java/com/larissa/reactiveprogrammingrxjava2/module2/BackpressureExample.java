package com.larissa.reactiveprogrammingrxjava2.module2;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BackpressureExample {

    private static Logger log = LoggerFactory.getLogger(BackpressureExample.class);

    public static void main(String[] args) {

        // Synchronization helper
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Create an Observable:
        // In this case though, we're going to create a number range from 1 to 1 billion and
        // Then to make matters worse, we're going to add the repeat operator, so it will count to one billion, forever.
        Observable<Integer> rangeOfNumbers = Observable.range(1, 1_000_000_000)
                .repeat()
                // doOnNext is not the same as the onNext operator we are used to seeing.
                // It is an onNext that is called every time this Observable emits an item
                .doOnNext( nextInt -> log.info("emitting int {}", nextInt))
                .subscribeOn(Schedulers.newThread()) // tell it to subscribe on a new thread
                .observeOn(Schedulers.newThread()); // tell it to observe on a new thread


        // Create a DemoSubscriber with a slight delay of 10ms
        // This should make the rangeOfNumber's emission far outpace the subscriber.
        DemoSubscriber<Integer> demoSubscriber = new DemoSubscriber<>(
                10L, TimeUnit.MILLISECONDS, //for every message that comes to this demoSubscriber, it's going to delay 10ms
                gate, "onError", "onComplete"
        );


        // Subscribe to start the numbers flowing
        rangeOfNumbers.subscribe(demoSubscriber);

        // Wait for things to finish
        gate.waitForAny("onError", "onComplete");

        System.exit(0);
    }
}
