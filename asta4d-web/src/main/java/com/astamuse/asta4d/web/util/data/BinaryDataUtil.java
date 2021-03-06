/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.web.util.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContext;

public class BinaryDataUtil {

    public final static InputStream retrieveInputStreamByPath(ServletContext servletContext, ClassLoader classLoader, String path) {
        if (path.startsWith("file:")) {
            try {
                return new URL(path).openStream();
            } catch (FileNotFoundException e) {
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (path.startsWith("classpath:")) {
            String cls = path.substring("classpath:".length());
            if (cls.startsWith("/")) {
                cls = cls.substring(1);
            }
            return classLoader.getResourceAsStream(cls);
        } else {
            return servletContext.getResourceAsStream(path);
        }
    }

    /**
     * 
     * @param servletContext
     * @param classLoader
     * @param path
     * @return 0 when something is wrong or the actual last modified time of the resource for given path
     */
    public final static long retrieveLastModifiedByPath(ServletContext servletContext, ClassLoader classLoader, String path) {
        if (path.startsWith("file:")) {
            try {
                URL url = new URL(path);
                return retriveLastModifiedFromURL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if (path.startsWith("classpath:")) {
            String cls = path.substring("classpath:".length());
            if (cls.startsWith("/")) {
                cls = cls.substring(1);
            }
            return retriveLastModifiedFromURL(classLoader.getResource(cls));
        } else {
            try {
                return retriveLastModifiedFromURL(servletContext.getResource(path));
            } catch (MalformedURLException e) {
                return 0L;
            }
        }
    }

    private final static long retriveLastModifiedFromURL(URL url) {
        try {
            URLConnection con = url.openConnection();
            try {
                return con.getLastModified();
            } catch (Exception ex) {
                return 0L;
            } finally {
                con.getInputStream().close();
            }
        } catch (IOException e) {
            return 0L;
        }
    }

}
