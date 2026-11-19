# Plano de trabalho — Gestão Financeira de Projetos Científicos

Disciplina: Programação para Dispositivos Móveis (UNISC) — trabalho final
Integrantes: João Pedro da Silveira Chagas, João Vitor Kaspary da Silveira e
Pablo Renan Nichterwistz
Linguagem: **Java** (Android nativo), no Android Studio
Escopo: **Opção 1** do documento de requisitos (GP-01 a GP-10)

## Objetivo

App Android em que pesquisadores, coordenadores e equipes consultam e registram
a execução do orçamento de projetos de pesquisa: projetos, rubricas, receitas e
despesas, comprovantes, dashboard com saldo e alertas, e relatórios em PDF ou
planilha.

O documento manda o app falar **só com uma API REST autenticada (JWT)**, nunca
direto com o banco. Como essa API não existe, o trabalho tem duas partes:

| Parte | O quê |
|---|---|
| `android/` | o app em Java: o produto principal do trabalho |
| `backend/` | API em Java (Spring Boot + H2), só para o app ter com quem falar |

> **Decisão da Fase 0:** se o professor aceitar uma API simulada, o `backend/`
> pode virar dados falsos dentro do app (classe `ApiFalsa`). O plano assume o
> backend mínimo, porque é a forma de cumprir login JWT, upload de comprovante
> e controle de acesso de verdade, e porque é em Java, que o grupo já usa.

## Divisão do trabalho

Cada integrante fica com **fatias de ponta a ponta**: o endpoint no backend e
as telas no app daquela funcionalidade. Assim ninguém fica só com backend ou
só com tela, e cada um consegue explicar código Android para o professor.

O trabalho anda em três rodadas. Em cada rodada, cada um faz uma fase. A letra
diz de quem é: **A = Pablo**, **B = João Vitor**, **C = João Pedro**.

| Rodada | Pablo (A) | João Vitor (B) | João Pedro (C) |
|---|---|---|---|
| 1 — Fundação | **1A** Backend base e login JWT | **1B** App base e tela de login | **1C** Visual e utilitários |
| 2 — Consulta | **2A** Projetos e rubricas | **2B** Lançamentos: lista, filtros, detalhe | **2C** Dashboard e alertas |
| 3 — Cadastro e saída | **3A** Relatório PDF/CSV | **3B** Lançamentos: formulário e edição | **3C** Comprovantes |
| 4 — Entrega | endpoints e permissões em `docs/`, README | falhas de rede em todas as telas, APK | telas e fluxos em `docs/`, prints |

Ajustem os nomes na Fase 0 se quiserem trocar. O importante é manter uma fase
por pessoa em cada rodada.

### Dependências entre as fases

- **1B precisa do login da 1A.** O Pablo sobe o `POST /auth/login` primeiro.
  Até lá, o João Vitor monta a tela seguindo o contrato da API.
- A **rodada 2** começa quando 1A, 1B e 1C estiverem na `main`.
- Os dados de exemplo da 1A já trazem projeto, rubricas e lançamentos. Por
  isso 2A, 2B e 2C não esperam umas pelas outras.
- **3A** usa a lista da 2B e o resumo da 2C. **3C** entra na tela de detalhe
  da 2B. Os dois só começam depois que a rodada 2 estiver na `main`.

### Git com três pessoas

- Uma branch por fase (`2b-lancamentos-lista`), pull request para a `main`, e
  **outro integrante revisa** antes do merge. Ninguém commita direto na `main`.
- Puxar a `main` para a sua branch todo dia (`git merge main`), para os
  conflitos chegarem pequenos.
- Arquivos que todos mexem, cada um no seu pedaço:
  - `strings.xml`: prefixo por área (`comum_`, `login_`, `proj_`, `rub_`,
    `lanc_`, `dash_`, `rel_`, `comp_`), em blocos separados por comentário
  - `ApiServico.java`: um bloco comentado por área
  - `AndroidManifest.xml` e `libs.versions.toml`: avisar o grupo antes de mexer

## Requisitos → onde ficam

| Código | Requisito (resumo) | Tela / parte | Fase |
|---|---|---|---|
| GP-01 | Login, seleção de projeto, perfis coordenador / gestor / consulta | Login, Projetos | 1A, 1B, 2A |
| GP-02 | Cadastro/consulta de projetos (vigência, fonte, responsáveis, orçamento) | Projetos, Cadastro de projeto | 2A |
| GP-03 | Rubricas e subrubricas configuráveis | Rubricas | 2A |
| GP-04 | Lançamento de receita/despesa: data, descrição, rubrica, qtd, valor unit., total, situação | Novo lançamento | 3B |
| GP-05 | Comprovante por foto ou arquivo, enviado à API | Detalhe do lançamento | 3C |
| GP-06 | Dashboard: receita, despesa, saldo, previsto × executado, participação por rubrica | Dashboard | 2C |
| GP-07 | Filtros por projeto, período, rubrica e situação; histórico | Lançamentos, Detalhe do lançamento | 2B |
| GP-08 | Relatório resumido e exportar/compartilhar em PDF ou planilha | Relatório | 3A |
| GP-09 | Validações, confirmação de alterações, autoria e data | Novo lançamento, Detalhe | 2B, 3B |
| GP-10 | Alerta visual de rubrica perto do limite ou sem saldo (regra do servidor) | Rubricas, Dashboard | 2A, 2C |

