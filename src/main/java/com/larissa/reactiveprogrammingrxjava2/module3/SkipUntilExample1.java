package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SkipUntilExample1 {

    private static final Logger log = LoggerFactory.getLogger(SkipUntilExample1.class);

    public static void main(String[] args) {

        // Get the usual Greek Alphabet and repeat it FOREVER!
        Observable<String> greekAlphabet = GreekAlphabet.greekAlphabetInEnglishObservable()
                .repeat();

        // Remember that for skipUntil and takeUntil, the triggering event is another Observable stream
        // So in this example, we are using interval with an initial delay of 2 seconds, followed by 10 seconds between emitions
        // This will cause our stream to skip until 2 seconds has passed.
        greekAlphabet.skipUntil( Observable.interval(2,10, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.newThread())
                .subscribe(new DemoSubscriber<>());

        // Wait for 3 seconds before terminating the process.
        ThreadHelper.sleep(3, TimeUnit.SECONDS);

        System.exit(0);
    }
}
