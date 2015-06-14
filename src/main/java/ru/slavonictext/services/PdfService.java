package ru.slavonictext.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;

public class PdfService {

    public void saveToPdf(File file, String content) throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDType0Font font = PDType0Font.load(document, getClass().getClassLoader().getResourceAsStream("font.ttf"));

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(font, 15);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(content);
        contentStream.endText();

        contentStream.close();

        document.save(file);
        document.close();
    }

}
