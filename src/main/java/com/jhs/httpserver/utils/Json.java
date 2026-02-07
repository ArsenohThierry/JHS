package com.jhs.httpserver.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Json {
    private static ObjectMapper objectMapper = defaultObjectMapper();

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //Empeche le programme de crasher si il y  a un proble de proprietes manquantes 
        return om;
    }

    public static JsonNode parse(String json) throws Exception {
        return objectMapper.readTree(json);
    }

    public static <A> A fromJson(JsonNode node, Class<A> clazz) throws JsonProcessingException, IllegalArgumentException {
        return objectMapper.treeToValue(node, clazz);
    }

    public static JsonNode toJson(Object obj) {
        return objectMapper.valueToTree(obj);
    }

    private static String generateJson(Object obj, boolean pretty) throws JsonProcessingException {
        ObjectWriter objectWritter = objectMapper.writer();
        if (pretty) {
            objectWritter = objectWritter.with(SerializationFeature.INDENT_OUTPUT);
        }
        return objectWritter.writeValueAsString(obj);
    }

    public static String stringifier(JsonNode node) throws JsonProcessingException{
        return generateJson(node,false);
    }

        public static String stringifierMeilleur(JsonNode node) throws JsonProcessingException{
        return generateJson(node,true);
    }
}
