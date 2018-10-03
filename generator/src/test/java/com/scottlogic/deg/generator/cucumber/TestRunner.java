package com.scottlogic.deg.generator.cucumber;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty"},
    features = {"src/test/java/com/scottlogic/deg/generator/cucumber"},
    glue={"src/test/java/com/scottlogic/deg/generator/cucumber"},
    monochrome = true
)

public class TestRunner {
}