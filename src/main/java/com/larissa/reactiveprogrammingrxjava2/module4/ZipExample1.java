package com.larissa.reactiveprogrammingrxjava2.module4;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekLetterPair;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;

public class ZipExample1 {

    public static void main(String[] args) {

        // The zip operator allows you to combine up to 9 different input streams into a single output stream.
        Observable<GreekLetterPair> greekWithEnglishObservable = Observable
                .zip(
                        GreekAlphabet.greekAlphabetInGreekObservable(),
                        GreekAlphabet.greekAlphabetInEnglishObservable(),
                        // ...for each observable entry, we return a LetterPair "zipping" them together
                        (greekLetter, english) -> new GreekLetterPair(greekLetter, english)
                );

        greekWithEnglishObservable.subscribe(new DemoSubscriber<GreekLetterPair>());

        System.exit(0);
    }
}
