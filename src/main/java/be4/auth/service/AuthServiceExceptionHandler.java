package be4.auth.service;

import be4.auth.service.exceptions.AlreadyExistsException;
import be4.auth.service.exceptions.InvalidPasswordException;
import be4.auth.service.exceptions.InvalidTokenException;
import be4.auth.service.exceptions.NotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.grpc.server.advice.GrpcAdvice;
import org.springframework.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class AuthServiceExceptionHandler {
    @GrpcExceptionHandler(AlreadyExistsException.class)
    public StatusRuntimeException alreadyExistsExceptionHandler(AlreadyExistsException e) {
        return Status.ALREADY_EXISTS
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(NotFoundException.class)
    public StatusRuntimeException notFoundExceptionHandler(NotFoundException e) {
        return Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(InvalidTokenException.class)
    public StatusRuntimeException invalidTokenExceptionHandler(InvalidTokenException e) {
        return Status.UNAUTHENTICATED
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(InvalidPasswordException.class)
    public StatusRuntimeException invalidPasswordExceptionHandler(InvalidPasswordException e) {
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}
