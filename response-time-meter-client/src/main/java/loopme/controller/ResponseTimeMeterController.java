/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme.controller;

import loopme.MeterRequest;
import loopme.service.ResponseTimeMeterGrpcService;
import loopme.pojo.Request;
import loopme.pojo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/getByAddress")
public class ResponseTimeMeterController {

    @Autowired
    private ResponseTimeMeterGrpcService responseTimeMeterGrpcService;

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getResponseTimeForResourceAddress(@RequestBody Request request){
        MeterRequest meterRequest = MeterRequest.newBuilder()
            .setAddress(request.getAddress())
            .build();

        return Response.fromMeterResponse(responseTimeMeterGrpcService.meterResponseTime(meterRequest));
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity errorHandler(Exception ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
