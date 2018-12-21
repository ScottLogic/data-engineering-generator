package com.scottlogic.deg.generator.smoke_tests;

import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecFactory;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecMerger;
import com.scottlogic.deg.generator.fieldspecs.RowSpecMerger;
import com.scottlogic.deg.generator.reducer.ConstraintReducer;
import com.scottlogic.deg.generator.walker.CartesianProductDecisionTreeWalker;
import com.scottlogic.deg.generator.walker.DecisionTreeWalker;
import com.scottlogic.deg.generator.walker.DecisionTreeWalkerFactory;

public class SmokeTestsDecisionTreeWalkerFactory implements DecisionTreeWalkerFactory {

    @Override
    public DecisionTreeWalker getDecisionTreeWalker(DecisionTree tree) {
        FieldSpecMerger fieldSpecMerger = new FieldSpecMerger();
        RowSpecMerger rowSpecMerger = new RowSpecMerger(fieldSpecMerger);
        ConstraintReducer constraintReducer = new ConstraintReducer(
            new FieldSpecFactory(),
            fieldSpecMerger);

        return new CartesianProductDecisionTreeWalker(
            constraintReducer,
            rowSpecMerger);
    }
}
