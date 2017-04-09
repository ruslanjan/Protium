/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.exceptions;

import java.io.File;


public class FileError extends CommonException {
    public FileError(Class causerClass, FileOperation operation, File object) {
        super(causerClass, "failed to perform operation [" + operation + "] on file '" + object.getAbsolutePath() + "'");
    }
}
