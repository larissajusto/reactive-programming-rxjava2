package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.FibonacciSequence;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class CreationFromExample1 {

    private static final Logger log = LoggerFactory.getLogger(CreationFromExample1.class);

    public static void main(String[] args) {

        Long[] firstFiveFibonacciNumbers = FibonacciSequence.toArray(5);

        //Example of "fromArray" using an array of 5 integers
        log.info("fromArray");

        Observable<Long> targetObservable = Observable.fromArray(firstFiveFibonacciNumbers);
        targetObservable.subscribe(new DemoSubscriber<>());

        log.info("");
        log.info("---------------------------------------------------------------------------------");
        log.info("");
        log.info("fromIterable");

        // example of "fromIterable" using an Array of 7 integers
        ArrayList<Long> fibonacciArray = FibonacciSequence.toArrayList(7);
        targetObservable = Observable.fromIterable(fibonacciArray);
        targetObservable.subscribe(new DemoSubscriber<>());

        log.info("");
        log.info("---------------------------------------------------------------------------------");
        log.info("");
        log.info("fromCallable");

        // example of "fromCallable" using an array of 9 integers
        // fromCallable allows us to provide a lambda reference or a method reference in order to create an Observable
        // Note how the Observable is structured: It's an Observable of type Long array.
        // For fromCallable, the one return value from the callable, is that which will become your event type within the Observable
        Observable<Long[]> workObservable = Observable.fromCallable(() -> FibonacciSequence.toArray(9));

        // Note that the fromCallable method only returns a single value in the return Observable
        // So here in this example, in order to make the shape of the output similar to the other example above
        // we are going to run the result of that working Observable into a from Array call
        // This will take the Long array that was returned from the callable and flatten it into a real sequence of long numbers
        targetObservable = Observable.fromArray(workObservable.blockingSingle());
        targetObservable.subscribe(new DemoSubscriber<>());

        log.info("");
        log.info("---------------------------------------------------------------------------------");
        log.info("");
        log.info("fromFuture");

        // The fromFuture allows you to more closely integrate with JAva's threading library

        // example of Observable creation via "fromFuture" using an array of 6 integers
        FutureTask<Long[]> futureTask = new FutureTask<>(() -> FibonacciSequence.toArray(6));

        // Create an ExecutorService that has a single thread in the pool.
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // Execute the futureTask to generate the fibonacci sequence... but this will be on a different thread.
        executor.execute(futureTask);

        // Take in the FutureTask (Future as it's base class) and create an Observable<Integer[]> based on its result.
        // In order to observe what that future creates, we call Observable.fromFuture, giving it the futureTask
        workObservable = Observable.fromFuture(futureTask);

        // Block to pull out the data from the future, and pass into an Observable<Integer> using fromArray
        targetObservable = Observable.fromArray(workObservable.blockingSingle());
        targetObservable.subscribe(new DemoSubscriber<>());

        System.exit(0);
    }


}
