package com.scottlogic.deg.output.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.scottlogic.deg.output.FileUtils;
import com.scottlogic.deg.output.FileUtilsImpl;
import com.scottlogic.deg.output.OutputPath;
import com.scottlogic.deg.output.manifest.JsonManifestWriter;
import com.scottlogic.deg.output.manifest.ManifestWriter;
import com.scottlogic.deg.output.outputtarget.SingleDatasetOutputTarget;
import com.scottlogic.deg.output.writer.OutputWriterFactory;

import java.nio.file.Path;

public class OutputModule extends AbstractModule {
    private final OutputConfigSource outputConfigSource;

    public OutputModule(OutputConfigSource outputConfigSource) {
        this.outputConfigSource = outputConfigSource;
    }

    @Override
    protected void configure() {
        bind(OutputConfigSource.class).toInstance(outputConfigSource);

        bind(OutputWriterFactory.class).toProvider(OutputWriterFactoryProvider.class);
        bind(SingleDatasetOutputTarget.class).toProvider(SingleDatasetOutputTargetProvider.class);
        bind(ManifestWriter.class).to(JsonManifestWriter.class);
        bind(FileUtils.class).to(FileUtilsImpl.class);

        bind(OutputPath.class).toInstance(new OutputPath(outputConfigSource.getOutputPath()));

        bind(boolean.class)
            .annotatedWith(Names.named("config:canOverwriteOutputFiles"))
            .toInstance(outputConfigSource.overwriteOutputFiles());
        bind(boolean.class)
            .annotatedWith(Names.named("config:tracingIsEnabled"))
            .toInstance(outputConfigSource.isEnableTracing());
    }
}
