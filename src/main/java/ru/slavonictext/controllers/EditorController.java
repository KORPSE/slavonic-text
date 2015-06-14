package ru.slavonictext.controllers;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import ru.slavonictext.app.LocalSettings;
import ru.slavonictext.services.PdfService;
import ru.slavonictext.util.ConfBean;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorController {

    final static Logger log = Logger.getLogger(EditorController.class.getName());

    @Inject
    private ConfBean conf;

    @Inject
    private PdfService pdfService;

    @Inject
    private LocalSettings localSettings;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea text;

    @FXML
    private ListView aboveSymbolsView;

    @FXML
    private ListView altSymbolsView;

    @FXML
    private ListView addSymbolsView;

    @FXML
    private Accordion accordion;

    public void setSettingsStage(Stage settingsStage) {
        this.settingsStage = settingsStage;
    }

    private Stage settingsStage;

    private String fileName = null;

    @FXML
    private void handleOpenEvent(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(text.getScene().getWindow());
        if (file != null && file.exists()) {
            try {
                text.setText(IOUtils.toString(new FileInputStream(file), "UTF-8"));
                fileName = file.getAbsolutePath();
            } catch (IOException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    private void doSave(File file, String content) throws IOException {
        log.info("trying to save " + file.getName());
        FileWriter w = new FileWriter(file);
        w.write(text.getText());
        w.close();
    }

    @FXML
    private void handleSaveEvent(Event event) {
        log.info("calling save");
        if (StringUtils.isEmpty(fileName)) {
            handleSaveAsEvent(event);
        } else {
            try {
                doSave(new File(fileName), text.getText());
            } catch (IOException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
    }
    @FXML
    private void handleSaveAsEvent(Event event) {
        log.info("calling save as");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        File file = fileChooser.showSaveDialog(text.getScene().getWindow());
        if (file != null) {
            try {
                doSave(file, text.getText());
                fileName = file.getAbsolutePath();
            } catch (IOException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    @FXML
    private void handleExportToPdf(Event event) {
        log.info("calling export to PDF");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PDF", ".pdf"));
        File file = fileChooser.showSaveDialog(text.getScene().getWindow());
        if (file != null) {
            try {
                pdfService.saveToPdf(file, text.getText());
            } catch (Exception ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    @FXML
    private void handleSelection(Event event) {
        aboveSymbolsView.getItems().clear();
        altSymbolsView.getItems().clear();
        String selection = text.getSelectedText();
        if (selection.length() == 1 || (selection.length() == 2 && conf.getAboveSymbols().contains(selection.substring(1, 2)))) {
            conf.getAboveSymbols().forEach(ch -> aboveSymbolsView.getItems().add(selection + ch));
            if (conf.getAltSymbols().containsKey(selection)) {
                ((List<String>) conf.getAltSymbols().get(selection)).forEach(ch -> altSymbolsView.getItems().add(ch));
            }
        }
    }

    @FXML
    private void handleAddAboveSymbol(Event event) {
        text.replaceSelection((String) aboveSymbolsView.getSelectionModel().getSelectedItem());
        aboveSymbolsView.getItems().clear();
        text.requestFocus();
    }

    @FXML
    private void handleAddAltSymbol(Event event) {
        text.replaceSelection((String) altSymbolsView.getSelectionModel().getSelectedItem());
        altSymbolsView.getItems().clear();
        text.requestFocus();
    }

    @FXML
    private void handleAddSymbol(Event event) {
        String value = (String) addSymbolsView.getSelectionModel().getSelectedItem();
        if (text.getSelectedText().length() > 0){
            text.replaceSelection(value);
        } else {
            text.insertText(text.getCaretPosition(), value);
        }
        text.requestFocus();
    }

    @FXML
    private void focusAltSymbols(Event event) {
        accordion.getPanes().get(0).setExpanded(true);
        altSymbolsView.requestFocus();
    }
    @FXML
    private void focusAboveSymbols(Event event) {
        accordion.getPanes().get(1).setExpanded(true);
        aboveSymbolsView.requestFocus();
    }
    @FXML
    private void focusAddSymbols(Event event) {
        accordion.getPanes().get(2).setExpanded(true);
        addSymbolsView.requestFocus();
    }

    @FXML
    private void clickOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Event.fireEvent(event.getTarget(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                    0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                    true, true, true, true, true, true, null));
        }
    }

    @FXML
    private void showSettings(Event event) {
        settingsStage.show();
    }

    @FXML
    void initialize() {
        text.setTextFormatter(new TextFormatter<String>(change -> {
            localSettings.getSpellingVariants().forEach(
                    (letter, variant) -> change.setText(change.getText().replace(letter, (String) variant)));
            return change;
        }));
        addSymbolsView.getItems().addAll(conf.getAddSymbols());
        log.info("it works");
    }

}
