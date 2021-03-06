package ru.slavonictext.services;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class LocalSettingsService {

    private Config config;

    private final static String spellingVariantsPath = "localSettings.spellingVariants";
    private final static String replacementsPath = "localSettings.replacements";

    private final String confPath;

    final static Logger log = Logger.getLogger(LocalSettingsService.class.getName());

    private final Map<String, Object> spellingVariants;

    private final Map<String, Object> replacements;

    public LocalSettingsService() {
        try {
            String dir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
            confPath = dir + "/settings.conf";
            File f = new File(confPath);
            if (f.exists()) {
                config = ConfigFactory.parseFile(f);
            } else {
                config = ConfigFactory.load("defaultSettings");
            }
            spellingVariants = config.getObject(spellingVariantsPath).unwrapped();
            replacements = config.getObject(replacementsPath).unwrapped();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getSpellingVariants() {
        return spellingVariants;
    }

    public Map<String, Object> getReplacements() {
        return replacements;
    }

    public void persist() {
        config = config
                .withValue(spellingVariantsPath, ConfigValueFactory.fromMap(spellingVariants))
                .withValue(replacementsPath, ConfigValueFactory.fromMap(replacements));
        try {
            FileUtils.write(new File(confPath), config.root().withOnlyKey("localSettings").render(
                    ConfigRenderOptions.defaults().setComments(false).setOriginComments(false)));
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }
    }

}
