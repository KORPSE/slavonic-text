package ru.slavonictext.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;

public class PdfService {

    private int[] possibleWrapPoints(String text) {
        String[] split = text.split("(?<=\\s)");
        int[] ret = new int[split.length];
        ret[0] = split[0].length();
        for ( int i = 1 ; i < split.length ; i++ )
            ret[i] = ret[i-1] + split[i].length();
        return ret;
    }

    public void saveToPdf(File file, String content) throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDType0Font font = PDType0Font.load(document, getClass().getClassLoader().getResourceAsStream("font.ttf"));

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        int fontSize = 15;
        int paragraphWidth = 500;
        contentStream.setFont(font, fontSize);

        int start = 0;
        int end = 0;
        int lineCount = 1;
        int pageCount = 1;

        final int stringPerPage = 25;

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
