package ru.slavonictext.modules;

import com.google.inject.AbstractModule;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import ru.slavonictext.services.LocalSettingsService;
import ru.slavonictext.services.PdfService;
import ru.slavonictext.util.ConfBean;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfBean.class).toInstance(ConfigBeanFactory.create(ConfigFactory.load(), ConfBean.class));
        bind(PdfService.class).asEagerSingleton();
        bind(LocalSettingsService.class).asEagerSingleton();
    }
}
