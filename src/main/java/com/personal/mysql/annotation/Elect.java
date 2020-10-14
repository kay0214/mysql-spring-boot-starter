/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.annotation;

import com.personal.mysql.elect.AbstractDataSourceElector;
import com.personal.mysql.elect.DefaultDataSourceElector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sunpeikai
 * @version Elect, v0.1 2020/10/14 11:16
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Elect {
    String[] params() default {};
    Class<? extends AbstractDataSourceElector> elector() default DefaultDataSourceElector.class;
}
