package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;

import java.util.concurrent.atomic.AtomicInteger;

public class takeWhileExample1 {

    public static void main(String[] args) {

        // Get the greek alphabet and repeat it 3 times.
        Observable<String> greekAlphabet = GreekAlphabet.greekAlphabetInEnglishObservable()
                .repeat(3);

        // Create a counter so we can count the number of times we have seen the "alpha" letter
        AtomicInteger numberOfAlphas = new AtomicInteger();

        // We want to take letters until we have seen "alpha" two times.
        // On the second time, we stop caring about the strem
        greekAlphabet.takeWhile( nextLetter -> {

            // It's an alpha! Count it!
            if( nextLetter.equals("alpha") ) {

                // If this is the second alpha, then return false - meaning that we are no longer skipping
                if( numberOfAlphas.incrementAndGet() == 2) {
                    return false; //we should see only the entire Greek alphabet of the first iteration, and the last two should get skipped
                }
            }

            // Return true - yes, we are still taking
            return true;
        }).subscribe(new DemoSubscriber<>());

        System.exit(0);

    }
}
