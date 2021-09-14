package com.larissa.reactiveprogrammingrxjava2.module5;

import com.larissa.reactiveprogrammingrxjava2.utility.MutableReference;
import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.events.AccountCredentialsUpdatedEvent;
import com.larissa.reactiveprogrammingrxjava2.utility.events.EventBase;
import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

// A dummy class for simulating a user service generating events
public class UserServiceEventObservable {

    private static final String[] emailList = new String[] {
            "test@test.com",
            "harold@pottery.com",
            "lazerus@moonslider.com",
            "mrwaterwings@deepwater.com"
    };

    // returns an Observable of type EventBase, which is a base class that is used to provide
    // those events with a UUID that is unique.
    // Because a subject is of only one type, we need to make sure we have a base class that
    // we derive all of our event classes from. This will allow our subject to work with any
    // type of event that is published.
    public static Observable<EventBase> userServiceEventGenerator() {

        // Create an Observable using the generate operator
        return Observable.generate(
                () -> new MutableReference<Integer>(0),
                (offset, eventBaseEmitter) -> {

                    // Restrict the offset to the size of our email list.
                    if( offset.getValue() >= emailList.length ) {

                        // If we are at the end of the list, then send
                        // the onComplete.
                        eventBaseEmitter.onComplete();
                    }
                    else {
                        // We are still in the list...send an update event with
                        // the correct email address.
                        // The AccountCredentialsUpdatedEvent is derived off of our base class, EventBase.
                        // The constructor for AccountCredentialsUpdatedEvent takes in an email address for
                        // what account was updated.
                        eventBaseEmitter.onNext(new AccountCredentialsUpdatedEvent(emailList[offset.getValue()]));
                    }

                    // Increment out array offset
                    offset.setValue(offset.getValue() + 1);

                    // Slow things down a little
                    ThreadHelper.sleep(1, TimeUnit.SECONDS);
                }
        );


    }
}
