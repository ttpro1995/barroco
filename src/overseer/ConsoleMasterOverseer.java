/*
 * The MIT License
 *
 * Copyright 2016 ar-khoi.hoang.
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
package overseer;

import config.Const;
import java.util.logging.Level;
import java.util.logging.Logger;
import runnable.Master;
import runnable.Slave;
import runnable.TrackableRunnable;

/**
 *
 * @author ar-khoi.hoang
 */
public class ConsoleMasterOverseer extends Overseer {

    private final Master master;
    private long total = -1;

    public ConsoleMasterOverseer(TrackableRunnable target) {
        super(target);
        master = (Master) target;
    }

    @Override
    protected long downloadedLength() {
        long sum = 0;

        for (Slave slave : master.getSlaves()) {
            sum += slave.getFile().length();
        }

        return sum;
    }

    @Override
    protected long totalLength() {
        if (total < 0) {
            total = 0;
            for (Slave slave : master.getSlaves()) {
                total += slave.getPlan().total2Download();
            }
        }
        return total;
    }

    @Override
    public void run() {
        long pre = 0;

        while (master.stillAlive()) {
            float progress = progress();
            System.out.printf("Downloading: %.2f%%\t\t%s\t\tSpeed: %s\r", progress * 100f, loadBar(progress), speed(pre));
            pre = downloadedLength();
            try {
                Thread.sleep(Const.REFRESH_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConsoleMasterOverseer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Finished!");
    }

}
