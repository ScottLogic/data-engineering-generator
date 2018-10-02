package com.scottlogic.deg.generator.smoke_tests;

import com.scottlogic.deg.generator.GenerationEngine;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.generation.combination_strategies.FieldExhaustiveCombinationStrategy;
import com.scottlogic.deg.generator.outputs.IDataSetOutputter;
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
    Collection<DynamicTest> shouldAllGenerateWithoutErrors() throws IOException {
        Collection<DynamicTest> dynamicTests = new ArrayList<>();

        File[] directoriesArray =
            Paths.get("..", "examples")
                .toFile()
                .listFiles(File::isDirectory);

        for (File dir : directoriesArray) {
            File profileFile = Paths.get(dir.getCanonicalPath(), "profile.json").toFile();

            GenerationConfig config = new GenerationConfig(GenerationConfig.DataGenerationType.Interesting,
                new FieldExhaustiveCombinationStrategy());

            DynamicTest test = DynamicTest.dynamicTest(dir.getName(), () -> {
                new GenerationEngine(
                        new NullDataSetOutputter())
                    .generateTestCases(profileFile.getAbsolutePath(), config);
            });

            dynamicTests.add(test);
        }

        return dynamicTests;
    }

    class NullDataSetOutputter implements IDataSetOutputter {
        @Override
        public void output(TestCaseGenerationResult dataSets) throws IOException {
            // iterate through the rows - assume lazy generation, so we haven't tested unless we've exhausted every iterable

            dataSets.datasets.iterator().forEachRemaining(
                ds -> ds.iterator().forEachRemaining(
                    row -> Assert.assertThat(row, notNullValue()))); // might as well assert non-null while we're at it
        }
    }
}
