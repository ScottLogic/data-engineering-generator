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

package com.scottlogic.datahelix.generator.orchestrator.cucumber.testframework.utils;

import com.google.inject.AbstractModule;
import com.scottlogic.datahelix.generator.core.generation.AbstractDataGeneratorMonitor;
import com.scottlogic.datahelix.generator.core.generation.DataGenerator;
import com.scottlogic.datahelix.generator.core.generation.NoopDataGeneratorMonitor;
import com.scottlogic.datahelix.generator.core.validators.ErrorReporter;
import com.scottlogic.datahelix.generator.output.outputtarget.SingleDatasetOutputTarget;
import com.scottlogic.datahelix.generator.profile.reader.FileReader;
import com.scottlogic.datahelix.generator.profile.reader.ProfileReader;
import com.scottlogic.datahelix.generator.profile.validators.ConfigValidator;

import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class which defines bindings for Guice injection specific for cucumber testing. The test state is persisted through
 * the various classes by binding the CucumberTestState object to the instance specified here.
 */
public class CucumberTestModule extends AbstractModule {
    private final CucumberTestState testState;

    public CucumberTestModule(CucumberTestState testState) {
        this.testState = testState;
    }

    @Override
    public void configure() {
        bind(CucumberTestState.class).toInstance(testState);
        bind(ProfileReader.class).to(CucumberProfileReader.class);
        bind(FileReader.class).to(CucumberFileReader.class);
        bind(ErrorReporter.class).toInstance(new CucumberErrorReporter(testState));

        bind(ConfigValidator.class).toInstance(mock(ConfigValidator.class));
        bind(SingleDatasetOutputTarget.class).toInstance(new InMemoryOutputTarget(testState));
        bind(AbstractDataGeneratorMonitor.class).to(NoopDataGeneratorMonitor.class);

        if (testState.shouldSkipGeneration) {
            DataGenerator mockDataGenerator = mock(DataGenerator.class);
            when(mockDataGenerator.generateData(any())).thenReturn(Stream.empty());
            bind(DataGenerator.class).toInstance(mockDataGenerator);
        }
    }
}
