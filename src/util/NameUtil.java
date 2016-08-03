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
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author hkhoi
 */
public class NameUtil {

    public static String makeUniqueName(String fileAbsPath) {
        String path = FilenameUtils.getPath(fileAbsPath);
        String base = FilenameUtils.getBaseName(fileAbsPath);
        String extension = FilenameUtils.getExtension(fileAbsPath);
        
        StringBuilder builder = new StringBuilder();
        
        int postfix = 0;
        
        while (isFilenameExisted(fileAbsPath)) {
            builder.setLength(0);
            builder.append(path)
                    .append(base)
                    .append('_')
                    .append(postfix++)
                    .append('.')
                    .append(extension);
            fileAbsPath = builder.toString();
        }
        return fileAbsPath;
    }

    public static boolean isFilenameExisted(String fileAbsPath) {
        File file = new File(fileAbsPath);
        return (file.exists() && !file.isDirectory());
    }
}
