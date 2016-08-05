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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FilenameUtils;
import util.ByteStreamUtil;
import config.Const;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Master implements TrackableRunnable {

    private final String fileAbsPath;
    private boolean alive = false;
    private Slave[] slaves;
    private Plan[] plans;

    public Master(String fileAbsPath, int connections, String urlString)
            throws IOException, Exception {
        if (fileAbsPath == null || fileAbsPath.isEmpty()) {
            fileAbsPath = FilenameUtils.getName(urlString);
            if (fileAbsPath == null || fileAbsPath.isEmpty()) {
                fileAbsPath = Const.DEFAULT_NAME;
            }
        }

        fileAbsPath = NameUtil.makeUniqueName(fileAbsPath);

        this.fileAbsPath = fileAbsPath;

        HeadRequestHandler headRequestUtil = new HeadRequestHandler(urlString);
        int reponseCode = headRequestUtil.getReponseCode();
        if (!headRequestUtil.isOK()) {
            throw new Exception("Not a success reponse=" + reponseCode);
        }
        plans = headRequestUtil.plan(fileAbsPath, connections);
        slaves = new Slave[connections];
        for (int i = 0; i < plans.length; ++i) {
            slaves[i] = new Slave(plans[i]);
        }
    }

    @Override
    public void run() {
        try {
            alive = true;
            Thread overseer = new Thread(new overseer.ConsoleMasterOverseer(this));
            overseer.start();
            
            long beginTime = System.currentTimeMillis();

            ExecutorService threadPool = Executors.newCachedThreadPool();

            for (Slave slave : slaves) {
                threadPool.submit(slave);
//                threadPool.submit(new ConsoleSlaveOverseer(slave));
            }

            threadPool.shutdown();
            threadPool.awaitTermination(14, TimeUnit.DAYS);

            ByteStreamUtil.merge(plans, fileAbsPath);

            long size = (new File(fileAbsPath)).length();
            long finishTime = (System.currentTimeMillis() - beginTime);

            System.out.println(UnitUtil.displaySize(size));
            System.out.println(UnitUtil.displayTime(finishTime));
            alive = false;

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            alive = false;
        }
    }

    public Slave[] getSlaves() {
        return slaves;
    }

    @Override
    public boolean stillAlive() {
        return alive;
    }
}
