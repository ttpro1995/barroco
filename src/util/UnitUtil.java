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
package util;

/**
 *
 * @author hkhoi
 */
public class UnitUtil {

    private static final String[] TIME_UNIT
            = {"miliseconds", "second", "minutes", "hours"};
    private static final String[] SIZE_UNIT
            = {"B", "KB", "MB", "GB"};

    public static String displayTime(long time) {
        // TODO: Implement UnitUtil.displayTime(int second);
        System.out.println(">>DEBUG: Raw:" + time);
        int unit = 0;
        if (time >= 1000) {
            time /= 1000;
            ++unit;
            if (time >= 60) {
                time /= 60;
                ++unit;
                if (time >= 60) {
                    time /= 60;
                    ++unit;
                }
            }
        }
        return "Downloaded in " + time + " " + TIME_UNIT[unit];
    }

    public static String displaySize(long size) {
        // TODO: Implement UnitUtil.displayTime(long bytes);
        int unit = 0;
        if (size >= 1024) {
            size /= 1024;
            ++unit;
            if (size >= 1024) {
                size /= 1024;
                ++unit;
                if (size >= 1024) {
                    size /= 1024;
                    ++unit;
                }
            }
        }

        return "File size: " + size + SIZE_UNIT[unit];
    }
}
