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
import runnable.Slave;
import runnable.TrackableRunnable;

/**
 *
 * @author ar-khoi.hoang
 */
public class ConsoleSlaveOverseer extends Overseer {

    private final Slave slave;

    public ConsoleSlaveOverseer(TrackableRunnable target) {
        super(target);
        slave = (Slave) target;
    }

    @Override
    public long downloadedLength() {
        return slave.getFile().length();
    }

    @Override
    public long totalLength() {
        return slave.getPlan().total2Download();
    }

    @Override
    public void run() {
        while (slave.stillAlive()) {
            System.out.printf("Thread %d: %.2f%%\n",
                    slave.getPlan().getId(), progress() * 100f);
            try {
                Thread.sleep(Const.REFRESH_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConsoleSlaveOverseer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.printf("Thread %d is finished!\n", slave.getPlan().getId());
    }
}
