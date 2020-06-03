package com.cufe.searchengine.query;

import com.cufe.searchengine.db.DBInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.Random;
@Component
public class PerfAnalyser implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(PerfAnalyser.class);
    private ApplicationContext context;
    @Autowired
    private QueryProcessor queryProcessor;
    private Random random = new Random();
    private String[] data = {"deep learning", "advanced programming techniques", "maths", "science", "psychology", "Ahmed Zewail",
                            "image processing", "Sherlock Holmes", "animation movies", "COVID-19"};
    @EventListener
    public void onDBInitializedEvent(final DBInitializer.DBInitializedEvent event) throws InterruptedException {
        log.info("received DBInitializedEvent");
        if ("1".equals(System.getenv("PAM"))) {
            log.info("in PAM mode");

                for(int i=0; i<20; ++i) {
                    int index = random.nextInt(9);
                    log.info(data[index]);
                    final long start_time = System.currentTimeMillis();
                    queryProcessor.search(data[index], "0.0.0.0", false);
                    final long end_time = System.currentTimeMillis();
                    log.info("searching for : " + data[index] + "  takes Time  = " + (end_time-start_time) + " ms.");
                }
            close();
        }
    }

    private void close() {
        log.info("closing");
        ((ConfigurableApplicationContext) context).close();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}