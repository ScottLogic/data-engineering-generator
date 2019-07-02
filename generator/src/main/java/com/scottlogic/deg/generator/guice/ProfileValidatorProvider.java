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
package com.scottlogic.deg.generator.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;
import com.scottlogic.deg.generator.inputs.validation.*;

import java.util.ArrayList;

public class ProfileValidatorProvider implements Provider<ProfileValidator> {
    private final GenerationConfigSource configSource;
    private final TypingRequiredPerFieldValidator untypedValidator;

    @Inject
    public ProfileValidatorProvider(
        GenerationConfigSource configSource,
        TypingRequiredPerFieldValidator untypedValidator) {

        this.configSource = configSource;
        this.untypedValidator = untypedValidator;
    }

    @Override
    public ProfileValidator get() {
        ArrayList<ProfileValidator> validators = new ArrayList<>();

        if(configSource.requireFieldTyping()) {
            validators.add(untypedValidator);
        }
        return new MultipleProfileValidator(validators);
    }
}
