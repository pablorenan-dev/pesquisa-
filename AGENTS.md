# AGENTS.md

App Android em **Java** para a gestão financeira de projetos científicos:
projetos, rubricas, receitas e despesas, comprovantes, dashboard e relatórios.
Trabalho final de Programação para Dispositivos Móveis (UNISC), Opção 1 do
documento de requisitos (GP-01 a GP-10).

## Documentos — ler primeiro

- [`PLANO-TRABALHO.md`](PLANO-TRABALHO.md): objetivo, divisão do grupo, fases
  (0, 1A–3C, 4) e tabela de testes. **Cada conversa trabalha uma fase só**, e a
  fase pedida define o escopo. Cada fase mexe no backend e no app da mesma
  funcionalidade.
- Este arquivo: regras técnicas, contrato da API e decisões da Fase 0.

## Stack

| Parte | Tecnologia |
|---|---|
| App (`android/`) | Java 17, Android Studio, Gradle (Kotlin DSL gerado pelo Android Studio, código só em Java), layouts em XML |
| Backend (`backend/`) | Java 21, Spring Boot (start.spring.io), Maven, H2 em arquivo |

**App:** Activities com layouts XML e `ViewModel` + `LiveData` para guardar o
estado da tela. O `ViewModel` sobrevive ao giro da tela sem refazer a busca.
Sem Kotlin, sem Compose, sem Dagger/Hilt, sem RxJava. Navegação por `Intent`
entre Activities.

**Backend:** só existe para o app ter com quem falar. Tem que ser simples e
correto, sem enfeite.

## Dependências do app

Adicionadas na Fase 1B, pelo `gradle/libs.versions.toml`. Usar as versões que o
Android Studio sugerir. As versões abaixo são referência.

| Biblioteca | Para quê |
|---|---|
| AndroidX AppCompat, Material Components, ConstraintLayout, RecyclerView, SwipeRefreshLayout | telas (já vêm ou são padrão) |
| `androidx.lifecycle:lifecycle-viewmodel` e `lifecycle-livedata` | estado da tela |
| `com.squareup.retrofit2:retrofit` + `converter-gson` (2.11.0) | chamadas à API |
| `com.squareup.okhttp3:logging-interceptor` (4.12.0) | log das requisições, **só em debug** |
| `com.github.PhilJay:MPAndroidChart` (v3.1.0, pelo JitPack) | gráficos do dashboard |

- O JitPack entra no `settings.gradle.kts`, em `dependencyResolutionManagement`:
  `maven { url = uri("https://jitpack.io") }`.
- PDF é feito com `android.graphics.pdf.PdfDocument`, que já vem no Android.
  CSV é texto. Nenhuma biblioteca a mais.
- Qualquer outro pacote só com aprovação do usuário.

## Configuração do app

- `minSdk 26` (Android 8.0), a confirmar na Fase 0. Com 26, `java.time`
  (`LocalDate`) funciona sem desugaring.
- `targetSdk` e `compileSdk`: os que o Android Studio gerar.
- `applicationId`: `br.unisc.gestaofinanceira`.
- URL da API em `buildConfigField` (precisa de `buildFeatures { buildConfig =
  true }`):
  - debug: `http://10.0.2.2:8080/api/`
  - release: a URL HTTPS, se houver hospedagem, ou a mesma da debug para a
    apresentação
- **HTTPS:** o Android bloqueia HTTP comum. Liberar só `10.0.2.2` e o IP do PC
  num `res/xml/network_security_config.xml` dentro de `src/debug/` (não em
  `main/`), apontado no manifesto de debug. Nunca usar
  `android:usesCleartextTraffic="true"` geral.
- Permissões no `AndroidManifest.xml`: `INTERNET` e `CAMERA`. Para escolher
  arquivo, usar `ActivityResultContracts.GetContent`, que não pede permissão de
  armazenamento.
- `FileProvider` (`androidx.core.content.FileProvider`) para a foto da câmera e
  para compartilhar o PDF e o CSV, com `res/xml/file_paths.xml` apontando para
  `cache/`.

## Estrutura esperada

