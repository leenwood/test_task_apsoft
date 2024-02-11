package leenwood.junior.apsoft.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import leenwood.junior.apsoft.model.Section;
import org.springframework.stereotype.Service;
import org.springframework.util.RouteMatcher;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ConverterService {


    public int saveFileToJson(Section rootSection, UUID id, String route) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(String.format("%s/%s.json", route, id)), rootSection);
        return 1;
    }


    public Section toJavaObject(String route) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(route), Section.class);
    }


}
