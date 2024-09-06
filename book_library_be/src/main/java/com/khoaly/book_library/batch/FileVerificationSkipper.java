package com.khoaly.book_library.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;

import java.io.FileNotFoundException;

public class FileVerificationSkipper implements SkipPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileVerificationSkipper.class);

    @Override
    public boolean shouldSkip(Throwable exception, long skipCount) throws SkipLimitExceededException {
        if (exception instanceof FileNotFoundException) {
            return false;
        } else if (exception instanceof FlatFileParseException && skipCount <= 5) {
            FlatFileParseException ffpe = (FlatFileParseException) exception;
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("An error occurred while processing the ").append(ffpe.getLineNumber()).append(" line of the file. Below was the faulty ").append("input.\n");
            errorMessage.append(ffpe.getInput()).append("\n");
            LOGGER.error("{}", errorMessage);
            return true;
        } else {
            return false;
        }
    }
}