```
pesquisa-/
├── CLAUDE.md
├── AGENTS.md
├── PLANO-TRABALHO.md
├── docs/
│   └── telas-e-fluxos.md                 entregável do PDF (Fase 4)
├── backend/
│   ├── pom.xml
│   └── src/main/java/br/unisc/financeiro/
│       ├── seguranca/                    JWT, filtro, login
│       ├── projeto/  rubrica/  lancamento/  anexo/  resumo/
│       └── DadosIniciais.java            usuários e planilha do PDF
└── android/
    ├── settings.gradle.kts  build.gradle.kts  gradle/libs.versions.toml
    └── app/src/
        ├── main/java/br/unisc/gestaofinanceira/
        │   ├── api/          ApiServico (interface Retrofit), ClienteApi, AuthInterceptor
        │   ├── modelo/       DTOs: Projeto, Rubrica, Lancamento, Resumo, Usuario...
        │   ├── dados/        Repositorios (chamam a API e devolvem Resultado<T>)
        │   ├── sessao/       SessaoUsuario (token + perfil)
        │   ├── telas/        login/ projetos/ rubricas/ lancamentos/ dashboard/ relatorio/
        │   │                 cada uma com Activity + ViewModel (+ Adapter, se lista)
        │   └── util/         Dinheiro, Datas, Validacao, GeradorPdf, GeradorCsv
        ├── main/res/          layout/, values/strings.xml, values/colors.xml, xml/
        ├── debug/res/xml/     network_security_config.xml
        └── test/java/...      testes JUnit das classes de util/ e dos DTOs
```

## Contrato da API

Base: `/api`. Tudo em JSON. Toda rota, menos o login, exige
`Authorization: Bearer <token>`. A Fase 0 revisa o contrato e, depois disso,
mudar só com acordo do grupo, porque o app e o backend são feitos por pessoas
diferentes.

| Método e rota | Quem pode | Devolve |
|---|---|---|
| `POST /auth/login` `{email, senha}` | todos | `{token, expiraEm, usuario: {id, nome, perfil}}` |
| `GET /projetos` | todos | lista de projetos do usuário |
| `GET /projetos/{id}` | todos | projeto com vigência, fonte, responsáveis, orçamento aprovado |
| `POST /projetos`, `PUT /projetos/{id}` | coordenador | projeto salvo |
| `GET /projetos/{id}/rubricas` | todos | rubricas e subrubricas com `orcado`, `executado`, `saldo`, `percentual`, `alerta` |
| `POST /projetos/{id}/rubricas`, `PUT /rubricas/{id}` | coordenador | rubrica salva |
| `GET /projetos/{id}/lancamentos?inicio=&fim=&rubricaId=&situacao=&tipo=&pagina=` | todos | página de lançamentos |
| `POST /projetos/{id}/lancamentos`, `PUT /lancamentos/{id}` | coordenador, gestor | lançamento salvo (com `criadoPor`, `criadoEm`) |
| `DELETE /lancamentos/{id}` | coordenador | 204 (exclusão lógica: situação `CANCELADO`) |
| `GET /lancamentos/{id}/historico` | todos | lista de `{acao, usuario, dataHora, descricao}` |
| `POST /lancamentos/{id}/comprovantes` (multipart, campo `arquivo`) | coordenador, gestor | `{id, nomeArquivo, tipo, tamanho}` |
| `GET /comprovantes/{id}` | todos | o arquivo (imagem ou PDF) |
| `GET /projetos/{id}/resumo?inicio=&fim=` | todos | `{receitaTotal, despesaTotal, saldo, orcamentoPrevisto, executadoPercentual, porRubrica: [{rubrica, orcado, executado, participacao, alerta}]}` |

**Formatos:**

- Dinheiro em JSON é **número com 2 casas** (`1600.00`). No Java, sempre
  `BigDecimal`, nunca `double`. No banco: `NUMERIC(14,2)`.
- Data: texto `yyyy-MM-dd`. Data e hora: ISO-8601 com fuso
  (`2026-11-19T14:30:00-03:00`). No DTO do app ficam como `String`, e a
  conversão mora em `util/Datas`.
- `tipo`: `RECEITA` | `DESPESA`. `situacao`: `PENDENTE` | `REALIZADO` |
  `CANCELADO`. `alerta`: `NORMAL` | `ATENCAO` | `SEM_SALDO`. `perfil`:
  `COORDENADOR` | `GESTOR` | `CONSULTA`.
- `valorTotal` = `quantidade × valorUnitario`, **calculado no servidor**. O app
  mostra a prévia enquanto a pessoa digita, mas vale o que voltar da API.
