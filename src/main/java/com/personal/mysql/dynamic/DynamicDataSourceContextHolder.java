/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.dynamic;

import com.personal.mysql.elect.AbstractDataSourceElector;
import com.personal.mysql.elect.DefaultDataSourceElector;
import com.personal.mysql.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author sunpeikai
 * @version DynamicDataSourceContextHolder, v0.1 2020/10/13 09:48
 * @description
 */
public class DynamicDataSourceContextHolder {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /**
     * 用于在切换数据源时保证不会被其他线程修改
     */
    private static final Lock lock = new ReentrantLock();

    /**
     * 目前使用的数据源
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 数据源选举算法
     * */
    public static Class<? extends AbstractDataSourceElector> elector;

    /**
     * 所有数据源列表
     */
    public static List<String> dataSourceKeys = new ArrayList<>();

    /**
     * 写库数据源
     */
    public static List<String> writeDataSourceKeys = new ArrayList<>();

    /**
     * 读库数据源
     */
    public static List<String> readDataSourceKeys = new ArrayList<>();

    /**
     * 切换至写库数据源
     * */
    public static void useWriteDataSource(Map<String, Object> params, Class<? extends AbstractDataSourceElector> customElector){
        lock.lock();
        try {
            int datasourceKeyIndex = elect(true, writeDataSourceKeys, params, customElector);
            CONTEXT_HOLDER.set(writeDataSourceKeys.get(datasourceKeyIndex));
            log.info("write data source switch [{}]", CONTEXT_HOLDER.get());
        } catch (Exception e) {
            log.error("Switch write datasource failed, error message is {}", e.getMessage());
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 切换至读库数据源
     * */
    public static void useReadDataSource(Map<String, Object> params, Class<? extends AbstractDataSourceElector> customElector){
        lock.lock();
        try {
            int datasourceKeyIndex = elect(false, readDataSourceKeys, params, customElector);
            CONTEXT_HOLDER.set(readDataSourceKeys.get(datasourceKeyIndex));
            log.info("read data source switch [{}]", CONTEXT_HOLDER.get());
        } catch (Exception e) {
            log.error("Switch read datasource failed, error message is {}", e.getMessage());
            useWriteDataSource(params, customElector);
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 选举
     * */
    private static int elect(boolean isWrite, List<String> dataSource, Map<String, Object> params, Class<? extends AbstractDataSourceElector> customElector){
        customElector = customElector == null ? elector : customElector;
        try{
            // 先将class作为spring管理的bean的形式执行
            return SpringUtils.getBean(customElector).elect(isWrite, dataSource, params);
        }catch (NoSuchBeanDefinitionException e){
            try{
                // 如果抛出异常,说明这个recordClass并不是spring管理的bean
                return customElector.newInstance().elect(isWrite, dataSource, params);
            }catch (InstantiationException | IllegalAccessException exception){
                log.error("elect fail => {}", exception.getMessage());
                return 0;
            }
        }
    }

    /**
     * 获取当前生效的数据源
     */
    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除数据源
     */
    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
}
