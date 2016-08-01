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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ByteStreamUtil;
import util.Constant;
import util.Plan;

/**
 * Input: Content URL, start byte, end byte and file name Output: A portion of
 * file from start to end byte
 *
 * @author hkhoi
 */
public class Slave implements Runnable {

    private final HttpURLConnection connection;
    private final long startByte;
    private final long endByte;
    private final String filename;
    private volatile long transfered = 0;
    private final long total;
    private boolean up;
    private final int id;

    public Slave(Plan downloadInfo) {
        this.up = true;
        this.filename = downloadInfo.getName();
        this.connection = downloadInfo.getConnection();
        this.connection.setRequestProperty("User-Agent", Constant.USER_AGENT);
        this.startByte = downloadInfo.getStart();
        this.endByte = downloadInfo.getEnd();
        this.total = this.endByte - this.startByte;
        this.id = downloadInfo.getId();
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        try {
            inputStream = getRequiredInputStream();
            ByteStreamUtil byteStreamUtil = new ByteStreamUtil();
            byteStreamUtil.stream2File(inputStream, filename);
        } catch (IOException ex) {
            Logger.getLogger(Slave.class.getName())
                    .log(Level.SEVERE, null, ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Slave.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
            up = false;
        }
    }

    /**
     * Returns the InputStream from start to end byte of the file
     *
     * @param url
     * @param from
     * @param to
     * @return
     * @throws IOException
     */
    private InputStream getRequiredInputStream()
            throws IOException {
        String rangeOption = new StringBuilder("bytes=")
                .append(startByte)
                .append('-')
                .append(endByte - 1).toString();

        connection.setRequestProperty("Range", rangeOption);
        connection.setRequestProperty("User-Agent", Constant.USER_AGENT);
        return connection.getInputStream();
    }

    public int getId() {
        return id;
    }

    public long getTransfered() {
        return transfered;
    }

    public long getTotal() {
        return total;
    }

    public boolean isUp() {
        return up;
    }

    public String getFilename() {
        return filename;
    }
}
