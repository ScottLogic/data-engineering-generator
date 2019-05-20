package com.scottlogic.deg.orchestrator.cucumber.testframework.utils;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.scottlogic.deg.generator.StandardGenerationEngine;
import com.scottlogic.deg.generator.decisiontree.DecisionTreeFactory;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;
import com.scottlogic.deg.generator.outputs.targets.OutputTargetFactory;
import com.scottlogic.deg.generator.outputs.targets.SingleDatasetOutputTarget;
import com.scottlogic.deg.profile.reader.ProfileReader;
import com.scottlogic.deg.generator.inputs.validation.ProfileValidator;
import com.scottlogic.deg.generator.inputs.validation.TypingRequiredPerFieldValidator;
import com.scottlogic.deg.generator.inputs.validation.reporters.ProfileValidationReporter;
import com.scottlogic.deg.generator.outputs.manifest.ManifestWriter;
import com.scottlogic.deg.orchestrator.validator.ConfigValidator;
import com.scottlogic.deg.generator.validators.ErrorReporter;

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
        bind(GenerationConfigSource.class).to(CucumberGenerationConfigSource.class);
        bind(ProfileValidator.class).to(TypingRequiredPerFieldValidator.class);
        bind(ErrorReporter.class).toInstance(new CucumberErrorReporter(testState));
        bind(DecisionTreeFactory.class).to(CucumberDecisionTreeFactory.class);

        bind(ConfigValidator.class).toInstance(mock(ConfigValidator.class));
        bind(ManifestWriter.class).toInstance(mock(ManifestWriter.class));
        bind(ProfileValidationReporter.class).toInstance(testState.validationReporter);
        bind(SingleDatasetOutputTarget.class).toInstance(new InMemoryOutputTarget(testState));

        OutputTargetFactory mockOutputTargetFactory = mock(OutputTargetFactory.class);
        when(mockOutputTargetFactory.create(any())).thenReturn(new InMemoryOutputTarget(testState));
        bind(OutputTargetFactory.class).toInstance(mockOutputTargetFactory);


        bind(boolean.class)
            .annotatedWith(Names.named("config:tracingIsEnabled"))
            .toInstance(false);

        if (testState.shouldSkipGeneration()) {
            bind(StandardGenerationEngine.class).toInstance(mock(StandardGenerationEngine.class));
        }
    }
}

