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

@Component
public class PerfAnalyser implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(PerfAnalyser.class);
    private ApplicationContext context;
    @Autowired
    private QueryProcessor queryProcessor;

    @EventListener
    public void onDBInitializedEvent(DBInitializer.DBInitializedEvent event) {
        log.info("received DBInitializedEvent");
        if ("1".equals(System.getenv("PAM"))) {
            log.info("in PAM mode");

            // TODO: all PAM logic here
//            queryProcessor.search("<TODO>", "123.123.123.123", false); // Do multiple times in parallel
            // To access db see src/main/java/com/cufe/searchengine/db/table

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