## Antes de começar

- [ ] **Android Studio** instalado (traz o Android SDK, o emulador e o JDK)
- [ ] No Android Studio: Device Manager → criar um emulador (Pixel, API 34 ou
      mais nova)
- [ ] Clonar o repositório do grupo: `github.com/pablorenan-dev/pesquisa-`
- [ ] Cada integrante com o projeto abrindo e rodando no próprio computador

### Onde rodar pra visualizar

| O quê | Como |
|---|---|
| **App** | Android Studio → abrir a pasta `android/` → escolher o emulador → **Run ▶** (Shift+F10) |
| **Backend** | `cd backend` e `mvnw.cmd spring-boot:run`. Sobe em `http://localhost:8080` |
| **App falando com o backend** | No emulador, o computador é `http://10.0.2.2:8080`. No celular de verdade, usar o IP do PC na rede (ex.: `http://192.168.0.10:8080`) |

Todo mundo roda o backend na própria máquina, porque as fatias mexem nos dois
lados. O Claude Code roda no terminal do Android Studio, ou no VS Code aberto
na mesma pasta.

### Quem faz o quê

| Quem | O quê |
|---|---|
| Claude Code | escreve o código, roda `gradlew.bat testDebugUnitTest`, `gradlew.bat assembleDebug` e `mvnw.cmd test` |
| Vocês | sobem o backend, rodam o app no emulador, olham a tela e testam |

## Fases

Cada fase é uma conversa curta com o Claude Code. No fim de cada uma: rodar,
testar, commit na branch, pull request e `/clear` antes da próxima.

### Fase 0 — Decisões e contrato da API (os três juntos, sem código)

Ative o plan mode (Shift+Tab) e mande:

> Leia o CLAUDE.md, o AGENTS.md e o PLANO-TRABALHO.md. Me liste as decisões em
> aberto da seção "Decisões da Fase 0" do AGENTS.md com a sua recomendação para
> cada uma, e revise o contrato da API: falta algum endpoint para cobrir GP-01
> a GP-10? Não altere nada.

Decidir em grupo e anotar no AGENTS.md:

- backend mínimo ou API falsa (perguntar ao professor)
- quem pode o quê (tabela de perfis)
- quais situações entram no saldo (só `REALIZADO`?)
- regra do alerta (80% = atenção? estourar o saldo bloqueia ou só avisa?)
- versão mínima do Android (sugestão: 8.0, API 26)
- cores e nome do app
- se a divisão de fatias fica como está

Checkpoint: contrato da API, tabela de perfis e divisão aprovados pelos três.

### Rodada 1 — Fundação

**Fase 1A — Backend base e login (Pablo)**

> Fase 1A: crie o backend em backend/ conforme o AGENTS.md: todas as
> entidades, banco H2, dados de exemplo, resposta de erro padrão e login que
> devolve JWT. Comece pelo login, que a 1B depende dele.

Checkpoint: `mvnw.cmd test` passando. `POST /api/auth/login` no Postman ou no
`curl` devolve um token, e qualquer outra rota sem token dá 401.

**Fase 1B — App base e login (João Vitor)**

> Fase 1B: crie o projeto Android em android/ com as dependências do AGENTS.md,
> a camada de rede (Retrofit, Resultado, SessaoUsuario, AuthInterceptor) e a
> tela de login, que abre uma tela de projetos ainda vazia.

Checkpoint: login certo abre a tela seguinte. Senha errada mostra mensagem.
Backend desligado mostra aviso, sem crash. Fechar e abrir o app mantém o login.

**Fase 1C — Visual e utilitários (João Pedro)**

> Fase 1C: crie o tema e as cores, as strings comuns, o layout reutilizável de
> estado da tela (carregando, erro com "tentar de novo", lista vazia) e as
> classes Dinheiro, Datas e Validacao em util/, com testes JUnit.

Checkpoint: `gradlew.bat testDebugUnitTest` passando com as linhas de
dinheiro da tabela de testes (vírgula, ponto, vazio, `R$ 1.234,56`).

### Rodada 2 — Consulta

**Fase 2A — Projetos e rubricas (Pablo)**

> Fase 2A: endpoints de projetos e rubricas com a regra de alerta, e no app a
> lista de projetos com seleção, o cadastro de projeto e a tela de rubricas e
> subrubricas, respeitando o perfil.

Checkpoint: coordenador cria um projeto e uma rubrica. Usuário de consulta não
vê os botões de criar e editar. Rubrica com 80% ou mais aparece destacada.

