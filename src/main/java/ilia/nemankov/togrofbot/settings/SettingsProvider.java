package ilia.nemankov.togrofbot.settings;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

@Slf4j
public class SettingsProvider {

    private static SettingsProvider instance;

    private Properties properties;

    private SettingsProvider() {

        try {
            properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("settings.properties");

            properties.load(inputStream);
        } catch (NullPointerException e) {
            log.error("File settings.properties not found", e);
        } catch (IOException e) {
            log.error("Appeared an error during reading settings.properties file", e);
        }

    }

    public static SettingsProvider getInstance() {
        if (instance == null){
            log.debug("Created {} class instance", SettingsProvider.class.getSimpleName());
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
            return properties.getProperty("pagination.list_separator");
        }
        else {
            return ":small_orange_diamond:";
        }
    }

    public int getDefaultPageSize() {
        if (!properties.isEmpty()) {
            try {
                return Integer.valueOf(properties.getProperty("pagination.default_page_size"));
            } catch (NumberFormatException e) {
                log.error("Page size isn't integer", e);
            }
        }
        return 10;
    }

    public Locale getLocale() {
        //TODO get locale from settings stored in database
        return new Locale("en", "EN");
    }

}
