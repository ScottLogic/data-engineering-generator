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
import com.scottlogic.deg.common.profile.constraints.delayed.DelayedAtomicConstraint;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;

import java.util.*;
import java.util.function.Supplier;

public class ConstraintNode implements Node {
    private final Collection<AtomicConstraint> atomicConstraints;
    private final Collection<DelayedAtomicConstraint> delayedAtomicConstraints;
    private final Collection<DecisionNode> decisions;
    private final Set<NodeMarking> nodeMarkings;

    private Optional<RowSpec> adaptedRowSpec = null;

    public ConstraintNode(Collection<AtomicConstraint> atomicConstraints,
                          Collection<DelayedAtomicConstraint> delayedAtomicConstraints,
                          Collection<DecisionNode> decisions,
                          Set<NodeMarking> nodeMarkings) {
        this.atomicConstraints = Collections.unmodifiableCollection(atomicConstraints);
        this.delayedAtomicConstraints = Collections.unmodifiableCollection(delayedAtomicConstraints);
        this.decisions = Collections.unmodifiableCollection(decisions);
        this.nodeMarkings = Collections.unmodifiableSet(nodeMarkings);
    }

    public Collection<AtomicConstraint> getAtomicConstraints() {
        return new HashSet<>(atomicConstraints);
    }

    public Collection<DelayedAtomicConstraint> getDelayedAtomicConstraints() {
        return new HashSet<>(delayedAtomicConstraints);
    }

    public Collection<DecisionNode> getDecisions() {
        return decisions;
    }

    public Optional<RowSpec> getOrCreateRowSpec(Supplier<Optional<RowSpec>> createRowSpecFunc) {
        if (adaptedRowSpec != null) {
            return adaptedRowSpec;
        }

        adaptedRowSpec = createRowSpecFunc.get();
        return adaptedRowSpec;
    }

    public String toString() {
        if (decisions.isEmpty())
            return atomicConstraints.size() > 5
                ? String.format("%d constraints", atomicConstraints.size())
                : Objects.toString(atomicConstraints);

        if (atomicConstraints.isEmpty())
            return decisions.size() > 5
                ? String.format("%d decisions", decisions.size())
                : Objects.toString(decisions);

        return String.format(
            "Decision: %s, Constraints: %s",
            decisions.size() > 5
                ? String.format("%d decisions", decisions.size())
                : Objects.toString(decisions),
            atomicConstraints.size() > 5
                ? String.format("%d constraints", atomicConstraints.size())
                : Objects.toString(atomicConstraints));
    }

    public ConstraintNodeBuilder builder() {
        return new ConstraintNodeBuilder(atomicConstraints, delayedAtomicConstraints, decisions, nodeMarkings);
    }

    @Override
    public boolean hasMarking(NodeMarking detail) {
        return this.nodeMarkings.contains(detail);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintNode that = (ConstraintNode) o;

        boolean atomicConstraintsEqual = atomicConstraints.containsAll(that.atomicConstraints) &&
                that.atomicConstraints.containsAll(atomicConstraints);
        boolean delayedAtomicConstraintsEqual = delayedAtomicConstraints.containsAll(that.delayedAtomicConstraints) &&
            that.delayedAtomicConstraints.containsAll(delayedAtomicConstraints);
        boolean decisionsEqual = decisions.containsAll(that.decisions) &&
            that.decisions.containsAll(decisions);

        return atomicConstraintsEqual &&
            delayedAtomicConstraintsEqual &&
            decisionsEqual;
    }

    @Override
    public int hashCode() {
        List<AtomicConstraint> atomicConstraintsList = new ArrayList<>(atomicConstraints);
        List<DelayedAtomicConstraint> delayedAtomicConstraintsList = new ArrayList<>(delayedAtomicConstraints);
        List<DecisionNode> decisionsList = new ArrayList<>(decisions);

        return Objects.hash(atomicConstraintsList, delayedAtomicConstraintsList, decisionsList);
    }

    static ConstraintNode merge(Iterator<ConstraintNode> constraintNodeIterator) {
        Collection<AtomicConstraint> atomicConstraints = new ArrayList<>();
        Collection<DelayedAtomicConstraint> delayedAtomicConstraints = new ArrayList<>();
        Collection<DecisionNode> decisions = new ArrayList<>();
        Set<NodeMarking> markings = new HashSet<>();

        while (constraintNodeIterator.hasNext()) {
            ConstraintNode constraintNode = constraintNodeIterator.next();

            atomicConstraints.addAll(constraintNode.getAtomicConstraints());
            delayedAtomicConstraints.addAll(constraintNode.getDelayedAtomicConstraints());
            decisions.addAll(constraintNode.getDecisions());
            markings.addAll(constraintNode.nodeMarkings);
        }

        return new ConstraintNodeBuilder()
            .addAtomicConstraints(atomicConstraints)
            .addDelayedAtomicConstraints(delayedAtomicConstraints)
            .setDecisions(decisions)
            .setNodeMarkings(markings)
            .build();
    }
}
