package uk.gov.dwp.uc.pairtest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesFactory
{
    private final static Logger LOG = Logger.getLogger(PropertiesFactory.class.getName());
    private final static String CONFIG_PATH="./src/main/resources/";
    private static Properties properties;

    private PropertiesFactory(){
    }

    public static Properties createPropertyInstance(){

        if (null == properties) {
            properties = new Properties();
            try {
                properties.load(new FileInputStream(CONFIG_PATH + "config.properties"));
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "config.properties not found in the expected directory: "+ CONFIG_PATH);
                throw new RuntimeException(e);
            }
        }

        return properties;
    }
}
