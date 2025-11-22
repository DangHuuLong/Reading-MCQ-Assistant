package com.readingmcqassistant.bootstrap;

import com.readingmcqassistant.service.JobWorker;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.ArrayList;
import java.util.List;

@WebListener
public class AppBootstrap implements ServletContextListener {
    private final List<JobWorker> workers = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();

    private static final int WORKER_COUNT = 4; 

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        for (int i = 0; i < WORKER_COUNT; i++) {
            String wname = "worker-" + i;
            JobWorker worker = new JobWorker(wname);

            Thread t = new Thread(worker, wname);
            t.setDaemon(true);

            workers.add(worker);
            threads.add(t);

            t.start();
        }
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (JobWorker wk : workers) {
            wk.shutdown();
        }
    }
}
