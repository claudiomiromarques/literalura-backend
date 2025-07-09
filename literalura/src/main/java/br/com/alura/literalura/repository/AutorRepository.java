package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.livros WHERE upper(a.nome) LIKE upper(concat('%', :nomeFiltro, '%'))")
    List<Autor> findAllByNomeContainingIgnoreCase(@Param("nomeFiltro") String nomeFiltro);

    @Query(value = "SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.livros WHERE a.anoNascimento <= :anoBusca AND (a.anoFalecimento IS NULL OR a.anoFalecimento >= :anoBusca)",
            countQuery = "SELECT COUNT(DISTINCT a) FROM Autor a WHERE a.anoNascimento <= :anoBusca AND (a.anoFalecimento IS NULL OR a.anoFalecimento >= :anoBusca)")
    Page<Autor> findAutoresVivosNoAno(@Param("anoBusca") int anoBusca, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.livros WHERE a.anoNascimento <= :anoBusca AND (a.anoFalecimento IS NULL OR a.anoFalecimento >= :anoBusca)")
    List<Autor> findAutoresVivosNoAno(@Param("anoBusca") int anoBusca);

    List<Autor> findByAnoNascimento(Integer anoNascimento);

    @Query(value = "SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.livros",
            countQuery = "SELECT COUNT(DISTINCT a) FROM Autor a")
    Page<Autor> findAllComLivros(Pageable pageable);

    @Query("SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.livros")
    List<Autor> findAllComLivros();

    @Query(value = "SELECT a FROM Autor a WHERE lower(a.nome) LIKE lower(concat('%', :nome, '%'))",
            countQuery = "SELECT count(a) FROM Autor a WHERE lower(a.nome) LIKE lower(concat('%', :nome, '%'))")
    Page<Autor> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

}
