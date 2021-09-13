package com.larissa.reactiveprogrammingrxjava2.module4;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.FibonacciSequence;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;

import java.util.List;

// The buffer operation can be useful if part of what you're doing on the output stream side has some overhead involved.
// Ex: whatever comes out on the output side, you need to go do something against a database.
// There is some overhead with starting up a database transaction.
// Perhaps on the output side, instead of creating a whole transaction per processed event,
// we might want to create a single transaction, single connection and process all 3 events within it.
// In this way, we're not going to incur the transaction overhead on each different item.
public class BufferExample1 {

    public static void main(String[] args) {

        // Create a Fibonacci Observable of 20 values.
        // Because we are going to buffer the results, we will expect
        // a Observable<List<Long>> instead of Observable<Long>.
        Observable<List<Long>> bufferedFibonacciSequence = FibonacciSequence.create(20)

                // Emit items 3 at a time.
                .buffer(3);

        // Subscribe using the demo subscriber.
        bufferedFibonacciSequence.subscribe(new DemoSubscriber<>());

        System.exit(0);
    }
}
