package leenwood.junior.apsoft.repository;

import leenwood.junior.apsoft.entity.FileParseOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileParseOrderRepository extends CrudRepository<FileParseOrder, UUID> {

    public FileParseOrder findOneById(UUID id);

}
