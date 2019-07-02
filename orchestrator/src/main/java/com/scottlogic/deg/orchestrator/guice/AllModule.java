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
package com.scottlogic.deg.orchestrator.guice;

import com.google.inject.AbstractModule;
import com.scottlogic.deg.generator.guice.GeneratorModule;
import com.scottlogic.deg.output.guice.OutputModule;
import com.scottlogic.deg.profile.guice.ProfileModule;

public class AllModule extends AbstractModule {
    private AllConfigSource configSource;

    public AllModule(AllConfigSource configSource) {
        this.configSource = configSource;
    }

    @Override
    protected void configure() {
        bind(AllConfigSource.class).toInstance(configSource);

        install(new ProfileModule(configSource));
        install(new GeneratorModule(configSource));
        install(new OutputModule(configSource));
    }
}
