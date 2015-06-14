package ru.slavonictext.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.util.Callback;
import ru.slavonictext.modules.MainModule;

public class GuiceControllerFactoryHolder {
    private final static Injector injector = Guice.createInjector(new MainModule());
    public final static Callback<Class<?>, Object> guiceControllerFactory = clazz -> injector.getInstance(clazz);
}
