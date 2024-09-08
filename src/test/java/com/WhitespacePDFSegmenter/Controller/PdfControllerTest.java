package com.WhitespacePDFSegmenter.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.WhitespacePDFSegmenter.Model.MetaDataResponse;
import com.WhitespacePDFSegmenter.Service.PdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class PdfControllerTest {

    @InjectMocks
    private PdfController pdfController;

    @Mock
    private PdfService pdfService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPdfMetadata() {
        when(pdfService.getSegmentDetails(anyString())).thenReturn(Arrays.asList("Segment1", "Segment2"));

        ResponseEntity<?> response = pdfController.getPdfMetadata("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MetaDataResponse);
        MetaDataResponse dataResponse = (MetaDataResponse) response.getBody();
        assertEquals(2, dataResponse.getTotalSegments());
        assertEquals(Arrays.asList("Segment1", "Segment2"), dataResponse.getSegments());
    }

    @Test
    void testUpdateSegmentation() {
        when(pdfService.updateSegmentDetails(anyString(), anyInt())).thenReturn(Arrays.asList("Segment1"));

        ResponseEntity<?> response = pdfController.updateSegmentation("sample.pdf", 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MetaDataResponse);
        MetaDataResponse dataResponse = (MetaDataResponse) response.getBody();
        assertEquals(1, dataResponse.getTotalSegments());
        assertEquals(Arrays.asList("Segment1"), dataResponse.getSegments());
    }

    @Test
    void testDeletePdf() {
        when(pdfService.removePDF(anyString())).thenReturn("1");

        ResponseEntity<?> response = pdfController.deletePdf("sample.pdf");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1", response.getBody());
    }
}