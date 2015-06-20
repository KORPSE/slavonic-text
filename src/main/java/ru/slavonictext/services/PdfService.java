package ru.slavonictext.services;

import com.sun.javafx.css.SimpleSelector;
import com.sun.javafx.css.Stylesheet;
import com.sun.javafx.css.parser.CSSParser;
import javafx.scene.text.Font;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class PdfService {

    final static Logger log = Logger.getLogger(PdfService.class.getName());

    private int[] possibleWrapPoints(String text) {
        String[] split = text.split("(?<=\\s)");
        int[] ret = new int[split.length];
        ret[0] = split[0].length();
        for ( int i = 1 ; i < split.length ; i++ )
            ret[i] = ret[i-1] + split[i].length();
        return ret;
    }

    private float getSizeFromCss() {
        try {
            Stylesheet style = new CSSParser().parse(getClass().getClassLoader().getResource("style.css"));
            return (float) style.getRules().stream().filter(
                    rule -> rule.getSelectors().stream().anyMatch(selector -> selector instanceof SimpleSelector && ((SimpleSelector) selector).getStyleClasses().contains("slavonic")))
                    .flatMapToDouble(rule -> rule.getDeclarations().stream().filter(declaration -> declaration.getProperty().equals("-fx-font"))
                            .mapToDouble(declaration -> ((Font) declaration.getParsedValue().convert(new Font(0))).getSize()))
                    .findFirst().getAsDouble();
        } catch (IOException e) {
            log.severe(e.getMessage());
            return 15.0f;
        }
    }
    public void saveToPdf(File file, String content) throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDType0Font font = PDType0Font.load(document, getClass().getClassLoader().getResourceAsStream("font.ttf"));

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        Stylesheet style = new CSSParser().parse(getClass().getClassLoader().getResource("style.css"));

        float fontSize = getSizeFromCss();

        float height15 = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 15;
        float heightUser = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        int paragraphWidth = 500;
        contentStream.setFont(font, fontSize);

        int start = 0;
        int end = 0;
        int lineCount = 1;
        int pageCount = 1;

        final int stringPerPage = (int) (25 * height15 / heightUser);

        final float hOffset = -font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        final float leftPos = 40;
        final float topPos = 750;

        contentStream.newLineAtOffset(leftPos, topPos);
        for ( int i : possibleWrapPoints(content) ) {
            float width = font.getStringWidth(content.substring(start, i)) / 1000 * fontSize;
            if ( start < end && width > paragraphWidth ) {
                contentStream.showText(content.substring(start, end));
                contentStream.newLineAtOffset(0, hOffset);
                start = end;
                lineCount++;
            }
            if (lineCount > pageCount * stringPerPage) {
                pageCount++;
                contentStream.endText();
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.newLineAtOffset(leftPos, topPos);
            }
            end = i;
        }

        contentStream.showText(content.substring(start));
        contentStream.endText();

        contentStream.close();

        document.save(file);
        document.close();
    }

}
