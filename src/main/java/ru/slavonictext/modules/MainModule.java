package ru.slavonictext.modules;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import javafx.stage.Stage;
import ru.slavonictext.app.LocalSettings;
import ru.slavonictext.services.PdfService;
import ru.slavonictext.util.ConfBean;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfBean.class).toInstance(ConfigBeanFactory.create(ConfigFactory.load(), ConfBean.class));
        bind(PdfService.class).asEagerSingleton();
        bind(LocalSettings.class).asEagerSingleton();
    }
}
