package com.scottlogic.deg.generator.outputs.targets;

import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.output.writer.DataSetWriter;

import java.io.IOException;

public interface SingleDatasetOutputTarget extends ValidatableOutput {
    DataSetWriter openWriter(ProfileFields fields) throws IOException;
}
