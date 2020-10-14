/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.actuator;

import com.personal.mysql.dynamic.DataSourceBean;
import com.personal.mysql.utils.MysqlUtil;
import com.personal.mysql.utils.SpringUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * @author sunpeikai
 * @version MysqlHealthIndicator, v0.1 2020/10/13 15:18
 * @description
 */
@Component("mysql")
public class MysqlHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        if(SpringUtils.getBean(DataSourceBean.class).getSize() > 0){
            // 把redis系统信息放入
            MysqlUtil.healthInfo().forEach(builder::withDetail);
            return builder.up().build();
        }else{
            return builder.down().build();
        }
    }
}
