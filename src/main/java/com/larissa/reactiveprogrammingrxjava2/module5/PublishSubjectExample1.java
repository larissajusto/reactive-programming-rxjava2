package com.larissa.reactiveprogrammingrxjava2.module5;

import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.FibonacciSequence;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subjects.SelectableSubject;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

// Looking at PublishSubject, and some of the ways you can use it to implement event-driven programming.
public class PublishSubjectExample1 {

    private final static Logger log = LoggerFactory.getLogger(PublishSubjectExample1.class);

    public static void main(String[] args) {

        // Create a SelectableSubject<String> using a PublishSubject

        // SelectableSubject is a helper class created
        // When you construct a selectable subject, you must pass it a subject implementation
        // In this way, it gives you the option to either create a PublishSubject or a BehaviorSubject
        // Or whichever kind best suits your needs.
        SelectableSubject subject = new SelectableSubject(PublishSubject.create());

        // At least one event consumer needs to be present, else the producers will detect that
        // no one is listening and dispose themselves.
        subject.addEventConsumer(new DemoSubscriber());
        subject.addEventConsumer(new DemoSubscriber());

        // Create an Observable that emits the English form of the GreekAlphabet
        subject.addEventProducer(
                // The base Observable will be the English version of the Greek Alphabet.
                GreekAlphabet.greekAlphabetInEnglishObservable()
                        .subscribeOn(Schedulers.computation())
        );

        // Then we hook up another producer, this time with a FibonacciSequence that is going to repeat forever
        subject.addEventProducer(
                // The base Observable will be the Fibonacci Numbers in String form.
                FibonacciSequence.create(20)
                        .repeat()
                        .map( nextNumber -> Long.toString(nextNumber))
                        .subscribeOn(Schedulers.computation())
        );

        ThreadHelper.sleep(10, TimeUnit.SECONDS);

        System.exit(0);
    }
}
