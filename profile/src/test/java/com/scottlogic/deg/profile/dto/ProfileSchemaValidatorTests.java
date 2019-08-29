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

package com.scottlogic.deg.profile.dto;

import com.scottlogic.deg.common.ValidationException;
import com.scottlogic.deg.profile.serialisation.SchemaVersionGetter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

public class ProfileSchemaValidatorTests {
    private final String TEST_PROFILE_DIR = "/test-profiles/";
    private final String INVALID_PROFILE_DIR = "invalid";
    private final String VALID_PROFILE_DIR = "valid";

    private FilenameFilter jsonFilter = (dir, name) -> name.toLowerCase().endsWith(".json");

    private File getFileFromURL(String profileDirName) {
        URL url = this.getClass().getResource(TEST_PROFILE_DIR + profileDirName);
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        return file;
    }

    Collection<DynamicTest> testInvalidProfiles(ProfileSchemaValidator profileValidator) {
        File[] arrayOfFiles = getFileFromURL(INVALID_PROFILE_DIR).listFiles(jsonFilter);
        Collection<DynamicTest> dynTsts = new ArrayList<>();

        for (File file : arrayOfFiles) {
            DynamicTest test = DynamicTest.dynamicTest(file.getName(), () -> {
                URL testProfileUrl =
                    this.getClass().getResource(
                        TEST_PROFILE_DIR + INVALID_PROFILE_DIR + "/" + file.getName()
                    );
                String schemaVersion = new SchemaVersionGetter().getSchemaVersionOfJson(file.toPath());
                URL schemaUrl =
                    Thread.currentThread().getContextClassLoader().getResource(getSchemaPath(schemaVersion));
                try {
                    profileValidator.validateProfile(new File(testProfileUrl.getPath()), schemaUrl);

                    Supplier<String> msgSupplier = () -> "Profile ["
                        + file.getName() + "] should not be valid";
                    Assertions.fail(msgSupplier);
                } catch (ValidationException e) {
                }
            });
            dynTsts.add(test);
        }
        return dynTsts;
    }

    Collection<DynamicTest> testValidProfiles(ProfileSchemaValidator profileValidator) {
        File[] arrayOfFiles = getFileFromURL(VALID_PROFILE_DIR).listFiles(jsonFilter);
        Collection<DynamicTest> dynTsts = new ArrayList<>();

        for (File file : arrayOfFiles) {
            String profileFilename = file.getName();
            DynamicTest test = DynamicTest.dynamicTest(profileFilename, () -> {
                URL testProfileUrl =
                    this.getClass().getResource(
                        TEST_PROFILE_DIR + VALID_PROFILE_DIR + "/" + profileFilename
                    );
                String schemaVersion = new SchemaVersionGetter().getSchemaVersionOfJson(file.toPath());
                URL schemaUrl =
                    Thread.currentThread().getContextClassLoader().getResource(getSchemaPath(schemaVersion));
                try {
                    profileValidator.validateProfile(new File(testProfileUrl.getPath()), schemaUrl);
                } catch (ValidationException e) {
                    Assertions.fail(
                        "Profile [" + profileFilename + "] should be valid [" + e.errorMessages + "]"
                    );
                }
            });
            dynTsts.add(test);
        }
        return dynTsts;
    }

    private String getSchemaPath(String schemaVersion) {
        return "profileschema/" + schemaVersion + "/datahelix.schema.json";
    }
}
