Copyright 2019 Scott Logic Ltd /
/
Licensed under the Apache License, Version 2.0 (the \"License\");/
you may not use this file except in compliance with the License./
You may obtain a copy of the License at/
/
    http://www.apache.org/licenses/LICENSE-2.0/
/
Unless required by applicable law or agreed to in writing, software/
distributed under the License is distributed on an \"AS IS\" BASIS,/
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied./
See the License for the specific language governing permissions and/
limitations under the License.
package com.scottlogic.deg.profile.reader;

import com.google.inject.Inject;
import com.scottlogic.deg.common.profile.constraints.Constraint;
import com.scottlogic.deg.common.profile.RuleInformation;
import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.common.profile.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.common.profile.constraints.grammatical.ConditionalConstraint;
import com.scottlogic.deg.common.profile.constraints.grammatical.OrConstraint;
import com.scottlogic.deg.profile.v0_1.ConstraintDTO;

import java.util.Set;

public class MainConstraintReader implements ConstraintReader {
    private final ConstraintReaderMap constraintReaderMap;

    @Inject
    public MainConstraintReader(ConstraintReaderMap constraintReaderMap) {
        this.constraintReaderMap = constraintReaderMap;
    }

    @Override
    public Constraint apply(
        ConstraintDTO dto,
        ProfileFields fields,
        Set<RuleInformation> rules) {

        if (dto == null) {
            throw new InvalidProfileException("Constraint is null");
        }

        if (dto.is == null) {
            throw new InvalidProfileException("Couldn't recognise 'is' property, it must be set to a value");
        }

        if (dto.is != ConstraintDTO.undefined) {
            ConstraintReader subReader = this.constraintReaderMap.getReader(
                (String) dto.is,
                ConstraintReaderHelpers.getValueAsString(dto.value)
            );

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
            if (dto.allOf.isEmpty()) {
                throw new InvalidProfileException("AllOf must contain at least one constraint.");
            }
            return new AndConstraint(
                JsonProfileReader.mapDtos(
                    dto.allOf,
                    subConstraintDto -> this.apply(
                        subConstraintDto,
                        fields,
                        rules)));
        }

        if (dto.anyOf != null) {
            return new OrConstraint(
                JsonProfileReader.mapDtos(
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
}
