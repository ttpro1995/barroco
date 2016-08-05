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
import util.DisplayUtil;
import util.UnitUtil;

/**
 *
 * @author ar-khoi.hoang
 */
public class MultiLoadBar implements Runnable {

    private Master master;

    public MultiLoadBar(Master master) {
        this.master = master;
    }

    @Override
    public void run() {
        StringBuilder barBuilder = new StringBuilder();
        long pre = 0;

        while (master.stillAlive()) {
            if (!master.isMonoThread()) {
                float progress = master.progress();
                barBuilder.setLength(0);
                for (int i = 0; i < master.getSlaves().length; ++i) {
                    float percentage = master.getSlaves()[i].progress();
                    barBuilder.append(DisplayUtil.loadBar(percentage, i));

                }
                System.out.printf("\r[%s] -> Speed: %s          ",
                        barBuilder.toString(), master.speed(pre));
                pre = master.downloadedLength();

                try {
                    Thread.sleep(Const.REFRESH_TIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MultiLoadBar.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.printf("Downloaded: %s\t\tSpeed: %s\r", UnitUtil.displaySize(master.downloadedLength()), master.speed(pre));
            }
        }
    }
}
