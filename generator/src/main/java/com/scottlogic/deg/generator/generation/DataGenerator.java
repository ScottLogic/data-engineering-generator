package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.common.profile.Profile;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.outputs.GeneratedObject;

import java.util.stream.Stream;

public interface DataGenerator {
    Stream<GeneratedObject> generateData(Profile profile,
                                         DecisionTree analysedProfile,
                                         GenerationConfigSource generationConfig);
}
