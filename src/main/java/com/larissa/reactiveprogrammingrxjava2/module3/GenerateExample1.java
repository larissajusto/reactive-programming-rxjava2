package com.larissa.reactiveprogrammingrxjava2.module3;

import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ---------------------------------------------------------------------------------------------------------------
// The generate operator works in a similar way to the create operation.
// 1.  In the case of the create operation, your generation function is called only one time.
// that one time your generation function is called, you're expected to create every event. (onNext, onComplete, onError)
// 2.  In the case of generate, we have to provide 2 different functions:
// The first is used to create an initial state that will be used in each subsequent call to our emitter function.
// The emitter function takes the state and, based on that state, make a decision as to whether it should
// generate an onNext, an onComplete or an onError call
// ---------------------------------------------------------------------------------------------------------------
public class GenerateExample1 {

    private static final Logger log = LoggerFactory.getLogger(GenerateExample1.class);

    public static void main(String[] args) {

        Observable<Integer> geometricSequence = makeObservable(1,3,20);
        geometricSequence.subscribe(new DemoSubscriber<>());
    }

    public static Observable<Integer> makeObservable(int start, int multiplier, int totalNumbers ) {

        return
                Observable.generate(() -> new GeometricSequenceState(start, multiplier, totalNumbers),
                        (state, emitter) -> {

                            // If we have reached the end, then emit an onComplete
                            if(state.getCount() >= state.getTotalNumbers()) {
                                emitter.onComplete();
                                return;
                            }

                            // Increment the number of values we have emitted
                            state.incrementCount();

                            // Emit the current calculated value of the Geometric Sequence.
                            emitter.onNext(state.getCurrentValue());

                            // Calculate the next value in the sequence
                            state.generateNextValue();
                        });
    }

    public static class GeometricSequenceState {

        private final int multiplier;
        private final int totalNumbers;

        private int count;
        private int currentValue;

        public GeometricSequenceState(int start, int multiplier, int totalNumbers) {
            this.multiplier = multiplier;
            this.totalNumbers = totalNumbers;

            this.count = 0;
            this.currentValue = start;
        }

        public int getTotalNumbers() {
            return totalNumbers;
        }

        public int getCount() {
            return count;
        }

        public int getCurrentValue() {
            return currentValue;
        }

        public void incrementCount() { ++this.count; }

        public void generateNextValue() { this.currentValue = this.currentValue * this.multiplier; }
    }
}
