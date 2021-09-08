package com.larissa.reactiveprogrammingrxjava2.utility.subscribers;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ---------------------------------------------------------------------------------------------------------------
// SingleObserver changes the event structure just a little bit
// Instead of having a bunch of onNext calls,
// we're going to have an onSuccess and an onError
// ---------------------------------------------------------------------------------------------------------------
public class SingleDemoSubscriber<TEvent> implements SingleObserver<TEvent> {

    private static final Logger log = LoggerFactory.getLogger(SingleDemoSubscriber.class);

    private final GateBasedSynchronization gate;
    private final String errorGateName;
    private final String successGateName;

    public SingleDemoSubscriber(GateBasedSynchronization gate, String errorGateName, String successGateName) {
        this.gate = gate;
        this.errorGateName = errorGateName;
        this.successGateName = successGateName;
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        log.info( "onSubscribe" );
    }

    // Since we only expect one and only one event, onSuccess will double as that event plus onComplete
    @Override
    public void onSuccess(TEvent tEvent) {
        log.info( "onSuccess - {} ", tEvent);
        gate.openGate(successGateName);
    }

    @Override
    public void onError(Throwable e) {
        log.error( "onError - {} ", e.getMessage());
        log.error(e.getMessage(),e);
        gate.openGate(errorGateName);
    }
}
