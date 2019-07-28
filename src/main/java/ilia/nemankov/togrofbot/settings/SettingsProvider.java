package ilia.nemankov.togrofbot.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SettingsProvider {

    private static final Logger logger = LoggerFactory.getLogger(SettingsProvider.class);

    private static SettingsProvider instance;

    private Properties properties;

    private SettingsProvider() {

        try {
            properties = new Properties();
            FileInputStream inputStream = new FileInputStream("src/main/resources/settings.properties");

            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            logger.error("File settings.properties not found", e);
        } catch (IOException e) {
            logger.error("Appeared an error during reading settings.properties file", e);
        }

    }

    public static SettingsProvider getInstance() {
        if (instance == null){
            logger.debug("Created {} class instance", SettingsProvider.class.getSimpleName());
            instance = new SettingsProvider();
        }
        return instance;
    }

    public String getCommandPrefix() {
        if (!properties.isEmpty()) {
            return properties.getProperty("command.prefix");
        } else {
            return "!";
        }
    }

    public String getListItemSeparator() {
        if (!properties.isEmpty()) {
            return properties.getProperty("command.list.separator");
        }
        else {
            return ":small_orange_diamond:";
        }
    }

}
