package shopex.toolkit.controller;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class APIController {


    @PostMapping("/pdfform")
    @ResponseBody
    public String PDFForm (
            HttpServletRequest request,
            @RequestPart("file") MultipartFile file
            ) throws IOException {
        if (file.isEmpty()) return "error";
        String fileName = file.getOriginalFilename();
        String path = request.getServletContext().getRealPath("/upload/pdf");
        File filePath = new File(path, fileName);
        if (!filePath.getParentFile().exists()) {
            filePath.getParentFile().mkdirs();
        }
        file.transferTo(new File(path + File.separator + fileName));
        // file added to local dir
        String inputPath = path + File.separator + fileName;
        String outputPath = request.getServletContext().getRealPath("/download/pdf") + File.separator + fileName;
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputPath), new PdfWriter(outputPath));
        PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdfDoc, true);
        // fill forms

        Map<String, String[]> formData = request.getParameterMap();
        Map<String, PdfFormField> formFields = pdfAcroForm.getFormFields();
        formFields.forEach((key, value)-> {
            try {
                FontProgram fontProgram = FontProgramFactory.createFont();
                PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, false);
                PdfFormField agreementId = formFields.get(key);
                agreementId.setFont(font);
                agreementId.setValue(formData.get(key)[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pdfAcroForm.flattenFields();
        pdfDoc.close();
        return "success";
    }
}
