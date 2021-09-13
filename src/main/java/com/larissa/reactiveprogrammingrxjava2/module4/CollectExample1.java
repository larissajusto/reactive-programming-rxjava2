package com.larissa.reactiveprogrammingrxjava2.module4;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.GreekAlphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CollectExample1 {

    private static final Logger log = LoggerFactory.getLogger(CollectExample1.class);

    public static void main(String[] args) {

        // Collect is useful to combine a stream of events into a single event or object
        // In this case, we will make an ArrayList<String> that contains all of the greek letters.
        ArrayList<String> greekLetterArray = GreekAlphabet.greekAlphabetInGreekObservable()
                .collect(
                        // what is the initial state? In this cae a blank ArrayList
                        ArrayList<String>::new,

                        // The collection function. Put the greekLetter into the arrayList.
                        (targetArrayList, greekLetter) -> targetArrayList.add(greekLetter)
                )
                // We block and get the value out of the Single that was returned by the collect operation.
                // blockingGet assumes that you have a stream of events that's just going to have a single item in it.
                // so blockingGet will do the work of subscribing and extracting the single item for you and then returning it.
                .blockingGet();

        System.out.println(greekLetterArray);

        //Next, we will take our greekLetterArray, turn it to a stream and execute a forEach on each member
        greekLetterArray.stream().forEach(
                nextLetter -> log.info(nextLetter)
        );
    }
}
