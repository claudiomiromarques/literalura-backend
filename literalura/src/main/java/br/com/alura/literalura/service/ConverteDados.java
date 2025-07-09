package br.com.alura.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ConverteDados implements IConverteDados {

    private ObjectMapper mapper = new ObjectMapper();

    public ConverteDados() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        if (json == null || json.trim().isEmpty()) {
            System.err.println("Erro ao converter JSON: String JSON está nula ou vazia.");
            return null;
        }
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter JSON para a classe " + classe.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }
}
