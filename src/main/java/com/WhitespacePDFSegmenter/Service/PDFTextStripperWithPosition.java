package com.WhitespacePDFSegmenter.Service;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PDFTextStripperWithPosition extends PDFTextStripper {

    private List<Float> yPositions = new ArrayList<>();

    public PDFTextStripperWithPosition() throws IOException {}

    public List<Float> getyPositions() {
        List<Float> temp = new ArrayList<>(yPositions);
        yPositions.clear();
        return temp;
    }

    @Override
    protected void processTextPosition(TextPosition textPosition) {
        if (yPositions.isEmpty() || textPosition.getYDirAdj() != yPositions.get(yPositions.size() - 1)) {
            yPositions.add(textPosition.getYDirAdj());
        }
    }
}
