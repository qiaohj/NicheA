/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Apr 10, 2012 12:44:41 PM
 * Author:   Huijie Qiao
 *
 ******************************************************************************
 * Copyright (c) 2012, Huijie Qiao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************************************************/


package org.ku.nicheanalyst.common;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Huijie Qiao
 *
 */
public class MessageResource {  
    private ResourceBundle rb; //资源绑定    
    /**  
     * 构造器  
     * @param props Properties 资源绑定  
     */  
    public MessageResource(ResourceBundle rb) {  
        this.rb = rb;  
    }  
    /**  
     * 得到消息字符  
     * @param key String 键  
     * @return String  
     */  
    public String getString(String key) {  
        try {  
            return rb.getString(key);  
        } catch (MissingResourceException ex) {  
            return "undefined";  
        }  
    }  
    /**  
     * 得到消息资源  
     * @param key String 键  
     * @param args Object[] 可变消息  
     * @return String  
     */  
    public String getString(String key, Object[] args) {  
        try {  
            String temp = rb.getString(key);  
            MessageFormat mFormat = new MessageFormat(temp);  
            return mFormat.format(temp, args);  
        } catch (MissingResourceException ex) {  
            return "undefined";  
        }  
    }  
}  
