/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.actuator;

import com.personal.mysql.utils.MysqlUtil;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.util.Map;

/**
 * @author sunpeikai
 * @version MysqlActuatorEndpoint, v0.1 2020/10/13 15:17
 * @description
 */
@WebEndpoint(id = "mysql")
public class MysqlActuatorEndpoint {
    @ReadOperation
    public Map<String, Object> dingTalkActuator() {
        return MysqlUtil.healthInfo();
    }
}
