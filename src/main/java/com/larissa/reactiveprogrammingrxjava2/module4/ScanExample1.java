package com.larissa.reactiveprogrammingrxjava2.module4;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.FibonacciSequence;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanExample1 {

    private final static Logger log = LoggerFactory.getLogger(ScanExample1.class);

    public static void main(String[] args) {

        // Scan works a little differently than collect.
        // Because scan emits every intermediate state to the output stream,
        // we are going to have an Observable of type long, instead of a single of type long
        Observable<Long> sumSequence = FibonacciSequence.create(12)
                .scan(
                        // Set the initial value
                        0L,

                        // The collection function. Sum the next number into the MutableReference.
                        (currentValue, nextValue) -> {
                            log.info("{} + {} = {}", currentValue, nextValue, currentValue + nextValue);
                            return currentValue + nextValue;
                        }
                );

        // Because this will be an Observable of Long, instead of a Single of Long
        // We can use our DemoSubscriber to cause the output to be generated
        sumSequence.subscribe(new DemoSubscriber<>());

        System.exit(0);
    }
}
