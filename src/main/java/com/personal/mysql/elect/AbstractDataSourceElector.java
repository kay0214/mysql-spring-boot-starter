/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.elect;

import com.personal.mysql.exception.DataSourceIndexOutOfBoundsException;

import java.util.List;
import java.util.Map;

/**
 * @author sunpeikai
 * @version AbstractDataSourceElector, v0.1 2020/10/13 10:18
 * @description
 */
public abstract class AbstractDataSourceElector {

    public int elect(boolean isWrite, List<String> dataSource, Map<String, Object> params){
        int size = dataSource.size();
        int index;
        if(isWrite){
            index = this.doWriteElect(dataSource, params);
        }else{
            index = this.doReadElect(dataSource, params);
        }
        if(index >= size){
            // 索引 >= 数据源数量
            throw new DataSourceIndexOutOfBoundsException(this.getClass().getName());
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