- Erro: status HTTP + `{mensagem, campos: {campo: mensagem}}`. 400 validação,
  401 sem token ou token vencido, 403 sem permissão, 404 não achou, 422 regra
  de negócio (ex.: saldo insuficiente).

**Regras que o servidor decide (GP-10 diz "regras fornecidas pelo servidor"):**

- Executado de uma rubrica = soma das despesas `REALIZADO` dela (a confirmar na
  Fase 0).
- Saldo do projeto = receitas `REALIZADO` − despesas `REALIZADO`.
- `alerta = ATENCAO` quando executado ≥ 80% do orçado, e `SEM_SALDO` quando
  executado ≥ 100%. O limite de 80% fica em `application.properties`.
- O app **não recalcula** saldo, percentual nem alerta: mostra o que veio.

## Backend

- start.spring.io: Maven, Java 21, dependências **Spring Web, Spring Data JPA,
  Validation, Spring Security, OAuth2 Resource Server, H2 Database**. O JWT é
  assinado com chave HS256 (`NimbusJwtEncoder` / `NimbusJwtDecoder`), sem jjwt.
- H2 em arquivo (`jdbc:h2:file:./dados/financeiro`), para os dados
  sobreviverem quando o servidor reinicia. `dados/` vai no `.gitignore`.
- Senhas com `BCryptPasswordEncoder`. A chave do JWT vem de variável de
  ambiente, com valor de desenvolvimento só no `application-dev.properties`.
- Comprovantes salvos em `./dados/anexos/`. No banco fica só o caminho.
  Aceitar só `image/jpeg`, `image/png` e `application/pdf`, até 10 MB.
- Toda alteração de lançamento grava uma linha de histórico (quem, quando, o
  que mudou).
- `DadosIniciais` cria três usuários (um por perfil, senha `123456`, só para
  desenvolvimento) e o projeto de exemplo com as rubricas e itens da planilha
  do PDF, **mais uma receita e alguns lançamentos** (realizados, pendentes e
  um cancelado). Assim as fases da rodada 2 têm dados para mostrar sem esperar
  pelo formulário da 3B.
- Testes com `@SpringBootTest` + `MockMvc`: login, 401, 403 por perfil e o
  resumo do projeto de exemplo.

## Decisões da Fase 0

Preenchido depois que o grupo aprovar. As fases seguintes leem daqui depois do
`/clear`.

| Decisão | Sugestão | Decidido |
|---|---|---|
| Backend mínimo ou API falsa no app | backend mínimo | _a preencher_ |
| Perfil global ou por projeto | global (vai no token) | _a preencher_ |
| Situações que contam no saldo | só `REALIZADO` | _a preencher_ |
| Despesa acima do saldo da rubrica | bloqueia com 422 | _a preencher_ |
| Limite de atenção | 80% | _a preencher_ |
| Versão mínima do Android | 8.0 (API 26) | _a preencher_ |
| Nome do app e cor principal | _a definir_ | _a preencher_ |

**Perfis (sugestão):**

| Ação | Coordenador | Gestor | Consulta |
|---|---|---|---|
| Ver projetos, rubricas, lançamentos, dashboard, relatórios | ✓ | ✓ | ✓ |
| Criar e editar projeto e rubricas | ✓ | | |
| Criar e editar lançamentos, anexar comprovante | ✓ | ✓ | |
| Cancelar lançamento | ✓ | | |

O app **esconde** o que o perfil não pode fazer, mas quem **garante** é o
servidor (403).

## Telas

| Tela | Activity | Requisitos |
|---|---|---|
| Login | `LoginActivity` | GP-01 |
| Projetos (lista e seleção) | `ProjetosActivity` | GP-01, GP-02 |
| Cadastro de projeto | `ProjetoFormActivity` | GP-02 |
| Dashboard do projeto (tela principal do projeto) | `DashboardActivity` | GP-06, GP-10 |
| Rubricas | `RubricasActivity` | GP-03, GP-10 |
| Lançamentos (lista + filtros) | `LancamentosActivity` | GP-07 |
| Novo / editar lançamento | `LancamentoFormActivity` | GP-04, GP-09 |
| Detalhe do lançamento (histórico + seção de comprovantes) | `LancamentoDetalheActivity` | GP-05, GP-07, GP-09 |

