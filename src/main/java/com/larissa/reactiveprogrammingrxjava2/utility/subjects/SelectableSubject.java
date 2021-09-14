package com.larissa.reactiveprogrammingrxjava2.utility.subjects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.HashMap;

public class SelectableSubject<TEventType> {

    private Subject<TEventType> internalSubject;
    // HashMaps to track the Observables on one side and the Observers on the other side of the Subject.
    private HashMap<Observable<TEventType>, Disposable> producerTrackingMap;
    private HashMap<Observer<TEventType>, Disposable> consumerTrackingMap;

    // If no subject is provided, we default to a PublishSubject.
    public SelectableSubject() {
        this.producerTrackingMap = new HashMap<>();
        this.consumerTrackingMap = new HashMap<>();
        this.internalSubject = PublishSubject.create();
    }

    public SelectableSubject(Subject<TEventType> subjectToUse) {
        this.producerTrackingMap = new HashMap<>();
        this.consumerTrackingMap = new HashMap<>();
        this.internalSubject = subjectToUse;
    }

    // This method takes in an Observable of the given event type
    public synchronized void addEventProducer(Observable<TEventType> newEventSource) {

        // then we subscribe to that new event source using the internal subject's event handlers
        // Have the internalSubject subscribe to the incoming event source.
        newEventSource.subscribe(
                internalSubject::onNext,
                internalSubject::onError,
                internalSubject::onComplete,
                // with just one exception
                // we're going to intercept the onSubscribe method, so that we can track the Disposable instance.
                disposable -> {
                    // Intercept the onSubscribe and track the associated disposable.
                    //add this producer to our tracking map
                    this.producerTrackingMap.put(newEventSource, disposable);

                    // Pass the event along to the Subject
                    //allow the onSubscribe call to be forwarded on to the internalSubject.
                    this.internalSubject.onSubscribe(disposable);
                }
        );
    }

    // we have the ability, because of the tracking array, to shut down specific input streams.
    // we pass in the Observable that we want to remove.
    public synchronized void removeEventProducer(Observable<TEventType> eventSourceToRemove) {

        // we make sure that that Observable is in the tracking array
        if( producerTrackingMap.containsKey(eventSourceToRemove)) {

            // And if it is, we remove the tracking reference and
            // call dispose to stop the flow of messages to the subject.
            producerTrackingMap.remove(eventSourceToRemove).dispose();
        }
    }

    // To add Event Consumer, we take in an Observer that is going to be our new Consumer on the consumption side of the subject.
    public synchronized void addEventConsumer(Observer<TEventType> newConsumer) {
        // This time we're going to subscribe to the subject, passing in the onNext, onError, and onComplete handlers
        // of the new consumer.
        internalSubject.subscribe(
                newConsumer::onNext,
                newConsumer::onError,
                newConsumer::onComplete,
                // As before, we're going to intercept the onSubscribe method.
                disposable -> {
                    // Now that we have our disposable by intercepting that method,
                    // we can put it in the consumer tracking map
                    consumerTrackingMap.put(newConsumer, disposable);

                    // Call onSubscribe on the new Consumer, passing it the Disposable.
                    newConsumer.onSubscribe(disposable);
                }
        );
    }

    // To detach event consumer, we pass in the Observer that we want to remove
    public synchronized void detachEventConsumer(Observer<TEventType> consumerToRemove) {

        // make sure our consumer-side tracking map contains that consumer
        if( consumerTrackingMap.containsKey(consumerToRemove)) {

            // Remove the reference from the tracking map and then
            // call dispose to stop the flow of events.
            consumerTrackingMap.remove(consumerToRemove).dispose();
        }
    }
}
