/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.actuator;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author sunpeikai
 * @version MysqlActuatorEndpointConfiguration, v0.1 2020/10/13 15:18
 * @description
 */
@ConditionalOnClass({MysqlActuatorEndpoint.class})
public class MysqlActuatorEndpointConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public MysqlActuatorEndpoint mysqlActuatorEndpoint(){
        return new MysqlActuatorEndpoint();
    }
}
