package com.larissa.reactiveprogrammingrxjava2.module4;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.FibonacciSequence;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscribeOnObserveOnExample {

    private static final Logger log = LoggerFactory.getLogger(SubscribeOnObserveOnExample.class);

    public static void main(String[] args) {
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Our base Observable for this example will be a Fibonacci sequence of 10 numbers
        // we will use it many times with different subscribeOn and ObserveOn combinations.
        Observable<Long> fibonacciObservable = FibonacciSequence.create(10)
                //use doOnSubscribe to log a message anytime someone subscribes
                // a part of that log message will be what thread is being subscribed on
                .doOnSubscribe(disposable -> {
                    log.info("fibonacciObservable::onSubscribe");
                });

        // ---------------------------------------------------------------------------------------

        // First, let's look at a subscription with no threading modification.
        // We will see that both the subscription and all the observations are done on the main thread
        fibonacciObservable.subscribe((new DemoSubscriber<>(gate)));

        // No threading, but do our subscription with no threading modification
        gate.waitForAny("onError", "onComplete");
        log.info("-----------------------------------------------------------");

        // ---------------------------------------------------------------------------------------
        // Scan the numbers on the computation thread pool
        // ---------------------------------------------------------------------------------------

        gate.resetAll();

        // SubscribeOn example illustrating how first SubscribeOn wins.
        fibonacciObservable
                .subscribeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io()) // This will be ignored. subscribeOn is always first come, first served.
                .subscribe(new DemoSubscriber<>(gate));

        // No threading, but do our subscription with no threading modification
        // we can see that subscribeOn is used as the default observeOn scheduler if we don't specify any other.
        // Wherever we subscribe, that will also be the scheduler that we observe, unless we request a different scheduler to be used.
        gate.waitForAny("onError", "onComplete");
        log.info("-----------------------------------------------------------");

        // ---------------------------------------------------------------------------------------
        // Illustrate how observeOn's position effects which scheduler is used.
        // ---------------------------------------------------------------------------------------

        gate.resetAll();

        // Illustrate how observeOn's position alters the scheduler that is
        // used for the observation portion of the code.
        fibonacciObservable
                // First observeOn... will be altered by the
                // observeOn further downstream
                .observeOn(Schedulers.computation())

                // The location of the subscribeOn doesn't matter.
                // First subscribeOn always wins.
                .subscribeOn(Schedulers.newThread())

                // The last observeOn takes precedence.
                .observeOn(Schedulers.io())

                .subscribe(new DemoSubscriber<>(gate));

        // No threading, but do our synchronization pattern anyway
        gate.waitForAny("onError", "onComplete");
        log.info("---------------------------------------------------------");

        System.exit(0);


    }
}
