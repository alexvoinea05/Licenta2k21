package com.localgrower.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InvalidAdressException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public InvalidAdressException() {
        super(ErrorConstants.INVALID_ADRESS_TYPE, "Incorrect adress", Status.BAD_REQUEST);
    }
}
