package com.WhitespacePDFSegmenter.Service;


import com.WhitespacePDFSegmenter.ExceptionHandler.ResourceNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfService {

    private static final String LOCAL_ZIP_PATH = "segmented_pdfs.zip"; // Path where zip will be saved

    private  HashMap<String, List<String>> segmentDetails  = new HashMap<>();
    public byte[] segmentPDF(PDDocument document, List<Float> yCoordinates, String filename) throws IOException {
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
            PDPage page = document.getPage(0);
            List<PDDocument> segmentedDocs = new ArrayList<>();

            List<Float> sortedYCoordinates = new ArrayList<>(yCoordinates);
            Collections.sort(sortedYCoordinates);

            PDFTextStripper textStripper = new PDFTextStripper() {
                @Override
                protected void processTextPosition(TextPosition text) {
                    super.processTextPosition(text);
                }
            };

            textStripper.setStartPage(0);
            textStripper.setEndPage(1);

            for (int i = 0; i < sortedYCoordinates.size()-1; i++) {
                float y1 = sortedYCoordinates.get(i);
//                float y2 = (i + 1 < sortedYCoordinates.size()) ? sortedYCoordinates.get(i + 1) : page.getMediaBox().getHeight();
                float y2 = sortedYCoordinates.get(i + 1);
                PDDocument newDoc = new PDDocument();
                PDPage newPage = new PDPage(page.getMediaBox());
                newDoc.addPage(newPage);

                try (PDPageContentStream contentStream = new PDPageContentStream(newDoc, newPage, PDPageContentStream.AppendMode.OVERWRITE, true)) {
                    // Copy font style and position from the original page
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12); // Defaulting to Helvetica-Bold, adjust as needed
                    contentStream.beginText();
                    contentStream.newLineAtOffset(10, newPage.getMediaBox().getHeight() - 50);
                    contentStream.showText("Segment from Y: " + y1 + " to Y: " + y2);
                    contentStream.endText();
                }

                segmentedDocs.add(newDoc);
            }

            try (ZipOutputStream zipStream = new ZipOutputStream(zipOut)) {
                for (int i = 0; i < segmentedDocs.size(); i++) {
                    ByteArrayOutputStream docStream = new ByteArrayOutputStream();
                    segmentedDocs.get(i).save(docStream);
                    ZipEntry entry = new ZipEntry("segment_" + i + ".pdf");
                    if (!segmentDetails.containsKey(filename)) {
                        // If the key does not exist, create a new ArrayList
                        segmentDetails.put(filename, new ArrayList<String>());
                    }
                    segmentDetails.get(filename).add("segment_" + i + ".pdf");
                    zipStream.putNextEntry(entry);
                    zipStream.write(docStream.toByteArray());
                    zipStream.closeEntry();
                }
            }

            // Save the zip file locally
            saveZipFileLocally(zipOut.toByteArray());

            return zipOut.toByteArray();
    }

    private void saveZipFileLocally(byte[] zipFileData) throws IOException {
        Path path = Paths.get(LOCAL_ZIP_PATH);
        Files.write(path, zipFileData);
    }


    public List<Float> getyTextPositions(List<Float> yDifferences, List<Float> yPositions, int numCuts) {

        TreeMap<Integer,Float> mappedPositions = new TreeMap<>(Comparator.reverseOrder());
        List<Float> storePosition = new ArrayList<>();

        for (int i = 1; i < yPositions.size(); i++) {
            Float fValue = yPositions.get(i) - yPositions.get(i - 1);
            yDifferences.add(fValue);
            mappedPositions.put((int)Math.floor(fValue), yPositions.get(i));

        }

        // Create a List<Integer> to store the converted values
        List<Integer> intList = new ArrayList<>();

        // Convert each Float to Integer and add to the List<Integer>
        for (Float f : yDifferences) {
            intList.add(f.intValue());  // Convert Float to Integer and add to the list
        }

//            intList.sort(Comparator.reverseOrder());
        storePosition.add(0.0f);

        int cnt = 0;
        for (Float value : mappedPositions.values()) {
            if(cnt >= numCuts) break;
            storePosition.add(value);
            cnt++;
        }
        Collections.sort(storePosition);
        return storePosition;

    }

    public List<String> getSegmentDetails(String id) throws ResourceNotFoundException {
        if(segmentDetails.containsKey(id)){
            return segmentDetails.get(id);
        }
        else{
            throw new ResourceNotFoundException("Id : " + id + " not found.");
        }

    }

    public String removePDF(String id) throws ResourceNotFoundException{

        if(segmentDetails.containsKey(id)){
            segmentDetails.remove(id);
            return id;
        }
        else{
            throw new ResourceNotFoundException("Id : " + id + " not found.");
        }

    }

    public List<String> updateSegmentDetails(String id, int newCuts) throws ResourceNotFoundException {


        if (segmentDetails.containsKey(id)) {
            segmentDetails.get(id).subList(Math.min(newCuts, segmentDetails.get(id).size()), segmentDetails.get(id).size()).clear();
            return segmentDetails.get(id);

        }

            throw new ResourceNotFoundException("Id : " + id + " not found.");
    }

}
