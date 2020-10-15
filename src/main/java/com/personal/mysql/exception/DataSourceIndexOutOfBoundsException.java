/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.exception;

/**
 * @author sunpeikai
 * @version DataSourceIndexOutOfBoundsException, v0.1 2020/10/15 10:02
 * @description
 */
public class DataSourceIndexOutOfBoundsException extends IndexOutOfBoundsException{
    public DataSourceIndexOutOfBoundsException(String name) {
        super("elect dataSource index out of bounds, please check your class [" + name + "]");
    }
}
