package com.scottlogic.deg.generator.constraints;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.inputs.visitor.IConstraintValidatorVisitor;
import com.scottlogic.deg.generator.inputs.visitor.ValidationAlert;

import java.time.LocalDateTime;
import java.util.*;

public class IsBeforeOrEqualToConstantDateTimeConstraint implements IConstraint {
    public final Field field;
    public final LocalDateTime referenceValue;

    public IsBeforeOrEqualToConstantDateTimeConstraint(Field field, LocalDateTime referenceValue) {
        this.field = field;
        this.referenceValue = referenceValue;
    }

    @Override
    public String toDotLabel() {
        return String.format("%s <= %s", field.name, referenceValue);
    }

    @Override
    public Collection<Field> getFields() {
        return Collections.singletonList(field);
    }

    @Override
    public List<ValidationAlert> accept(IConstraintValidatorVisitor visitor) {
        return new ArrayList<>();
    }


    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IsBeforeOrEqualToConstantDateTimeConstraint constraint = (IsBeforeOrEqualToConstantDateTimeConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(referenceValue, constraint.referenceValue);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, referenceValue);
    }

    @Override
    public String toString() {
        return String.format("`%s` <= %s", field.name, referenceValue);
    }
}