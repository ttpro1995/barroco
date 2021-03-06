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

import interfaces.SlaveOverseer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ByteStreamUtil;
import util.Plan;

/**
 * Input: Content URL, start byte, end byte and file name Output: A portion of
 * file from start to end byte
 *
 * @author hkhoi
 */
public class Slave implements TrackableRunnable {

    private boolean alive;
    private final Plan plan;
    private Thread overseerThread = null;
    private final File file;

    public Slave(Plan plan) {
        this.plan = plan;
        this.alive = false;
        file = new File(plan.getFileAbsPath());
    }

    public void addOverseer(SlaveOverseer overseer) {
        if (overseer != null) {
            overseerThread = new Thread(overseer);
            overseer.setSlave(this);
        }
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        try {
            alive = true;
            inputStream = plan.getInputStream();
            if (overseerThread != null) {
                overseerThread.start();
            }
            ByteStreamUtil.stream2File(inputStream, plan.getFileAbsPath(), false);
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
            alive = false;
        }
    }

    public Plan getPlan() {
        return plan;
    }

    @Override
    public boolean stillAlive() {
        return alive;
    }

    public File getFile() {
        return file;
    }
    
    public long downloadedLength() {
        return this.getFile().length();
    }
    
    public long totalLength() {
        return this.getPlan().total2Download();
    }
    
    public float progress() {
        return (float) downloadedLength() / totalLength();
    }
}
