package com.WhitespacePDFSegmenter.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PdfV2Service {

    public List<File> segmentPdf(PDDocument document, List<Float> yCoordinates) throws IOException {
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        List<File> segmentedPDFs = new ArrayList<>();
        PDPage firstPage = document.getPage(0);
        float pageWidth = firstPage.getMediaBox().getWidth();

        // For each y-coordinate, extract the segment
        for (int i = 0; i < yCoordinates.size() - 1; i++) {
            float y1 = yCoordinates.get(i);
            float y2 = yCoordinates.get(i + 1);

            // Define the area to extract text from
            Rectangle rect = new Rectangle(0, (int) y2, (int) pageWidth, (int) (y1 - y2));
            stripper.addRegion("segment" + i, rect);
            stripper.extractRegions(firstPage);

            String segmentText = stripper.getTextForRegion("segment" + i);
            File segmentFile = createPDFSegment(segmentText, i, pageWidth, (y1 - y2));
            segmentedPDFs.add(segmentFile);
        }

        return segmentedPDFs;
    }



    private File createPDFSegment(String segmentText, int index, float pageWidth, float segmentHeight) throws IOException {
        File segmentFile = new File("segment" + index + ".pdf");

        try (PDDocument segmentDocument = new PDDocument()) {
            PDPage page = new PDPage();
            segmentDocument.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(segmentDocument, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, segmentHeight);
                contentStream.showText(segmentText);
                contentStream.endText();
            }

            segmentDocument.save(segmentFile);
        }

        return segmentFile;
    }
}
