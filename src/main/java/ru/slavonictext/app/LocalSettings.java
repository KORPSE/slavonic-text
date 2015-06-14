package ru.slavonictext.app;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class LocalSettings {

    private Config config;

    private final static String spellingVariantsPath = "localSettings.spellingVariants";

    private final String confPath;

    final static Logger log = Logger.getLogger(LocalSettings.class.getName());

    private final Map<String, Object> spellingVariants;

    public LocalSettings() {
        try {
            confPath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "settings.conf";
            File f = new File(confPath);
            if (f.exists()) {
                config = ConfigFactory.parseFile(f);
            } else {
                config = ConfigFactory.load("defaultSettings");
            }
            spellingVariants = config.getObject(spellingVariantsPath).unwrapped();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getSpellingVariants() {
        return spellingVariants;
    }

    public void persist() {
        config = config.withValue(spellingVariantsPath, ConfigValueFactory.fromMap(spellingVariants));
        try {
            FileUtils.write(new File(confPath), config.root().withOnlyKey("localSettings").render(
                    ConfigRenderOptions.defaults().setComments(false).setOriginComments(false)));
        } catch (IOException ex) {
            log.warning(ex.getMessage());
        }
    }

}
