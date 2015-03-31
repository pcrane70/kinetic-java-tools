package com.seagate.kinetic.tools.management.cli.impl.util;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtil {
    public static String toJson(Object obj) throws JsonGenerationException,
            JsonMappingException, IOException {
        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonGenerator jsonGenerator = null;
        StringWriter out = new StringWriter();
        jsonGenerator = jsonFactory.createJsonGenerator(out);
        objectMapper.writeValue(jsonGenerator, obj);
        jsonGenerator.close();
        return out.toString();
    }
}
