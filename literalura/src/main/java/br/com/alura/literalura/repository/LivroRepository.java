package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByTituloIgnoreCase(String titulo);
    List<Livro> findAllByIdiomaContainingIgnoreCase(String idioma);

    @EntityGraph(attributePaths = {"autor"})
    List<Livro> findFirst10ByOrderByNumeroDownloadsDesc();

    @Query(value = "SELECT l FROM Livro l JOIN FETCH l.autor",
            countQuery = "SELECT COUNT(l) FROM Livro l")
    Page<Livro> findAllComAutores(Pageable pageable);

    @Query("SELECT l FROM Livro l JOIN FETCH l.autor")
    List<Livro> findAllComAutores();

    @Query("SELECT l FROM Livro l JOIN FETCH l.autor WHERE l.id = :id")
    Optional<Livro> findByIdComAutor(@Param("id") Long id);

    @Query(value = "SELECT l FROM Livro l JOIN FETCH l.autor WHERE l.idioma LIKE %:idioma%",
            countQuery = "SELECT COUNT(l) FROM Livro l WHERE l.idioma LIKE %:idioma%")
    Page<Livro> findByIdiomaContainingIgnoreCase(@Param("idioma") String idioma, Pageable pageable);
}
