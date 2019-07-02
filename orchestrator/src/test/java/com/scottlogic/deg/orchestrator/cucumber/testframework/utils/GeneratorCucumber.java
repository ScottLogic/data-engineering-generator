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
package com.scottlogic.deg.orchestrator.cucumber.testframework.utils;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.junit.FeatureRunner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class GeneratorCucumber extends Cucumber {

    private RunNotifier notifier;

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws InitializationError if there is another problem
     */
    public GeneratorCucumber(Class clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        if (this.notifier == null)
            this.notifier = new TreatUndefinedCucumberStepsAsTestFailuresRunNotifier(notifier); //NOTE: Not thread safe

        super.runChild(child, this.notifier);
    }
}
