package com.scottlogic.deg.generator.decisiontree;

import com.scottlogic.deg.generator.constraints.IConstraint;
import com.scottlogic.deg.generator.restrictions.RowSpec;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TreeConstraintNode implements ConstraintNode {
    public static final ConstraintNode empty = new TreeConstraintNode(Collections.emptySet(), Collections.emptySet());

    private final Collection<IConstraint> atomicConstraints;
    private final Collection<DecisionNode> decisions;

    public TreeConstraintNode(Collection<IConstraint> atomicConstraints, Collection<DecisionNode> decisions) {
        this.atomicConstraints = Collections.unmodifiableCollection(atomicConstraints);
        this.decisions = Collections.unmodifiableCollection(decisions);
    }

    public TreeConstraintNode(IConstraint... atomicConstraints) {
        this(
            Arrays.asList(atomicConstraints),
            Collections.emptySet());
    }

    public TreeConstraintNode(IConstraint singleAtomicConstraint) {
        decisions = Collections.unmodifiableCollection(Collections.emptySet());
        atomicConstraints = Collections.unmodifiableCollection(Arrays.asList(singleAtomicConstraint));
    }

    TreeConstraintNode(DecisionNode... decisionNodes) {
        this(
            Collections.emptyList(),
            Arrays.asList(decisionNodes));
    }

    public Collection<IConstraint> getAtomicConstraints() {
        return new HashSet<>(atomicConstraints);
    }

    public Collection<DecisionNode> getDecisions() {
        return new HashSet<>(decisions);
    }

    public Optional<RowSpec> getOrCreateRowSpec(Supplier<Optional<RowSpec>> createRowSpecFunc) {
        if (adaptedRowSpec != null)
            return adaptedRowSpec;

        adaptedRowSpec = createRowSpecFunc.get();
        return adaptedRowSpec;
    }
    private Optional<RowSpec> adaptedRowSpec = null;

    public String toString(){
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

    public ConstraintNode removeDecisions(Collection<DecisionNode> decisionsToRemove) {
        Function<DecisionNode, Boolean> shouldRemove = existingDecision -> decisionsToRemove.stream()
            .anyMatch(decisionToExclude -> decisionToExclude.equals(existingDecision));

        return new TreeConstraintNode(
          this.atomicConstraints,
          decisions
            .stream()
            .filter(existingDecision -> !shouldRemove.apply(existingDecision))
            .collect(Collectors.toList())
        );
    }

    public ConstraintNode cloneWithoutAtomicConstraint(IConstraint excludeAtomicConstraint) {
        return new TreeConstraintNode(
            this.atomicConstraints
                .stream()
                .filter(c -> !c.equals(excludeAtomicConstraint))
                .collect(Collectors.toList()),
            decisions);
    }

    public boolean atomicConstraintExists(IConstraint constraint) {
        return atomicConstraints
            .stream()
            .anyMatch(c -> c.equals(constraint));
    }

    public ConstraintNode addAtomicConstraints(Collection<IConstraint> constraints) {
        return new TreeConstraintNode(
            Stream
                .concat(
                    this.atomicConstraints.stream(),
                    constraints.stream())
                .collect(Collectors.toList()),
            this.decisions
        );
    }

    @Override
    public ConstraintNode addDecisions(Collection<DecisionNode> decisions) {
        return new TreeConstraintNode(
            atomicConstraints,
            Stream
                .concat(
                    this.decisions.stream(),
                    decisions.stream())
                .collect(Collectors.toList())
        );
    }

    @Override
    public ConstraintNode setDecisions(Collection<DecisionNode> decisions) {
        return new TreeConstraintNode(this.atomicConstraints, decisions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeConstraintNode that = (TreeConstraintNode) o;
        return Objects.equals(atomicConstraints, that.atomicConstraints) &&
            Objects.equals(decisions, that.decisions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomicConstraints, decisions);
    }
}
