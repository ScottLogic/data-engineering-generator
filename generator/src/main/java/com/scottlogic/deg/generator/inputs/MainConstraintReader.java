package com.scottlogic.deg.generator.inputs;

import com.scottlogic.deg.generator.ProfileFields;
import com.scottlogic.deg.generator.constraints.*;
import com.scottlogic.deg.generator.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.ConditionalConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.OrConstraint;
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

        if (dto.is != null) {
            ConstraintReader subReader = this.atomicConstraintReaderLookup.getByTypeCode(dto.is);

            if (subReader == null) {
                throw new InvalidProfileException("Couldn't recognise constraint type from DTO: " + dto.is);
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

        // dto.is is not missing, therefore if not present dto.is is null - simple fix
        if (dto.is == null) {
            throw new InvalidProfileException("Couldn't recognise is null from DTO: " + dto.is);
        }

        throw new InvalidProfileException("Couldn't interpret constraint");
    }
}
