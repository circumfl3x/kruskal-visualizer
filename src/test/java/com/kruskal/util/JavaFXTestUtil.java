package com.kruskal.util;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;

public class JavaFXTestUtil {

    private static boolean initialized = false;


    public static void init() {

        if (initialized) {
            return;
        }


        CountDownLatch latch = new CountDownLatch(1);


        Platform.startup(() -> {
            initialized = true;
            latch.countDown();
        });


        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}