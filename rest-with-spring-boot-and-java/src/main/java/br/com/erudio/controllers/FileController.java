package br.com.erudio.controllers;

import br.com.erudio.controllers.docs.FileControllerDocs;
import br.com.erudio.data.dto.UploadFileResponseDTO;
import br.com.erudio.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file/v1")
public class FileController implements FileControllerDocs {

    @Autowired
    private FileStorageService service;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Override
    public UploadFileResponseDTO UploadFile(MultipartFile file) {
        return null;
    }

    @Override
    public List<UploadFileResponseDTO> UploadMultipleFiles(MultipartFile[] files) {
        return List.of();
    }

    @Override
    public ResponseEntity<ResponseEntity> downloadFile(String fileName, HttpServletRequest request) {
        return null;
    }
}
