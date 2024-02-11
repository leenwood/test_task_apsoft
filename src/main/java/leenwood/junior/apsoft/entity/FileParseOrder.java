package leenwood.junior.apsoft.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class FileParseOrder {

    @Id
    @GeneratedValue
    private UUID id;

    private String filePath;

    private String fileType;

    private String fileName;

    private Boolean processing = false;

    private Boolean complete = null;
}
