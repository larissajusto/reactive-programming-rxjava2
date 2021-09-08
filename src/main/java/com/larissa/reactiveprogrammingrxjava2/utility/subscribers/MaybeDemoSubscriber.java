package com.larissa.reactiveprogrammingrxjava2.utility.subscribers;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ---------------------------------------------------------------------------------------------------------------
// MaybeObserver has a slightly different event structure
// Maybe supports either 0 events and an onComplete call
// Or one item and an onSuccess call
// ---------------------------------------------------------------------------------------------------------------
public class MaybeDemoSubscriber<TEvent> implements MaybeObserver<TEvent> {
    private static final Logger log = LoggerFactory.getLogger(MaybeDemoSubscriber.class);

    private final GateBasedSynchronization gate;
    private final String errorGateName;
    private final String successGateName;
    private final String completeGateName;

    public MaybeDemoSubscriber() {
        this.gate = new GateBasedSynchronization();
        this.errorGateName = "onError";
        this.successGateName = "onSuccess";
        this.completeGateName = "onComplete";

    }

    public MaybeDemoSubscriber(GateBasedSynchronization gate, String errorGateName, String successGateName, String completeGateName) {
        this.gate = gate;
        this.errorGateName = errorGateName;
        this.successGateName = successGateName;
        this.completeGateName = completeGateName;
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        log.info( "onSubscribe" );
    }

    // onSuccess will be called with an event,
    // if there is an item that needs to be emitted
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

    // onComplete will be called if there is no event to be emitted
    @Override
    public void onComplete() {
        log.info( "onComplete" );
        gate.openGate(completeGateName);
    }
}
