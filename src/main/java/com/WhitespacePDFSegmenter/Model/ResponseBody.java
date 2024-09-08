package com.WhitespacePDFSegmenter.Model;

public class ResponseBody {

    private String status;

    private String statusCode;

    public ResponseBody(String status, String statusCode) {
        this.status = status;
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
