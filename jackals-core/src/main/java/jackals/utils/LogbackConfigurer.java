package jackals.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;

/**
 */
public class LogbackConfigurer {
    private static final Logger log = LoggerFactory.getLogger(LogbackConfigurer.class);
    public String CONFIG_LOCATION_PARAM = "/jar/config/logback.xml";
    public LogbackConfigurer(){
        init();
    }
    public LogbackConfigurer(String path){
        CONFIG_LOCATION_PARAM = path;
        init();
    }
    public void init() {
        init(CONFIG_LOCATION_PARAM);
    }

    public void init(String path) {
        try {
            URL logURL = new ClassPathResource(path).getURL();
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.reset();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            configurator.doConfigure(logURL);
            if (log.isInfoEnabled())
                log.info("loaded logback configure sucess!");
        } catch (IOException e) {
            log.error("load logback config fail !" + e.toString());
        } catch (JoranException e) {
            log.error("load logback config fail !" + e.toString());
        }
    }
}
