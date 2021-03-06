/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.datahelix.generator.core.walker.pruner;

import com.scottlogic.datahelix.generator.common.profile.Field;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpecFactory;
import com.scottlogic.datahelix.generator.core.fieldspecs.relations.FieldSpecRelation;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.datahelix.generator.core.decisiontree.ConstraintNode;
import com.scottlogic.datahelix.generator.core.decisiontree.ConstraintNodeBuilder;
import com.scottlogic.datahelix.generator.core.decisiontree.DecisionNode;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpec;

import java.util.*;

class PrunedConstraintState {
    private final Set<AtomicConstraint> newAtomicConstraints;
    private final Set<FieldSpecRelation> newRelations;
    private final Set<DecisionNode> newDecisionNodes = new HashSet<>();
    private final Set<AtomicConstraint> pulledUpAtomicConstraints = new HashSet<>();
    private final Set<FieldSpecRelation> pulledUpRelations = new HashSet<>();

    PrunedConstraintState(ConstraintNode constraintNode){
        newAtomicConstraints = new HashSet<>(constraintNode.getAtomicConstraints());
        newRelations = new HashSet<>(constraintNode.getRelations());
    }

    void addPrunedDecision(DecisionNode prunedDecisionNode) {
        if (!onlyOneOption(prunedDecisionNode)) {
            newDecisionNodes.add(prunedDecisionNode);
            return;
        }
        
        ConstraintNode remainingConstraintNode = getOnlyRemainingOption(prunedDecisionNode);
        pulledUpAtomicConstraints.addAll(remainingConstraintNode.getAtomicConstraints());
        pulledUpRelations.addAll(remainingConstraintNode.getRelations());
        newAtomicConstraints.addAll(remainingConstraintNode.getAtomicConstraints());
        newRelations.addAll(remainingConstraintNode.getRelations());
        newDecisionNodes.addAll(remainingConstraintNode.getDecisions());
    }

    boolean hasPulledUpDecisions() {
        return !(pulledUpAtomicConstraints.isEmpty() || pulledUpRelations.isEmpty());
    }

    ConstraintNode getNewConstraintNode() {
        return new ConstraintNodeBuilder()
            .addAtomicConstraints(newAtomicConstraints)
            .addRelations(newRelations)
            .setDecisions(newDecisionNodes)
            .build();
    }

    Map<Field, FieldSpec> addPulledUpFieldsToMap(Map<Field, FieldSpec> previousFieldSpecs) {
        Map<Field, FieldSpec> mapWithPulledUpFields = new HashMap<>(previousFieldSpecs);
        for (AtomicConstraint c : pulledUpAtomicConstraints) {
            mapWithPulledUpFields.putIfAbsent(c.getField(), FieldSpecFactory.fromType(c.getField().getType()));
        }
        return mapWithPulledUpFields;
    }

    private boolean onlyOneOption(DecisionNode prunedDecisionNode) {
        return prunedDecisionNode.getOptions().size() == 1;
    }

    private ConstraintNode getOnlyRemainingOption(DecisionNode prunedDecisionNode) {
        return prunedDecisionNode.getOptions().iterator().next();
    }
}
