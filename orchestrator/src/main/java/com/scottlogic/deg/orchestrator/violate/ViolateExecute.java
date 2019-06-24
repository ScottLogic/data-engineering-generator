package com.scottlogic.deg.orchestrator.violate;

import com.google.inject.Inject;
import com.scottlogic.deg.common.ValidationException;
import com.scottlogic.deg.common.profile.Profile;
import com.scottlogic.deg.generator.generation.DataGenerator;
import com.scottlogic.deg.common.output.GeneratedObject;
import com.scottlogic.deg.output.FileUtilsImpl;
import com.scottlogic.deg.output.manifest.ManifestWriter;
import com.scottlogic.deg.common.profile.ViolatedProfile;
import com.scottlogic.deg.output.writer.DataSetWriter;
import com.scottlogic.deg.output.outputtarget.SingleDatasetOutputTarget;
import com.scottlogic.deg.orchestrator.guice.AllConfigSource;
import com.scottlogic.deg.generator.inputs.profileviolation.ProfileViolator;
import com.scottlogic.deg.generator.inputs.validation.ProfileValidator;
import com.scottlogic.deg.output.outputtarget.OutputTargetFactory;
import com.scottlogic.deg.output.FileUtils;
import com.scottlogic.deg.generator.validators.ErrorReporter;
import com.scottlogic.deg.orchestrator.validator.ConfigValidator;
import com.scottlogic.deg.profile.reader.ProfileReader;
import com.scottlogic.deg.profile.v0_1.ProfileSchemaValidator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Stream;

public class ViolateExecute {
    private final ErrorReporter errorReporter;
    private final AllConfigSource configSource;
    private final ConfigValidator configValidator;
    private final OutputTargetFactory outputTargetFactory;
    private final ProfileReader profileReader;
    private final ProfileValidator profileValidator;
    private final ProfileSchemaValidator profileSchemaValidator;
    private final ProfileViolator profileViolator;
    private final DataGenerator dataGenerator;
    private final ViolateOutputValidator violateOutputValidator;
    private final ManifestWriter manifestWriter;

    @Inject
    ViolateExecute(
        ProfileReader profileReader,
        AllConfigSource configSource,
        OutputTargetFactory outputTargetFactory,
        ConfigValidator configValidator,
        ErrorReporter errorReporter,
        ProfileValidator profileValidator,
        ProfileSchemaValidator profileSchemaValidator,
        ProfileViolator profileViolator,
        DataGenerator dataGenerator,
        ViolateOutputValidator violateOutputValidator,
        ManifestWriter manifestWriter) {

        this.profileReader = profileReader;
        this.configSource = configSource;
        this.outputTargetFactory = outputTargetFactory;
        this.configValidator = configValidator;
        this.profileSchemaValidator = profileSchemaValidator;
        this.errorReporter = errorReporter;
        this.profileValidator = profileValidator;
        this.profileViolator = profileViolator;
        this.dataGenerator = dataGenerator;
        this.violateOutputValidator = violateOutputValidator;
        this.manifestWriter = manifestWriter;
    }

    public void execute() throws IOException {
        configValidator.preProfileChecks(configSource);
        profileSchemaValidator.validateProfile(configSource.getProfileFile());

        Profile profile = profileReader.read(configSource.getProfileFile().toPath());

        profileValidator.validate(profile);
        violateOutputValidator.validate(profile);

        doGeneration(profile);
    }

    private void doGeneration(Profile profile) throws IOException {
        List<ViolatedProfile> violatedProfiles = profileViolator.violate(profile);
        if (violatedProfiles.isEmpty()) {
            return;
        }
        manifestWriter.writeManifest(violatedProfiles);

        DecimalFormat intFormatter = FileUtilsImpl.getDecimalFormat(violatedProfiles.size());

        int filename = 1;
        for (Profile violatedProfile : violatedProfiles) {
            SingleDatasetOutputTarget outputTarget =
                outputTargetFactory.create(intFormatter.format(filename++));
            Stream<GeneratedObject> generatedObjectStream = dataGenerator.generateData(violatedProfile);
            outputData(profile, generatedObjectStream, outputTarget);
        }
    }

    private void outputData(
        Profile profile,
        Stream<GeneratedObject> generatedDataItems,
        SingleDatasetOutputTarget outputTarget) throws IOException
    {
        try (DataSetWriter writer = outputTarget.openWriter(profile.getFields())) {
            generatedDataItems.forEach(row -> {
                try {
                    writer.writeRow(row);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
