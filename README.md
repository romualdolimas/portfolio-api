## Sobre o projeto

Antes de iniciar a aplicação, crie no PostgreSQL um banco de dados chamado portfolio. Não é necessário criar as tabelas manualmente, pois o Hibernate realiza essa etapa ao iniciar a aplicação.


Esta API foi desenvolvida para gerenciar o portfólio de projetos de uma empresa.

O sistema permite cadastrar e acompanhar projetos, definir responsáveis, associar membros da equipe e controlar a evolução de cada projeto ao longo do seu ciclo de vida.

Também foram implementadas regras de negócio para:

- classificação automática de risco;
- controle de transição de status;
- limite de membros por projeto;
- controle de alocação dos membros;
- validação para exclusão de projetos;
- geração de relatório resumido do portfólio.

O objetivo foi construir uma API organizada, com separação clara entre controller, service e repository, utilizando boas práticas de desenvolvimento com Spring Boot.