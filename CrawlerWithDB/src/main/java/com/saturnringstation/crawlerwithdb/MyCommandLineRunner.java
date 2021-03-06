package com.saturnringstation.crawlerwithdb;

import com.saturnringstation.crawlerwithdb.service.PersistenceService;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by tom on 4/11/2017.
 */
@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Resource(name = "${spring.profiles.active}")
    PersistenceService persistenceService;

    @Override
    public void run(String... args) throws Exception {
        String[] crawlDomains = {"https://pixabay.com/en/", "https://cdn.pixabay.com/photo/"};
        String crawlStorageFolder = "./images";
        int numberOfCrawlers = 10;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setIncludeBinaryContentInCrawling(true);
        config.setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
        config.setResumableCrawling(true);

        PageFetcher fetcher = new PageFetcher(config);
        RobotstxtConfig robotsConfig = new RobotstxtConfig();
        robotsConfig.setEnabled(false);
        RobotstxtServer robotsSvr = new RobotstxtServer(robotsConfig, fetcher);

        CrawlController controller = new CrawlController(config, fetcher, robotsSvr);
        for (String domain : crawlDomains) {
            controller.addSeed(domain);
        }

        MyCrawler.configure(crawlDomains, crawlStorageFolder);
        MyCrawlerFactory factory = new MyCrawlerFactory(persistenceService);
        controller.startNonBlocking(factory, numberOfCrawlers);
    }
}
