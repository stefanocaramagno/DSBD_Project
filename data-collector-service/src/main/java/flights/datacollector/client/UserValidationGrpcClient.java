package flights.datacollector.client;

import flights.usermanager.UserRequest;
import flights.usermanager.UserResponse;
import flights.usermanager.UserValidationServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class UserValidationGrpcClient {

    @GrpcClient("user-manager")
    private UserValidationServiceGrpc.UserValidationServiceBlockingStub blockingStub;

    public boolean userExists(String email) {
        UserRequest request = UserRequest.newBuilder()
                .setEmail(email)
                .build();

        UserResponse response = blockingStub.checkUserExists(request);
        return response.getExists();
    }
}