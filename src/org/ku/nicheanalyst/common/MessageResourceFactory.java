/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Apr 10, 2012 12:45:27 PM
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

/**
 * @author Huijie Qiao
 *
 */
import java.util.HashMap;  
import java.util.Locale;  
import java.util.Map;  
import java.util.ResourceBundle;  
  
public class MessageResourceFactory {    
    private static MessageResourceFactory factory = new MessageResourceFactory();    
   //存放消息资源   
    private Map msgResourceMap = null;   
       
    
    private MessageResourceFactory() {    
        msgResourceMap = new HashMap();    
    }    
    
    public static MessageResourceFactory getInstance() {    
        return factory;    
    }    
    /**  
     * 创建消息资源  
     * @param name String 消息资源文件名  
     * @param locale Locale   
     * @return MessageResource  
     */    
    public MessageResource createMessageResource(String fileResName, Locale locale) {    
        Object msgResObj = msgResourceMap.get(fileResName);    
        if (msgResObj == null) {                 
            ResourceBundle rb = ResourceBundle.getBundle(fileResName, locale);    
            MessageResource msgRes = new MessageResource(rb);    
            msgResourceMap.put(fileResName, msgRes);    
            return msgRes;    
        } else {    
            return (MessageResource)msgResObj;    
        }    
    }    
}