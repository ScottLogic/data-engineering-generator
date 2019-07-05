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

package com.scottlogic.deg.generator.decisiontree;

import com.scottlogic.deg.common.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface ConstraintNode extends Node {
    Collection<AtomicConstraint> getAtomicConstraints();
    Collection<DecisionNode> getChildren();
    Optional<RowSpec> getOrCreateRowSpec(Supplier<Optional<RowSpec>> createRowSpecFunc);
    ConstraintNode removeChildren(Collection<DecisionNode> decisionsToRemove);
    ConstraintNode cloneWithoutAtomicConstraint(AtomicConstraint excludeAtomicConstraint);
    boolean atomicConstraintExists(AtomicConstraint constraint);
    ConstraintNode addAtomicConstraints(Collection<AtomicConstraint> constraints);
    ConstraintNode addChildren(Collection<DecisionNode> decisions);
    ConstraintNode setChildren(Collection<DecisionNode> decisions);
    ConstraintNode markNode(NodeMarking marking);
    ConstraintNode accept(NodeVisitor visitor);

    static ConstraintNode merge(Iterator<ConstraintNode> constraintNodeIterator) {
        Collection<AtomicConstraint> atomicConstraints = new ArrayList<>();
        Collection<DecisionNode> decisions = new ArrayList<>();
        Set<NodeMarking> markings = new HashSet<>();

        while (constraintNodeIterator.hasNext()) {
            ConstraintNode constraintNode = constraintNodeIterator.next();

            atomicConstraints.addAll(constraintNode.getAtomicConstraints());
            decisions.addAll(constraintNode.getChildren());
            markings.addAll(constraintNode.getNodeMarkings());
        }

        return new TreeConstraintNode(atomicConstraints, decisions, markings);
    }

    default Set<NodeMarking> getNodeMarkings(){
        return Arrays.stream(NodeMarking.values())
            .filter(this::hasMarking)
            .collect(Collectors.toSet());
    }
}

