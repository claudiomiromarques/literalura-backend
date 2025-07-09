package br.com.alura.literalura.model.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public record DadosLivro(
        @JsonAlias("id") Integer idApi,
        @JsonAlias("title") String titulo,
        @JsonAlias("authors")
        List<DadosAutor> autores,
        @JsonAlias("languages") List<String> idiomas,
        @JsonAlias("download_count") Integer numeroDownloads,
        @JsonAlias("formats") DadosFormatos formatos,
        @JsonAlias("subjects") List<String> subjects) {


}
