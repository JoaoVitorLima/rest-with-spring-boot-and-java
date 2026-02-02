package br.com.erudio.file.importer.impl;

import br.com.erudio.data.dto.PersonDTO;
import br.com.erudio.file.importer.contract.FileImporter;

import java.io.InputStream;
import java.util.List;

public class CsvImporter implements FileImporter {

    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        return List.of();
    }
}
