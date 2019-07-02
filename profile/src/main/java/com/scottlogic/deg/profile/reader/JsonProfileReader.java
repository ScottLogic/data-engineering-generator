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
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.RuleInformation;
import com.scottlogic.deg.common.profile.Profile;
import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.common.profile.Rule;
import com.scottlogic.deg.profile.serialisation.ProfileDeserialiser;
import com.scottlogic.deg.profile.v0_1.ProfileDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * JsonProfileReader is responsible for reading and validating a profile from a path to a profile JSON file.
 * It returns a Profile object for consumption by a generator
 */
public class JsonProfileReader implements ProfileReader {
    ConstraintReaderMap readerMap;

    @Inject
    public JsonProfileReader(ConstraintReaderMap mappings) {
        readerMap = mappings;
    }

    public Profile read(Path filePath) throws IOException {
        byte[] encoded = Files.readAllBytes(filePath);
        String profileJson = new String(encoded, StandardCharsets.UTF_8);

        return this.read(profileJson);
    }

    public Profile read(String profileJson) throws IOException {
        ProfileDTO profileDto = (ProfileDTO) new ProfileDeserialiser()
            .deserialise(
                profileJson,
                ProfileDTO.SchemaVersion);

        if (profileDto.fields == null) {
            throw new InvalidProfileException("Profile is invalid: 'fields' have not been defined.");
        }
        if (profileDto.rules == null) {
            throw new InvalidProfileException("Profile is invalid: 'rules' have not been defined.");
        }

        ProfileFields profileFields = new ProfileFields(
            profileDto.fields.stream()
                .map(fDto -> new Field(fDto.name))
                .collect(Collectors.toList()));

        ConstraintReader constraintReader = new MainConstraintReader(readerMap);

        Collection<Rule> rules = mapDtos(
            profileDto.rules,
            r -> {
                if (r.constraints.isEmpty()) {
                    throw new InvalidProfileException("Profile is invalid: unable to find 'constraints' for rule: " + r.rule);
                }
                RuleInformation constraintRule = new RuleInformation(r.rule);
                return new Rule(
                    constraintRule,
                    mapDtos(
                        r.constraints,
                        dto -> {
                            try {
                                return constraintReader.apply(
                                    dto,
                                    profileFields,
                                    Collections.singleton(constraintRule));
                            } catch (InvalidProfileException e) {
                                throw new InvalidProfileException("Rule: " + r.rule + "\n" + e.getMessage());
                            }
                        }));
            });

        return new Profile(profileFields, rules, profileDto.description);
    }

    static <TInput, TOutput> Collection<TOutput> mapDtos(
        Collection<TInput> dtos,
        DtoConverterFunction<TInput, TOutput> mapFunc) {

        Collection<TOutput> resultSet = new ArrayList<>();

        for (TInput dto : dtos) {
            resultSet.add(mapFunc.apply(dto));
        }

        return resultSet;
    }

    @FunctionalInterface
    interface DtoConverterFunction<TInput, TOutput> {
        TOutput apply(TInput t);
    }
}
