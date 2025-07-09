# 📚 LiterAlura - Catálogo de Livros

Bem-vindo ao **LiterAlura**, um projeto Java desenvolvido com Spring Boot que oferece um **catálogo interativo de livros via console**. O sistema permite buscar, salvar e consultar livros e autores diretamente de uma **API pública de obras literárias**, armazenando as informações em um banco de dados relacional.

---

## 🎯 Objetivo

Desenvolver um sistema Java que permita ao usuário interagir com um **catálogo de livros via terminal**, implementando no mínimo **5 funcionalidades interativas**, incluindo:

- Buscar livros por título e salvá-los no banco de dados
- Listar livros e autores cadastrados
- Consultar livros por idioma
- Exibir estatísticas de downloads
- Buscar autores por nome, ano de nascimento ou se estavam vivos em determinado ano

---

## 🚀 Funcionalidades Implementadas

- ✅ Busca de livros pela [API Gutendex](https://gutendex.com/)
- ✅ Armazenamento de dados com Spring Data JPA
- ✅ Tradução automática de tópicos via [MyMemory API](https://mymemory.translated.net/)
- ✅ Interface interativa via terminal (CLI)
- ✅ Estatísticas de downloads com `DoubleSummaryStatistics`
- ✅ Tratamento de exceções e logs informativos

---

## 🛠️ Tecnologias Utilizadas

- Java 17+
- Spring Boot 3
- Spring Data JPA
- Hibernate
- MySQL ou PostgreSQL (configurável)
- API HTTP Client (Java 11+)
- Jackson (para leitura de JSON)
- Maven

---

## 🔄 Estrutura do Projeto

literalura/
├── model/ # Entidades JPA (Livro, Autor, Tópico)
├── repository/ # Repositórios Spring Data
├── service/ # Regras de negócio e consumo de APIs
├── principal/ # Classe Principal com menu interativo
├── resources/ # application.properties
└── LiteraluraApplication.java

---

## 📦 Como Executar o Projeto

### Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- MySQL ou outro banco de dados compatível (configurar no `application.properties`)

### Passos

1. **Clone o projeto:**
   ```bash
   git clone  https://github.com/claudiomiromarques/literalura-backend.git
   cd literalura

Configure o banco de dados:
Edite o arquivo src/main/resources/application.properties com as credenciais do seu banco de dados.

Interação via console..

*** LiterAlura - Catálogo de Livros ***
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

📈 Backlog / Em desenvolvimento
Integração com outras APIs literárias

Interface web (Spring MVC ou Thymeleaf)

Testes automatizados com JUnit

📜 Licença
Este projeto é licenciado sob os termos da MIT License.

👨‍💻 Desenvolvido por
Projeto prático da formação Java + Spring da Alura, com adaptações e melhorias próprias.

   
