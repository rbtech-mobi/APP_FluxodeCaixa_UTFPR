

Documentação de Encerramento: CaixaAPP

1. Visão do Produto
O CaixaAPP é um ecossistema nativo Android (Kotlin) para gestão financeira familiar. Ele se destaca pela robustez arquitetural (MVC) e pela flexibilidade de configuração. Através de um motor de inteligência que consome dados de arquivos JSON, o app oferece segurança biométrica configurável, rateio dinâmico de despesas e uma interface analítica completa.

2. Épicos e Histórias de Usuário (Requisitos Atendidos)
ÉPICO 1: Segurança e Acesso Seguro (Feature Toggle)
Objetivo: Garantir a privacidade dos dados através de autenticação nativa.

Estória de Usuário: Como usuário, quero que meu acesso seja protegido por biometria ou senha do sistema, podendo essa função ser alternada conforme as configurações do app.

Critérios de Aceite (Realizados):

[X] Integração com o arquivo parametros_app.json: o parâmetro login_seguro (1 para ligado, 0 para desligado) controla dinamicamente a exigência de biometria.
[X] Uso de autenticação nativa do Android (PIN, Padrão ou Digital).

ÉPICO 2: Cadastro e Lançamentos Financeiros
Objetivo: Permitir a entrada de dados de forma simples e validada.

Estória de Usuário: Como usuário, quero registrar minhas movimentações financeiras com precisão para manter meu histórico atualizado.

Critérios de Aceite (Realizados):

[X] Tela de Lançamento: Campos para Descrição, Valor (R$), Data e Tipo (Receita/Despesa).
[X] DatePicker (Plus): Implementação de calendário para seleção amigável da data.
[X] Consistência de Dados: Validação de campos obrigatórios e tratamento de exceções em tipos numéricos para evitar crashes.

ÉPICO 3: Persistência e Arquitetura (MVC)
Objetivo: Garantir a integridade dos dados e a manutenibilidade do código.

Estória de Usuário: Como desenvolvedor, quero que o código seja organizado em camadas para facilitar futuras expansões.

Critérios de Aceite (Realizados):

[X] Persistência: Implementação de banco de dados local SQLite.
[X] Organização: Uso de DatabaseHelper para infraestrutura e LancamentoDAO para a lógica de acesso a dados (CRUD).
[X] Navegabilidade: Fluxo de navegação consistente e intuitivo entre as telas de Cadastro, Extrato e Gráficos.

ÉPICO 4: Extrato e Inteligência Analítica
Objetivo: Transformar registros em informações úteis para o planejamento familiar.

Estória de Usuário: Como usuário, quero visualizar meu saldo e meus gastos categorizados por cores e gráficos.

Critérios de Aceite (Realizados):

[X] Tela de Extrato: Listagem completa de todos os registros persistidos.
[X] Diferenciação Visual (Plus): Adapter customizado exibindo Receitas em VERDE e Despesas em VERMELHO.
[X] Cálculo de Saldo (Plus): Exibição da soma dinâmica (Receitas - Despesas) no cabeçalho do extrato.
[X] Gráficos e JSON: Motor de leitura de assets para rateio familiar e exibição de gráficos via MPAndroidChart.


