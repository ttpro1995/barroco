# barroco
Multi-connection Downloader #weekend_project #just4fun

barroco - Download Accelerator written in Java
Gets a file using multiple concurrent connections (like IDM in Windows or axel, aria2 in Linux), will support resuming feature soon.
Benchmark:
- Google Chrome: >1min
- wget: 24 secs
- axel: 12 seconds
-> barroco: 14 seconds (yay) <-
Source: https://github.com/hkhoi/barroco
Demo: https://www.youtube.com/watch?v=hwpcwgQY45A

I'm gonna post a tutorial later

===================
## How to build and run
1. Install [Ant](http://ant.apache.org/)
2. Build
```bash
$ ant -Dnb.internal.action.name=rebuild clean jar
$ cp -r lib/ dist/
```

3. Run
```bash
$ cd dist
$ java -jar barroco.jar meow.png http://i.imgur.com/MJu3vL2.png
```
meow.png is file name <br>
http://i.imgur.com/MJu3vL2.png is download link
