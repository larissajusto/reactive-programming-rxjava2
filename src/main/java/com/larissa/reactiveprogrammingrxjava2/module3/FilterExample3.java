package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterExample3 {

    private static final Logger log = LoggerFactory.getLogger(FilterExample3.class);

    public static void main(String[] args) {

        // Get the usual Greek alphabet and repeat it 3 times.
        Observable<String> greekAlphabet = GreekAlphabet.greekAlphabetInEnglishObservable()
                        .repeat(3);

        // We want only distinct values. This will make it emit only one time.
        // the distinct will block the second and third emition
        greekAlphabet.distinct().subscribe(new DemoSubscriber<>());

        System.exit(0);
    }
}
