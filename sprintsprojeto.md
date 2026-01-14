
Visão Geral das Sprints

I) Sprint 0
Duração: 03-08
Objetivo: preparar terreno técnico, reuniões iniciais, Épico e Casos de Uso.

II) Sprint 1
Duração: 09 a 11
Objetivo: funcionalidade principal e regra de negócio completa

III) Sprint 2
Duração: 12 a 15
Objetivo: consolidação, gráficos, testes e build release

Entrega planejada : 14 (Noite) ou 15 (prazo máximo).

Resumidamente 

SPRINT 0 – Preparação e Base Técnica
Data: 03-08

Essa sprint existe para evitar retrabalho. É curta e focada.

Objetivo
Garantir que a base do projeto esteja estável antes de entrar em operação.

Itens do Backlog
• Criação do Épico , Casos de Uso
• Criação do projeto no Android Studio em Kotlin
• Definição da estrutura MVC
• Configuração de navegação entre Activities
• Criação dos modelos de domínio principais
Transaction
Pessoa
• Leitura e parsing dos arquivos JSON locais
pessoas.json
rateio_familia.json
• Criação da interface TransactionRepository
• Decisão e configuração inicial do banco principal
Room como default
• Configuração do DatePicker padrão
• Setup inicial de estilos e layouts base

Entregável da Sprint 0
Projeto compilando
Arquitetura definida
JSONs lidos corretamente
Sem funcionalidade visível ao usuário ainda

SPRINT 1 – Núcleo Funcional do Produto
Datas: 09 a 11

Aqui entra o coração do app. Se isso funcionar, o projeto está salvo.

Objetivo
Entregar o fluxo completo de uso básico com regra de negócio correta solicitada pelo requerimento principal.

Itens do Backlog
Autenticação
• Tela de login
• Autenticação biométrica com fallback
• Redirecionamento correto após sucesso

***** Lançamentos
• Tela de lançamento funcional
• Validação de campos
• Spinner de pessoas carregado do JSON
• Persistência de lançamentos no banco
• Diferenciação entre crédito e débito

Regra de Rateio
• Implementação da lógica de rateio familiar
• Cálculo dinâmico sem persistência
• Aplicável a crédito e débito

***** Extrato
• Tela de extrato
• Filtro por pessoa e família
• Aplicação correta do rateio
• Ordenação por data decrescente
• Exibição de saldo final
• Diferenciação visual entre C e D

Entregável da Sprint 1

App utilizável de ponta a ponta
Regra de rateio validada manualmente
Extrato matematicamente correto
Persistência funcional

Esse é o ponto em que o produto já resolve o problema principal.

SPRINT 2 – Gráficos, Qualidade e Entrega

Datas: 12 a 15

Sprint de consolidação. Aqui se fecha o projeto.

Objetivo

Adicionar visão analítica, garantir estabilidade e gerar o build final.

Items do Backlog
Gráficos
• Tela de gráfico comparativo
• Série temporal dos últimos 6 meses
• Agregação mensal de crédito e débito
• Reuso exato da lógica de extrato e rateio
• Exibição do resultado líquido final

Qualidade
• Testes unitários da lógica de rateio
• Testes unitários de agregação mensal
• Testes básicos de navegação
• Ajustes visuais finais
• Tratamento de estados vazios

Entrega

• Revisão geral de código
• Limpeza de logs e código morto
• Configuração de build em modo release
• Geração do AAB assinado
• Validação final de execução

Entregável da Sprint 2

Entrega Final – Dia 15

Build em modo release
App funcional
Regra de negócio consistente entre telas
Repo para entrega ao professor




