package com.larissa.reactiveprogrammingrxjava2.nitrite;

import org.dizitart.no2.Nitrite;

public interface NitriteUnitOfWork {

    void apply(Nitrite database) throws Exception;
}
