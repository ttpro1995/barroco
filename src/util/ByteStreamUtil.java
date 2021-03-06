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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author hkhoi
 */
public class ByteStreamUtil {

    public static void merge(Plan[] info, String fileAbsPath) throws FileNotFoundException, IOException {
        if (info.length == 1) {
            File curFile = new File(info[0].getFileAbsPath());
            curFile.renameTo(new File(fileAbsPath));
            return;
        }

        int id = 0;

        for (Plan it : info) {
            try (FileInputStream inStream = new FileInputStream(it.getFileAbsPath())) {
                stream2File(inStream, fileAbsPath, true);
                (new File(it.getFileAbsPath())).delete();
            }
        }
    }

    public static void stream2File(InputStream inputStream, String name, boolean fileFlag)
            throws FileNotFoundException, IOException {
        try (FileOutputStream outStream = new FileOutputStream(name, true)) {
            if (fileFlag) {
                try (WritableByteChannel channel = Channels.newChannel(outStream)) {
                    ((FileInputStream) inputStream).getChannel()
                            .transferTo(0, Long.MAX_VALUE, channel);
                }
            } else {
                try (ReadableByteChannel channel = Channels.newChannel(inputStream)) {
                    outStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                }
            }
        }
    }
}
