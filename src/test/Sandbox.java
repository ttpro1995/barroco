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
package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import runnable.Slave;
import util.Info;
import util.Util;

/**
 *
 * @author hkhoi
 */
public class Sandbox {

    static final String URL_STR = "https://docs.oracle.com/javase/tutorial/collections/interfaces/examples/dictionary.txt";
    static final int PARTS = 8;

    public static void main(String[] args) throws Exception {
        Util util = new Util(URL_STR);
        
        ExecutorService threadPool = Executors.newFixedThreadPool(PARTS);
        
        Info[] infoList = util.split("dicky_dick.txt", PARTS);
        
        for (Info it : infoList) {
            threadPool.submit(new Slave(it));
        }
        
        System.out.println("> Downloading");
        
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.DAYS);
        
        System.out.println("> Merging");
        util.merge(infoList);
        
        System.out.println("All done");
    }
}
