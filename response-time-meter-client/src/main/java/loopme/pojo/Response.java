/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import loopme.MeterResponse;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Response {
    private Integer responseCode;
    private Long responseTime;

    public static Response fromMeterResponse(MeterResponse meterResponse){
        return new Response(meterResponse.getResponseCode(), meterResponse.getResponseTime());
    }
}
