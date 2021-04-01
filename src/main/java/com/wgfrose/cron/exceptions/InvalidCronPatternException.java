package com.wgfrose.cron.exceptions;

public class InvalidCronPatternException extends Exception {

    public InvalidCronPatternException(final String reason) {
        super(reason);
    }

}
