package com.larissa.reactiveprogrammingrxjava2.module5;

import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ConnectableObservableExample3 {

    private final static Logger log = LoggerFactory.getLogger(ConnectableObservableExample3.class);

    public static void main(String[] args) {

        // Create an Observable that emits every 100 millisecond.
        Observable<Long> intervalSequence = Observable.interval(100, TimeUnit.MILLISECONDS)

                // Process the sequence on the computation scheduler.
                .subscribeOn(Schedulers.computation())

                // Log what the interval is emitting so we can see what is going on.
                .doOnNext( nextLong -> log.info("doOnNext - {}", nextLong))

                // Call share to turn this in to a multicast observable.
                // Share is a synonym for publish().refCount();
                .share();

        // Create the 2 DemoSubscribers that we will use.
        DemoSubscriber<Long> demoSubscriber1 = new DemoSubscriber<>();
        DemoSubscriber<Long> demoSubscriber2 = new DemoSubscriber<>();

        // Have both DemoSubscribers subscribe to the interval sequence.
        intervalSequence.subscribe(demoSubscriber1);
        intervalSequence.subscribe(demoSubscriber2);

        // Allow the messages to flow for 2 seconds.
        // Notice that we never had to call connect on the ConnectableObserver
        // This is because the refCount is aware that somebody subscribed, and so it calls connect for you.
        ThreadHelper.sleep(2, TimeUnit.SECONDS);

        // Dispose of the first subscriber.
        // Notice that the events continue to  flow.
        demoSubscriber1.dispose();

        // Wait another 2 seconds
        ThreadHelper.sleep(2, TimeUnit.SECONDS);

        // Dispose of the second subscriber.
        // Notice that the events stop flowing.
        demoSubscriber2.dispose();

        // Wait for another 2 seconds and emit a message so we see that no events are flowing.
        log.info("Pausing for 2 seconds...");
        ThreadHelper.sleep(2, TimeUnit.SECONDS);
        log.info("... pause complete");

        ThreadHelper.sleep(2, TimeUnit.SECONDS);
        System.exit(0);

    }
}
