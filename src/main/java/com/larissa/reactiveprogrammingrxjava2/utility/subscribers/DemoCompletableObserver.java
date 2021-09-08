package com.larissa.reactiveprogrammingrxjava2.utility.subscribers;

import com.larissa.reactiveprogrammingrxjava2.utility.GateBasedSynchronization;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ---------------------------------------------------------------------------------------------------------------
// A CompletableObserver will have only one of two messages:
// It either completed successfully
// Or It errored out
// ---------------------------------------------------------------------------------------------------------------
public class DemoCompletableObserver implements CompletableObserver {

    private static final Logger log = LoggerFactory.getLogger(DemoCompletableObserver.class);

    private final GateBasedSynchronization gate;
    private final String errorGateName;
    private final String completeGateName;

    public DemoCompletableObserver(GateBasedSynchronization gate, String errorGateName, String completeGateName) {
        this.gate = gate;
        this.errorGateName = errorGateName;
        this.completeGateName = completeGateName;
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        log.info( "onSubscribe" );
    }

    @Override
    public void onComplete() {
        log.info( "onComplete" );
        gate.openGate(completeGateName);
    }

    @Override
    public void onError(Throwable e) {
        log.error( "onError - {} ", e.getMessage());
        log.error(e.getMessage(),e);
        gate.openGate(errorGateName);
    }

}
