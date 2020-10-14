/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.exception;

/**
 * @author sunpeikai
 * @version ElectDefinedParamNotFoundException, v0.1 2020/10/14 16:43
 * @description
 */
public class ElectDefinedParamNotFoundException extends RuntimeException{
    public ElectDefinedParamNotFoundException(String name){
        super("Annotation @Elect defined param[" + name + "] was not found in the arguments, check your @Elect");
    }
}
