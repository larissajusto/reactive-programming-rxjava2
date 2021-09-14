package com.larissa.reactiveprogrammingrxjava2.module5;

import com.larissa.reactiveprogrammingrxjava2.nitrite.NO2;
import com.larissa.reactiveprogrammingrxjava2.nitrite.NitriteTestDatabase;
import com.larissa.reactiveprogrammingrxjava2.nitrite.NitriteUnitOfWorkWithResult;
import com.larissa.reactiveprogrammingrxjava2.nitrite.datasets.NitriteGreekAlphabetSchema;
import com.larissa.reactiveprogrammingrxjava2.nitrite.entity.LetterPair;
import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

// ---------------------------------------------------------------------------------------------------------------
// For the using example, we needed to come up with some kind of resource that we could use, that would be:
// 1.  Small and to the point.
// Decided to use a Nitrite document database.
// Nitrite is an embedded document database for Java that's very easy to use and extremely easy to set up.
// A few wrapper classes around Nitrite were written, so that we could apply a schema and populate data in an easy way
// ---------------------------------------------------------------------------------------------------------------
public class UsingExample1 {

    private static final Logger log = LoggerFactory.getLogger(UsingExample1.class);

    public static void main(String[] args) {
        GateBasedSynchronization gate = new GateBasedSynchronization();

        // Set up an Observable that needs to manage the lifetime of a resource.
        // In this case, we will use a Nitrite document database.
        Observable.using(

                // The first parameter takes the resource creation function.
                UsingExample1::resourceCreator,

                // Next we provide a function that will makeObservable the Observable
                // that will watch the events on this Observable.
                UsingExample1::observableCreator,

                // Finally, "using" takes a function that is called to dispose of the resource.
                // In this case, that's our Nitrite database.
                UsingExample1::resourceDisposer)

                // * CAN ADD HERE ANY OPERATOR THAT YOU WANT
                // TO DO WHATEVER WORK NEED TO BE DONE AGAINST THAT DATABASE RESULT

                // Subscribe using the DemoSubscriber.
                .subscribe(new DemoSubscriber<>(gate));

            // Wait for things to finish
            gate.waitForAny("onComplete", "onError");

            System.exit(0);
    }

    private static NitriteTestDatabase resourceCreator() {
        try {
            log.info("resourceCreator - opening database" );

            // Create a Nitrite database and initialize it with a collection that contains LetterPairs
            // the Greek alphabet with English word representations.
            // This NitriteGreekAlphabet schema does the work of setting up the collection
            // the resource we're going to create is a NitriteTestDatabase
            return new NitriteTestDatabase(Optional.of(new NitriteGreekAlphabetSchema()));
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    private static ObservableSource<LetterPair> observableCreator(NitriteTestDatabase inDatabase) {

        log.info("observableCreator - opening Observable");

        // Create an observable that pulls the Greek alphabet from the Nitrite database.
        return NO2.execute(
                // NO2 is a helper class I created to make some of the details about
                // working with Nitrite opaque for purposes of our examples.

                // NO2.execute's first parameter is the actual Nitrite instance.
                inDatabase.getNitriteDatabase(),

                // The next parameter is a NitriteUnitOfWorkWithResult that takes the
                // Nitrite database instance and passed it to a lambda expression that generates our Observable.
                // The end result of this lambda expression is an Observable of type LetterPair that
                // The LetterPairs are going to come out of the LetterPair collection in our Nitrite database.
                (NitriteUnitOfWorkWithResult<ObservableSource<LetterPair>>) database ->

                        // This creates the Observable from an Iterable, which happens to be
                        // a Nitrite result set cursor.
                        Observable.fromIterable(

                                // Get the LetterPair repository
                                database.getRepository(LetterPair.class)

                                        // Call "find" with no query to get all of the
                                        // LetterPair documents in the collection.
                                        .find()));
    }

    private static void resourceDisposer(NitriteTestDatabase database) {

        log.info("resourceDisposer - closing database");

        try{
            // close the nitrite database
            database.close();
        } catch (IOException e) {

        }
    }
}
