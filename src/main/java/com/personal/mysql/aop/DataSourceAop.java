/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.aop;


import com.alibaba.fastjson.JSONObject;
import com.personal.mysql.annotation.Elect;
import com.personal.mysql.dynamic.DynamicDataSourceContextHolder;
import com.personal.mysql.elect.AbstractDataSourceElector;
import com.personal.mysql.elect.DefaultDataSourceElector;
import com.personal.mysql.exception.ElectDefinedParamNotFoundException;
import com.personal.mysql.properties.MysqlProperties;
import com.personal.mysql.utils.SpringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sunpeikai
 * @version DataSourceAop, v0.1 2020/10/13 10:13
 * @description
 */
public class DataSourceAop implements MethodInterceptor {
    // 方法参数缓存
    private static final Map<String, Integer> parameterConfigs = new HashMap<>();
    private static final String DOT = ".";
    private static final String SPLIT = "\\.";

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<? extends AbstractDataSourceElector> customElector = null;
        Method method = methodInvocation.getMethod();
        // 传给选举方法的入参map
        Map<String, Object> paramMap = new HashMap<>();
        Elect elect = method.getAnnotation(Elect.class);
        if(elect != null && elect.params().length > 0){
            // 初始化方法的入参
            initMethodParameters(method);
            // 实际传入的参数值
            Object[] args = methodInvocation.getArguments();
            for (String param : elect.params()) {
                // 去掉前后空格
                param = param.trim();
                if(!param.contains(DOT)){
                    // 不包含[.]说明只有一级
                    paramMap.put(param, args[getParameterIndex(method,param)]);
                }else{
                    // 缓存的入参只取第一级
                    String parameterName = param.substring(0,param.indexOf(DOT));
                    // 包含[.]说明有多级
                    JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(args[getParameterIndex(method,parameterName)]));
                    String[] levels = param.split(SPLIT);
                    int size = levels.length;
                    for(int i=1; i<size; i++){
                        String level = levels[i];
                        // 参数检查
                        if(!object.containsKey(level)){
                            throw new ElectDefinedParamNotFoundException(level);
                        }
                        if(i == size - 1){
                            paramMap.put(level, object.get(level));
                        }else{
                            object = object.getJSONObject(level);
                        }
                    }
                }
            }
            // 不是默认的DefaultDataSourceElector才去赋值
            if(elect.elector() != DefaultDataSourceElector.class){
                customElector = elect.elector();
            }
        }
        // 切换数据源
        if(isQueryMethod(methodInvocation.getMethod().getName())){
            DynamicDataSourceContextHolder.useReadDataSource(paramMap, customElector);
        }else{
            DynamicDataSourceContextHolder.useWriteDataSource(paramMap, customElector);
        }
        Object object = null;
        try {
            object = methodInvocation.proceed();
        }catch (Throwable throwable){
            throw throwable;
        }finally {
            DynamicDataSourceContextHolder.clearDataSource();
        }
        return object;
    }

    private void initMethodParameters(Method method){
        int index = 0;
        for (Parameter parameter : method.getParameters()) {
            String key = method.getDeclaringClass().getName() + method.getName() + parameter.getName();
            if(!parameterConfigs.containsKey(key)){
                parameterConfigs.put(key, index++);
            }
        }
    }

    private Integer getParameterIndex(Method method, String parameterName){
        String key = method.getDeclaringClass().getName() + method.getName() + parameterName;
        if(parameterConfigs.containsKey(key)){
            return parameterConfigs.get(key);
        }
        throw new ElectDefinedParamNotFoundException(parameterName);
    }

    private Boolean isQueryMethod(String methodName) {
        for (String prefix : SpringUtils.getBean(MysqlProperties.class).getQueryMethodPrefixes()) {
            if (methodName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
