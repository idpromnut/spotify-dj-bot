package org.unrecoverable.spotifydjbot.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
public class JsonUtils {
 
    public static byte[] convertObjectToBytes(Object object, boolean wrapRoot) throws IOException {
        return createBaseBuilder()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRoot)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build().writeValueAsBytes(object);
    }
    
    public static String convertObjectToString(Object object, boolean wrapRoot) throws IOException {
    	return convertObjectToString(object, wrapRoot, false);
    }

    public static String convertObjectToString(Object object, boolean wrapRoot, boolean humanFormat) throws IOException {
        return createBaseBuilder()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRoot)
                .configure(SerializationFeature.INDENT_OUTPUT, humanFormat)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build().writeValueAsString(object);
    }

    public static void convertObjectToStream(Object source, OutputStream target, boolean wrapRoot, 
                                            boolean humanFormat) throws IOException {
        
        JsonMapper mapper = createBaseBuilder()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRoot)
                .configure(SerializationFeature.INDENT_OUTPUT, humanFormat)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
        
        mapper.writerFor(source.getClass()).writeValue(target, source);
    }

    public static JsonNode convertBytesToJsonNode(byte[] objectBytes) throws IOException {
    	return createBaseBuilder()
    	    .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
    	    .build().readTree(objectBytes);
    }

    public static <T> T convertBytesToObject(byte[] objectBytes, Class<T> clazz) throws IOException {
        return createBaseBuilder()
            .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
            .build().readValue(objectBytes, clazz);
    }

    public static <T> T convertStringToObject(String jsonString, Class<T> clazz) throws IOException {
        return createBaseBuilder()
            .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
            .build().readValue(jsonString, clazz);
    }
    
    public static <T> T convertStringToObject(String jsonString, Class<T> clazz, boolean unwrapRoot) throws IOException {
        return createBaseBuilder()
            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, unwrapRoot)
            .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
            .build().readValue(jsonString, clazz);
    }
    
    public static void convertStreamToObject(InputStream source, Object target, boolean unwrapRoot) throws IOException {

        JsonMapper mapper = createBaseBuilder()
            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, unwrapRoot)
            .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
            .build();
        
        mapper.readerForUpdating(target).readValue(source);
    }

    private static JsonMapper.Builder createBaseBuilder() {
        return JsonMapper
                .builder()
                .findAndAddModules()
                .addModule(new JavaTimeModule())
                .addModule(new ProtobufModule());
    }
}