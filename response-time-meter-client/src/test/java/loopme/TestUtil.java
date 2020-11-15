/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestUtil {

    private static Map<String, MeterResponse> testData = new LinkedHashMap<>();

    public static Map<String, MeterResponse> testData(){
        testData.put("google.com", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(100).build());
        testData.put("http://google.com", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(101).build());
        testData.put("https://google.com", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(102).build());
        testData.put("grpc.io", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(103).build());
        testData.put("http://grpc.io", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(104).build());
        testData.put("https://grpc.io", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(105).build());
        testData.put("https://www.linkedin.com/feed/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(106).build());
        testData.put("www.linkedin.com", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(107).build());
        testData.put("https://github.com/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(108).build());
        testData.put("github.com/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(109).build());
        testData.put("https://mail.google.com/mail/u/0/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(110).build());
        testData.put("mail.google.com", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(111).build());
        testData.put("https://spring.io/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(112).build());
        testData.put("http://spring.io/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(113).build());
        testData.put("https://hibernate.org/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(114).build());
        testData.put("http://hibernate.org/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(115).build());
        testData.put("hibernate.org", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(116).build());
        testData.put("hibernate.org/", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(117).build());
        testData.put("https://rammstein.com", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(118).build());
        testData.put("rammstein.de", MeterResponse.newBuilder().setResponseCode(200).setResponseTime(119).build());

        return testData;
    }
}
