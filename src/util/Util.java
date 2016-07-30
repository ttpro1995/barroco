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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * This class supports some utility static functions.
 *
 * @author hkhoi
 */
public class Util {

    private static final String PREFIX = ".";   // Make a file hidden
    private static final String POSTFIX = ".part";

    private final HttpURLConnection connection;
    private final String url;

    public Util(String url) throws MalformedURLException, IOException {
        this.url = url;
        connection
                = (HttpURLConnection) (new URL(url)).openConnection();
    }

    private long getContentLength() throws MalformedURLException, IOException {
        return Long.parseLong(connection.getHeaderField("Content-Length"));
    }

    public Info[] split(String filename, int parts)
            throws IOException {
        Info[] result = new Info[parts];

        long total = getContentLength();
        long partSize = total / parts;
        long current = 0;
        StringBuilder nameBuilder = new StringBuilder();
        
        for (int i = 0; i < result.length; ++i) {
            nameBuilder.setLength(0);   // Clear buffer
            nameBuilder
                    .append(PREFIX)
                    .append(filename)
                    .append(POSTFIX)
                    .append(i);

            result[i] = new Info(url, nameBuilder.toString(),
                    current, current + partSize);

            current += partSize;
        }

        result[result.length - 1].setEnd(total);
        
        return result;
    }

    public void merge(Info[] info) throws FileNotFoundException, IOException {
        File firstFile = new File(info[0].getName());
        
        String absPath = firstFile.getAbsolutePath();
        String fileName = firstFile.getName();
        
        String dirPath = absPath.substring(0, absPath.length() - fileName.length());
        
        String actualName = firstFile.getName();
        actualName = actualName.substring(PREFIX.length(), 
                actualName.length() - 1 - POSTFIX.length());
    
        String actualFullPath = dirPath + actualName;
        
        FileOutputStream outStream = null;
        outStream = new FileOutputStream(actualFullPath);
        WritableByteChannel writeChannel = Channels.newChannel(outStream);
        
        try {
            for (Info it : info) {
                File curFile = new File(it.getName());
                FileInputStream inStream = new FileInputStream(curFile);

                inStream.getChannel().transferTo(0, Long.MAX_VALUE, writeChannel);

                inStream.close();
                curFile.delete();
            }
        } finally {
            if (outStream != null) {
                outStream.close();
            }
        }
    }
    
    /**
     * Get the default name, will implement later
     * @return 
     */
    public String getContentDisposition() {
        return connection.getHeaderField("content-disposition");
    }
    
    /**
     * Unit test for size consistency
     */
}
