/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.elect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author sunpeikai
 * @version AbstractDataSourceElector, v0.1 2020/10/13 10:18
 * @description
 */
public abstract class AbstractDataSourceElector {

    private static final Logger log = LoggerFactory.getLogger(AbstractDataSourceElector.class);

    public int elect(boolean isWrite, List<String> dataSource, Map<String, Object> params){
        int index;
        if(isWrite){
            index = this.doWriteElect(dataSource, params);
        }else{
            index = this.doReadElect(dataSource, params);
        }
        if(index >= dataSource.size()){
            // 索引 >= 数据源数量
            log.error("elect index is [{}], the first dataSource enable", index);
            return 0;
        }else{
            return index;
        }
    }

    /**
     * 写库选举
     * */
    protected abstract int doWriteElect(List<String> writeDataSource, Map<String, Object> params);

    /**
     * 读库选举
     * */
    protected abstract int doReadElect(List<String> readDataSource, Map<String, Object> params);
}
