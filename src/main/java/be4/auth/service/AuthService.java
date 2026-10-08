package be4.auth.service;

import be4.auth.datasource.dao.User;
import be4.auth.service.exceptions.NotFoundException;
import be4.auth.service.password.PasswordService;
import be4.auth.proto.*;
import be4.auth.service.exceptions.AlreadyExistsException;
import be4.auth.service.exceptions.InvalidPasswordException;
import be4.auth.service.jwt.JwtService;
import be4.auth.service.user.UserService;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@GrpcService
public class AuthService extends AuthServiceGrpc.AuthServiceImplBase {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordService passwordService;

    private TokenResponse getTokenResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return TokenResponse.newBuilder()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .build();
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<TokenResponse> responseObserver) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("User with this email already exists");
        }

        User user = new User(
                request.getEmail(), request.getFirstName(), request.getLastName(), request.getPhoneNumber(),
                passwordEncoder.encode(request.getPassword())
        );

        user = userService.save(user);

        TokenResponse response = getTokenResponse(user);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<TokenResponse> responseObserver) {
        User user = userService.getByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NotFoundException("The password is invalid");
        }

        TokenResponse response = getTokenResponse(user);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<BoolValue> responseObserver) {
        boolean isValid = jwtService.validateToken(request.getToken());

        BoolValue response = BoolValue.newBuilder()
                .setValue(isValid)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void refreshToken(RefreshTokenRequest request, StreamObserver<TokenResponse> responseObserver) {
        UUID id = jwtService.getSubject(request.getRefreshToken());
        User user = userService.get(id);

        TokenResponse response = getTokenResponse(user);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void changePassword(ChangePasswordRequest request, StreamObserver<Empty> responseObserver) {
        UUID id = jwtService.getSubject(request.getAccessToken());
        User user = userService.get(id);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("The password is invalid");
        }

        String newPass = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newPass);
        userService.save(user);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void resetPassword(ResetPasswordRequest request, StreamObserver<Empty> responseObserver) {
        UUID id = jwtService.getSubject(request.getAccessToken());
        User user = userService.get(id);

        String newPass = passwordService.generatePassword();

        user.setPassword(passwordEncoder.encode(newPass));
        userService.save(user);

        passwordService.sendEmail(newPass);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
