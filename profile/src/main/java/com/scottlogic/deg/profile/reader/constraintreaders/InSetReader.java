/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.deg.profile.reader.constraintreaders;

import com.scottlogic.deg.common.ValidationException;
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.common.profile.constraints.Constraint;
import com.scottlogic.deg.common.profile.constraints.atomic.IsInSetConstraint;
import com.scottlogic.deg.generator.fieldspecs.whitelist.DistributedSet;
import com.scottlogic.deg.generator.fieldspecs.whitelist.FrequencyDistributedSet;
import com.scottlogic.deg.generator.fieldspecs.whitelist.WeightedElement;
import com.scottlogic.deg.profile.reader.AtomicConstraintReader;
import com.scottlogic.deg.profile.reader.atomic.FromFileReader;
import com.scottlogic.deg.profile.reader.file.CsvInputStreamReader;
import com.scottlogic.deg.profile.dto.ConstraintDTO;

import java.io.*;
import java.util.stream.Collectors;

import static com.scottlogic.deg.profile.reader.ConstraintReaderHelpers.getValidatedValues;

public class InSetReader implements AtomicConstraintReader {
    private final FromFileReader fromFileReader;

    public InSetReader(String fromFilePath) {
        this.fromFileReader = new FromFileReader(fromFilePath);
    }

    @Override
    public Constraint apply(ConstraintDTO dto, ProfileFields fields) {
        if (dto.file != null) {
            return setFromFile(dto, fields);
        }

        return setFromProfile(dto, fields);
    }

    private Constraint setFromProfile(ConstraintDTO dto, ProfileFields fields) {
        return new IsInSetConstraint(
            fields.getByName(dto.field),
            FrequencyDistributedSet.uniform(getValidatedValues(dto)));
    }

    private Constraint setFromFile(ConstraintDTO dto, ProfileFields fields) {
        Field field = fields.getByName(dto.field);
        return new IsInSetConstraint(field, fromFileReader.setFromFile(dto.file));
    }
}
