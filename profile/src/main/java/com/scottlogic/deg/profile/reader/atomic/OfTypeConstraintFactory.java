package com.scottlogic.deg.profile.reader.atomic;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.constraintdetail.ParsedGranularity;
import com.scottlogic.deg.common.profile.constraints.Constraint;
import com.scottlogic.deg.common.profile.constraints.atomic.*;
import com.scottlogic.deg.common.profile.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.generator.fieldspecs.whitelist.DistributedSet;
import com.scottlogic.deg.profile.reader.InvalidProfileException;
import com.scottlogic.deg.profile.reader.file.names.NameRetriever;

import java.math.BigDecimal;

import static com.scottlogic.deg.common.profile.constraints.atomic.IsOfTypeConstraint.Types.*;
import static com.scottlogic.deg.common.profile.constraints.atomic.IsOfTypeConstraint.Types.NUMERIC;

public class OfTypeConstraintFactory {
    public static Constraint create(Field field, String value){
        switch (value) {
            case "decimal":
                return new IsOfTypeConstraint(field, NUMERIC);

            case "string":
                return new IsOfTypeConstraint(field, STRING);

            case "datetime":
                return new IsOfTypeConstraint(field, DATETIME);

            case "integer":
                return new AndConstraint(
                    new IsOfTypeConstraint(field, NUMERIC),
                    new IsGranularToNumericConstraint(field, new ParsedGranularity(BigDecimal.ONE)));

            case "ISIN":
            case "SEDOL":
            case "CUSIP":
            case "RIC":
                return new AndConstraint(
                    new MatchesStandardConstraint(field, StandardConstraintTypes.valueOf(value)),
                    new IsOfTypeConstraint(field, IsOfTypeConstraint.Types.STRING)
                );
        }

        try {
            NameConstraintTypes nameType = NameConstraintTypes.lookupProfileText(value);
            DistributedSet<Object> objectDistributedSet = NameRetriever.loadNamesFromFile(nameType);
            return new IsInSetConstraint(field, objectDistributedSet);
        } catch (UnsupportedOperationException e){
            throw new InvalidProfileException("Profile is invalid: no constraints known for \"is\": \"ofType\", \"value\": \"" + value + "\"");
        }
    }
}
