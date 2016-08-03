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

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import util.ByteStreamUtil;
import util.Constant;
import util.HeadRequestHandler;
import util.NameUtil;
import util.Plan;
import util.UnitUtil;

/**
 * Master takes URL, filename and number of connections to divide tasks for
 * slaves
 *
 * @author hkhoi
 */
public class Master implements Runnable {

    private final String fileAbsPath;
    private final int connections;
    private final String urlString;

    public Master(String fileAbsPath, int connections, String urlString) {
        if (fileAbsPath == null || fileAbsPath.isEmpty()) {
            fileAbsPath = FilenameUtils.getName(urlString);
            if (fileAbsPath == null || fileAbsPath.isEmpty()) {
                fileAbsPath = Constant.DEFAULT_NAME;
            }
        }

        fileAbsPath = NameUtil.makeUniqueName(fileAbsPath);
        System.out.println(">>DEBUG: " + fileAbsPath);

        this.fileAbsPath = fileAbsPath;
        this.connections = connections;
        this.urlString = urlString;
    }

    @Override
    public void run() {
        try {
            long beginTime = System.currentTimeMillis();

            System.out.println("REPORT -- \tPreparing to download...");

            ExecutorService threadPool = Executors.newCachedThreadPool();
            HeadRequestHandler headRequestUtil = new HeadRequestHandler(urlString);

            int reponseCode = headRequestUtil.getReponseCode();

            if (!headRequestUtil.isOK()) {
                System.out.println("INFO -- \tResponse code: " + reponseCode);
                System.out.println(
                        "ERROR -- \tReponse = Not a successful response, terminating...");
                return;
            }

            System.out.println("REPORT -- \tSuccessful request");
            System.out.println("REPORT -- \tCalculating file parts...");
            Plan[] plans = headRequestUtil.plan(fileAbsPath, connections);
            System.out.println(
                    "\n-----------------DOWNLOADING PHASE-------------------\n");

            for (Plan it : plans) {
                Slave curSlave = new Slave(it);
                threadPool.submit(curSlave);
                threadPool.submit(new SlaveOverseer(curSlave));
            }

            threadPool.shutdown();
            threadPool.awaitTermination(14, TimeUnit.DAYS);

            System.out.println(
                    "\n--------------------MERGING PHASE--------------------\n");
            ByteStreamUtil.merge(plans, fileAbsPath);

            long size = (new File(fileAbsPath)).length();
            long finishTime = (System.currentTimeMillis() - beginTime) / 1000;

            System.out.println(UnitUtil.displaySize(size));
            System.out.println(UnitUtil.displayTime(finishTime));

        } catch (InterruptedException | SocketTimeoutException ex) {
            Logger.getLogger(Master.class.getName()).log(Level.SEVERE,
                    "ABORT -- \t\tTime out!!", ex);
        } catch (IOException ex) {
            Logger.getLogger(Master.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
