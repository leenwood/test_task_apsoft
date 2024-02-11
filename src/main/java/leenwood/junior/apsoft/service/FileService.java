package leenwood.junior.apsoft.service;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import leenwood.junior.apsoft.entity.FileParseOrder;
import leenwood.junior.apsoft.model.Section;
import leenwood.junior.apsoft.repository.FileParseOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStreamReader;


@Service
public class FileService {

    @Value("${application.save.route}")
    private String saveRoute;

    private final ConverterService converterService;

    private Logger logger = LoggerFactory.getLogger(FileService.class);

    private final FileParseOrderRepository fileParseOrderRepository;

    public FileService(ConverterService converterService, FileParseOrderRepository fileParseOrderRepository) {
        this.converterService = converterService;
        this.fileParseOrderRepository = fileParseOrderRepository;
    }


    public UUID uploadFile(MultipartFile multipartFile) throws IOException {
        FileParseOrder fileParseOrder = new FileParseOrder();

        fileParseOrder.setFileName(multipartFile.getOriginalFilename());
        fileParseOrder.setFileType(multipartFile.getContentType());
        Files.copy(
                multipartFile.getInputStream(),
                Path.of(String.format("%s/%s", this.saveRoute, multipartFile.getName())),
                StandardCopyOption.REPLACE_EXISTING
        );
        fileParseOrder.setFilePath(String.format("%s/%s", this.saveRoute, multipartFile.getOriginalFilename()));
        fileParseOrder.setProcessing(true);
        var writeQuery = this.fileParseOrderRepository.save(fileParseOrder);


        Observable.fromCallable(() -> this.parseFile(multipartFile, writeQuery.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(exitCode -> {
                    if (exitCode == 1 ) {
                        writeQuery.setProcessing(false);
                        writeQuery.setComplete(true);
                    } else {
                        writeQuery.setProcessing(false);
                        writeQuery.setComplete(false);
                    }
                    this.fileParseOrderRepository.save(writeQuery);
                }, Throwable::printStackTrace);

        return writeQuery.getId();
    }

    public int parseFile(MultipartFile file, UUID id) {
        Section rootSection = new Section();
        Section currentSection = rootSection;
        int currentLevel = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int level = getSectionLevel(line);
                String text = line.trim().replaceFirst("^#+\\s*", "");

                if (level > currentLevel) {
                    Section subSection = new Section();
                    subSection.setText(text);
                    currentSection.getSubSections().add(subSection);
                    subSection.setParent(currentSection);
                    currentSection = subSection;
                    currentLevel = level;
                } else {
                    while (level <= currentLevel && currentSection != rootSection) {
                        currentSection = currentSection.getParent();
                        currentLevel--;
                    }

                    Section subSection = new Section();
                    subSection.setText(text);
                    if (currentSection != rootSection) {
                        currentSection.getSubSections().add(subSection);
                    } else {
                        rootSection.getSubSections().add(subSection);
                    }
                    subSection.setParent(currentSection);
                    currentSection = subSection;
                    currentLevel = level;
                }
            }
        } catch (IOException e) {
            this.logger.error(e.toString());
        }

        int result = 0;
        try {
            result = this.converterService.saveFileToJson(rootSection, id, this.saveRoute);
        } catch (IOException e) {
            this.logger.error(e.toString());
            return -1;
        }

        return result;
    }


    private int getSectionLevel(String line) {
        int level = 0;
        while (level < line.length() && line.charAt(level) == '#') {
            level++;
        }
        return level;
    }


    public Section getSectionById(UUID id) throws Exception {

        FileParseOrder fileParseOrder = this.fileParseOrderRepository.findOneById(id);
        if (fileParseOrder == null) {
            throw new Exception("Данный запрос не найден");
        }

        if (fileParseOrder.getComplete() == null || !fileParseOrder.getComplete()) {
            throw new Exception("Обработка файла не закончена");
        }

        Section result;
        try {
            result = this.converterService.toJavaObject(String.format("%s/%s.json", this.saveRoute, id));
        } catch (IOException e) {
            this.logger.error(e.toString());
            throw e;
        }
        return result;
    }
}
