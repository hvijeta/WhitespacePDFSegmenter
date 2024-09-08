package com.WhitespacePDFSegmenter.Controller;

import com.WhitespacePDFSegmenter.ExceptionHandler.ResourceNotFoundException;
import com.WhitespacePDFSegmenter.Model.MetaDataResponse;
import com.WhitespacePDFSegmenter.Model.ResponseBody;
import com.WhitespacePDFSegmenter.Service.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.*;



@RestController
public class PdfController {

    @Autowired
    private PDFTextStripperWithPosition stripper;

    @Autowired
    private PdfService pdfService;

    @PostMapping("/segment-pdf")
    public ResponseEntity<?> analyzePdf(@RequestParam("file") MultipartFile file,
                                        @RequestParam("cuts") int numCuts) {

        List<Float> yDifferences = new ArrayList<>();

        try {
            // Load the document
            PDDocument document = PDDocument.load(file.getInputStream());
            stripper.setSortByPosition(true);
            stripper.getText(document);

            // Get the yCoOrdinates of each new line (Computing largest vertical whiteSpace)
            List<Float> yPositions = stripper.getyPositions();


            List<Float> storePosition = pdfService.getyTextPositions(yDifferences, yPositions, numCuts);


            byte[] zipData = pdfService.segmentPDF(document, storePosition, file.getOriginalFilename());

            document.close();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipData);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=segmented.pdf.zip");

            return new ResponseEntity<>(
                    new InputStreamResource(byteArrayInputStream),
                    headers,
                    HttpStatus.OK
            );

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process PDF file");
        }
    }


    @GetMapping("/pdf-metadata/{id}")
    public ResponseEntity<?> getPdfMetadata(@PathVariable String id) {
        MetaDataResponse dataResponse = new MetaDataResponse();

        try{
            List<String> segments = pdfService.getSegmentDetails(id);
            dataResponse.setSegments(segments);
            dataResponse.setTotalSegments(segments.size());
            return ResponseEntity.ok(dataResponse);
        }catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process PDF file");
        }
    }

    @PutMapping("/update-segmentation/{id}")
    public ResponseEntity<?> updateSegmentation(@PathVariable String id, @RequestParam int newCuts) {
        MetaDataResponse dataResponse = new MetaDataResponse();

        try{
            List<String> segments = pdfService.updateSegmentDetails(id, newCuts);
            dataResponse.setSegments(segments);
            dataResponse.setTotalSegments(segments.size());
            return ResponseEntity.ok(dataResponse);
        }catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseBody(ex.getMessage(),"404"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody(ex.getMessage(),"500"));
        }
    }

    @DeleteMapping("/delete-pdf/{id}")
    public ResponseEntity<?> deletePdf(@PathVariable String id) {

        try{
            String segments = pdfService.removePDF(id);
            return ResponseEntity.ok(new ResponseBody("200", "Successfully deleted."));
        }catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseBody(ex.getMessage(),"404"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody(ex.getMessage(),"500"));
        }
    }


}
