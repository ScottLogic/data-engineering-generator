package com.scottlogic.deg.generator.constraints;

import com.scottlogic.deg.generator.Field;
import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class IsEqualToConstantConstraintTest {

    @Test
    public void testConstraintIsEqual() {
        Field field1 = new Field("TestField");
        Field field2 = new Field("TestField");
        IsEqualToConstantConstraint constraint1 = new IsEqualToConstantConstraint(field1, "abc");
        IsEqualToConstantConstraint constraint2 = new IsEqualToConstantConstraint(field2, "abc");
        Assert.assertThat(constraint1, equalTo(constraint2));
    }

    @Test
    public void testConstraintIsNotEqualDueToField() {
        Field field1 = new Field("TestField");
        Field field2 = new Field("TestField2");
        IsEqualToConstantConstraint constraint1 = new IsEqualToConstantConstraint(field1, "abc");
        IsEqualToConstantConstraint constraint2 = new IsEqualToConstantConstraint(field2, "abc");
        Assert.assertNotEquals(constraint1, constraint2);
    }

    @Test
    public void testConstraintIsNotEqualDueToValue() {
        Field field1 = new Field("TestField");
        Field field2 = new Field("TestField");
        IsEqualToConstantConstraint constraint1 = new IsEqualToConstantConstraint(field1, "abc");
        IsEqualToConstantConstraint constraint2 = new IsEqualToConstantConstraint(field2, "abcd");
        Assert.assertNotEquals(constraint1, constraint2);
    }

}
