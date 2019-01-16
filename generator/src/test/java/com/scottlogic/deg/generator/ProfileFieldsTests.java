package com.scottlogic.deg.generator;

import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class ProfileFieldsTests {
    @Test
    void equals_objIsNull_returnsFalse() {
        ProfileFields fields = new ProfileFields(
            Arrays.asList(
                new Field("Test")
            )
        );

        boolean result = fields.equals(null);

        assertFalse(
            "Expected when other object is null a false value is returned but was true",
            result
        );
    }

    @Test
    void equals_objTypeIsNotProfileFields_returnsFalse() {
        ProfileFields fields = new ProfileFields(
            Arrays.asList(
                new Field("Test")
            )
        );

        boolean result = fields.equals("Test");

        assertFalse(
            "Expected when the other object is a different type a false value is returned but was true",
            result
        );
    }

    @Test
    void equals_rowSpecFieldsLengthNotEqualToOtherObjectFieldsLength_returnsFalse() {
        ProfileFields fields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Second Field")
            )
        );

        boolean result = fields.equals(
            new ProfileFields(
                Arrays.asList(
                    new Field("First Field")
                )
            )
        );

        assertFalse(
            "Expected when the fields length do not match a false value is returned but was true",
            result
        );
    }

    @Test
    void equals_rowSpecFieldsLengthEqualToOterObjectFieldsLengthButValuesDiffer_returnsFalse() {
        ProfileFields fields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Second Field")
            )
        );

        boolean result = fields.equals(
            new ProfileFields(
                Arrays.asList(
                    new Field("First Field"),
                    new Field("Third Field")
                )
            )
        );

        assertFalse(
            "Expected when the values of the fields property differs from the fields of the other object a false value is returned but was true",
            result
        );
    }

    @Test
    void equals_rowSpecFieldsAreEqualToTheFieldsOfTheOtherObject_returnsTrue() {
        ProfileFields fields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Second Field")
            )
        );

        boolean result = fields.equals(
            new ProfileFields(
                Arrays.asList(
                    new Field("First Field"),
                    new Field("Second Field")
                )
            )
        );

        assertTrue(
            "Expected when the fields of both objects are equal a true value is returned but was false",
            result
        );
    }

    @Test
    void hashCode_valuesinFieldsDifferInSize_returnsDifferentHashCodes() {
        ProfileFields firstProfileFields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Second Field")
            )
        );
        ProfileFields secondProfileFields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Second Field"),
                new Field("Third Field")
            )
        );

        int firstHashCode = firstProfileFields.hashCode();
        int secondHashCode = secondProfileFields.hashCode();

        assertNotEquals(
            "Expected that when the profile fields length differ the hash codes should not be the same but were equal",
            firstHashCode,
            secondHashCode
        );
    }

    @Test
    void hashCode_valuesInFieldsAreEqualSizeButValuesDiffer_returnsDifferentHashCodes() {
        ProfileFields firstProfileFields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Second Field")
            )
        );
        ProfileFields secondProfileFields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Third Field")
            )
        );

        int firstHashCode = firstProfileFields.hashCode();
        int secondHashCode = secondProfileFields.hashCode();

        assertNotEquals(
            "Expected when the fields length are equal but their values differ unique hash codes are returned but were equal",
            firstHashCode,
            secondHashCode
        );
    }

    @Test
    void hashCode_valuesInFieldsAreEqual_identicalHashCodesAreReturned() {
        ProfileFields firstProfileFields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Second Field")
            )
        );
        ProfileFields secondProfileFields = new ProfileFields(
            Arrays.asList(
                new Field("First Field"),
                new Field("Second Field")
            )
        );

        int firstHashCode = firstProfileFields.hashCode();
        int secondHashCode = secondProfileFields.hashCode();

        assertEquals(
            "Expected that when the profile fields are equal an equivalent hash code should be returned for both but were different",
            firstHashCode,
            secondHashCode
        );
    }
}
