package com.larissa.reactiveprogrammingrxjava2.module5;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BehaviorSubjectExample1 {

    private final static Logger log = LoggerFactory.getLogger(BehaviorSubjectExample1.class);

    public static void main(String[] args) {

        // Create our test BehaviorSubject with an initial state of "omega".
        BehaviorSubject<String> behaviorSubject = BehaviorSubject.createDefault("omega");

        // Whenever you subscribe to a BehaviorSubject, you will always see the last event
        // that the BehaviorSubject called onNext for.

        // Subscribe and notice that we will have an initial state -
        // the last event that was emitted into the BehaviorSubject.
        Disposable subscription1 = behaviorSubject.subscribe(
                // what we should see, because we've sent no other events, is omega.
                nextLetter -> log.info("onNext - {}", nextLetter));

        subscription1.dispose();

        // Use our BehaviorSubject to emit a few more letters
        behaviorSubject.onNext("alpha");
        behaviorSubject.onNext("beta");
        behaviorSubject.onNext("gamma");

        // Subscribe again with a different Observable, and see what letter we get
        // we should get gamma, because that is the last onNext call that was called on our subject.
        Disposable subscription2 = behaviorSubject.subscribe(
                nextLetter -> log.info("onNext - {}", nextLetter));

        subscription2.dispose();

        System.exit(0);
    }
}
