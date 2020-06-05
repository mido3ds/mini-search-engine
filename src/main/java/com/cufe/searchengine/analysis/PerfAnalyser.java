package com.cufe.searchengine.analysis;

import com.cufe.searchengine.db.DBInitializer;
import com.cufe.searchengine.db.table.DocumentsTable;
import com.cufe.searchengine.db.table.KeywordsTable;
import com.cufe.searchengine.query.QueryProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

@Component
public class PerfAnalyser {
    private static final Logger log = LoggerFactory.getLogger(PerfAnalyser.class);

    @Autowired
    private DocumentsTable documentsTable;
    @Autowired
    private KeywordsTable keywordsTable;
    @Autowired
    private QueryProcessor queryProcessor;

    private final Random RAND = new Random();
    private final String[] DATA = {"deep learning", "advanced programming techniques", "maths", "science", "psychology", "Ahmed Zewail",
            "image processing", "Sherlock Holmes", "animation movies", "COVID-19"};
    private static final int TOTAL_THREADS = 200;
    private static final int TIMEOUT_MS = 2 * 60 * 1000;

    @EventListener
    public void onDBInitializedEvent(final DBInitializer.DBInitializedEvent event) throws Exception {
        log.info("received DBInitializedEvent");
        if ("1".equals(System.getenv("PAM"))) {
            log.info("in PAM mode");

            Results results = doExperiment();

            File outFile = getOutFile();
            log.info("saving results to " + outFile.getCanonicalPath());
            writeJson(results, outFile);

            log.info("closing");
            System.exit(0);
        }
    }

    private void writeJson(Results results, File outFile) throws IOException {
        new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValue(outFile, results);
    }

    private File getOutFile() {
        return new File("performance-analysis-" + System.currentTimeMillis() + ".json");
    }

    private Results doExperiment() throws Exception {
        Results results = new Results();
        results.totalParallelRequests = TOTAL_THREADS;
        results.numCrawledPages = documentsTable.size();
        results.numIndexedKeywords = keywordsTable.size();

        // with ranking
        LinkedList<URLLoader> urlLoaders = createUrlLoaders(true);
        LinkedList<Thread> threads = createThreads(urlLoaders);
        sleep();
        for (Thread thread : threads) {
            thread.interrupt();
        }
        results.avgTimeWithRanking = getAvgTime(urlLoaders);
        results.successPercentageWithRanking = getSuccessPercentage(urlLoaders);

        // without ranking
        urlLoaders = createUrlLoaders(false);
        threads = createThreads(urlLoaders);
        sleep();
        for (Thread thread : threads) {
            thread.interrupt();
        }
        results.avgTimeWithoutRanking = getAvgTime(urlLoaders);
        results.successPercentageWithoutRanking = getSuccessPercentage(urlLoaders);

        return results;
    }

    private double getSuccessPercentage(LinkedList<URLLoader> urlLoaders) {
        int num = 0;
        for (URLLoader urlLoader : urlLoaders) {
            if (urlLoader.time != -1) {
                num++;
            }
        }
        return ((double) num) / urlLoaders.size();
    }

    private double getAvgTime(LinkedList<URLLoader> urlLoaders) {
        int total = 0;
        int num = 0;
        for (URLLoader urlLoader : urlLoaders) {
            if (urlLoader.time != -1) {
                num++;
                total += urlLoader.time;
            }
        }
        return total / (double) num;
    }

    private void sleep() {
        try {
            Thread.sleep(TIMEOUT_MS);
        } catch (InterruptedException ignored) {
        }
    }

    private LinkedList<Thread> createThreads(LinkedList<URLLoader> urlLoaders) {
        LinkedList<Thread> threads = new LinkedList<>();
        for (URLLoader urlLoader : urlLoaders) {
            Thread thread = new Thread(urlLoader);
            thread.start();
            threads.add(thread);
        }
        return threads;
    }

    private LinkedList<URLLoader> createUrlLoaders(boolean enableRanking) {
        LinkedList<URLLoader> urlLoaders = new LinkedList<>();
        for (int i = 0; i < TOTAL_THREADS; i++) {
            urlLoaders.add(new URLLoader(enableRanking));
        }
        return urlLoaders;
    }

    private class URLLoader implements Runnable {
        private final boolean enableRanking;
        public long time = -1;

        public URLLoader(boolean enableRanking) {
            this.enableRanking = enableRanking;
        }

        @Override
        public void run() {
            long l = System.currentTimeMillis();
            queryProcessor.search(DATA[RAND.nextInt(DATA.length)], "0.0.0.0", false, enableRanking);
            time = System.currentTimeMillis() - l;
        }
    }

    private static class Results {
        public int totalParallelRequests;

        public double avgTimeWithRanking;
        public double successPercentageWithRanking;
        public double avgTimeWithoutRanking;
        public double successPercentageWithoutRanking;

        public Integer numCrawledPages;
        public Integer numIndexedKeywords;
    }
}
