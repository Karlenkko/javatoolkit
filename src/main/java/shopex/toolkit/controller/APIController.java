package shopex.toolkit.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shopex.toolkit.service.PDFService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class APIController {
    public static final String FONT = "./src/main/resources/font/siyuan-song.ttf";

    @PostMapping("/pdfform")
    @ResponseBody
    public ResponseEntity<byte[]> PDFForm (
            HttpServletRequest request,
            @RequestPart("file") MultipartFile file
            ) throws IOException {
        if (file.isEmpty()) return null;
        String fileName = UUID.randomUUID() + file.getOriginalFilename();
        String path = request.getServletContext().getRealPath("/upload/pdf");
        File filePath = new File(path, fileName);
        if (!filePath.getParentFile().exists()) {
            filePath.getParentFile().mkdirs();
        }
        file.transferTo(new File(path + File.separator + fileName));
        // file added to local dir
        String inputPath = path + File.separator + fileName;
        String outputPath = request.getServletContext().getRealPath("/download/pdf") + File.separator + fileName;
        Map<String, String[]> params = request.getParameterMap();
        Map<String, String> formData = new HashMap<>();
        params.forEach((key, value)->{
            formData.put(key, value[0]);
        });
        PDFService.fillForm(inputPath, outputPath, formData);
        File filledPDF = new File(outputPath);
        // setup response
        byte[] body = null;
        InputStream is = new FileInputStream(filledPDF);
        body = new byte[is.available()];
        is.read(body);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + fileName);
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, statusCode);
        // remove file
        filledPDF.delete();
        File originTemplate = new File(inputPath);
        originTemplate.delete();
        return entity;
    }


}
