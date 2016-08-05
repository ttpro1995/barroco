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
import java.util.logging.Level;
import java.util.logging.Logger;
import config.Const;
import util.Plan;

/**
 *
 * @author hkhoi
 */
public class ConsoleSlaveOverseer  extends SlaveOverseer{

    @Override
    public void run() {
        Plan plan = slave.getPlan();
        long total = plan.bytes2Download();
        int id = plan.getId();

        while (slave.isUp()) {
            float percentage
                    = (float) file.length() / total * 100f;
            System.out.printf("DOWNLOADING -- \tThread %d: %.2f%%\n",
                    id,
                    percentage);
            try {
                Thread.sleep(Const.REFRESH_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConsoleSlaveOverseer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.printf("REPORT ------------------- \t\tThread %d finished!\n", id);
    }
}
