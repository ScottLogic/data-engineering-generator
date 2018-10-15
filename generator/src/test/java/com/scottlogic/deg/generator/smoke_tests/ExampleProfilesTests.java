package com.scottlogic.deg.generator.smoke_tests;

import com.scottlogic.deg.generator.GenerationEngine;
import com.scottlogic.deg.generator.ProfileFields;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.generation.combination_strategies.FieldExhaustiveCombinationStrategy;
import com.scottlogic.deg.generator.inputs.InvalidProfileException;
import com.scottlogic.deg.generator.outputs.GeneratedObject;
import com.scottlogic.deg.generator.outputs.targets.IOutputTarget;
import com.scottlogic.deg.generator.outputs.TestCaseGenerationResult;
import org.junit.Assert;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.notNullValue;

class ExampleProfilesTests {
    @TestFactory
    Collection<DynamicTest> shouldGenerateAsTestCasesWithoutErrors() throws IOException {
        return forEachProfileFile(((generationEngine, profileFile) -> {
            GenerationConfig config = new GenerationConfig(GenerationConfig.DataGenerationType.Interesting,
                new FieldExhaustiveCombinationStrategy());

            generationEngine.generateTestCases(profileFile.toPath(), config);
        }));
    }

    @TestFactory
    Collection<DynamicTest> shouldGenerateWithoutErrors() throws IOException {
        return forEachProfileFile(((generationEngine, profileFile) -> {
            GenerationConfig config = new GenerationConfig(GenerationConfig.DataGenerationType.Interesting,
                new FieldExhaustiveCombinationStrategy());

            generationEngine.generateDataSet(profileFile.toPath(), config);
        }));
    }

    private Collection<DynamicTest> forEachProfileFile(IGenerateConsumer consumer) throws IOException {
        Collection<DynamicTest> dynamicTests = new ArrayList<>();

        File[] directoriesArray =
            Paths.get("..", "examples")
                .toFile()
                .listFiles(File::isDirectory);

        for (File dir : directoriesArray) {
            File profileFile = Paths.get(dir.getCanonicalPath(), "profile.json").toFile();

            DynamicTest test = DynamicTest.dynamicTest(dir.getName(), () -> {
                consumer.generate(
                    new GenerationEngine(new NullOutputTarget()),
                    profileFile);
            });

            dynamicTests.add(test);
        }

        return dynamicTests;
    }

    private class NullOutputTarget implements IOutputTarget {
        @Override
        public void outputDataset(Iterable<GeneratedObject> generatedObjects, ProfileFields profileFields) throws IOException {
            // iterate through the rows - assume lazy generation, so we haven't tested unless we've exhausted the iterable

            generatedObjects.iterator().forEachRemaining(
                row -> Assert.assertThat(row, notNullValue())); // might as well assert non-null while we're at it
        }

        @Override
        public void outputTestCases(TestCaseGenerationResult dataSets) throws IOException {
            // iterate through the rows - assume lazy generation, so we haven't tested unless we've exhausted every iterable

            dataSets.datasets.iterator().forEachRemaining(
                ds -> ds.iterator().forEachRemaining(
                    row -> Assert.assertThat(row, notNullValue()))); // might as well assert non-null while we're at it
        }
    }

    @FunctionalInterface
    private interface IGenerateConsumer {
        void generate(GenerationEngine engine, File profileFile) throws IOException, InvalidProfileException;
    }
}
