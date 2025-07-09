package br.com.alura.literalura.model;

import br.com.alura.literalura.model.DTO.DadosLivro;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "livros")

public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String titulo;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "livro_topico",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "topico_id")
    )
    private Set<Topico> topicos = new HashSet<>();

    private String idioma;
    private Integer numeroDownloads;
    private Integer idApi;
    private String posterUrl;

    public Livro() {}

    public Livro(DadosLivro dadosLivro) {
        this.idApi = dadosLivro.idApi();
        this.titulo = dadosLivro.titulo();
        this.idioma = dadosLivro.idiomas() != null && !dadosLivro.idiomas().isEmpty() ?
                String.join(",", dadosLivro.idiomas()) : "Desconhecido";
        this.numeroDownloads = dadosLivro.numeroDownloads();
        if (dadosLivro.formatos() != null && dadosLivro.formatos().imagemJpeg() != null) {
            this.posterUrl = dadosLivro.formatos().imagemJpeg(); // << ATRIBUIR AQUI
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public Integer getNumeroDownloads() { return numeroDownloads; }
    public void setNumeroDownloads(Integer numeroDownloads) { this.numeroDownloads = numeroDownloads; }
    public Integer getIdApi() { return idApi; }
    public void setIdApi(Integer idApi) { this.idApi = idApi; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public Set<Topico> getTopicos() {
        return topicos;
    }

    public void setTopicos(Set<Topico> topicos) {
        this.topicos = topicos;
    }

    @Override
    public String toString() {
        String nomeAutor = (autor != null && autor.getNome() != null) ? autor.getNome() : "Autor desconhecido";
        return String.format("""
                ----- LIVRO -----
                Título: %s
                Autor: %s
                Idioma: %s
                Número de Downloads: %d
                Poster: %s
                -----------------
                """, titulo, nomeAutor, idioma, numeroDownloads, posterUrl != null ? posterUrl : "N/A");
    }
}


