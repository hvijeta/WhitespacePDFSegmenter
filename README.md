# PDF Content Segmenter with REST API

## Description
A Spring Boot application that segments a PDF based on whitespace and provides a REST API to accept Single Paged PDF files, segment them, retrieve metadata, update segmentation, and delete processed PDFs.

## Technologies Used
- Java 8
- Spring Boot
- Apache PDFBox
- Swagger for API documentation

## Running the Application
1. Clone the repository.
2. Run `mvn spring-boot:run`.
3. Access Swagger documentation at `http://localhost:8080/swagger-ui.html`.

## API Endpoints
1. **POST /segment-pdf**: Accepts a PDF file and the number of cuts, returns segmented PDFs in zipped folder.
2. **GET /pdf-metadata/{id}**: Retrieves metadata for a processed PDF.
3. **PUT /update-segmentation/{id}**: Updates segmentation details.
4**DELETE /delete-pdf/{id}**: Deletes processed PDF.
