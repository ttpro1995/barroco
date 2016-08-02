/*
 * The MIT License
 *
 * Copyright 2016 hkhoi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An instance of this class provides necessary information to class Downloader
 * @author hkhoi
 */
public class Plan {

    private final HttpURLConnection connection;
    private final long startByte;
    private long endByte;
    private final String fileAbsPath;
    private final int id;
    
    public Plan(String url, String name, int id,
            long start, long end) throws MalformedURLException, IOException {
        this.id = id;
        this.startByte = start;
        this.endByte = end;
        this.fileAbsPath = name;
        this.connection =
                (HttpURLConnection) (new URL(url)).openConnection();
    }

    public long bytes2Download() {
        return endByte - startByte;
    }
    
    public InputStream getInputStream() {
        return null;
    }
    
    public long getStartByte() {
        return startByte;
    }

    public long getEndByte() {
        return endByte;
    }

    public String getFileAbsPath() {
        return fileAbsPath;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setEnd(long end) {
        this.endByte = end;
    }

    public int getId() {
        return id;
    }
}