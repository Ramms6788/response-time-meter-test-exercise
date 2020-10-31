/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package loopme.pojo;

import lombok.Data;

@Data
public class Response {
    private Integer responseCode;
    private Long responseTime;
}
