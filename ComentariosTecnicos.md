

DOCUMENTAÇÃO TÉCNICA DO PROJETO: 
APP FLUXO DE CAIXA (UTFPR)

1. OBJETIVO
Desenvolvimento de um aplicativo nativo Android (Kotlin) para controle 
financeiro pessoal, permitindo o registro de receitas e despesas com 
persistência de dados local (Sqlite).

2. ARQUITETURA E PADRÕES DE PROJETO (MVC)
O projeto foi estruturado seguindo o padrão Model-View-Controller (MVC) 
para garantir separação de responsabilidades e facilidade de manutenção:

   - MODEL: Localizado no pacote '.model'. Contém a classe 'Lancamento', 
     que representa a entidade de dados do sistema de forma tipada.
   
   - VIEW: Composta pelos layouts XML em 'res/layout' e pelas Activities. 
     As views são responsáveis apenas pela interação com o usuário.
   
   - CONTROLLER/DATA ACCESS: Implementado através da classe 'LancamentoDAO'. 
     Toda a lógica de banco de dados (SQL) foi isolada das Activities, 
     Seguindo as melhores práticas de engenharia de software.

3. FUNCIONALIDADES IMPLEMENTADAS (CHECKLIST)
[X] Tela de Lançamento: Cadastro com descrição, valor, data e tipo.
[X] Tela de Extrato: Listagem completa dos registros.
[X] Persistência: Uso de SQLite através do DatabaseHelper e do LancamentoDAO.
[X] Navegabilidade: Fluxo consistente entre as telas de cadastro e lista.
[X] Consistência de Dados: Validação de campos obrigatórios e tratamento 
    de tipos numéricos para evitar crashes.
[X] Diferenciação Visual (Plus): Uso de um Adapter customizado que exibe 
    Receitas em VERDE e Despesas em VERMELHO.
[X] Cálculo de Saldo (Plus): Soma dinâmica de entradas e saídas exibida 
    no cabeçalho do extrato.
[X] DatePicker (Plus): Interface amigável para seleção de datas.

4. DETALHES TÉCNICOS
- Linguagem: Kotlin
- Banco de Dados: SQLite
- Componentes UI: RecyclerView/ListView com Custom Adapter, RadioGroup, 
  Material Design Components.

5. ESTRUTURA DE PASTAS
- /model: Classes de dados (POJOs).
- /database: Configuração do SQLite e classes DAO.
- /adapter: Lógica de personalização da lista (Cores e formatação).
- /view: Activities e interface do usuário.




