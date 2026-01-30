package br.com.erudio.controllers.docs;

import br.com.erudio.data.dto.UploadFileResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File Endpoint")
public interface FileControllerDocs {

    UploadFileResponseDTO UploadFile(MultipartFile file);
    List<UploadFileResponseDTO> UploadMultipleFiles(MultipartFile[] files);
    ResponseEntity<ResponseEntity> downloadFile(String fileName, HttpServletRequest request);
}
