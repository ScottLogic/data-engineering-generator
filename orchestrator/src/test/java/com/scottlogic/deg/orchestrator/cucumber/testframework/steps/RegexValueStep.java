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
package com.scottlogic.deg.orchestrator.cucumber.testframework.steps;

import com.scottlogic.deg.orchestrator.cucumber.testframework.utils.CucumberTestHelper;
import com.scottlogic.deg.orchestrator.cucumber.testframework.utils.CucumberTestState;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.regex.Pattern;

public class RegexValueStep {

    private final CucumberTestState state;
    private final CucumberTestHelper helper;

    public RegexValueStep(CucumberTestState state, CucumberTestHelper helper){
        this.state = state;
        this.helper = helper;
    }

    @When("{fieldVar} is {operator} {regex}")
    public void whenFieldIsConstrainedByRegex(String fieldName, String constraintName, String value) throws Exception {
        this.state.addConstraint(fieldName, constraintName, value);
    }

    @When("{fieldVar} is anything but {operator} {regex}")
    public void whenFieldIsNotConstrainedByRegex(String fieldName, String constraintName, String value) throws Exception {
        this.state.addNotConstraint(fieldName, constraintName, value);
    }

    @Then("{fieldVar} contains strings matching {regex}")
    public void producedDataShouldContainStringValuesMatchingRegex(String fieldName, String regex){
        Pattern pattern = Pattern.compile(regex);

        helper.assertFieldContainsNullOrMatching(
            fieldName,
            String.class,
            value -> pattern.matcher(value).matches());
    }

    @Then("{fieldVar} contains anything but strings matching {regex}")
    public void producedDataShouldContainStringValuesNotMatchingRegex(String fieldName, String regex){
        Pattern pattern = Pattern.compile(regex);

        helper.assertFieldContainsNullOrMatching(
            fieldName,
            String.class,
            value -> !pattern.matcher(value).matches());
    }
}
