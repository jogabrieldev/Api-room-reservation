# API de Reserva de Salas

API REST para gerenciamento de usuários, salas e reservas de ambientes. O projeto foi desenvolvido com Java e Spring Boot, seguindo uma arquitetura em camadas com controllers, services, repositories, DTOs e entidades JPA.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- MySQL
- Springdoc OpenAPI
- Maven
- JUnit e Mockito

## Funcionalidades atuais

- Cadastro e consulta de usuários;
- cadastro, consulta, atualização e desativação de salas;
- consulta de salas disponíveis por período;
- criação e consulta de reservas;
- consulta de reservas por usuário ou sala;
- cancelamento de reservas sem exclusão do histórico;
- validação de intervalos e conflitos de horário;
- bloqueio pessimista para impedir reservas simultâneas conflitantes;
- validação dos dados de entrada;
- tratamento global e padronizado de erros;
- documentação interativa com Swagger/OpenAPI.

## Configuração

Copie o arquivo de exemplo:

```text
src/main/resources/application.example.properties
```

Crie localmente o arquivo `application.properties` e configure as variáveis de conexão com o MySQL. O arquivo real é ignorado pelo Git para evitar a exposição de credenciais.

## Executando o projeto

Com o MySQL configurado e disponível, execute:

```bash
mvn spring-boot:run
```

Para executar os testes:

```bash
mvn test
```

## Documentação Swagger

Com a aplicação em execução, acesse:

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

O Swagger permite consultar os contratos e testar as operações disponíveis diretamente pelo navegador.
