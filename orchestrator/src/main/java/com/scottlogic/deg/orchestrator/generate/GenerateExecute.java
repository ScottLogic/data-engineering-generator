package com.scottlogic.deg.orchestrator.generate;

import com.google.inject.Inject;
import com.scottlogic.deg.common.ValidationException;
import com.scottlogic.deg.common.profile.Profile;
import com.scottlogic.deg.generator.generation.DataGenerator;
import com.scottlogic.deg.generator.generation.DataGeneratorMonitor;
import com.scottlogic.deg.common.output.GeneratedObject;
import com.scottlogic.deg.output.writer.DataSetWriter;
import com.scottlogic.deg.orchestrator.guice.AllConfigSource;
import com.scottlogic.deg.generator.inputs.validation.ProfileValidator;
import com.scottlogic.deg.output.outputtarget.SingleDatasetOutputTarget;
import com.scottlogic.deg.profile.reader.ProfileReader;
import com.scottlogic.deg.orchestrator.validator.ConfigValidator;
import com.scottlogic.deg.generator.validators.ErrorReporter;
import com.scottlogic.deg.profile.v0_1.ProfileSchemaValidator;

import java.io.IOException;
import java.util.stream.Stream;

public class GenerateExecute {
    private final ErrorReporter errorReporter;
    private final AllConfigSource configSource;
    private final SingleDatasetOutputTarget singleDatasetOutputTarget;
    private final ConfigValidator configValidator;
    private final ProfileReader profileReader;
    private final DataGenerator dataGenerator;
    private final ProfileValidator profileValidator;
    private final DataGeneratorMonitor monitor;
    private final ProfileSchemaValidator profileSchemaValidator;

    @Inject
    GenerateExecute(
        ProfileReader profileReader,
        DataGenerator dataGenerator,
        AllConfigSource configSource,
        SingleDatasetOutputTarget singleDatasetOutputTarget,
        ConfigValidator configValidator,
        ErrorReporter errorReporter,
        ProfileValidator profileValidator,
        ProfileSchemaValidator profileSchemaValidator,
        DataGeneratorMonitor monitor) {
        this.profileReader = profileReader;
        this.dataGenerator = dataGenerator;
        this.configSource = configSource;
        this.singleDatasetOutputTarget = singleDatasetOutputTarget;
        this.configValidator = configValidator;
        this.profileSchemaValidator = profileSchemaValidator;
        this.errorReporter = errorReporter;
        this.profileValidator = profileValidator;
        this.monitor = monitor;
    }

    public void execute() throws IOException {
        configValidator.preProfileChecks(configSource);
        profileSchemaValidator.validateProfile(configSource.getProfileFile());

        Profile profile = profileReader.read(configSource.getProfileFile().toPath());

        profileValidator.validate(profile);
        singleDatasetOutputTarget.validate();

        Stream<GeneratedObject> generatedDataItems = dataGenerator.generateData(profile);

        outputData(profile, generatedDataItems);
    }

    private void outputData(Profile profile, Stream<GeneratedObject> generatedDataItems) throws IOException {
        try (DataSetWriter writer = singleDatasetOutputTarget.openWriter(profile.getFields())) {
            generatedDataItems.forEach(row -> {
                try {
                    writer.writeRow(row);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        monitor.endGeneration();
    }
}