**Fase 2B — Lançamentos: lista, filtros e detalhe (João Vitor)**

> Fase 2B: endpoints de listar lançamentos com filtros e de histórico, e no app
> a lista com filtros por período, rubrica, tipo e situação, e o detalhe do
> lançamento com autoria e histórico. Deixe no detalhe o espaço da seção de
> comprovantes para a 3C.

Checkpoint: filtrar por rubrica e por período mostra só o esperado. O detalhe
mostra quem lançou e quando.

**Fase 2C — Dashboard e alertas (João Pedro)**

> Fase 2C: endpoint de resumo do projeto e, no app, o dashboard com receita,
> despesa e saldo, previsto × executado por rubrica, participação por rubrica e
> os alertas que vêm do servidor, com o menu para as outras telas do projeto.

Checkpoint: receita − despesa = saldo na tela, e os números batem com
`GET /resumo` no Postman.

### Rodada 3 — Cadastro e saída

**Fase 3A — Relatório (Pablo)**

> Fase 3A: tela de relatório do projeto com escolha de período, gerando PDF e
> CSV no aparelho a partir do resumo e dos lançamentos, e compartilhando pelo
> menu do Android.

Checkpoint: o PDF abre em outro app, e o CSV abre no Excel ou no Google
Planilhas com acentos certos.

**Fase 3B — Lançamentos: formulário e edição (João Vitor)**

> Fase 3B: endpoints de criar, editar e cancelar lançamento, com validação,
> regra de saldo e gravação de histórico, e no app o formulário com prévia do
> total, validação no campo, confirmação, edição e cancelamento.

Checkpoint: as linhas de lançamento da tabela de testes conferidas na tela. O
histórico do detalhe mostra a edição.

**Fase 3C — Comprovantes (João Pedro)**

> Fase 3C: endpoints de enviar e baixar comprovante, e no detalhe do
> lançamento a seção de comprovantes: foto pela câmera ou arquivo, compressão,
> envio, lista, abrir e reenviar o que falhou.

Checkpoint: tirar foto no emulador, enviar, abrir de novo no detalhe. Negar a
permissão da câmera não pode travar o app.

### Rodada 4 — Entrega (os três)

> Fase 4: revise o tratamento de falhas em todas as telas, rode os testes e o
> lint, remova o que não é usado, deixe comentários curtos em português e me
> explique em tópicos o fluxo do app pra apresentar pro professor.

- [ ] Cada integrante sabe explicar as próprias fatias, do botão na tela até o
      banco (o professor pode perguntar)
- [ ] APK de debug gerado (`gradlew.bat assembleDebug`) e testado num celular
      de verdade
- [ ] `docs/` com telas, fluxo de navegação, endpoints e permissões
      (entregável do PDF)
- [ ] Prints de cada tela, do sem internet e do usuário de consulta
- [ ] Zip sem `build/`, `.gradle/`, `.idea/`, `target/`, `dados/` e
      `local.properties`

## Testes

Dados de exemplo = planilha do PDF (projeto com orçamento de R$ 30.000,00).

| Situação | Esperado | Fase |
|---|---|---|
| Diárias nacionais: 5 × 320,00 | total 1.600,00 | 3B |
| Adaptador USB-C/USB: 2 × 100,00 | total 200,00 | 3B |
| Soma das rubricas do exemplo | 1.600 + 2.000 + 1.900 + 1.000 + 23.500 = 30.000,00 | 2A |
| Receita 559.453,02 e despesa 96.078,83 | saldo 463.374,19 (o painel do PDF) | 2C |
| Quantidade 0, valor vazio ou só `,` | mensagem no campo, sem crash | 1C, 3B |
| Lançamento sem rubrica ou sem data | mensagem no campo, não envia | 3B |
| Despesa maior que o saldo da rubrica | aviso conforme a regra da Fase 0 | 3B |
| Rubrica com 80% ou mais executado | destaque de atenção | 2A, 2C |
| Usuário de consulta | não vê botões de criar e editar. Forçando pela API: 403 | 2A, 3B |
| Token vencido | volta ao login com aviso | 1B |

Também testar:

- [ ] Aceita vírgula e ponto como separador decimal
- [ ] Valores sempre em `R$ 1.234,56`
- [ ] Sem internet ou backend desligado: aviso amigável e botão "tentar de
      novo" em todas as telas
- [ ] Girar a tela não apaga o formulário nem refaz a busca
- [ ] Salvar e cancelar lançamento pedem confirmação
- [ ] O detalhe do lançamento mostra quem lançou e quando

## Dicas pro Claude Code

- **Shift+Tab** alterna entre modo normal, aceitar edições automaticamente e
  plan mode
- **@arquivo** no prompt pra apontar um arquivo
- **Esc** interrompe se ele sair do rumo
- **/clear** entre fases
- Sempre olhar o diff antes de aceitar
- Commit no fim de cada fase. Se algo quebrar, `git checkout .` volta ao último
  commit
