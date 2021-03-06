package ru.slavonictext.controllers;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
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
import ru.slavonictext.services.LocalSettingsService;
import ru.slavonictext.services.PdfService;
import ru.slavonictext.util.ConfBean;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EditorController {

    final static Logger log = Logger.getLogger(EditorController.class.getName());

    @Inject
    private ConfBean conf;

    @Inject
    private PdfService pdfService;

    @Inject
    private LocalSettingsService localSettings;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea text;

    @FXML
    private ListView accentsView;

    @FXML
    private ListView aspirationsView;

    @FXML
    private ListView titloView;

    private List<ListView> aboveSymbolsViews() {
        return Lists.newArrayList(accentsView, aspirationsView, titloView);
    }

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
                InputStream is = new FileInputStream(file);
                text.setText(IOUtils.toString(is, "UTF-8"));
                is.close();
                fileName = file.getAbsolutePath();
            } catch (IOException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    private void doSave(File file, String content) throws IOException {
        log.info("trying to save " + file.getName());
        OutputStream os = new FileOutputStream(file);
        IOUtils.write(content, os, "UTF-8");
        os.close();
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
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(text.getScene().getWindow());
        if (file != null) {
            try {
                pdfService.saveToPdf(file, text.getText());
            } catch (Exception ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    private boolean oneInlineChar(String str) {
        if (StringUtils.isEmpty(str) || str.length() > 10) {
            return false;
        } else if (str.length() > 1) {
            if (Lists.charactersOf(str.substring(1)).stream().allMatch(chr -> conf.getAboveSymbols().all().contains(chr.toString()))) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @FXML
    private void handleSelection(Event event) {
        aboveSymbolsViews().forEach(listView -> listView.getItems().clear());
        altSymbolsView.getItems().clear();
        String selection = text.getSelectedText();
        if (oneInlineChar(selection)) {
            conf.getAboveSymbols().getAccents().forEach(ch -> accentsView.getItems().add(selection + ch));
            conf.getAboveSymbols().getAspirations().forEach(ch -> aspirationsView.getItems().add(selection + ch));
            conf.getAboveSymbols().getTitlo().forEach(ch -> titloView.getItems().add(selection + ch));
            if (conf.getAltSymbols().containsKey(selection)) {
                ((List<String>) conf.getAltSymbols().get(selection)).forEach(ch -> altSymbolsView.getItems().add(ch));
            }
        }
    }

    private void doReplaceSelection(String replacement) {
        text.setTextFormatter(null);
        text.replaceSelection(replacement);
        text.setTextFormatter(formatter);
    }

    @FXML
    private void handleAddAboveSymbol(Event event) {
        text.replaceSelection((String) ((ListView) event.getSource()).getSelectionModel().getSelectedItem());
        aboveSymbolsViews().forEach(listView -> listView.getItems().clear());
        text.requestFocus();
    }

    @FXML
    private void handleAddAltSymbol(Event event) {
        doReplaceSelection((String) altSymbolsView.getSelectionModel().getSelectedItem());
        altSymbolsView.getItems().clear();
        text.requestFocus();
    }

    @FXML
    private void handleAddSymbol(Event event) {
        String value = (String) addSymbolsView.getSelectionModel().getSelectedItem();
        if (text.getSelectedText().length() > 0){
            doReplaceSelection(value);
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
    private void focusAccentsSymbols(Event event) {
        accordion.getPanes().get(1).setExpanded(true);
        accentsView.requestFocus();
    }
    @FXML
    private void focusAspirationsSymbols(Event event) {
        accordion.getPanes().get(2).setExpanded(true);
        aspirationsView.requestFocus();
    }
    @FXML
    private void focusTitloSymbols(Event event) {
        accordion.getPanes().get(3).setExpanded(true);
        titloView.requestFocus();
    }
    @FXML
    private void focusAddSymbols(Event event) {
        accordion.getPanes().get(4).setExpanded(true);
        addSymbolsView.requestFocus();
    }

    @FXML
    private void clickOnEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Event.fireEvent(event.getTarget(), new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                    0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                    true, true, true, true, true, true, null));
        } else if (event.getCode().equals(KeyCode.ESCAPE)) {
            text.requestFocus();
        }
    }

    @FXML
    private void showSettings(Event event) {
        settingsStage.show();
    }

    private TextFormatter formatter = new TextFormatter<String>(change -> {
        localSettings.getSpellingVariants().forEach(
                (letter, variant) -> change.setText(change.getText().replace(letter, (String) variant)));
        localSettings.getReplacements().forEach(
                (letter, replacement) -> {
                    change.setText(change.getText().replace(letter, (String) replacement)
                            .replace(letter.toUpperCase(), ((String) replacement).toUpperCase()));
                }
        );
        return change;
    });

    @FXML
    void initialize() {
        text.setTextFormatter(formatter);
        Set<Object> additionalChars = new TreeSet<Object>(localSettings.getReplacements().values());
        Set<Object> additionalCharsUp = additionalChars.stream().map(chr -> ((String) chr).toUpperCase()).collect(Collectors.toSet());
        additionalChars.addAll(additionalCharsUp);
        additionalChars.addAll(conf.getAddSymbols());
        addSymbolsView.getItems().addAll(additionalChars);
        log.info("it works");
    }

}