O comprovante é anexado **no detalhe**, depois que o lançamento existe (a rota
de upload precisa do id). A seção é um layout à parte
(`secao_comprovantes.xml` + classe `SecaoComprovantes`), incluído no detalhe
com `<include>`. Assim a 2B e a 3C não mexem no mesmo arquivo.
| Relatório | `RelatorioActivity` | GP-08 |

Fluxo: Login → Projetos → Dashboard → (Rubricas | Lançamentos → Detalhe/Form |
Relatório). Uma barra inferior ou um menu no Dashboard leva às outras telas
do projeto.

## Comandos

```bash
# app (dentro de android/)
gradlew.bat testDebugUnitTest   # testes JUnit
gradlew.bat assembleDebug       # gera app/build/outputs/apk/debug/app-debug.apk
gradlew.bat lintDebug           # lint

# backend (dentro de backend/)
mvnw.cmd test                   # testes
mvnw.cmd spring-boot:run        # sobe a API (fica rodando: quem roda é o usuário)
```

- O Claude Code não abre o emulador nem vê a tela. Testes e build passando não
  querem dizer que a tela está certa.
- O primeiro build do Gradle e do Maven baixa muita coisa e demora: usar
  timeout longo.
- `local.properties` (caminho do SDK) é gerado pelo Android Studio e não vai
  para o git.

## Regras rígidas

- **Dinheiro é `BigDecimal`.** Nunca `double` nem `float` para valor. Somar e
  multiplicar com os métodos do `BigDecimal`, arredondar com
  `setScale(2, RoundingMode.HALF_EVEN)`, que é o arredondamento bancário. O
  backend faz o mesmo.
- **Entrada de dinheiro:** `inputType="numberDecimal"`. Ler em
  `util/Dinheiro.ler(String)`: `trim()`, tirar os pontos de milhar, trocar `,`
  por `.`, `new BigDecimal(...)` dentro de `try`. Inválido, vazio, ≤ 0 → `null`.
  Quantidade em `util/Validacao` com `Integer.parseInt` dentro de `try`,
  aceitando só > 0.
- **Saída de dinheiro:** `NumberFormat.getCurrencyInstance(new Locale("pt",
  "BR"))`, sempre em `util/Dinheiro.formatar`. O espaço depois do `R$` é não
  separável (` `): o teste que compara o texto formatado precisa usar ele.
- **Zero texto fixo.** Rótulo, hint, botão, mensagem e título vêm do
  `res/values/strings.xml`. Nada de `setText("Salvar")` nem texto concatenado.
  Valor variável usa placeholder (`%1$s`) com `getString(R.string.x, valor)`.
- **Rede fora da thread principal.** Sempre `call.enqueue(...)` do Retrofit,
  nunca `execute()` na UI. O repositório devolve um `Resultado<T>` (sucesso,
  erro de rede, erro HTTP com a mensagem da API), e o `ViewModel` publica um
  estado: carregando, conteúdo, vazio ou erro.
- **Toda tela que busca dados** mostra o carregamento (`ProgressBar` ou
  `SwipeRefreshLayout`) e, na falha, uma mensagem do `strings.xml` com um botão
  "Tentar de novo". Nunca crash, nunca stack trace na tela.
- **401** em qualquer chamada → `AuthInterceptor` limpa a sessão e volta ao
  login com aviso de sessão expirada.
- **Credenciais:** a senha nunca é guardada. Só o token e o perfil ficam no
  `SharedPreferences` privado (`MODE_PRIVATE`), pela classe `SessaoUsuario`.
  Sair apaga tudo. O `logging-interceptor` só existe em debug e nunca loga o
  header `Authorization`.
- **Confirmação (GP-09):** salvar alteração, cancelar lançamento e sair pedem
  `MaterialAlertDialogBuilder`. Erro de campo aparece no campo
  (`TextInputLayout.setError`).
- **Perfil:** botão que o perfil não pode usar fica `GONE`. A decisão vem de
  `SessaoUsuario.pode(Acao)`, num lugar só.
- **Câmera:** `ActivityResultContracts.RequestPermission` e depois
  `TakePicture` com URI do `FileProvider`. Permissão negada → mensagem e segue
  sem foto.
