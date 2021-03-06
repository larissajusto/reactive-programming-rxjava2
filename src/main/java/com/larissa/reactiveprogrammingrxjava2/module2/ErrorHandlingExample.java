package com.larissa.reactiveprogrammingrxjava2.module2;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekLetterPair;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

// ---------------------------------------------------------------------------------------------------------------
// Error Handling example with onErrorResumeNext
// 1.  Any of the onError operator in RxJava will allow you to emit one or more results, but then it will
// terminate the stream. Other onError operators: onErrorReturn, onErrorReturnItem
// 2. If you want to suppress errors and then just continue with on with your stream,
// then you would need a normal try-catch construct
// ---------------------------------------------------------------------------------------------------------------
public class ErrorHandlingExample {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingExample.class);

    public static void main(String[] args) {
        //Let's keep this thread from exiting until all of our test code has executed
        GateBasedSynchronization gate = new GateBasedSynchronization();

        AtomicInteger counter = new AtomicInteger();

        // Create an Observable and store it to a local variable.
        // This will zip together two streams of the same length into a single
        // stream of a composite object (GreekLetterPair)
        Observable<GreekLetterPair> zipTogether = Observable.zip(
                GreekAlphabet.greekAlphabetInGreekObservable(),
                GreekAlphabet.greekAlphabetInEnglishObservable(),
                ( greekLetter, englishLetter) -> {

                    // Cause an exception on the 5th event
                    if( counter.incrementAndGet() >= 5) {
                        throw new IllegalStateException("BOOM!");
                    }

                    return new GreekLetterPair(greekLetter, englishLetter);
                }
        );

        zipTogether
                // onErrorResumeNext
                // The placement of onError operators matter. It must be downstream of the Exceptions they are
                // meant to guard against.
                .onErrorResumeNext(Observable.just(new GreekLetterPair("????????????", "BOOM")))
                .subscribe(new Observer<GreekLetterPair>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        log.info("onSubscribe");
                    }

                    @Override
                    public void onNext(GreekLetterPair nextPair) {
                        log.info("onNext - ({},{})", nextPair.getGreekLetter(), nextPair.getEnglishLetter());
                    }

                    @Override
                    public void onError(Throwable e) {
                        log.info("onError - {}", e.getMessage());
                        log.error(e.getMessage(), e);
                        gate.openGate("onError");
                    }

                    @Override
                    public void onComplete() {
                        log.info("onComplete");
                        gate.openGate("onComplete");
                    }
                });

        // Wait for either onComplete or onError call
        gate.waitForAny("onComplete", "onError");

        System.exit(0);
    }
}
