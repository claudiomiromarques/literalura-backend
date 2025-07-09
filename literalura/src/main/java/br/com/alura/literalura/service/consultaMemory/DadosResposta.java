package br.com.alura.literalura.service.consultaMemory;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record DadosResposta(
        @JsonAlias("translatedText") String textoTraduzido)
 {
}
