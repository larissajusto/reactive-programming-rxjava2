package com.larissa.reactiveprogrammingrxjava2.module2;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// ---------------------------------------------------------------------------------------------------------------
// Flowable does have a cost:
// 1.  The extra communication to control backpressure does incur some CPU overhead and
// a lot of extra bookkeeping internally for RxJava.
// ---------------------------------------------------------------------------------------------------------------
public class FlowableExample {

    private static final Logger log = LoggerFactory.getLogger(FlowableExample.class);

    public static void main(String[] args) {

        // Synchroniztion helper
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Create an ever-repeating number counter that counts from 1 to 1 billion
        Flowable<Integer> rangeOfNumbers = Flowable.range(1, 1_000_000_000)
                .repeat()
                .doOnNext( nextInt -> log.info("emitting int {}", nextInt))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread(), false, 3);

        // Create a DemoSubscriber with a slight delay of 10ms
        // This should make the rangeOfNumber's emission far outpace the subscriber.
        FlowableSubscriber<Integer> demoSubscriber = new FlowableSubscriber<Integer>() {

            private AtomicInteger counter = new AtomicInteger(0); //keeps track of how many messages we've processed
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;

                log.info( "onSubscribe" );
                subscription.request(3); //pass in how many events we want to process
            }

            @Override
            public void onNext(Integer integer) {
                log.info("onNext - {}", integer);

                // Slow things down a bit
                ThreadHelper.sleep(10L, TimeUnit.MILLISECONDS);

                // Every three events, request 3 more
                if(counter.incrementAndGet() % 3 == 0) {
                    subscription.request(3);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("onError - {}" , throwable.getMessage());
                gate.openGate("onError");
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
                gate.openGate("onComplete");
            }
        };

        // Subscribe to start the numbers flowing
        rangeOfNumbers.subscribe(demoSubscriber);

        // Let it run for 20 seconds or until something completes...or blows up.
        gate.waitForAny(20, TimeUnit.SECONDS, "onComplete", "onError");

        System.exit(0);
    }
}
