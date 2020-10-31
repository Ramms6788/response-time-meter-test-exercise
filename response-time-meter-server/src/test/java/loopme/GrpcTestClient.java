/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme;

import io.grpc.ManagedChannel;
import org.springframework.stereotype.Service;

@Service
public class GrpcTestClient {

    public MeterResponse singleThreadedBlockingCall(ManagedChannel channel, String givenAddress){
        ResponseTimeMeterServiceGrpc.ResponseTimeMeterServiceBlockingStub stub =
            ResponseTimeMeterServiceGrpc.newBlockingStub(channel);

        MeterRequest meterRequest = MeterRequest.newBuilder()
            .setAddress(givenAddress)
            .build();

        return stub.meter(meterRequest);
    }

}
