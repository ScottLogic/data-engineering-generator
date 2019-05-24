package com.scottlogic.deg.output.outputtarget;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.output.FileUtils;
import com.scottlogic.deg.output.OutputPath;
import com.scottlogic.deg.output.writer.DataSetWriter;
import com.scottlogic.deg.output.writer.OutputWriterFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileOutputTarget implements SingleDatasetOutputTarget {
    private final OutputPath filePath;
    private final boolean canOverwriteExistingFiles;
    private final OutputWriterFactory outputWriterFactory;
    private final FileUtils fileUtils;

    @Inject
    public FileOutputTarget(
        OutputPath filePath,
        OutputWriterFactory outputWriterFactory,
        @Named("config:canOverwriteOutputFiles") boolean canOverwriteOutputFiles, FileUtils fileUtils) {
        this.canOverwriteExistingFiles = canOverwriteOutputFiles;
        this.outputWriterFactory = outputWriterFactory;
        this.fileUtils = fileUtils;
        this.filePath = filePath;
    }

    @Override
    public DataSetWriter openWriter(ProfileFields fields) throws IOException {
        final OutputStream stream = new FileOutputStream(
            actualPath().toFile(),
            false);

        try {
            return outputWriterFactory.createWriter(stream, fields);
        } catch (Exception e) {
            stream.close();
            throw e;
        }
    }

    @Override
    public void validate() throws OutputTargetValidationException, IOException {
        if (fileUtils.isDirectory(actualPath())) {
            throw new OutputTargetValidationException("target is a directory, please use a different output filename");
        }
        else if (!canOverwriteExistingFiles && fileUtils.exists(actualPath())) {
            throw new OutputTargetValidationException("file already exists, please use a different output filename or use the --replace option");
        }
        else if (!fileUtils.exists(actualPath())) {
            Path parent = actualPath().toAbsolutePath().getParent();
            if (!fileUtils.createDirectories(parent)) {
                throw new OutputTargetValidationException("parent directory of output file already exists but is not a directory, please use a different output filename");
            }
        }
    }

    private Path actualPath(){
        Path directoryPath = filePath.get().getParent();
        if (directoryPath == null) {
            directoryPath = Paths.get(System.getProperty("user.dir"));
        }
        return directoryPath.resolve(filePath.get().getFileName());
    }
}
