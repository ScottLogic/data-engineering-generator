package com.scottlogic.deg.generator.walker;

import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.reducer.ConstraintReducer;
import com.scottlogic.deg.generator.restrictions.FieldSpecFactory;
import com.scottlogic.deg.generator.restrictions.FieldSpecMerger;
import com.scottlogic.deg.generator.restrictions.RowSpecMerger;
import com.scottlogic.deg.generator.walker.routes.ExhaustiveProducer;
import com.scottlogic.deg.generator.walker.routes.RandomisedProducer;

public class RuntimeDecisionTreeWalkerFactory implements  DecisionTreeWalkerFactory {

    private final GenerationConfig config;

    public RuntimeDecisionTreeWalkerFactory(GenerationConfig config) {
        this.config = config;
    }

    @Override
    public DecisionTreeWalker getDecisionTreeWalker() {
        FieldSpecMerger fieldSpecMerger = new FieldSpecMerger();
        RowSpecMerger rowSpecMerger = new RowSpecMerger(fieldSpecMerger);
        ConstraintReducer constraintReducer = new ConstraintReducer(
            new FieldSpecFactory(),
            fieldSpecMerger);

        switch (config.getWalkerType()){
            case Routed:
                return new DecisionTreeRoutesTreeWalker(
                    constraintReducer,
                    rowSpecMerger,
                    new ExhaustiveProducer());
            case Exhaustive:
                return new ExhaustiveDecisionTreeWalker(
                    constraintReducer,
                    rowSpecMerger);
            case Random:
                return new DecisionTreeRoutesTreeWalker(
                    constraintReducer,
                    rowSpecMerger,
                    new RandomisedProducer(50000));
        }

        throw new UnsupportedOperationException(
            String.format("Walker type %s", config.getWalkerType()));
    }
}
