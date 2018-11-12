package com.scottlogic.deg.generator.walker;

import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.decisiontree.DecisionNode;
import com.scottlogic.deg.generator.walker.builder.ConstraintBuilder;
import com.scottlogic.deg.generator.walker.builder.IConstraintIterator;
import com.scottlogic.deg.generator.walker.builder.IDecisionIterator;
import com.scottlogic.deg.generator.walker.routes.RowSpecRoute;

import java.util.*;

public class EndDecisionIterator implements IDecisionIterator {
    private List<IConstraintIterator> options = new ArrayList<>();
    private int currentOption;

    public EndDecisionIterator(DecisionNode decisionNode){
        int count = 0;
        for (ConstraintNode constraintNode: decisionNode.getOptions()) {
            options.add(ConstraintBuilder.build(constraintNode, count));
            count++;
        }
    }

    @Override
    public boolean hasNext() {
        return currentOption < options.size();
    }

    @Override
    public List<RowSpecRoute> next() {
        IConstraintIterator currentOptionIterator = options.get(currentOption);
        List<RowSpecRoute> r = new ArrayList<>();
        r.add(0, currentOptionIterator.next());

        if (!currentOptionIterator.hasNext()){
            currentOption++;
        }
        return r;
    }

    @Override
    public void reset(){
        currentOption = 0;
        for (IConstraintIterator option: options) {
            option.reset();
        }
    }
}
