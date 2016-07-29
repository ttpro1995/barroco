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
package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author hkhoi
 */
public class Sandbox {

    static final String URL_STR = "https://docs.oracle.com/javase/tutorial/collections/interfaces/examples/dictionary.txt";

    public static void main(String[] args) throws MalformedURLException, IOException {
        URL url = new URL(URL_STR);
        InputStream inStream = getRequiredInputStream(url, 0, 1023);
        handleInputStream(inStream, "test_function");
        System.out.println("Mfofo");
    }

    private static InputStream getRequiredInputStream(URL url, int from, int to) 
            throws IOException {
        HttpURLConnection connection = 
                (HttpURLConnection) new URL(URL_STR).openConnection();
        
        String rangeOption = new StringBuilder("bytes=")
                .append(from)
                .append('-')
                .append(to).toString();
        
        
        
        connection.setRequestProperty("Range", rangeOption);    
        return connection.getInputStream();
    }
    
    private static void handleInputStream(InputStream inputStream, String name)
            throws FileNotFoundException, IOException {
        ReadableByteChannel channel = Channels.newChannel(inputStream);
        FileOutputStream outStream = new FileOutputStream(name);
        
        outStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
    }
}