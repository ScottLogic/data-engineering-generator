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
package com.scottlogic.deg.orchestrator.validator;

import com.scottlogic.deg.common.ValidationException;
import com.scottlogic.deg.generator.config.detail.DataGenerationType;
import com.scottlogic.deg.orchestrator.guice.AllConfigSource;
import com.scottlogic.deg.output.FileUtils;
import com.scottlogic.deg.output.outputtarget.OutputTargetValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigValidatorTests {
    @Mock
    private FileUtils mockFileUtils;
    @Mock
    private AllConfigSource mockConfigSource;
    @Mock
    private File mockProfileFile = mock(File.class);

    @Test
    public void preProfileChecks_withValid_returnsNoErrorMessages() {
        ConfigValidator configValidator = new ConfigValidator(mockFileUtils);
        when(mockConfigSource.getProfileFile()).thenReturn(mockProfileFile);
        when(mockFileUtils.containsInvalidChars(mockProfileFile)).thenReturn(false);
        when(mockProfileFile.exists()).thenReturn(true);
        when(mockProfileFile.isDirectory()).thenReturn(false);
        when(mockFileUtils.isFileEmpty(mockProfileFile)).thenReturn(false);

        assertDoesNotThrow(()->configValidator.preProfileChecks(mockConfigSource),"Expected no exception, but one was thrown.");
    }

    @Test
    public void preProfileChecks_randomWithMaxRows_returnsNoErrorMessages() {
        ConfigValidator configValidator = new ConfigValidator(mockFileUtils);
        when(mockConfigSource.getProfileFile()).thenReturn(mockProfileFile);
        when(mockFileUtils.containsInvalidChars(mockProfileFile)).thenReturn(false);
        when(mockProfileFile.exists()).thenReturn(true);
        when(mockProfileFile.isDirectory()).thenReturn(false);
        when(mockFileUtils.isFileEmpty(mockProfileFile)).thenReturn(false);
        when(mockConfigSource.getGenerationType()).thenReturn(DataGenerationType.RANDOM);
        when(mockConfigSource.getMaxRows()).thenReturn(25L);

        assertDoesNotThrow(()->configValidator.preProfileChecks(mockConfigSource),"Expected no exception, but one was thrown.");
    }

    @Test
    public void preProfileChecks_profileFilePathContainsInvalidChars_throwsException() {
        ConfigValidator configValidator = new ConfigValidator(mockFileUtils);
        when(mockConfigSource.getProfileFile()).thenReturn(mockProfileFile);
        when(mockFileUtils.containsInvalidChars(mockProfileFile)).thenReturn(true);

        assertThrows(ValidationException.class, ()->configValidator.preProfileChecks(mockConfigSource),"Expected ValidationException to throw, but didn't");
    }

    @Test
    public void preProfileChecks_profileFileDoesNotExist_throwsException() {
        ConfigValidator configValidator = new ConfigValidator(mockFileUtils);
        when(mockConfigSource.getProfileFile()).thenReturn(mockProfileFile);
        when(mockFileUtils.containsInvalidChars(mockProfileFile)).thenReturn(false);
        when(mockProfileFile.exists()).thenReturn(false);

        assertThrows(ValidationException.class, ()->configValidator.preProfileChecks(mockConfigSource),"Expected ValidationException to throw, but didn't");
    }

    @Test
    public void preProfileChecks_profileFileIsDir_throwsException() {
        ConfigValidator configValidator = new ConfigValidator(mockFileUtils);
        when(mockConfigSource.getProfileFile()).thenReturn(mockProfileFile);
        when(mockFileUtils.containsInvalidChars(mockProfileFile)).thenReturn(false);
        when(mockProfileFile.exists()).thenReturn(true);
        when(mockProfileFile.isDirectory()).thenReturn(true);

        assertThrows(ValidationException.class, ()->configValidator.preProfileChecks(mockConfigSource),"Expected ValidationException to throw, but didn't");
    }

    @Test
    public void preProfileChecks_profileFileIsEmpty_throwsException() {
        ConfigValidator configValidator = new ConfigValidator(mockFileUtils);
        when(mockConfigSource.getProfileFile()).thenReturn(mockProfileFile);
        when(mockFileUtils.containsInvalidChars(mockProfileFile)).thenReturn(false);
        when(mockProfileFile.exists()).thenReturn(true);
        when(mockProfileFile.isDirectory()).thenReturn(false);
        when(mockFileUtils.isFileEmpty(mockProfileFile)).thenReturn(true);

        assertThrows(ValidationException.class, ()->configValidator.preProfileChecks(mockConfigSource),"Expected ValidationException to throw, but didn't");
    }
}
