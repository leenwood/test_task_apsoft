package leenwood.junior.apsoft.controller;


import leenwood.junior.apsoft.model.ExceptionModelResponse;
import leenwood.junior.apsoft.model.Section;
import leenwood.junior.apsoft.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequestMapping("/api/file")
@RestController
public class FileRestController {

    private final FileService fileService;

    public FileRestController(FileService fileService) {
        this.fileService = fileService;
    }


    @PutMapping
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return file.getContentType();
        }
        return this.fileService.uploadFile(file).toString();
    }

    @GetMapping("{uuid}")
    public Section getSectionById(@PathVariable UUID uuid) throws Exception {
        return this.fileService.getSectionById(uuid);
    }


    @ExceptionHandler(Throwable.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionModelResponse exceptionListener(Throwable exception) {
        return new ExceptionModelResponse(exception.getMessage());
    }
}
