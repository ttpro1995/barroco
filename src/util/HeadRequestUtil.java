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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class supports some utility static functions.
 *
 * @author hkhoi
 */
public class HeadRequestUtil {

    private final String url;
    private final HttpURLConnection headRequest;

    public enum Unit {
        BYTE, MEGABYTE;
    }

    public HeadRequestUtil(String url) throws MalformedURLException, IOException {
        this.url = url;
        headRequest = (HttpURLConnection) (new URL(url)).openConnection();
        headRequest.setRequestMethod("HEAD");
        headRequest.setRequestProperty("User-Agent", Constant.USER_AGENT);
    }

    public long getContentLength(Unit unit) throws MalformedURLException, IOException {
        String contentLength = headRequest.getHeaderField("Content-Length");
        if (contentLength == null || contentLength.isEmpty()) {
            System.out.println(
                    "INFO -- \tContent's size is unknown, download with 1 thread");
            return Long.MAX_VALUE;
        }
        long result = Long.parseLong(contentLength);
        switch (unit) {
            case MEGABYTE:
                result /= (1024 * 1024);
                break;
        }

        return result;
    }

    public Plan[] plan(String filename, int parts)
            throws IOException {
        StringBuilder nameBuilder = new StringBuilder();
        long total = getContentLength(Unit.BYTE);
        long partSize = Long.MAX_VALUE;
        long current = 0;

        if (total == Long.MAX_VALUE) {   // Undefined Content-Length
            partSize = total / parts;
            parts = 1;
        }

        Plan[] slavePlans = new Plan[parts];

        for (int i = 0; i < slavePlans.length; ++i) {
            nameBuilder.setLength(0);   // Clear buffer
            nameBuilder
                    .append(Constant.PREFIX)
                    .append(filename)
                    .append(Constant.POSTFIX)
                    .append(i);

            slavePlans[i] = new Plan(url, nameBuilder.toString(), i,
                    current, current + partSize);

            current += partSize;
        }

        slavePlans[slavePlans.length - 1].setEnd(total);

        return slavePlans;
    }

    public int getReponseCode() throws IOException {
        return headRequest.getResponseCode();
    }

    public boolean isOK() throws IOException {
        return getReponseCode() / 100 == 2;
    }
}
