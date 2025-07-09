package br.com.alura.literalura.model.DTO;

import br.com.alura.literalura.model.Topico;

import java.util.List;
import java.util.stream.Collectors;

public record LivroResponseDTO(
        Long id,
        String titulo,
        AutorResponseDTO autor,
        String idioma,
        Integer numeroDownloads,
        String posterUrl,
        List<String> topicos
) {

    public LivroResponseDTO(br.com.alura.literalura.model.Livro livro) {
        this(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor() != null ? new AutorResponseDTO(livro.getAutor()) : null,
                livro.getIdioma(),
                livro.getNumeroDownloads(),
                livro.getPosterUrl(),
                livro.getTopicos().stream().map(Topico::getNome).collect(Collectors.toList())
        );
    }
}
