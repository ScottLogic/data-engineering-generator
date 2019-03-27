package com.scottlogic.deg.generator.builders;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.Constraint;
import com.scottlogic.deg.generator.constraints.atomic.*;
import com.scottlogic.deg.generator.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.ConditionalConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.OrConstraint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Defines a builder for a class that can contain constraints.
 */
public abstract class ConstraintChainBuilder<T> extends BaseConstraintBuilder<T> {
    private final Constraint headConstraint;
    protected final List<Constraint> tailConstraints;

    ConstraintChainBuilder(Constraint headConstraint, List<Constraint> tailConstraints) {
        this.headConstraint = headConstraint;
        this.tailConstraints = tailConstraints;
    }

    ConstraintChainBuilder() {
        this.tailConstraints = new ArrayList<>();
        this.headConstraint = null;
    }

    abstract ConstraintChainBuilder<T> create(Constraint headConstraint, List<Constraint> tailConstraints);

    abstract T buildInner();

    /**
     * Saves the current head constraint to the tail and builds the resulting tail of constraints.
     * @return Built object of type T
     */
    @Override
    public T build() {
        return this.save().buildInner();
    }

    /**
     * Calls the negate method on the current head constraint.
     * @return New builder with the negated head constraint and the same tail.
     */
    public ConstraintChainBuilder<T> negate() {
        return this.set(headConstraint.negate());
    }

    /**
     * Wraps the current constraint in a ViolatedAtomicConstraint.
     * @return New builder with the violated head constraint and the same tail.
     */
    public ConstraintChainBuilder<T> violate() {
        if (!(headConstraint instanceof AtomicConstraint)) {
            throw new RuntimeException("Can only mark atomic constraints as violated.");
        }

        return this.set(new ViolatedAtomicConstraint((AtomicConstraint) headConstraint));
    }

    public ConstraintChainBuilder<T> appendBuilder(ConstraintChainBuilder<? extends Constraint> builder) {
        return this.saveAndSet(builder.headConstraint, builder.tailConstraints);
    }

    public ConstraintChainBuilder<T> withLessThanConstraint(Field field, int referenceValue) {
        return saveAndSet(new IsLessThanConstantConstraint(field, referenceValue, null));
    }

    public ConstraintChainBuilder<T> withGreaterThanConstraint(Field field, int referenceValue) {
        return saveAndSet(new IsGreaterThanConstantConstraint(field, referenceValue, null));
    }

    public ConstraintChainBuilder<T> withEqualToConstraint(Field barField, Object referenceValue) {
        return saveAndSet(new IsInSetConstraint(barField, Collections.singleton(referenceValue), null));
    }

    public ConstraintChainBuilder<T> withOrConstraint(ConstraintChainBuilder<OrConstraint> orBuilder) {
        return saveAndSet(orBuilder.build());
    }

    public ConstraintChainBuilder<T> withAndConstraint(ConstraintChainBuilder<AndConstraint> andBuilder) {
        return saveAndSet(andBuilder.build());
    }

    public ConstraintChainBuilder<T> withIfConstraint(BaseConstraintBuilder<ConditionalConstraint> builder) {
        return saveAndSet(builder.build());
    }

    public ConstraintChainBuilder<T> withInSetConstraint(Field field, Object[] legalArray) {
        Set<Object> legalSet = new HashSet<>(Arrays.asList(legalArray));
        return saveAndSet(new IsInSetConstraint(field, legalSet, null ));
    }

    public ConstraintChainBuilder<T> withOfLengthConstraint(Field fooField, int length) {
        return saveAndSet(new StringHasLengthConstraint(fooField, length, null));
    }

    public ConstraintChainBuilder<T> withOfTypeConstraint(Field fooField, IsOfTypeConstraint.Types requiredType) {
        return saveAndSet(new IsOfTypeConstraint(fooField, requiredType, null));
    }

    public ConstraintChainBuilder<T> withAtomicConstraint(
        Field fooField,
        Class<? extends AtomicConstraint> atomicConstraint,
        Object value) {

        if(value == null) {
            try {
                final Constructor<?>[] constructors = atomicConstraint.getConstructors();
                AtomicConstraint constraint = (AtomicConstraint)constructors[0].newInstance(fooField, null);
                return saveAndSet(constraint);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Unable to build constraint of type " + atomicConstraint.toString() +
                    "from class alone");
            }
        }
        else {
            try {
                final Constructor<?>[] constructors = atomicConstraint.getConstructors();
                AtomicConstraint constraint = (AtomicConstraint)constructors[0].newInstance(fooField, value, null);
                return saveAndSet(constraint);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Unable to build constraint of type " + atomicConstraint.toString() +
                    "from class and sample value: " + value.toString());
            }
        }
    }

    /**
     * Saves the current head constraint to the tail and sets a new head.
     * H|T + nh -> nh|(H|T)
     * @param newHead New head constraint for the builder.
     * @return New builder with new head constraint and tail consisting of old head added to old tail.
     */
    private ConstraintChainBuilder<T> saveAndSet(Constraint newHead){
        return create(newHead, appendHead());
    }

    /**
     * Saves the current head constraint to the tail, appends a new tail to the existing tail and sets a new head.
     * H|T + nH|nT -> nH|(nT|H|T)
     * @param newHead New head constraint for the builder.
     * @param newTail New section of tail to add to the old tail and old head.
     * @return New builder with the new head constraint and tail consisting of the new tail added to the old head and
     * old tail.
     */
    private ConstraintChainBuilder<T> saveAndSet(Constraint newHead, List<Constraint> newTail){
        return create(newHead, appendTail(newTail));
    }

    /**
     * Discards the current head and sets it to be the new head.
     * H|T + nh -> nH|T
     * @param newHead New head constraint for the builder
     * @return New builder with the new head constraint and same tail.
     */
    private ConstraintChainBuilder<T> set(Constraint newHead) {
        return create(newHead, tailConstraints);
    }

    /**
     * Appends the current head to the tail
     * H|T -> ()|(H|T)
     * @return New builder with an null head constraint and tail consisting of consisting of old head added to old tail.
     */
    private ConstraintChainBuilder<T> save() {
        return saveAndSet(null);
    }

    /**
     * Appends the current head to the tail.
     * @return New tail consisting of the current head added to the old tail.
     */
    private List<Constraint> appendHead() {
        List<Constraint> newTail = new ArrayList<>(tailConstraints);
        if(headConstraint != null) {
            newTail.add(headConstraint);
        }
        return newTail;
    }

    /**
     * Appends a new tail to the current head and old tail.
     * @param tail New tail of constraints.
     * @return New tail consisting of new tail added to current head and old tail
     */
    private List<Constraint> appendTail(List<Constraint> tail) {
        List<Constraint> newTail = appendHead();
        if(tail != null) {
            newTail.addAll(tail);
        }
        return newTail;
    }
}
