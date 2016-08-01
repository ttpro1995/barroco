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
    public void merge(Plan[] info) throws FileNotFoundException, IOException {
        File firstFile = new File(info[0].getName());
        
        // Name processing
        String absPath = firstFile.getAbsolutePath();
        String fileName = firstFile.getName();
        String dirPath = absPath.substring(0, absPath.length() - fileName.length());
        String actualName = firstFile.getName();
        actualName = actualName.substring(Constant.PREFIX.length(), 
                actualName.length() - 1 - Constant.POSTFIX.length());
        String actualFullPath = dirPath + actualName;
        
        FileOutputStream outStream = new FileOutputStream(actualFullPath);
        WritableByteChannel writeChannel = Channels.newChannel(outStream);
        
        if (info.length == 1) {
            System.out.println("INFO -- \tMERGE: One file only, renaming...");
            File curFile = new File(info[0].getName());
            curFile.renameTo(new File(actualName));
            return;
        }
        
        int id = 0;
        
        try {
            for (Plan it : info) {
                System.out.printf("MERGE -- \tMerging part %d\n", id++);
                File curFile = new File(it.getName());
                try (FileInputStream inStream = new FileInputStream(curFile)) {
                    inStream.getChannel().transferTo(0, Long.MAX_VALUE, writeChannel);
                }
                curFile.delete();
            }
        } finally {
                outStream.close();
        }
    }
    
    /**
     * Receives an InputStream, then assembles it into a file
     *
     * @param inputStream
     * @param name
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void stream2File(InputStream inputStream, String name)
            throws FileNotFoundException, IOException {
        try (FileOutputStream outStream = new FileOutputStream(name)) {
            ReadableByteChannel channel = Channels.newChannel(inputStream);
            outStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
        }
    }
}
