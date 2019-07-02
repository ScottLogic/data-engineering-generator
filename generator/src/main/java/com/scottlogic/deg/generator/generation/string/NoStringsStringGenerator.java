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
package com.scottlogic.deg.generator.generation.string;

import com.scottlogic.deg.generator.utils.RandomNumberGenerator;

import java.util.Arrays;
import java.util.Collections;

public class NoStringsStringGenerator implements StringGenerator {
    private final String stringRepresentation;

    public NoStringsStringGenerator(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @Override
    public String toString() {
        return String.format("No strings: %s", this.stringRepresentation);
    }

    @Override
    public StringGenerator intersect(StringGenerator stringGenerator) {
        return new NoStringsStringGenerator(
            RegexStringGenerator.intersectRepresentation(this.stringRepresentation, stringGenerator.toString()));
    }

    @Override
    public StringGenerator complement() {
        throw new RuntimeException("Not implemented: Return a string generator able to emit ALL strings");
    }

    @Override
    public boolean isFinite() {
        return true;
    }

    @Override
    public long getValueCount() {
        return 0;
    }

    @Override
    public boolean match(String subject) {
        return false;
    }

    @Override
    public Iterable<String> generateInterestingValues() {
        return Collections.emptySet();
    }

    @Override
    public Iterable<String> generateAllValues() {
        return Collections.emptySet();
    }

    @Override
    public Iterable<String> generateRandomValues(RandomNumberGenerator randomNumberGenerator) {
        return Collections.emptySet();
    }
}
