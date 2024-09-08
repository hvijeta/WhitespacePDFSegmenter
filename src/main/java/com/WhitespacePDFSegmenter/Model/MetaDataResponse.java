package com.WhitespacePDFSegmenter.Model;

import java.util.ArrayList;
import java.util.List;

public class MetaDataResponse {

    private Integer totalSegments;

    private List<String> segments = new ArrayList<>();

    public Integer getTotalSegments() {
        return totalSegments;
    }

    public void setTotalSegments(Integer totalSegments) {
        this.totalSegments = totalSegments;
    }

    public List<String> getSegments() {
        return segments;
    }

    public void setSegments(List<String> segments) {
        this.segments = segments;
    }
}
