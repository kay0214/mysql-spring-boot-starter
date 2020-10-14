/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.elect;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author sunpeikai
 * @version DefaultDataSourceElector, v0.1 2020/10/13 10:22
 * @description
 */
public class DefaultDataSourceElector extends AbstractDataSourceElector{

    @Override
    protected int doWriteElect(List<String> writeDataSource, Map<String, Object> params) {
        return new Random().nextInt(100) % writeDataSource.size();
    }

    @Override
    protected int doReadElect(List<String> readDataSource, Map<String, Object> params) {
        return new Random().nextInt(100) % readDataSource.size();
    }
}
