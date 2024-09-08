package com.WhitespacePDFSegmenter.utils;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomPDFTextStripper extends PDFTextStripper {
    private List<TextPosition> textPositions = new ArrayList<>();

    public CustomPDFTextStripper() throws IOException {
        super();
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        // Add text positions to the list for later analysis
        this.textPositions.addAll(textPositions);
        super.writeString(string, textPositions);  // Keep original behavior
    }

    public List<TextPosition> getTextPositions() {
        return textPositions;
    }
}
