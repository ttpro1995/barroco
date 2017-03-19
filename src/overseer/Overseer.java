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
import runnable.TrackableRunnable;
import util.UnitUtil;

/**
 *
 * @author ar-khoi.hoang
 */
public abstract class Overseer implements Runnable {
    
    TrackableRunnable target;

    public Overseer(TrackableRunnable target) {
        this.target = target;
    }
    
    protected float progress() {
        return (float) downloadedLength() / totalLength();
    }
    
    protected String speed(long pre) {
        long diff = downloadedLength() - pre;
        return UnitUtil.displaySize(1000 * diff / Const.REFRESH_TIME) + "/s";
    }
    protected abstract long downloadedLength();
    
    protected abstract long totalLength();
    
    protected static String loadBar(float percentage) {
        int loaded = (int) (percentage * Const.LOAD_BAR_LENGTH);
        int unloaded = Const.LOAD_BAR_LENGTH - loaded;
        StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i < loaded; ++i) {
            builder.append("=");
        }
        
        for (int i = 0; i < unloaded - 1; ++i) {
            builder.append('.');
        }
         
        return builder.toString();
    }
}
