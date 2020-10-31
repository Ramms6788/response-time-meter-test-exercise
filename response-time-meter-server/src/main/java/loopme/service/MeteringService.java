/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme.service;

import loopme.MeterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class MeteringService {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    public static final Pattern ADDRESS_REGEX_PATTERN = Pattern.compile("^(http://|https://|)(.+?)\\.\\w{1,3}(.+?)$");

    @Autowired
    private RestTemplate restTemplate;

    public MeterResponse meterResponseTime(String resourceAddress){
        if(Objects.isNull(resourceAddress) || !ADDRESS_REGEX_PATTERN.matcher(resourceAddress).matches()){
            throw new IllegalArgumentException("Resource address incorrect format: " + resourceAddress);
        }

        if(!resourceAddress.startsWith(HTTP_PREFIX) && !resourceAddress.startsWith(HTTPS_PREFIX)){
            resourceAddress = HTTP_PREFIX + resourceAddress;
        }

        Date start = new Date();

        ResponseEntity<String> responseEntity = restTemplate.exchange(resourceAddress, HttpMethod.GET, null, String.class);

        Date end = new Date();

        return MeterResponse.newBuilder()
            .setResponseCode(responseEntity.getStatusCodeValue())
            .setResponseTime(end.getTime() - start.getTime())
            .build();
    }
}
