# TransCarga

Aplicação web para gestão de fretes. Cadastro e listagem de fretes, com autenticação de usuários.

## Stack

- Java 17, Jakarta EE 9+
- Hibernate 6.4 (JPA)
- MariaDB 10.11
- Tomcat 10
- Maven

## Pré-requisitos

- JDK 17
- Maven 3.9+
- Docker e Docker Compose (para o modo recomendado)

## Configuração

A conexão com o banco é definida por variáveis de ambiente. Sem elas, a aplicação não inicia.

| Variável      | Descrição                       | Exemplo            |
| ------------- | ------------------------------- | ------------------ |
| `DB_HOST`     | Host do MariaDB                 | `mariadb`          |
| `DB_PORT`     | Porta do MariaDB                | `3306`             |
| `DB_NAME`     | Nome do banco                   | `Transcarga`       |
| `DB_USER`     | Usuário do banco                | `transcarga_user`  |
| `DB_PASSWORD` | Senha do usuário                | `sua-senha`        |

## Execução com Docker (recomendado)

Sobe a aplicação e o banco em containers:

```bash
mvn package
docker-compose up --build
```

A aplicação fica disponível em [http://localhost:8080/](http://localhost:8080/).

As variáveis de ambiente já estão definidas em `docker-compose.yml`. Para alterar a senha ou demais credenciais em produção, edite o arquivo ou utilize um `.env` (não versionado).

Para encerrar:

```bash
docker-compose down
```

Para remover também os dados persistidos do banco:

```bash
docker-compose down -v
```

## Execução em Tomcat standalone

1. Gerar o WAR:

   ```bash
   mvn package
   ```

2. Copiar `target/transcarga.war` para `<TOMCAT_HOME>/webapps/`.

3. Definir as variáveis de ambiente antes de iniciar o Tomcat.

   PowerShell:

   ```powershell
   $env:DB_HOST="localhost"
   $env:DB_PORT="3306"
   $env:DB_NAME="Transcarga"
   $env:DB_USER="transcarga_user"
   $env:DB_PASSWORD="sua-senha"
   ```

   Bash:

   ```bash
   export DB_HOST=localhost \
          DB_PORT=3306 \
          DB_NAME=Transcarga \
          DB_USER=transcarga_user \
          DB_PASSWORD=sua-senha
   ```

4. Iniciar o Tomcat. A aplicação fica disponível em [http://localhost:8080/transcarga/](http://localhost:8080/transcarga/).

## Estrutura

```
src/main/java/br/com/transcarga/
├── negocios/        # Servlets, filtros e listeners
└── persistencia/    # Entidades JPA, DAOs e JpaUtil

src/main/webapp/     # Páginas HTML, CSS e assets estáticos
src/main/resources/META-INF/persistence.xml   # Unidade de persistência
```

## Build

```bash
mvn clean package
```

O artefato gerado é `target/transcarga.war`.
