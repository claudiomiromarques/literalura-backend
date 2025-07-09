package br.com.alura.literalura.model.DTO;

public record EstatisticasDTO(
        long totalLivros,
        double mediaDownloads,
        long maxDownloads,
        long minDownloads,
        long somaDownloads) {
}
