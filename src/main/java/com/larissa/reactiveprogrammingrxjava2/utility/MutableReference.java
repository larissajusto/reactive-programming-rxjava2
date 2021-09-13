package com.larissa.reactiveprogrammingrxjava2.utility;

import java.util.Optional;

// class used to accumulate the sum as we go through the list of Fibonacci numbers.
public class MutableReference<TContainedType> {

    // MutableReference simply has a value that is an optional of whatever type you request
    private Optional<TContainedType> value;

    // the default constructor creates an empty optional
    public MutableReference() {
        value = Optional.empty();
    }

    // Or we can construct it with a specific value, in which case, we'll get an Optional of that particular value, including nullable.
    public MutableReference(TContainedType containedValue) {
        this.value = Optional.ofNullable(containedValue);
    }

    // determining whether or not there is a value present
    public boolean hasValue() {
        return value.isPresent();
    }

    // get the value
    public TContainedType getValue() {
        return value.get();
    }

    public TContainedType getValue( TContainedType defaultValue ) {
        return value.orElse(defaultValue);
    }

    // set the value
    public void setValue(TContainedType newValue) {
        value = Optional.ofNullable(newValue);
    }
}
