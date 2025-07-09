package br.com.alura.literalura.model.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DadosFormatos(
        @JsonAlias("image/jpeg") String imagemJpeg) {
}
