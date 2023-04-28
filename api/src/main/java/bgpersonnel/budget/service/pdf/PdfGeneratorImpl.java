package bgpersonnel.budget.service.pdf;


import bgpersonnel.budget.exeception.GenerationRapportException;
import com.itextpdf.html2pdf.HtmlConverter;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class PdfGeneratorImpl<T> implements PdfGenerator<T> {

    private final FreeMarkerConfigurer freeMarkerConfigurer;

    public PdfGeneratorImpl(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;

    }

    @Override
    public ByteArrayInputStream generatePdf(List<T> data, String templateHtml, String[] header) {

        // Créez un contexte Freemarker pour remplir le modèle
        Map<String, Object> context = new HashMap<>();
        context.put("data", data);
        context.put("header", header);

        try {
            // Chargez le modèle Freemarker à partir du fichier HTML
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateHtml);
            // Remplissez le modèle avec les données
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
            // Générez le PDF à partir du HTML
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Erreur lors de la génération du fichier PDF", e);
            throw new GenerationRapportException("Erreur lors de la génération du fichier PDF: " + e.getMessage());
        }

    }


}
