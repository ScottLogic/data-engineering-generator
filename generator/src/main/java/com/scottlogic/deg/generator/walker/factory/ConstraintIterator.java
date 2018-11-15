package com.scottlogic.deg.generator.walker.factory;

import com.scottlogic.deg.generator.walker.routes.RowSpecRoute;

import java.util.Iterator;

public interface ConstraintIterator extends Iterator<RowSpecRoute> {
    void reset();
}
