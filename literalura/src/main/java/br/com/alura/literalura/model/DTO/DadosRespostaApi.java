package br.com.alura.literalura.model.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record DadosRespostaApi(
        @JsonAlias("count") Integer total,
        @JsonAlias("results") List<DadosLivro> livros) {
}
