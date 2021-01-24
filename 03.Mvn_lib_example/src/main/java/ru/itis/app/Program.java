package ru.itis.app;

import com.beust.jcommander.JCommander;
import ru.itis.utils.Downloader;

public class Program {
    public static void main(String[] args) {
        Main main = new Main();

        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);

        if (main.mode.equals("one-thread")) {
            Downloader downloader = new Downloader(main.files, main.folder);
            downloader.download();
        } else if (main.mode.equals("multi-thread")) {
            Downloader downloader = new Downloader(main.count, main.files, main.folder);
            downloader.download();
        }
    }
}
