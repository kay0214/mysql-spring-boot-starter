/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.utils;

import com.personal.mysql.dynamic.DataSourceBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sunpeikai
 * @version MysqlUtil, v0.1 2020/10/13 15:19
 * @description
 */
public class MysqlUtil {
    public static Map<String, Object> healthInfo(){
        Map<String, Object> result = new HashMap<>();
        Map<String, DataSourceBean> dataSourceBeans = SpringUtils.getBeanOfType(DataSourceBean.class);
        int size = 0;
        Map<String, Object> dataSourcesMap = new HashMap<>();
        for (Map.Entry<String, DataSourceBean> entry : dataSourceBeans.entrySet()) {
            // 读写库信息
            String key = entry.getKey();
            DataSourceBean dataSourceBean = entry.getValue();
            size += dataSourceBean.getSize();
            List<Map<String, Object>> dataSourceList = new ArrayList<>();
            for (DataSourceBean.DataSources dataSources : dataSourceBean.getDataSources()) {
                // dataSource信息
                Map<String, Object> dataSourceMap = new HashMap<>();
                dataSourceMap.put("name", dataSources.getName());
                dataSourceMap.put("isMaster", dataSources.isMaster());
                dataSourceList.add(dataSourceMap);
            }
            dataSourcesMap.put(key, dataSourceList);
        }
        result.put("size",size);
        result.put("dataSources",dataSourcesMap);
        return result;
    }
}
