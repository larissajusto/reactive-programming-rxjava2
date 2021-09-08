package com.larissa.reactiveprogrammingrxjava2.module2;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ---------------------------------------------------------------------------------------------------------------
// This demonstration is intended to illustrate:
// 1.  Simple Observable creation using the "from" creation methods.
// 2.  Illustrate the order that Observables will emit events calls into subscribing Observers.
// ---------------------------------------------------------------------------------------------------------------
public class EventSequenceExample {

    private static Logger log = LoggerFactory.getLogger(EventSequenceExample.class);

    public static void main(String[] args) {

        // Keep this thread from exiting until all of the test code has executed
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Create an Observable<String> that contains the 24 greek letters
        Observable.fromArray(GreekAlphabet.greekLetters)
                // Using dot-chaining syntax, subscribe to the Observable
                // we just created. In this case, I am creating an anonymous class
                // based on Observer<String> so we can see all of the methods that are available
                .subscribe(new Observer<String>() {

                    // onSubscribe is called when we first subscribe to the Observable
                    // It's typically called on the same thread that the "subscribe" methos is invoked on
                    // The "Disposable" that is passed can be used to dispose the observable before it has
                    // completed in order to stop the flow of events.
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        log.info( "onSubscribe" );
                    }

                    // onNext is called for each event that is emitted by an observable.
                    // Once onError or onComplete has been called, onNext is guaranteed to never be called again
                    // on this observable.
                    @Override
                    public void onNext(String nextLetter) {
                        log.info( "onNext - {} ", nextLetter );
                    }

                    // onError is called when an Exception is thrown either in the Observable code,
                    // or from the Observer code.
                    @Override
                    public void onError(Throwable throwable) {

                        // Send the error message to the log.
                        log.error("onError - {}", throwable.getMessage());

                        // Open the gate for "onError" so that the main thread will be allowed to continue.
                        gate.openGate("onError");
                    }

                    // onComplete is called when the Observable finishes emitting all events.
                    // If onError is called, you will not see an onComplete call.
                    // Likewise, once onComplete is called, onError is guaranteed not to be called.
                    @Override
                    public void onComplete() {
                        log.info( "onComplete" );
                        gate.openGate("onComplete");
                    }
                });

        // Wait for either "onComplete" or "onError" to be called
        gate.waitForAny("onComplete", "onError");

        System.exit(0);
    }
}
