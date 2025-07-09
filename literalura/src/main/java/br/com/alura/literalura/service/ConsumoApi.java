package br.com.alura.literalura.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component

public class ConsumoApi {

    public String obterDados(String endereco) {
        // Cria o cliente HTTP
        HttpClient client = HttpClient.newHttpClient();

        // Cria a requisição HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();

        // Envia a requisição e captura a resposta
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Erro HTTP: " + response.statusCode() + " - " + response.body());
                return null;
            }

            return response.body();

        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            System.err.println("Erro ao consumir API: " + endereco + " - " + e.getMessage());
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            return null;
        }
    }
}

