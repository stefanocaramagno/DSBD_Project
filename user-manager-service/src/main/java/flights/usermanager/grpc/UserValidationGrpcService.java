package flights.usermanager.grpc;

import flights.usermanager.UserRequest;
import flights.usermanager.UserResponse;
import flights.usermanager.UserValidationServiceGrpc;
import flights.usermanager.service.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserValidationGrpcService extends UserValidationServiceGrpc.UserValidationServiceImplBase {

    private final UserService userService;

    public UserValidationGrpcService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void checkUserExists(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        String email = request.getEmail();
        boolean exists = userService.userExists(email);

        UserResponse response = UserResponse.newBuilder()
                .setExists(exists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}