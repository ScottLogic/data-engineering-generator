package com.scottlogic.deg.generator.inputs;

import com.scottlogic.deg.generator.ProfileFields;
import com.scottlogic.deg.generator.constraints.*;
import com.scottlogic.deg.generator.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.ConditionalConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.OrConstraint;
import com.scottlogic.deg.schemas.common.InvalidSchemaException;
import com.scottlogic.deg.schemas.v3.ConstraintDTO;

import java.util.Set;

public class MainConstraintReader implements ConstraintReader {
    private final AtomicConstraintReaderLookup atomicConstraintReaderLookup;

    public MainConstraintReader() {
        this.atomicConstraintReaderLookup = new AtomicConstraintReaderLookup();
    }

    @Override
    public Constraint apply(
        ConstraintDTO dto,
        ProfileFields fields,
        Set<RuleInformation> rules)
        throws InvalidProfileException {

        if (dto == null) {
            throw new InvalidProfileException("Constraint is null");
        }

        String isConstraintValue = readIsConstraint(dto);

        if (isConstraintValue != null) {
            ConstraintReader subReader = this.atomicConstraintReaderLookup.getByTypeCode(isConstraintValue);

            if (subReader == null) {
                throw new InvalidProfileException("Couldn't recognise constraint type from DTO: " + isConstraintValue);
            }

            try {
                return subReader.apply(dto, fields, rules);
            } catch (IllegalArgumentException e) {
                throw new InvalidProfileException(e.getMessage());
            }
        }

        if (dto.not != null) {
            return this.apply(dto.not, fields, rules).negate();
        }

        if (dto.allOf != null) {
            return new AndConstraint(
                ProfileReader.mapDtos(
                    dto.allOf,
                    subConstraintDto -> this.apply(
                        subConstraintDto,
                        fields,
                        rules)));
        }

        if (dto.anyOf != null) {
            return new OrConstraint(
                ProfileReader.mapDtos(
                    dto.anyOf,
                    subConstraintDto -> this.apply(
                        subConstraintDto,
                        fields,
                        rules)));
        }

        if (dto.if_ != null) {
            return new ConditionalConstraint(
                this.apply(
                    dto.if_,
                    fields,
                    rules),
                this.apply(
                    dto.then,
                    fields,
                    rules),
                dto.else_ != null
                    ? this.apply(
                        dto.else_,
                        fields,
                        rules)
                    : null);
        }

        throw new InvalidProfileException("Couldn't interpret constraint");
    }

    private String readIsConstraint(ConstraintDTO dto) throws InvalidProfileException {
        try {
            return dto.getIs();
        }
        catch (InvalidSchemaException e) {
            throw new InvalidProfileException(e);
        }
    }
}
