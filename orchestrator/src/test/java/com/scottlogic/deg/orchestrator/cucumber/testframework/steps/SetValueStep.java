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

import com.scottlogic.deg.orchestrator.cucumber.testframework.utils.CucumberTestState;
import cucumber.api.java.en.When;

import java.util.List;

public class SetValueStep {

    private final CucumberTestState state;

    public SetValueStep(CucumberTestState state){
        this.state = state;
    }

    @When("{fieldVar} is in set:")
    public void whenFieldIsConstrainedBySetValue(String fieldName, List<Object> values) {
        this.state.addConstraint(fieldName, "in set", values);
    }

    @When("{fieldVar} is anything but in set:")
    public void whenFieldIsNotConstrainedBySetValue(String fieldName, List<Object> values) {
        this.state.addNotConstraint(fieldName, "in set", values);
    }
}
