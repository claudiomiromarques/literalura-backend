package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component

public class Principal {

    // Scanner para leitura de entrada do usuário via terminal
    private final Scanner leitura = new Scanner(System.in);

    // Serviço principal contendo a lógica de negócio (injeção de dependência)
    private final CatalogoService catalogoService;

    @Autowired
    public Principal(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    /**
     * Exibe o menu interativo da aplicação e processa as opções escolhidas pelo usuário.
     */
    public void exibirMenu() {
        var opcao = -1;
        while (opcao != 0) {
            System.out.println(getMenu());
            opcao = lerOpcaoDoUsuario();

            switch (opcao) {
                case 1 -> processarBuscaESalvamentoDeLivro();
                case 2 -> listarLivrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosEmAno();
                case 5 -> listarLivrosPorIdioma();
                case 6 -> buscarAutorPeloNome();
                case 7 -> listarTop10Livros();
                case 8 -> exibirEstatisticasDeDownloads();
                case 9 -> listarAutoresPorAnoDeNascimento();
                case 0 -> System.out.println("Saindo do LiterAlura...");
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    /**
     * Retorna o conteúdo do menu principal em formato de texto.
     */
    private String getMenu() {
        return """
                \n*** LiterAlura - Catálogo de Livros ***
                1 - Buscar livro pelo título e salvar
                2 - Listar livros registrados
                3 - Listar autores registrados
                4 - Listar autores vivos em um determinado ano
                5 - Listar livros em um determinado idioma
                6 - Buscar autor por nome
                7 - Listar Top 10 livros mais baixados
                8 - Exibir estatísticas de downloads
                9 - Listar autores por ano de nascimento
                0 - Sair
                """;
    }

    private int lerOpcaoDoUsuario() {
        System.out.print("Escolha uma opção: ");
        try {
            return Integer.parseInt(leitura.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Lê um valor inteiro representando ano, com validação de entrada.
     */
    private Optional<Integer> lerAnoDoUsuario(String prompt) {
        System.out.print(prompt);
        try {
            return Optional.of(Integer.parseInt(leitura.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Erro: Entrada inválida. Por favor, digite um número para o ano.");
            return Optional.empty();
        }
    }

    /**
     * Exibe informações detalhadas de um autor, incluindo seus livros.
     */
    private void exibirDetalhesAutor(Autor autor) {
        String titulosLivros = autor.getLivros().stream()
                .map(Livro::getTitulo)
                .collect(Collectors.joining(" | "));

        System.out.printf("""
                Autor: %s (Nasc: %s, Falec: %s)
                Livros: [%s]
                """,
                autor.getNome(),
                autor.getAnoNascimento() != null ? autor.getAnoNascimento().toString() : "N/A",
                autor.getAnoFalecimento() != null ? autor.getAnoFalecimento().toString() : "N/A",
                !titulosLivros.isEmpty() ? titulosLivros : "Nenhum livro registrado"
        );
    }

    private void processarBuscaESalvamentoDeLivro() {
        System.out.print("Digite o título do livro para busca: ");
        var nomeLivro = leitura.nextLine();

        try {
            Optional<Livro> livroProcessado = catalogoService.buscarEsalvarLivroDaApiPorTitulo(nomeLivro);
            livroProcessado.ifPresentOrElse(
                    livro -> {
                        System.out.println("\n Livro salvo com sucesso!");
                        System.out.println(livro); // Usa o toString() customizado do Livro
                    },
                    () -> System.out.println("\n Livro não encontrado na API ou já cadastrado.")
            );
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao processar a busca: " + e.getMessage());
        }
    }

    private void listarLivrosRegistrados() {
        List<Livro> livros = catalogoService.listarTodosOsLivros();

        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado no banco de dados.");
        } else {
            System.out.println("\n--- Livros Registrados ---");
            livros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = catalogoService.listarTodosOsAutores();
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor registrado no banco de dados.");
        } else {
            System.out.println("\n--- Autores Registrados ---");
            autores.forEach(this::exibirDetalhesAutor);
        }
    }

    private void listarAutoresVivosEmAno() {
        lerAnoDoUsuario("Digite o ano para verificar autores vivos: ").ifPresent(ano -> {
            List<Autor> autoresVivos = catalogoService.listarAutoresVivosEmDeterminadoAno(ano);
            if (autoresVivos.isEmpty()) {
                System.out.println("Nenhum autor vivo registrado para o ano de " + ano + ".");
            } else {
                System.out.println("\n--- Autores Vivos em " + ano + " ---");
                autoresVivos.forEach(this::exibirDetalhesAutor);
            }
        });
    }

    private void listarLivrosPorIdioma() {
        System.out.print("Digite o código do idioma (ex: en, pt, es, fr): ");
        String idioma = leitura.nextLine().toLowerCase().trim();

        if (idioma.length() != 2) {
            System.out.println("Código de idioma inválido. Use 2 letras.");
            return;
        }

        List<Livro> livros = catalogoService.listarTodosLivrosPorIdioma(idioma);

        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado para o idioma '" + idioma.toUpperCase() + "'.");
        } else {
            System.out.println("\n--- Livros no Idioma '" + idioma.toUpperCase() + "' ---");
            livros.forEach(System.out::println);
        }
    }

    private void buscarAutorPeloNome() {
        System.out.print("Digite o nome do autor para busca: ");
        String nomeAutor = leitura.nextLine();
        List<Autor> autores = catalogoService.buscarAutoresPorNome(nomeAutor);
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor encontrado com o nome: " + nomeAutor);
        } else {
            System.out.println("\n--- Autores Encontrados ---");
            autores.forEach(this::exibirDetalhesAutor);
        }
    }

    private void listarTop10Livros() {
        List<Livro> top10Livros = catalogoService.buscarTop10LivrosMaisBaixados();
        if (top10Livros.isEmpty()) {
            System.out.println("Não há livros no banco de dados para gerar um Top 10.");
        } else {
            System.out.println("\n--- Top 10 Livros Mais Baixados ---");
            top10Livros.forEach(System.out::println);
        }
    }

    private void exibirEstatisticasDeDownloads() {
        catalogoService.calcularEstatisticasDownloads().ifPresentOrElse(
                stats -> System.out.printf("""
                    \n--- Estatísticas de Downloads ---
                    Total de Livros Analisados: %d
                    Média de Downloads: %.2f
                    Máximo de Downloads: %.0f
                    Mínimo de Downloads: %.0f
                    ---------------------------------
                    """, stats.getCount(), stats.getAverage(), stats.getMax(), stats.getMin()),
                () -> System.out.println("Não foi possível calcular estatísticas (nenhum livro com dados de download).")
        );
    }

    private void listarAutoresPorAnoDeNascimento() {
        lerAnoDoUsuario("Digite o ano de nascimento para buscar autores: ").ifPresent(ano -> {
            List<Autor> autores = catalogoService.listarAutoresPorAnoNascimento(ano);
            if (autores.isEmpty()) {
                System.out.println("Nenhum autor encontrado nascido em " + ano + ".");
            } else {
                System.out.println("\n--- Autores Nascidos em " + ano + " ---");
                autores.forEach(this::exibirDetalhesAutor);
            }
        });
    }
}
