/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import loopme.service.MeteringService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(ResponseTimeMeterServiceGrpcImplTest.TEST_PROFILE)
public class ResponseTimeMeterServiceGrpcImplTest {

    public static final String TEST_PROFILE = "test";

    @Value("${grpc.server.host}")
    private String grpcServerHost;

    @Value("${grpc.server.port}")
    private int grpcServerPort;

    @Autowired
    private GrpcTestClient grpcTestClient;

    @MockBean
    private MeteringService meteringServiceMock;

    private ManagedChannel channel;

    @BeforeEach
    public void setup() {
        channel = ManagedChannelBuilder.forAddress(grpcServerHost, grpcServerPort)
            .usePlaintext()
            .build();
    }

    @AfterEach
    public void shutdown() {
        channel.shutdown();
    }

    @Test
    public void singleThreadedBlockingCallsTest(){
         TestUtil.testData().forEach((givenAddress, meterRequest) -> {
            //GIVEN
            MeterResponse meterResponseMock = MeterResponse.newBuilder()
                .setResponseCode(meterRequest.getResponseCode())
                .setResponseTime(meterRequest.getResponseTime())
                .build();

            Mockito.when(meteringServiceMock.meterResponseTime(ArgumentMatchers.eq(givenAddress))).thenReturn(meterResponseMock);

            //WHEN
            MeterResponse meterResponse = grpcTestClient.singleThreadedBlockingCall(channel, givenAddress);

            //THEN
            Assertions.assertThat(meterResponse).isNotNull();
            Assertions.assertThat(meterResponse.getResponseCode()).isEqualTo(meterRequest.getResponseCode());
            Assertions.assertThat(meterResponse.getResponseTime()).isEqualTo(meterRequest.getResponseTime());
        });
    }

}
