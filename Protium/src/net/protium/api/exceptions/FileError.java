package net.protium.api.exceptions;

import java.io.File;


public class FileError extends CommonException {
    public FileError(Class causerClass, FileOperation operation, File object) {
        super(causerClass, "failed to perform operation [" + operation + "] on file '" + object.getAbsolutePath() + "'");
    }
}
