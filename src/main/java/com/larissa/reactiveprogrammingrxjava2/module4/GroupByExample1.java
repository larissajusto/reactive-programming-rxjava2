package com.larissa.reactiveprogrammingrxjava2.module4;

import com.larissa.reactiveprogrammingrxjava2.utility.datasets.FibonacciSequence;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.GroupedObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupByExample1 {

    private static final Logger log = LoggerFactory.getLogger(GroupByExample1.class);

    // We first define an enumeration GroupTypeEnum
    // We need this because we will take a series of Fibonacci numbers and then group them by either odd or even.
    private enum GroupTypeEnum {
        EVEN,
        ODD
    }

    public static void main(String[] args) {

        // Make an Observable from the Fibonacci sequence and group by odd or even numbers.
        // When we use groupBy, we get back an Observable of GroupedObservable of whatever your key type is and whatever the event type is.
        Observable<GroupedObservable<GroupTypeEnum, Long>> groupedFibonacci =
                FibonacciSequence.create(20)
                        .groupBy(
                                // we need to provide a key selection function
                                nextNumber -> nextNumber % 2L == 0L ?
                                        GroupTypeEnum.EVEN :
                                        GroupTypeEnum.ODD
                        );

        // The stream comes through as two GroupedObservables of Long ( the group Key ) and Long values.
        groupedFibonacci.subscribe(
                nextGroupedObservable -> {
                    // Get the key for this grouped observable and
                    // determine a header for it.

                    // A GroupedObservable really isn't any different than a normal Observable,
                    // The only difference is this method getKey.
                    // When you receive a groupedByObservable, you can ask it for its key, and in this case
                    // we'll get back either ODD or EVEN
                    String header = nextGroupedObservable.getKey().name();

                    // Subscribe to this GroupedObservable to process the values.
                    nextGroupedObservable.subscribe(new Observer<Long>() {

                        // StringBuilder used to concatenate the values coming from the stream.
                        // we will accumulate the numbers in a comma-separated list
                        // so that we can spit out the whole list of odd or even.
                        private StringBuilder valueList = new StringBuilder();

                        @Override
                        public void onSubscribe(Disposable disposable) {
                            // we are not really interested in onSubscribe for this
                        }

                        @Override
                        public void onNext(Long nextValue) {
                            // Add a comma if the buffer is not empty.
                            if (valueList.length() > 0) {
                                valueList.append(", ");
                            }

                            // Add the next value from the Observable
                            valueList.append(nextValue);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            // we are not really interested in onError for this
                        }

                        @Override
                        public void onComplete() {
                            log.info("{} - {}", header, valueList.toString());
                        }
                    });
                });

        System.exit(0);
    }
}
