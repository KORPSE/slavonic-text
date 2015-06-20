package ru.slavonictext.controllers;

import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Inject;
import com.typesafe.config.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;
import ru.slavonictext.services.LocalSettingsService;
import ru.slavonictext.util.ConfBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SettingsController {
    final static Logger log = Logger.getLogger(EditorController.class.getName());

    @Inject
    private ConfBean conf;

    @Inject
    private LocalSettingsService localSettings;

    @FXML
    private ListView baseLetters;

    @FXML
    private ListView chooseLetters;

    @FXML
    private TextArea replacementsEdit;

    private final Map<String, Object> spellingVariants = new HashMap<String, Object>();

    private ConfigRenderOptions renderOptions = ConfigRenderOptions.defaults().setJson(false).setComments(false).setOriginComments(false);

    private void resetStage() {
        spellingVariants.clear();
        spellingVariants.putAll(localSettings.getSpellingVariants());
        baseLetters.getItems().clear();
        baseLetters.getItems().addAll(ImmutableSortedSet.copyOf(conf.getAltSymbols().keySet()));
        chooseLetters.getItems().clear();
        replacementsEdit.setText(ConfigValueFactory.fromMap(localSettings.getReplacements())
                .render(renderOptions));
    }

    @FXML
    void initialize() {
        resetStage();

        final ChangeListener listener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String letter = (String) baseLetters.getSelectionModel().getSelectedItem();
                changeVariant(letter, newValue);
            }
        };

        baseLetters.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                chooseLetters.getSelectionModel().selectedItemProperty().removeListener(listener);
                selectLetter(newValue);
                chooseLetters.getSelectionModel().selectedItemProperty().addListener(listener);
            }
        });
    }

    private void selectLetter(String letter) {
        if (StringUtils.isEmpty(letter)) {
            return;
        }
        chooseLetters.getItems().clear();
        if (StringUtils.isEmpty(letter)) {
            return;
        }
        chooseLetters.getItems().addAll((List<String>) conf.getAltSymbols().get(letter));
        if (spellingVariants.containsKey(letter)) {
            chooseLetters.getSelectionModel().selectIndices(((List<String>) conf.getAltSymbols().get(letter))
                    .indexOf(spellingVariants.get(letter)));
        } else {
            chooseLetters.getSelectionModel().selectIndices(0);
        }
    }

    private void changeVariant(String letter, String variant) {
        if (StringUtils.isEmpty(letter) || StringUtils.isEmpty(variant)) {
            return;
        }
        if (StringUtils.isNotEmpty(variant)) {
            if (!letter.equals(variant)) {
                spellingVariants.put(letter, variant);
            } else {
                spellingVariants.remove(letter);
            }
        }
    }

    private void doClose(Event event) {
        Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void close(Event event) {
        resetStage();
        doClose(event);
    }

    @FXML
    private void save(Event event) {
        try {
            Config conf = ConfigFactory.parseString(replacementsEdit.getText());
            localSettings.getReplacements().clear();
            localSettings.getReplacements().putAll(conf.root().unwrapped());
            localSettings.getSpellingVariants().clear();
            localSettings.getSpellingVariants().putAll(spellingVariants);
            replacementsEdit.setText(conf.root().render(renderOptions));
            localSettings.persist();
            doClose(event);
        } catch (ConfigException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText("Формат конфигурации неверен");
            alert.setContentText("Используйте формат вида <вводимый_символ> = <замена>\nдопустима символов в виде юникод последовательностей в кавычках: \"\\u0000\"");

            alert.showAndWait();
        }
    }

    @FXML
    private void resetVariants(Event event) {
        spellingVariants.clear();
        chooseLetters.getSelectionModel().select(0);
    }
}
