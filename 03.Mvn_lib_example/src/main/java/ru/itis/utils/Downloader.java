package ru.itis.utils;

import java.io.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader implements Runnable {
    private String imgURL;
    private String imgName;
    private String folder;
    private int countOfThreads;

    public Downloader(String imgURL, String folder) {
        this.imgURL = imgURL;
        this.folder = folder;
    }

    public Downloader(int countOfThreads,String imgURL,  String folder) {
        this.imgURL = imgURL;
        this.folder = folder;
        this.countOfThreads = countOfThreads;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(imgURL);
            inputStream = url.openStream();
            outputStream = new FileOutputStream(folder + imgName + ".jpg");

            byte[] buffer = new byte[2048];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void download() {
        ExecutorService executorService = Executors.newFixedThreadPool(countOfThreads);
        for (int i = 0; i < countOfThreads; i++) {
            imgName = String.valueOf(i);
            executorService.submit(this::run);
            System.out.println(i + " is downloading ...!");
        }
    }
}