- **Código que vai ser apresentado.** Cada integrante precisa explicar cada
  linha para o professor: métodos pequenos, nomes em português, comentários
  curtos em português. Sem `System.out.println`, sem `printStackTrace()`: usar
  `Log.d`/`Log.e` com TAG da classe.

## Casos de borda

| Situação | Comportamento esperado |
|---|---|
| Campo vazio, texto inválido, só `,`, valor ≤ 0, quantidade 0 ou fracionária | erro no campo, não envia |
| Sem internet, backend desligado, timeout (15 s) | aviso + "Tentar de novo", o resto do app não trava |
| Token vencido (401) | volta ao login com aviso |
| Sem permissão (403) | mensagem "sem permissão"; o botão nem deveria aparecer |
| Saldo insuficiente (422) | mostra a `mensagem` da API |
| Lista vazia (projeto sem lançamento) | texto de lista vazia, não tela em branco |
| Girar a tela com formulário preenchido ou busca em andamento | nada se perde e a busca não se repete (`ViewModel`) |
| Activity fechada antes da resposta | nada acontece: `LiveData` só entrega para tela ativa |
| Foto grande | comprimir para JPEG ~80%, lado maior 1600 px, antes de enviar |
| Upload falhou | o lançamento fica salvo e o comprovante mostra "não enviado" com opção de reenviar |
| Toque duplo em "Salvar" | botão desabilitado enquanto envia (evita lançamento duplicado) |

---

## Fluxo obrigatório

Para toda instrução, seguir nesta ordem, sem exceção:

1. **Ler** os arquivos que a fase toca: as classes Java envolvidas, os layouts,
   o `strings.xml`, o `build.gradle.kts` / `libs.versions.toml` (app) ou o
   `pom.xml` (backend), e o `AndroidManifest.xml` se mexer em rede, câmera ou
   arquivo.
2. **Confirmar**, respondendo com:
   - **Entendi:** (resumo de 2–3 frases do que foi pedido)
   - **Arquivos que vou alterar ou criar:** (nome e caminho de cada um)
   - **O que muda em cada um:** (descrição breve por arquivo)
3. **Aguardar.** Não fazer nenhuma alteração até receber confirmação explícita
   ("ok", "pode ir", "sim" ou equivalente).
4. **Executar.** Só depois da confirmação, implementar exatamente o combinado,
   só da fase pedida. Não ampliar o escopo nem adiantar a próxima fase sem
   avisar.
5. **Fechar a fase:**
   - rodar os testes e o build da parte mexida e relatar o resultado real (se
     falhou, mostrar o erro);
   - listar o que o usuário precisa testar no emulador (as linhas de "Testes"
     do plano que valem para esta fase);
   - explicar em poucas linhas, em português simples, o que foi feito e por quê;
   - sugerir a mensagem de commit. **O commit é do usuário**, depois de testar.

A Fase 0 é só leitura: o passo 2 vira o relatório pedido no plano e o fluxo
para aí.

Se qualquer parte da instrução estiver ambígua, perguntar antes de ler os
arquivos.

---

## Regras

**Fazer:**

- Seguir os padrões existentes do projeto (nomes, pacotes, estilo dos layouts)
- Em arquivo compartilhado (`strings.xml`, `ApiServico.java`), mexer só no
  bloco da área da fase, com o prefixo dela (ver "Git com três pessoas" no
  plano)
- Ficar no escopo estrito da fase pedida
- Conferir que o app e o backend continuam batendo com o contrato da API
- Relatar qualquer problema encontrado fora do escopo antes de agir sobre ele
- Avisar se uma mudança pode quebrar algo em outro lugar, principalmente no
  contrato da API, que é usado pelos dois lados

**Não fazer:**

- Alterar arquivos sem apresentar o plano e aguardar confirmação
- Assumir regra de negócio que não esteja aqui. Na dúvida sobre saldo, alerta
  ou perfil, perguntar
- Refatorar código além do que foi pedido
- Renomear, mover ou apagar arquivos sem aprovação explícita
- Adicionar dependências sem informar e aguardar confirmação
- Mudar o contrato da API sem avisar que o outro lado também precisa mudar
- Rodar `git commit`, `git push`, `git checkout .` ou qualquer comando que
  descarte mudanças
- Mexer em `build/`, `.gradle/`, `.idea/`, `target/`, `dados/` ou no
  `local.properties`
