package com.larissa.reactiveprogrammingrxjava2.module4;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekLetterPair;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

// Create some executors so that we can control the names of the individual threads
// Illustrate that flatMap is very powerful, we can thread it like crazy and use all our cores,
// but need to be aware that it's also going to do things in a way that's going to put our events out of order.
public class FlatMapExample1 {

    private static final Logger log = LoggerFactory.getLogger(FlatMapExample1.class);

    public static void main(String[] args) {
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Create the first thread pool
        AtomicInteger threadPool1Counter = new AtomicInteger();
        Executor threadPool1 = Executors.newFixedThreadPool(20, runnable -> {
            Thread returnThread = new Thread(runnable, "Pool 1 Thread " + threadPool1Counter.getAndIncrement());
            return returnThread;
        });
        // Run it through Schedulers.from so that we get a Scheduler that is compatible with RxJava
        Scheduler scheduler1 = Schedulers.from(threadPool1);

        // Create a second thread pool
        AtomicInteger threadPool2Counter = new AtomicInteger();
        Executor threadPool2 = Executors.newFixedThreadPool(20, runnable -> {
            Thread returnThread = new Thread(runnable, "Pool 2 Thread " + threadPool2Counter.getAndIncrement());
            return returnThread;
        });
        // Run it through Schedulers.from so that we get a Scheduler that is compatible with RxJava
        Scheduler scheduler2 = Schedulers.from(threadPool2);


        // flatMap is used to process a single event into an Observable of zero or many events.
        // In this case, we will take a single Greek letter, find its English counterpart and pair them together.
        // But first we emit strings that represent the Greek and English strings.
        Observable<Object> greekLetterPairs = GreekAlphabet.greekAlphabetInGreekObservable()
                .flatMap((String greekLetter) -> {

                    // Find the offset into the array of this greek character.
                    int offset = GreekAlphabet.findGreekLetterOffset(greekLetter);

                    return Observable.just(
                            greekLetter,
                            GreekAlphabet.greekLettersInEnglish[offset],
                            new GreekLetterPair(greekLetter, GreekAlphabet.greekLettersInEnglish[offset])
                    )
                            .doOnSubscribe( d -> log.info("Observable onSubscribe"))
                            .doOnNext( event -> log.info("Observable onNext - {} ", event))
                            .doOnComplete( () -> log.info("Observable onComplete" ))
                            .subscribeOn(scheduler2); //gives flatMAp the license to go use a different thread pool while processing your events

                }, 3) // ensures flatMap will never use more than 3 threads out of whatever pool you've assigned to it
                .observeOn(scheduler1);

        // Now that we have our greekLetterPairs Observable all assembled, we'll attach some doOns
        // so that we can see logging information at this level, and what we'll see is flatMap in front of them

        greekLetterPairs
                .doOnSubscribe( d -> log.info("flatMap onSubscribe"))
                .doOnNext( e -> log.info("flatMap onNext - {}", e))
                .doOnComplete(() -> gate.openGate("onComplete"))
                .doOnError((throwable) -> {
                    gate.openGate("onError");
                    log.error(throwable.getMessage(), throwable);
                })
                // Subscribe with our DemoSubscriber
                .subscribe(new DemoSubscriber<>());

        gate.waitForAny("onComplete", "onError");

        System.exit(0);
    }
}
