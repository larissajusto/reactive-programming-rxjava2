package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

//Example showing that if we get some component being slow, we can fallback to a cached version or something else.
public class TimeoutExample2 {

    private final static Logger log = LoggerFactory.getLogger(TimeoutExample2.class);

    public static void main(String[] args) {
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Sleep for 1 day, and after that take every letter of the Greek alphabet and do an onNext()
        Observable<Object> greekAlphabetWithBigDelay = Observable.create(emitter -> {
            ThreadHelper.sleep(1, TimeUnit.DAYS);           // Wait 1 day

            Arrays.stream(GreekAlphabet.greekLettersInEnglish)
                            .forEach(nextLetter -> emitter.onNext(nextLetter));
            emitter.onComplete();
        })
                // timeout emits the "timeout" onError on the computation thread pool
                // so we make the entire subscription happen on the same thread pool.
                // This is because the main thread may be the thread that is hung and causes the timeout.
                .subscribeOn(Schedulers.computation())
                // if in 2 seconds we don't receive any messages, we're going to fall back to the actual
                // Greek alphabet with actual Greek letters.
                .timeout(2, TimeUnit.SECONDS
                    , GreekAlphabet.greekAlphabetInGreekObservable());

        greekAlphabetWithBigDelay.subscribe(new DemoSubscriber<>(gate));

        gate.waitForAny("onComplete","onError");

        System.exit(0);
    }
}
