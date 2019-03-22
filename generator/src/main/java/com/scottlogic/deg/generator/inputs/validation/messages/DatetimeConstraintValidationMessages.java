package com.scottlogic.deg.generator.inputs.validation.messages;

import com.scottlogic.deg.generator.restrictions.DateTimeRestrictions;

import java.time.LocalDateTime;

public class DatetimeConstraintValidationMessages implements StandardValidationMessages {


    private DateTimeRestrictions restriction;
    private LocalDateTime newValue;

    public DatetimeConstraintValidationMessages(
        DateTimeRestrictions restriction,
        LocalDateTime newValue) {

        this.restriction = restriction;
        this.newValue = newValue;
    }

    @Override
    public String getVerboseMessage() {

        return String.format(
            "Datetime constraint with value %s has been applied. The range is %s.",
            newValue,
            restriction.toString());
    }
}
