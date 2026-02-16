package br.com.erudio.file.exporter.factory;

import br.com.erudio.exception.BadRequestlException;
import br.com.erudio.file.exporter.MediaTypes;
import br.com.erudio.file.exporter.contract.PersonExporter;
import br.com.erudio.file.exporter.impl.CsvExporter;
import br.com.erudio.file.exporter.impl.PdfExporter;
import br.com.erudio.file.exporter.impl.XlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileExporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public PersonExporter getExporter(String acceptHeader) throws Exception {
        if (acceptHeader.equals(MediaTypes.APPLICATION_XLSX_VALUE)) {
            return context.getBean(XlsxExporter.class);
            //return new XlsxImporter();
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return context.getBean(CsvExporter.class);
            //return new CsvImporter();
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return context.getBean(PdfExporter.class);
            //return new CsvImporter();
        } else {
            throw new BadRequestlException("invalid File Format!");
        }
    }
}
