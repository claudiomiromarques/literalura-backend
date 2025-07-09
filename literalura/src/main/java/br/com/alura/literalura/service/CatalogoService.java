package br.com.alura.literalura.service;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.DTO.DadosAutor;
import br.com.alura.literalura.model.DTO.DadosLivro;
import br.com.alura.literalura.model.DTO.DadosRespostaApi;
import br.com.alura.literalura.model.DTO.EstatisticasDTO;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.model.Topico;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.repository.TopicoRepository;
import br.com.alura.literalura.service.consultaMemory.ConsultaMemory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CatalogoService {

    // Repositórios e serviços auxiliares injetados via construtor
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final TopicoRepository topicoRepository;
    private final ConsumoApi consumoApi;
    private final IConverteDados conversor;
    private final ConsultaMemory consultaMemory;

    // Endereço base da API pública do Projeto Gutenberg (com valor default)
    @Value("${literalura.api.baseurl:https://gutendex.com/books/?search=}")
    private String enderecoBaseApi;

    @Autowired
    public CatalogoService(LivroRepository livroRepository,
                           AutorRepository autorRepository,
                           TopicoRepository topicoRepository,
                           ConsumoApi consumoApi,
                           IConverteDados conversor,
                           ConsultaMemory consultaMemory) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.topicoRepository = topicoRepository;
        this.consumoApi = consumoApi;
        this.conversor = conversor;
        this.consultaMemory = consultaMemory;
    }

    /**
     * Busca um livro por título na API externa, e se não existir localmente, salva no banco.
     * Também persiste autor e tópicos associados.
     */
    @Transactional
    public Optional<Livro> buscarEsalvarLivroDaApiPorTitulo(String tituloLivro) {
        try {
            String tituloEncoded = URLEncoder.encode(tituloLivro, StandardCharsets.UTF_8.toString());
            var json = consumoApi.obterDados(enderecoBaseApi + tituloEncoded);

            if (json == null || json.isEmpty()) return Optional.empty();

            DadosRespostaApi dadosResposta = conversor.obterDados(json, DadosRespostaApi.class);
            if (dadosResposta == null || dadosResposta.livros() == null || dadosResposta.livros().isEmpty()) {
                return Optional.empty();
            }

            DadosLivro dadosPrimeiroLivro = dadosResposta.livros().get(0);

            // Verifica se o livro já existe no repositório
            Optional<Livro> livroExistente = livroRepository.findByTituloIgnoreCase(dadosPrimeiroLivro.titulo());
            if (livroExistente.isPresent()) {
                System.out.println("INFO: Livro '" + dadosPrimeiroLivro.titulo() + "' já cadastrado.");
                return livroExistente;
            }

            // Processa o autor e os tópicos do livro
            Autor autorEntity = processarAutor(dadosPrimeiroLivro);
            Set<Topico> topicos = processarTopicos(dadosPrimeiroLivro);

            // Cria e salva a entidade Livro
            Livro novoLivro = new Livro(dadosPrimeiroLivro);
            novoLivro.setAutor(autorEntity);
            novoLivro.setTopicos(topicos);

            System.out.println("INFO: Salvando novo livro: " + novoLivro.getTitulo());
            return Optional.of(livroRepository.save(novoLivro));

        } catch (Exception e) {
            System.err.println("Erro inesperado no serviço ao buscar e salvar livro: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Busca um autor existente no repositório ou cria um novo a partir dos dados recebidos da API.
     */
    private Autor processarAutor(DadosLivro dadosLivro) {
        if (dadosLivro.autores() == null || dadosLivro.autores().isEmpty()) {
            return null;
        }
        DadosAutor dadosAutorApi = dadosLivro.autores().get(0);
        return autorRepository.findByNomeContainingIgnoreCase(dadosAutorApi.nome())
                .orElseGet(() -> new Autor(dadosAutorApi));
    }

    /**
     * Processa e persiste os tópicos do livro, realizando tradução se necessário.
     * Evita duplicidade no banco e limita nomes a 255 caracteres.
     */
    private Set<Topico> processarTopicos(DadosLivro dadosLivro) {
        Set<Topico> topicos = new HashSet<>();
        if (dadosLivro.subjects() != null && !dadosLivro.subjects().isEmpty()) {
            String idiomaDoLivro = dadosLivro.idiomas().isEmpty() ? "en" : dadosLivro.idiomas().get(0);

            for (String nomeTopico : dadosLivro.subjects()) {
                String nomeFinal = nomeTopico;

                if (!"en".equalsIgnoreCase(idiomaDoLivro)) {
                    try {
                        String traducao = consultaMemory.obterTraducao(nomeTopico, idiomaDoLivro);
                        if (traducao != null && !traducao.isBlank()) {
                            nomeFinal = traducao;
                            System.out.println("Traduzido: '" + nomeTopico + "' -> '" + nomeFinal + "'");
                        } else {
                            System.out.println("INFO: Tradução retornou vazia para '" + nomeTopico + "'. Usando original.");
                        }
                    } catch (Exception e) {
                        System.err.println("AVISO: Falha ao traduzir o tópico '" + nomeTopico + "'. Usando o nome original. Erro: " + e.getMessage());
                    }
                }

                if (nomeFinal.length() > 255) {
                    nomeFinal = nomeFinal.substring(0, 255);
                }

                final String nomeParaLambda = nomeFinal;
                Topico topico = topicoRepository.findByNomeIgnoreCase(nomeParaLambda)
                        .orElseGet(() -> topicoRepository.save(new Topico(nomeParaLambda)));
                topicos.add(topico);
            }
        }
        return topicos;
    }

    // ===================== CONSULTAS E RELATÓRIOS =====================

    /** Lista todos os livros com paginação, incluindo autores. */
    @Transactional(readOnly = true)
    public Page<Livro> listarTodosOsLivros(Pageable pageable) {
        return livroRepository.findAllComAutores(pageable);
    }

    /** Lista todos os livros com autores, sem paginação. */
    @Transactional(readOnly = true)
    public List<Livro> listarTodosOsLivros() {
        return livroRepository.findAllComAutores();
    }

    /** Lista todos os autores com paginação, incluindo livros. */
    @Transactional(readOnly = true)
    public Page<Autor> listarTodosOsAutores(Pageable pageable) {
        return autorRepository.findAllComLivros(pageable);
    }

    /** Lista todos os autores com seus livros. */
    @Transactional(readOnly = true)
    public List<Autor> listarTodosOsAutores() {
        return autorRepository.findAllComLivros();
    }

    /** Busca autores que contenham parte do nome informado. */
    @Transactional(readOnly = true)
    public List<Autor> buscarAutoresPorNome(String nome) {
        List<Autor> autores = autorRepository.findAllByNomeContainingIgnoreCase(nome);
        for (Autor autor : autores) {
            // Garante inicialização de coleções lazy (evita LazyInitializationException)
            Hibernate.initialize(autor.getLivros());
        }
        return autores;
    }

    /** Busca autores por nome com suporte a paginação. */
    @Transactional(readOnly = true)
    public Page<Autor> buscarAutoresPorNome(String nome, Pageable pageable) {
        return autorRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    /** Retorna os 10 livros mais baixados. */
    @Transactional(readOnly = true)
    public List<Livro> buscarTop10LivrosMaisBaixados() {
        return livroRepository.findFirst10ByOrderByNumeroDownloadsDesc();
    }

    /** Calcula estatísticas (média, soma, min, max) sobre os downloads dos livros. */
    @Transactional(readOnly = true)
    public Optional<DoubleSummaryStatistics> calcularEstatisticasDownloads() {
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()) return Optional.empty();

        DoubleSummaryStatistics stats = livros.stream()
                .filter(livro -> livro.getNumeroDownloads() != null && livro.getNumeroDownloads() > 0)
                .mapToDouble(Livro::getNumeroDownloads)
                .summaryStatistics();

        return (stats.getCount() == 0) ? Optional.empty() : Optional.of(stats);
    }

    /** Lista autores nascidos em determinado ano. */
    @Transactional(readOnly = true)
    public List<Autor> listarAutoresPorAnoNascimento(Integer ano) {
        List<Autor> autores = autorRepository.findByAnoNascimento(ano);
        for (Autor autor : autores) {
            Hibernate.initialize(autor.getLivros()); // força o carregamento
        }
        return autores;
    }

    /** Busca livro por ID com autor carregado. */
    @Transactional(readOnly = true)
    public Livro buscarLivroPorId(Long id) {
        return livroRepository.findByIdComAutor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado com o ID: " + id));
    }

    /** Lista autores que estavam vivos em determinado ano (paginado). */
    @Transactional(readOnly = true)
    public Page<Autor> listarAutoresVivosEmDeterminadoAno(int ano, Pageable pageable) {
        return autorRepository.findAutoresVivosNoAno(ano, pageable);
    }

    /** Lista autores vivos em um ano específico (sem paginação). */
    @Transactional(readOnly = true)
    public List<Autor> listarAutoresVivosEmDeterminadoAno(int ano) {
        return autorRepository.findAutoresVivosNoAno(ano);
    }

    /** Lista livros por idioma com paginação. */
    @Transactional(readOnly = true)
    public Page<Livro> listarLivrosPorIdioma(String siglaIdioma, Pageable pageable) {
        return livroRepository.findByIdiomaContainingIgnoreCase(siglaIdioma, pageable);
    }

    /** Lista todos os livros em um determinado idioma. */
    @Transactional(readOnly = true)
    public List<Livro> listarTodosLivrosPorIdioma(String siglaIdioma) {
        return livroRepository.findAllByIdiomaContainingIgnoreCase(siglaIdioma);
    }

    /** Calcula estatísticas e encapsula em DTO com dados formatados. */
    @Transactional(readOnly = true)
    public Optional<EstatisticasDTO> obterEstatisticasDeDownloads() {
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()) return Optional.empty();

        DoubleSummaryStatistics stats = livros.stream()
                .filter(livro -> livro.getNumeroDownloads() != null && livro.getNumeroDownloads() > 0)
                .mapToDouble(Livro::getNumeroDownloads)
                .summaryStatistics();

        if (stats.getCount() == 0) return Optional.empty();

        EstatisticasDTO estatisticasDTO = new EstatisticasDTO(
                stats.getCount(),
                stats.getAverage(),
                (long) stats.getMax(),
                (long) stats.getMin(),
                (long) stats.getSum()
        );
        return Optional.of(estatisticasDTO);
    }
}