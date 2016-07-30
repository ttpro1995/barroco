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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import util.Info;
import util.Util;

/**
 *
 * @author hkhoi
 */
public class Master implements Runnable {

    private final String filename;
    private final int connections;
    private final String urlString;

    public Master(String filename, int connections, String urlString) {
        if (filename == null || filename.isEmpty()) {
            filename = FilenameUtils.getName(urlString);
        }

        this.filename = filename;
        this.connections = connections;
        this.urlString = urlString;
    }

    @Override
    public void run() {
        try {
            long beginTime = System.currentTimeMillis();

            System.out.println("REPORT -- \tPreparing to download...");

            ExecutorService threadPool = Executors.newCachedThreadPool();
            Util util = new Util(urlString);

            System.out.println("REPORT -- \tCalculating file parts...");
            Info[] infoList = util.split(filename, connections);

            for (Info it : infoList) {
                Slave curSlave = new Slave(it);
                threadPool.submit(curSlave);
                threadPool.submit(new Tracker(curSlave));
            }

            System.out.println("\n---------------------DOWNLOAD PHASE----------------------\n");
            threadPool.shutdown();
            threadPool.awaitTermination(14, TimeUnit.DAYS);
            System.out.println("\n---------------------MERGE PHASE----------------------\n");

            util.merge(infoList);

            long finishTime = (System.currentTimeMillis() - beginTime) / 1000;

            float sizeMb = (float) util.getContentLength() / 1024 / 1024;

            System.out.printf("REPORT -- \tDownload finished in %d seconds\n",
                    finishTime);
            System.out.printf("REPORT -- \tSize(MB) = %.2f\n", sizeMb);
        } catch (InterruptedException ex) {
            Logger.getLogger(Master.class.getName()).log(Level.SEVERE,
                    "Takes more than 2 weeks to download, get a life!", ex);
        } catch (IOException ex) {
            Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
