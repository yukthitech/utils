/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package freemarker.ext.servlet;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * TemplateHashModel wrapper for a ServletContext attributes.
 */
public final class ServletContextHashModel implements TemplateHashModel {
    private final GenericServlet servlet;
    private final ServletContext servletctx;
    private final ObjectWrapper wrapper;

    public ServletContextHashModel(
        GenericServlet servlet, ObjectWrapper wrapper) {
        this.servlet = servlet;
        this.servletctx = servlet.getServletContext();
        this.wrapper = wrapper;
    }
    
    /**
     * @deprecated use 
     * {@link #ServletContextHashModel(GenericServlet, ObjectWrapper)} instead.
     */
    @Deprecated
    public ServletContextHashModel(
        ServletContext servletctx, ObjectWrapper wrapper) {
        this.servlet = null;
        this.servletctx = servletctx;
        this.wrapper = wrapper;
    }

    public TemplateModel get(String key) throws TemplateModelException {
        return wrapper.wrap(servletctx.getAttribute(key));
    }

    public boolean isEmpty() {
        return !servletctx.getAttributeNames().hasMoreElements();
    }
    
    /**
     * Returns the underlying servlet. Can return null if this object was
     * created using the deprecated constructor.
     */
    public GenericServlet getServlet() {
        return servlet;
    }
}
