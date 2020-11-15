/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme.service;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import loopme.MeterRequest;
import loopme.MeterResponse;
import loopme.ResponseTimeMeterServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;

@Service
public class ResponseTimeMeterGrpcService {

    private Logger logger = LoggerFactory.getLogger(ResponseTimeMeterGrpcService.class);

    @Value("${response-time-meter.grpc.server.host}")
    private String grpcServerHost;

    @Value("${response-time-meter.grpc.server.port}")
    private int grpcServerPort;

    private ManagedChannel channel;
    private ResponseTimeMeterServiceGrpc.ResponseTimeMeterServiceBlockingStub blockingStub;
    private ResponseTimeMeterServiceGrpc.ResponseTimeMeterServiceFutureStub futureStub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(grpcServerHost, grpcServerPort)
            .usePlaintext()
            .build();

       blockingStub = ResponseTimeMeterServiceGrpc.newBlockingStub(channel);
       futureStub = ResponseTimeMeterServiceGrpc.newFutureStub(channel);
    }

    @PreDestroy
    public void shutdown(){
        if(Objects.nonNull(channel)){
            channel.shutdown();
        }
    }

    public MeterResponse meterResponseTime(MeterRequest meterRequest){
        try {
            return blockingStub.meter(meterRequest);
        } catch (Exception ex){
            String errorMessage = "Error while metering response time for resource address " + meterRequest.getAddress() +
                ". Reason: " + ex.getMessage();
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    public ListenableFuture<MeterResponse> meterResponseTimeAsync(MeterRequest meterRequest) {
        try {
            return futureStub.meter(meterRequest);
        } catch (Exception ex) {
            String errorMessage = "Error while metering response time for resource address " + meterRequest.getAddress() +
                ". Reason: " + ex.getMessage();
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }
}
