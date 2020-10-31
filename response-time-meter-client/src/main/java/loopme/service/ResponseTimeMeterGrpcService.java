/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import loopme.MeterRequest;
import loopme.MeterResponse;
import loopme.ResponseTimeMeterServiceGrpc;
import loopme.pojo.Request;
import loopme.pojo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResponseTimeMeterGrpcService {

    private Logger logger = LoggerFactory.getLogger(ResponseTimeMeterGrpcService.class);

    @Value("${response-time-meter.grpc.server.host}")
    private String grpcServerHost;

    @Value("${response-time-meter.grpc.server.port}")
    private int grpcServerPort;

    public Response meterResponseTime(Request request){
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcServerHost, grpcServerPort)
            .usePlaintext()
            .build();

        ResponseTimeMeterServiceGrpc.ResponseTimeMeterServiceBlockingStub stub =
            ResponseTimeMeterServiceGrpc.newBlockingStub(channel);

        MeterRequest meterRequest = MeterRequest.newBuilder()
            .setAddress(request.getAddress())
            .build();

        try {
            MeterResponse meterResponse = stub.meter(meterRequest);

            Response response = new Response();
            response.setResponseCode(meterResponse.getResponseCode());
            response.setResponseTime(meterResponse.getResponseTime());

            return response;
        } catch (Exception ex){
            String errorMessage = "Error while metering response time for resource address " + request.getAddress() +
                ". Reason: " + ex.getMessage();
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        } finally {
            channel.shutdown();
        }
    }
}
