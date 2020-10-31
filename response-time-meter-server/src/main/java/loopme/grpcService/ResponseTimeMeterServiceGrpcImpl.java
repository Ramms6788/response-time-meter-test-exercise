/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme.grpcService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import loopme.MeterRequest;
import loopme.MeterResponse;
import loopme.ResponseTimeMeterServiceGrpc;
import loopme.service.MeteringService;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class ResponseTimeMeterServiceGrpcImpl extends ResponseTimeMeterServiceGrpc.ResponseTimeMeterServiceImplBase {

    private Logger logger = LoggerFactory.getLogger(ResponseTimeMeterServiceGrpcImpl.class);

    @Autowired
    private MeteringService meteringService;

    @Override
    public void meter(MeterRequest request, StreamObserver<MeterResponse> responseObserver) {
        try {
            MeterResponse meterResponse = meteringService.meterResponseTime(request.getAddress());

            logger.info("Address: {}. Response Code: {}. Response Time: {}",
                request.getAddress(), meterResponse.getResponseCode(), meterResponse.getResponseTime());

            responseObserver.onNext(meterResponse);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex){
            logger.error("Error while metering response time for resource address {}. Reason: {}",
                request.getAddress(), ex.getMessage());

            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(ex.getMessage())
                .asRuntimeException());
        }
    }
}
