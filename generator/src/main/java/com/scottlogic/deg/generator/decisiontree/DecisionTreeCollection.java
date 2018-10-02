package com.scottlogic.deg.generator.decisiontree;

import com.scottlogic.deg.generator.ProfileFields;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DecisionTreeCollection {
    private final ProfileFields fields;
    private final Collection<DecisionTree> decisionTrees;

    DecisionTreeCollection(ProfileFields fields, Collection<DecisionTree> decisionTrees) {
        this.fields = fields;
        this.decisionTrees = decisionTrees;
    }

    public ProfileFields getFields() {
        return fields;
    }

    public Collection<DecisionTree> getDecisionTrees() {
        return decisionTrees;
    }

    public DecisionTree getMergedTree() {
        return new DecisionTree(
            ConstraintNode.merge(decisionTrees
                .stream()
                .map(DecisionTree::getRootNode)
                .iterator()),
            fields);
    }
}
