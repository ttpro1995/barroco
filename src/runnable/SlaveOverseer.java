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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hkhoi
 */
public class SlaveOverseer implements Runnable {

    private static final int REFRESH_TIME = 750;

    private final Slave slave;
    private final File file;

    public SlaveOverseer(Slave slave) {
        this.file = new File(slave.getFilename());
        this.slave = slave;
    }

    @Override
    public void run() {
        while (slave.isUp()) {
            float percentage = 
                    (float) file.length() / slave.getTotal() * 100f;
            System.out.printf("DOWNLOADING -- \tThread %d: %.2f%%\n",
                    slave.getId(),
                    percentage);
            try {
                Thread.sleep(REFRESH_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(SlaveOverseer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.printf("REPORT ------------------- \t\tThread %d finished!\n", slave.getId());
    }
}
