package com.larissa.reactiveprogrammingrxjava2.module5;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class AsyncSubjectExample1 {

    private final static Logger log = LoggerFactory.getLogger(AsyncSubjectExample1.class);

    public static void main(String[] args) {
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Create an AsyncSubject tat will contain the last event streamed to it before it closed.
        AsyncSubject<String> asyncFetchSubject = AsyncSubject.create();

        // Perform a long running operation that will emit an event into our AsyncSubject
        Runnable longRunningAction = () -> {

            // Sleep for 2 seconds
            ThreadHelper.sleep(2, TimeUnit.SECONDS);

            // Emit some data
            asyncFetchSubject.onNext("Hello Word 1");
            asyncFetchSubject.onNext("Hello Word 2");
            asyncFetchSubject.onNext("Hello Word 3");

            // Sleep some more
            ThreadHelper.sleep(1, TimeUnit.SECONDS);

            // Complete the stream
            asyncFetchSubject.onComplete();

            // Open the synchronization gate called "onComplete"
            gate.openGate("onComplete");
        };

        //  Create a subscriber to the AsyncSubject
        asyncFetchSubject
                .subscribeOn(Schedulers.computation())
                .subscribe(new DemoSubscriber<>());

        // Execute the long running action on the IO scheduler
        Schedulers.io().scheduleDirect(longRunningAction);

        gate.waitForAny("onComplete");

        System.exit(0);

    }
}
