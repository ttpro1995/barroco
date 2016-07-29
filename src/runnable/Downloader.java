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
package runnable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hkhoi
 */
public class Downloader implements Runnable {

    private final String urlString;
    private final long startByte;
    private final long endByte;
    private final String filename;

    public Downloader(String urlString, long startByte,
            long endByte, String filename) {
        this.urlString = urlString;
        this.startByte = startByte;
        this.endByte = endByte;
        this.filename = filename;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        try {
            inputStream = getRequiredInputStream(urlString, startByte, endByte);
            handleInputStream(inputStream, filename);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName())
                    .log(Level.SEVERE, null, ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Downloader.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private InputStream getRequiredInputStream(String url, long from, long to)
            throws IOException {
        HttpURLConnection connection;
        connection = (HttpURLConnection) new URL(url).openConnection();

        String rangeOption = new StringBuilder("bytes=")
                .append(from)
                .append('-')
                .append(to).toString();

        connection.setRequestProperty("Range", rangeOption);
        return connection.getInputStream();
    }

    private static void handleInputStream(InputStream inputStream, String name)
            throws FileNotFoundException, IOException {
        FileOutputStream outStream = null;
        try {
            ReadableByteChannel channel = Channels.newChannel(inputStream);
            outStream = new FileOutputStream(name);

            outStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
        } finally {
            if (outStream != null) {
                outStream.close();
            }
        }
    }
}
