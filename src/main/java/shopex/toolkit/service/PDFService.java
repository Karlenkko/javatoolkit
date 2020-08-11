package shopex.toolkit.service;

import org.springframework.stereotype.Service;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class PDFService {
    public static final String FONT = "./src/main/resources/font/siyuan-song.ttf";

    public static void fillForm(String inputPath, String outputPath, Map<String, String> data) throws IOException {
        File file = new File(outputPath);
        file.getParentFile().mkdirs();
        // crate partially flattened form
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputPath), new PdfWriter(outputPath));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.setGenerateAppearance(true);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H);
        Map<String, PdfFormField> fields = form.getFormFields();
        data.forEach((key, value)->{
            fields.get(key).setValue(value, font, 10);
        });
        form.flattenFields();
        pdfDoc.close();
    }
}
