package shopex.toolkit.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class APIController {


    @PostMapping("/pdfform")
    @ResponseBody
    public String PDFForm (
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file
            ) throws IOException {
        if (file.isEmpty()) return "error";
        String fileName = file.getOriginalFilename();
        String path = request.getServletContext().getRealPath("/upload/pdf");
        File filePath = new File(path, fileName);
        if (!filePath.getParentFile().exists()) {
            filePath.getParentFile().mkdirs();
        }
        file.transferTo(new File(path + File.separator + fileName));
//        file added to local dir
        return "success";
    }
}
