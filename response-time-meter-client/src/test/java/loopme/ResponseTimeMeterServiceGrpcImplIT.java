/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme;

import com.google.common.util.concurrent.ListenableFuture;
import loopme.pojo.Response;
import loopme.service.ResponseTimeMeterGrpcService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles(ResponseTimeMeterServiceGrpcImplIT.TEST_PROFILE)
public class ResponseTimeMeterServiceGrpcImplIT {

    private Logger logger = LoggerFactory.getLogger(ResponseTimeMeterServiceGrpcImplIT.class);

    public static final String TEST_PROFILE = "test";

    @Autowired
    private ResponseTimeMeterGrpcService responseTimeMeterGrpcService;

    @Test
    public void singleThreadedBlockingCallsTest(){
         TestUtil.testData().forEach((givenAddress, meterResponse) -> {
            //GIVEN
            MeterRequest meterRequest = MeterRequest.newBuilder().setAddress(givenAddress).build();
            //WHEN
            Response meteredResponse =
                Response.fromMeterResponse(responseTimeMeterGrpcService.meterResponseTime(meterRequest));

            //THEN
            assertThat(meteredResponse).isNotNull();
            assertThat(meteredResponse.getResponseCode()).isGreaterThan(0);
            assertThat(meteredResponse.getResponseTime()).isGreaterThan(0);
        });
    }

    @Test
    public void multiThreadedBlockingCallsTest(){
        //GIVEN
        List<String> addressList = new ArrayList<>(TestUtil.testData().keySet());

        int threadCount = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<MeterResponse>> futureList = new ArrayList<>();

        for(int i = 0; i < threadCount; i++){
            String givenAddress = addressList.get(i);

            MeterRequest meterRequest = MeterRequest.newBuilder().setAddress(givenAddress).build();

            Callable<MeterResponse> task = () -> responseTimeMeterGrpcService.meterResponseTime(meterRequest);
            Future<MeterResponse> future = executorService.submit(task);
            futureList.add(future);
        }

        //WHEN
        List<MeterResponse> responseList = new ArrayList<>();
        futureList.forEach(future -> {
            try {
                responseList.add(future.get());
            } catch (InterruptedException e) {
                logger.error("Task was interrupted. Reason: " + e.getMessage());
            } catch (ExecutionException e) {
                logger.error("Task execution exception. Reason: " + e.getMessage());
            }
        });

        MeterResponse testMeterResponse = MeterResponse.newBuilder().setResponseCode(-1).setResponseTime(-1L).build();
        responseList.add(testMeterResponse);
        //THEN
        assertThat(responseList).isNotNull().hasSize(threadCount + 1);
        for(int i = 0; i < threadCount; i++){
            assertThat(responseList.get(i).getResponseCode()).isGreaterThan(0);
            assertThat(responseList.get(i).getResponseTime()).isGreaterThan(0);
        }

        assertThat(responseList.get(threadCount).getResponseCode()).isEqualTo(-1);
        assertThat(responseList.get(threadCount).getResponseTime()).isEqualTo(-1L);
    }

    @Test
    public void singleThreadedNonBlockingCallsTest(){
        TestUtil.testData().forEach((givenAddress, meterResponse) -> {
            //GIVEN
            MeterRequest givenRequest = MeterRequest.newBuilder().setAddress(givenAddress).build();
            //WHEN
            ListenableFuture<MeterResponse> response = responseTimeMeterGrpcService.meterResponseTimeAsync(givenRequest);
            //THEN
            try {
                MeterResponse meteredResponse = response.get();
                assertThat(meteredResponse).isNotNull();
                assertThat(meteredResponse.getResponseCode()).isGreaterThan(0);
                assertThat(meteredResponse.getResponseTime()).isGreaterThan(0);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void multiThreadedNonBlockingCallsTest(){
        //GIVEN
        List<String> addressList = new ArrayList<>(TestUtil.testData().keySet());
        List<MeterRequest> givenRequestList = new ArrayList<>();

        for(int i = 0; i < addressList.size(); i++){
            MeterRequest givenRequest = MeterRequest.newBuilder().setAddress(addressList.get(0)).build();
            givenRequestList.add(givenRequest);
        }

        //WHEN
        List<ListenableFuture<MeterResponse>> listenableFutureList = new ArrayList<>();
        for(int i = 0; i < givenRequestList.size(); i++){
            ListenableFuture<MeterResponse> listenableFuture =
                responseTimeMeterGrpcService.meterResponseTimeAsync(givenRequestList.get(i));
            listenableFutureList.add(listenableFuture);
        }

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //THEN
        try {
            for(int i = 0; i < listenableFutureList.size(); i++){
                MeterResponse meteredResponse = listenableFutureList.get(i).get();
                assertThat(meteredResponse).isNotNull();
                assertThat(meteredResponse.getResponseCode()).isGreaterThan(0);
                assertThat(meteredResponse.getResponseTime()).isGreaterThan(0);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
