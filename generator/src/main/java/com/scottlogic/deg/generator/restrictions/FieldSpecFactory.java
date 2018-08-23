package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.constraints.*;
import com.scottlogic.deg.generator.utils.IStringGenerator;
import com.scottlogic.deg.generator.utils.StringGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.regex.Pattern;

public class FieldSpecFactory {
    public FieldSpec construct(IConstraint constraint) {
        final FieldSpec fieldSpec = new FieldSpec();
        apply(fieldSpec, constraint, false);
        return fieldSpec;
    }

    private void apply(FieldSpec fieldSpec, IConstraint constraint, boolean negate) {
        if (constraint instanceof NotConstraint) {
            apply(fieldSpec, ((NotConstraint) constraint).negatedConstraint, !negate);
        } else if (constraint instanceof IsInSetConstraint) {
            apply(fieldSpec, (IsInSetConstraint) constraint, negate);
        } else if (constraint instanceof IsEqualToConstantConstraint) {
            apply(fieldSpec, (IsEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsGreaterThanConstantConstraint) {
            apply(fieldSpec, (IsGreaterThanConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsGreaterThanOrEqualToConstantConstraint) {
            apply(fieldSpec, (IsGreaterThanOrEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsLessThanConstantConstraint) {
            apply(fieldSpec, (IsLessThanConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsLessThanOrEqualToConstantConstraint) {
            apply(fieldSpec, (IsLessThanOrEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsAfterConstantDateTimeConstraint) {
            apply(fieldSpec, (IsAfterConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsAfterOrEqualToConstantDateTimeConstraint) {
            apply(fieldSpec, (IsAfterOrEqualToConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsBeforeConstantDateTimeConstraint) {
            apply(fieldSpec, (IsBeforeConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsBeforeOrEqualToConstantDateTimeConstraint) {
            apply(fieldSpec, (IsBeforeOrEqualToConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsNullConstraint) {
            apply(fieldSpec, (IsNullConstraint) constraint, negate);
        } else if (constraint instanceof MatchesRegexConstraint) {
            apply(fieldSpec, (MatchesRegexConstraint) constraint, negate);
        } else if (constraint instanceof IsOfTypeConstraint) {
            apply(fieldSpec, (IsOfTypeConstraint) constraint, negate);
        } else if (constraint instanceof StringHasLengthConstraint) {
            apply(fieldSpec, (StringHasLengthConstraint) constraint, negate);
        } else if (constraint instanceof IsStringLongerThanConstraint) {
            apply(fieldSpec, (IsStringLongerThanConstraint) constraint, negate);
        } else if (constraint instanceof IsStringShorterThanConstraint) {
            apply(fieldSpec, (IsStringShorterThanConstraint) constraint, negate);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void apply(FieldSpec fieldSpec, IsEqualToConstantConstraint constraint, boolean negate) {
        apply(
                fieldSpec,
                new IsInSetConstraint(
                        constraint.field,
                        Collections.singleton(constraint.requiredValue)
                ),
                negate
        );
    }

    private void apply(FieldSpec fieldSpec, IsInSetConstraint constraint, boolean negate) {
        SetRestrictions setRestrictions = fieldSpec.getSetRestrictions();
        if (setRestrictions == null) {
            setRestrictions = new SetRestrictions();
            fieldSpec.setSetRestrictions(setRestrictions);
        }
        if (negate) {
            setRestrictions.blacklist = constraint.legalValues;
        } else {
            setRestrictions.whitelist = constraint.legalValues;
        }
    }

    private void apply(FieldSpec fieldSpec, IsNullConstraint constraint, boolean negate) {
        NullRestrictions nullRestrictions = fieldSpec.getNullRestrictions();
        if (nullRestrictions == null) {
            nullRestrictions = new NullRestrictions();
            fieldSpec.setNullRestrictions(nullRestrictions);
        }
        nullRestrictions.nullness = negate
                ? NullRestrictions.Nullness.MustNotBeNull
                : NullRestrictions.Nullness.MustBeNull;
    }

    private void apply(FieldSpec fieldSpec, IsOfTypeConstraint constraint, boolean negate) {
        if (negate) {
            throw new UnsupportedOperationException();
        }

        switch (constraint.requiredType) {
            case String:
                if (fieldSpec.getStringRestrictions() == null)
                    fieldSpec.setStringRestrictions(new StringRestrictions());
                break;
            case Numeric:
                if (fieldSpec.getNumericRestrictions() == null)
                    fieldSpec.setNumericRestrictions(new NumericRestrictions());
                break;
            case Temporal:
                if (fieldSpec.getDateTimeRestrictions() == null)
                    fieldSpec.setDateTimeRestrictions(new DateTimeRestrictions());
                break;
            default:
                throw new UnsupportedOperationException("Can't create restrictions for specified data type");
        }
    }

    private void apply(FieldSpec fieldSpec, IsGreaterThanConstantConstraint constraint, boolean negate) {
        applyGreaterThanConstraint(fieldSpec, constraint.referenceValue, false, negate);
    }

    private void apply(FieldSpec fieldSpec, IsGreaterThanOrEqualToConstantConstraint constraint, boolean negate) {
        applyGreaterThanConstraint(fieldSpec, constraint.referenceValue, true, negate);
    }

    private void applyGreaterThanConstraint(FieldSpec fieldSpec, Number limitValue, boolean inclusive, boolean negate) {
        NumericRestrictions numericRestrictions = fieldSpec.getNumericRestrictions();
        if (numericRestrictions == null) {
            numericRestrictions = new NumericRestrictions();
            fieldSpec.setNumericRestrictions(numericRestrictions);
        }
        final BigDecimal limit = numberToBigDecimal(limitValue);
        if (negate) {
            numericRestrictions.max = new NumericRestrictions.NumericLimit(
                    limit,
                    !inclusive
            );
        } else {
            numericRestrictions.min = new NumericRestrictions.NumericLimit(
                    limit,
                    inclusive
            );
        }
    }

    private void apply(FieldSpec fieldSpec, IsLessThanConstantConstraint constraint, boolean negate) {
        applyLessThanConstraint(fieldSpec, constraint.referenceValue, false, negate);
    }

    private void apply(FieldSpec fieldSpec, IsLessThanOrEqualToConstantConstraint constraint, boolean negate) {
        applyLessThanConstraint(fieldSpec, constraint.referenceValue, true, negate);
    }

    private void applyLessThanConstraint(FieldSpec fieldSpec, Number limitValue, boolean inclusive, boolean negate) {
        NumericRestrictions numericRestrictions = fieldSpec.getNumericRestrictions();
        if (numericRestrictions == null) {
            numericRestrictions = new NumericRestrictions();
            fieldSpec.setNumericRestrictions(numericRestrictions);
        }
        final BigDecimal limit = numberToBigDecimal(limitValue);
        if (negate) {
            numericRestrictions.min = new NumericRestrictions.NumericLimit(
                    limit,
                    !inclusive
            );
        } else {
            numericRestrictions.max = new NumericRestrictions.NumericLimit(
                    limit,
                    inclusive
            );
        }
    }

    private void apply(FieldSpec fieldSpec, IsAfterConstantDateTimeConstraint constraint, boolean negate) {
        applyIsAfterConstraint(fieldSpec, constraint.referenceValue, false, negate);
    }

    private void apply(FieldSpec fieldSpec, IsAfterOrEqualToConstantDateTimeConstraint constraint, boolean negate) {
        applyIsAfterConstraint(fieldSpec, constraint.referenceValue, true, negate);
    }

    private void applyIsAfterConstraint(FieldSpec fieldSpec, LocalDateTime limit, boolean inclusive, boolean negate) {
        DateTimeRestrictions dateTimeRestrictions = fieldSpec.getDateTimeRestrictions();
        if (dateTimeRestrictions == null) {
            dateTimeRestrictions = new DateTimeRestrictions();
            fieldSpec.setDateTimeRestrictions(dateTimeRestrictions);
        }
        if (negate) {
            dateTimeRestrictions.max = new DateTimeRestrictions.DateTimeLimit(limit, !inclusive);
        } else {
            dateTimeRestrictions.min = new DateTimeRestrictions.DateTimeLimit(limit, inclusive);
        }
    }

    private void apply(FieldSpec fieldSpec, IsBeforeConstantDateTimeConstraint constraint, boolean negate) {
        applyIsBeforeConstraint(fieldSpec, constraint.referenceValue, false, negate);
    }

    private void apply(FieldSpec fieldSpec, IsBeforeOrEqualToConstantDateTimeConstraint constraint, boolean negate) {
        applyIsBeforeConstraint(fieldSpec, constraint.referenceValue, true, negate);
    }

    private void applyIsBeforeConstraint(FieldSpec fieldSpec, LocalDateTime limit, boolean inclusive, boolean negate) {
        DateTimeRestrictions dateTimeRestrictions = fieldSpec.getDateTimeRestrictions();
        if (dateTimeRestrictions == null) {
            dateTimeRestrictions = new DateTimeRestrictions();
            fieldSpec.setDateTimeRestrictions(dateTimeRestrictions);
        }
        if (negate) {
            dateTimeRestrictions.min = new DateTimeRestrictions.DateTimeLimit(limit, !inclusive);
        } else {
            dateTimeRestrictions.max = new DateTimeRestrictions.DateTimeLimit(limit, inclusive);
        }
    }

    private void apply(FieldSpec fieldSpec, MatchesRegexConstraint constraint, boolean negate) {
        applyPattern(fieldSpec, constraint.regex, negate);
    }

    private void apply(FieldSpec fieldSpec, StringHasLengthConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".*{%s}", constraint.referenceValue));
        applyPattern(fieldSpec, regex, negate);
    }

    private void apply(FieldSpec fieldSpec, IsStringShorterThanConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".*{0,%d}", constraint.referenceValue + 1));
        applyPattern(fieldSpec, regex, negate);
    }

    private void apply(FieldSpec fieldSpec, IsStringLongerThanConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".*{%d,}", constraint.referenceValue + 1));
        applyPattern(fieldSpec, regex, negate);
    }

    private void applyPattern(FieldSpec fieldSpec, Pattern pattern, boolean negate) {
        StringRestrictions stringRestrictions = new StringRestrictions();
        fieldSpec.setStringRestrictions(stringRestrictions);

        IStringGenerator nominalStringGenerator = new StringGenerator(pattern.toString());
        nominalStringGenerator = negate
            ? nominalStringGenerator.complement()
            : nominalStringGenerator;

        stringRestrictions.stringGenerator = nominalStringGenerator;
    }

    private BigDecimal numberToBigDecimal(Number number) {
        if (number instanceof Long) {
            return BigDecimal.valueOf(number.longValue());
        } else if (number instanceof Integer) {
            return BigDecimal.valueOf(number.intValue());
        } else if (number instanceof Double) {
            return BigDecimal.valueOf(number.doubleValue());
        } else if (number instanceof Float) {
            return BigDecimal.valueOf(number.floatValue());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
