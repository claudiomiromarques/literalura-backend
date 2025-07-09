package br.com.alura.literalura.model.DTO;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean isLast,
        boolean isFirst
) {
    public PaginatedResponseDTO(Page<T> page) {
        this(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst()
        );
    }

}
