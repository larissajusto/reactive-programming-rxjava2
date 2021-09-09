package com.larissa.reactiveprogrammingrxjava2.module2;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class UnsubscribeExample {

    private static final Logger log = LoggerFactory.getLogger(UnsubscribeExample.class);

    public static void main(String[] args) {

        // Simple counter so we can cut things of in the middle
        AtomicInteger counter = new AtomicInteger(0);

        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Create an Observable<String> that contains the 24 greek letters
        Observable.fromArray(GreekAlphabet.greekLetters)

                // Using dot-chaining syntax, subscribe to the observable we just created
                // In this case, I am creating an anonymous class based on Observer<String> so that
                // we can see all of the methods that are available.
                .subscribe(new Observer<String>() {

                    // Field used to hold the Disposable instance so we can unsubscribe from
                    // the stream of greek letters
                    private Disposable disposable;

                    // This time we want to capture the "Disposable" so we can illustrate its use.
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        this.disposable = disposable;

                        log.info("onSubscribe");
                    }

                    @Override
                    public void onNext(String nextLetter) {
                        log.info("onNext - {}", nextLetter );

                        // Once we have reached 5 events, we want to unsubscribe
                        if( counter.incrementAndGet() >= 5) {

                            // We hit how many messages we want, so we call dispose on our subscription.
                            // This will make the Observable stop sending us onNext events
                            disposable.dispose();

                            // We open the "eventMaxReached" gate so that it will allow the main thread to terminate
                            gate.openGate("eventMaxReached");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // send error message to the log
                        log.error("onError - {}", e.getMessage());

                        // Open the gate for "onError" so that the main thread will be allowed to continue
                        gate.openGate("onError");
                    }

                    @Override
                    public void onComplete() {
                        log.info("onComplete");

                        gate.openGate("onComplete");
                    }
                });

        // Wait for one of these gates to open: "eventMaxReached", "onComplete", or "onError"
        gate.waitForAny("eventMaxReached", "onComplete", "onError");

        // Emit the current state of each gate when we get finished, so we can see that
        // when we terminate the subscription, it terminates. Everything stops.

        // Is the "eventMaxReached" gate open?
        log.info("eventMaxReached gate status: {}", gate.isGateOpen("eventMaxReached"));

        // Did "onComplete" get called?
        log.info("onComplete    gate status: {}", gate.isGateOpen("onComplete"));

        // Did "onError" get called?
        log.info("onError    gate status: {}", gate.isGateOpen("onError"));

        System.exit(0);
    }
}
