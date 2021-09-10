package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterExample2 {

    private static final Logger log = LoggerFactory.getLogger(FilterExample2.class);

    public static void main(String[] args) {

        // Create an Observable to filter, exactly the same as example 1
        Observable<String> greekAlphabet = GreekAlphabet.greekAlphabetInEnglishObservable()

                // Filter out "delta"
                .filter( nextLetter -> !nextLetter.equals("delta"));

        // the difference is how we do our subscription
        // illustrates some of the method everloads that are available when you subscribe to an Observable

        // Here, instead of implementing the observer interface, we're going to provide 3 different methods
        greekAlphabet.subscribe(
                nextLetter -> log.info("onNext - {}", nextLetter),
                throwable -> log.error(throwable.getMessage(), throwable),
                () -> log.info("onComplete")
        );

        System.exit(0);
    }
}
