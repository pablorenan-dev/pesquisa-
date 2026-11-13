# AGENTS.md

App **Balaio de Lenha** em Flutter: divide a conta do restaurante entre as
pessoas e mostra o total em dólar com a cotação do dia. Trabalho de Programação
para Dispositivos Móveis (UNISC), recriando a interface da aula de
Gerenciadores de Layout.

## Documentos — ler primeiro

- [`PLANO-TRABALHO.md`](PLANO-TRABALHO.md) — objetivo, fases (0 a 6) e tabela
  de testes. **Cada conversa trabalha uma fase só**; a fase pedida define o
  escopo.
- Este arquivo — regras técnicas. O plano aponta para as seções
  [Dependências](#dependências), [API de cotação](#api-de-cotação) e
  [Casos de borda](#casos-de-borda) daqui.
- `referencia/` — print da tela da aula (e o XML do layout, se houver). É o
  modelo do layout.

## Stack

Flutter + Dart, projeto criado com `flutter create balaio_de_lenha`. Estado da
tela só com `StatefulWidget` + `setState`. Nada de Provider, Riverpod, Bloc,
GetX, MVVM ou outro padrão que a disciplina não pediu.

A tela é a da aula recriada, e o assunto da aula é o gerenciador de layout:
**os widgets de layout seguem o gerenciador original.** A equivalência usada
fica no [Mapa da tela](#mapa-da-tela), decidida na Fase 0.

| Android (aula) | Flutter |
|---|---|
| `LinearLayout` vertical / horizontal | `Column` / `Row` |
| `TableLayout` + `TableRow` | `Table` + `TableRow` |
| `GridLayout` | `Table` ou `GridView` |
| `FrameLayout` | `Stack` |
| `RelativeLayout` / `ConstraintLayout` | `Row`/`Column` com `Expanded` e `Align`, ou `Stack` + `Positioned` |
| `ScrollView` | `SingleChildScrollView` |

## Dependências

Só estas três, adicionadas na Fase 1:

```bash
flutter pub add flutter_localizations --sdk=flutter
flutter pub add intl:any
flutter pub add http
```

- `intl:any` é de propósito: a versão do `intl` é travada pelo
  `flutter_localizations`, e fixar outra quebra o `pub get`.
- No `pubspec.yaml`, dentro de `flutter:`, colocar `generate: true` (gera as
  classes de texto a partir dos `.arb`).
- Qualquer outro pacote só com aprovação do usuário. Não mexer na versão do
  SDK (`environment:`) nem no `analysis_options.yaml`.

## Internacionalização

`l10n.yaml` na raiz do projeto:

```yaml
arb-dir: lib/l10n
template-arb-file: app_pt.arb
output-localization-file: app_localizations.dart
nullable-getter: false
```

- `app_pt.arb` é o modelo: toda chave nasce nele. `app_en.arb` e `app_es.arb`
  têm exatamente as mesmas chaves.
- Valor variável vai por placeholder, já formatado como texto:

  ```json
  "porPessoa": "{valor} por pessoa",
  "@porPessoa": {
    "placeholders": { "valor": { "type": "String" } }
  }
  ```

  Uso: `textos.porPessoa(formatarReal(valor))`.
- No `MaterialApp`: `localizationsDelegates:
  AppLocalizations.localizationsDelegates`, `supportedLocales:
  AppLocalizations.supportedLocales` e o título por `onGenerateTitle` (o
  `title:` fixo não é traduzido).
- O lugar do arquivo gerado (e portanto o `import`) depende da versão do
  Flutter. Nas recentes é `lib/l10n/app_localizations.dart`. Conferir na
  Fase 1 e anotar aqui.
- Os `.dart` gerados não se editam à mão. Mudou um `.arb` → `flutter gen-l10n`.
- Aparelho numa língua fora das três: o Flutter usa a primeira de
  `supportedLocales`. Conferir na Fase 1 qual é e, se não for pt, perguntar.
- Para testar uma língua, forçar `locale: const Locale('en')` no
  `MaterialApp` — e tirar antes do commit.

## Estrutura esperada

```
balaio_de_lenha/
├── pubspec.yaml                       dependências + generate: true (Fase 1)
├── l10n.yaml                          configuração dos textos (Fase 1)
├── analysis_options.yaml              veio do flutter create, não mexer
├── android/app/src/main/
│   └── AndroidManifest.xml            permissão de internet (Fase 4)
├── lib/
│   ├── main.dart                      MaterialApp, línguas, abre a tela (Fase 1)
│   ├── telas/balaio_tela.dart         layout e setState (Fases 2 a 5)
│   ├── servicos/cotacao_servico.dart  busca e lê a cotação (Fase 4)
│   ├── utils/calculo.dart             funções puras: leitura, divisão, conversão (Fases 3 e 5)
│   └── l10n/
│       ├── app_pt.arb                 modelo
│       ├── app_en.arb
│       ├── app_es.arb
│       └── app_localizations*.dart    gerados — não editar
└── test/
    ├── widget_test.dart               só abre a tela (Fase 1)
    ├── calculo_test.dart              (Fases 3 e 5)
    └── cotacao_test.dart              leitura do JSON, sem rede (Fase 4)
```

## Mapa da tela

Preenchido depois que o usuário aprovar a lista da Fase 0, para as fases
seguintes não precisarem redescobrir tudo depois do `/clear`.

| Elemento da aula | Widget Flutter | Nome no código | Função |
|---|---|---|---|
| _a preencher_ | | | |

Decisões tomadas na Fase 0 (taxa de serviço entra no total? erro aparece no
campo — `errorText` — ou em `SnackBar`? calcula no botão ou enquanto digita?
tela deitada é exigida? mostra a sobra dos centavos?):

- _a preencher_

## Comandos

```bash
flutter pub get        # baixa as dependências
flutter gen-l10n       # regenera os textos depois de mudar um .arb
flutter analyze        # tem que terminar em "No issues found!"
flutter test           # roda os testes de test/
```

- `flutter run` / F5 é do usuário: o Claude Code não abre o app nem vê a tela.
  analyze e test passando não quer dizer tela certa.
- O `test/widget_test.dart` do `flutter create` testa o contador do app de
  exemplo e quebra assim que o `main.dart` muda (Fase 1). Substituir por um
  teste que só abre a tela do Balaio — avisando antes, porque é apagar o
  conteúdo de um arquivo.
- O caminho contém `!` (`projetoBah!Tech`), que já quebrou o webpack em outro
  projeto e é separador de caminho de `.jar` no Java (Gradle). Se o build
  Android ou Windows der erro estranho de caminho, suspeitar disso antes de
  mexer em configuração. A saída é mover o projeto para um caminho sem `!` —
  com aprovação do usuário.

## Regras rígidas

- **Zero texto fixo nos widgets.** Rótulo, hint, botão, mensagem de erro,
  `SnackBar` e título vêm do `.arb` por `AppLocalizations.of(context)`, com a
  mesma chave em pt, en e es. Nada de `Text('Total')` nem texto concatenado.
  No código só fica o que não aparece na tela: URL da API, padrões de formato,
  chaves do JSON.
- **Layout fiel à referência.** Mesma ordem e mesmo agrupamento da tela da
  aula, com o widget equivalente ao gerenciador original (tabela da
  [Stack](#stack)). Os dois rótulos novos entram junto dos outros resultados e
  copiam o estilo deles. O corpo da tela fica num `SingleChildScrollView`, para
  não estourar (faixa amarela e preta) com o teclado aberto ou a tela deitada.
- **Cálculo fora do widget.** Funções puras em `lib/utils/calculo.dart`:
  recebem números ou texto e devolvem números, sem `BuildContext` nem
  `setState`. Cada uma com teste em `test/`. A leitura do que foi digitado
  também mora lá (por exemplo `lerValor`, `lerPessoas`), para os casos de
  borda serem testáveis.
- **Entrada de dinheiro:** campo com
  `keyboardType: TextInputType.numberWithOptions(decimal: true)`. Aceitar
  vírgula e ponto: `trim()`, trocar `,` por `.`, `double.tryParse`. O
  `tryParse` aceita `NaN` e `Infinity`, então exigir também `valor.isFinite`.
  Se o teclado não deixar digitar vírgula, avisar.
- **Número de pessoas:** `int.tryParse` — fração (`2,5`) vira `null` e cai na
  mensagem de erro.
- **Saída de dinheiro:** reais com `NumberFormat.simpleCurrency(locale:
  'pt_BR')` (`R$ 25,00`) em qualquer língua do aparelho; dólar com
  `NumberFormat.simpleCurrency(locale: 'en_US')` (`$4.95`). Arredondar só na
  exibição. O espaço depois do `R$` é não separável (` `): teste que
  compara o texto formatado precisa usar ele.
- **`$` em string Dart é interpolação:** escrever `'R\$'` quando for literal.
- **Controllers:** todo `TextEditingController` criado no `State` é liberado
  no `dispose()`.
- **Código que vai ser apresentado.** O usuário precisa explicar cada linha
  para o professor: métodos pequenos, nomes claros em português (como
  `calculo.dart`), comentários curtos em português. Sem `print` (o analyze
  reclama).

## API de cotação

```
GET https://economia.awesomeapi.com.br/json/last/USD
```

Resposta real (conferida em 22/10/2026), campos que importam:

```json
{"USDBRL": {"code": "USD", "codein": "BRL", "bid": "5.0526",
            "create_date": "2026-10-22 18:30:19", ...}}
```

- Os campos ficam **dentro de `USDBRL`**, não na raiz.
- `bid` vem como texto com ponto → `double.parse`. Nunca com `NumberFormat`
  da língua do aparelho: em pt, `"5.0526"` vira 50526.
- `create_date` vem como `yyyy-MM-dd HH:mm:ss`, horário de Brasília. Ler com
  `DateTime.parse` (aceita o espaço no meio e não converte fuso). Exibir como
  `dd/MM/yyyy HH:mm` (formato fixo do plano, nas três línguas) com
  `DateFormat('dd/MM/yyyy HH:mm', 'en_US')` — `en_US` já vem carregado no
  `intl`, as outras línguas precisariam de inicialização.

Implementação, em `lib/servicos/cotacao_servico.dart`:

1. Classe `Cotacao` com `bid` (`double`) e `data` (`DateTime`).
2. `Cotacao lerCotacao(String corpo)` — função pura que transforma o JSON em
   `Cotacao`; `bid` ≤ 0 é erro. Testada em `test/cotacao_test.dart` com a
   resposta real acima, sem rede.
3. `Future<Cotacao> buscarCotacao()` — `http.get` com
   `.timeout(const Duration(seconds: 10))`. Status diferente de 200 é erro (a
   API gratuita tem limite de requisições). Erro vira `throw Exception(...)`;
   essa mensagem nunca vai para a tela.
4. Na tela, `_carregarCotacao()` é chamado no `initState`, sem `await`
   (`initState` não pode ser `async`). O checkpoint da Fase 4 é abrir o app e
   ver a data.
5. `try`/`catch (e)` em volta do `await buscarCotacao()`. O `catch` genérico é
   de propósito: JSON fora do formato gera `TypeError`, que não é `Exception`.
6. Depois do `await`, `if (!mounted) return;` antes do `setState` — a tela
   pode ter sido fechada enquanto a resposta não vinha.
7. Qualquer falha (sem rede, timeout, HTTP ≠ 200, JSON inesperado) → mensagem
   do `.arb` no rótulo da cotação. Nunca crash, nunca erro técnico na tela. O
   resto do app continua funcionando.
8. Guardar a `Cotacao` no `State` para a conversão da Fase 5.
9. `<uses-permission android:name="android.permission.INTERNET" />` em
   `android/app/src/main/AndroidManifest.xml`, fora de `<application>`. O
   Flutter só põe essa permissão no manifesto de debug; sem ela no `main/`, o
   APK de release fica sem internet.
10. Se a requisição falhar só no Chrome, com erro de CORS no console do
    navegador, não é bug do código: testar em Windows ou Android.

**Conversão (Fase 5):** total em dólar = total da conta em R$ ÷ `bid`. Função
pura em `calculo.dart`, com teste. "Total" é o mesmo valor que a divisão usa —
com taxa de serviço se a Fase 0 decidiu que ela entra. Guardar o total
calculado e a cotação no `State` e montar o texto do dólar no `build` a partir
dos dois: assim ele se preenche sozinho quando o `bid` chega.

## Casos de borda

| Situação | Comportamento esperado |
|---|---|
| Campo vazio, texto inválido, só `,`, `NaN`/`Infinity` | mensagem de erro, sem crash |
| Total ≤ 0 | mensagem pedindo valor válido |
| Pessoas ≤ 0 ou fracionário | mensagem pedindo nº de pessoas válido |
| Sem internet, API fora ou timeout | aviso amigável no lugar da cotação; a divisão continua funcionando; o rótulo do dólar indica que não há cotação |
| Calcular antes de a cotação chegar | a divisão aparece; o dólar mostra que está aguardando e é preenchido quando o `bid` chegar |
| Tela fechada antes da resposta | nada acontece (`mounted`), sem erro no console |
| Teclado aberto ou tela deitada | sem faixa de overflow. Os valores não somem: o `State` do Flutter sobrevive ao giro, não precisa salvar nada nem refazer a requisição |
| Centavos que não fecham (100 ÷ 3) | 33,33 por pessoa; perguntar se precisa mostrar a sobra |

Todas as mensagens de erro usam a mesma forma de aviso (`errorText` no campo
ou `SnackBar`, conforme decidido na Fase 0). Os testes de aceite estão na
seção "Testes" do plano.

---

## Fluxo obrigatório

Para toda instrução, seguir nesta ordem — sem exceção:

1. **Ler** — os `.dart` de `lib/` que a fase toca, os três `.arb`, o
   `pubspec.yaml` e o `l10n.yaml`; também o `AndroidManifest.xml` se a fase
   mexer em rede e a `referencia/` se mexer em layout.
2. **Confirmar** — responder com:
   - **Entendi:** (resumo de 2–3 frases do que foi pedido)
   - **Arquivos que vou alterar:** (nome e caminho de cada um)
   - **O que muda em cada um:** (descrição breve por arquivo)
3. **Aguardar** — não fazer nenhuma alteração até receber confirmação explícita
   ("ok", "pode ir", "sim" ou equivalente).
4. **Executar** — só depois da confirmação, implementar exatamente o combinado,
   só da fase pedida. Não ampliar escopo nem adiantar a próxima fase sem avisar.
5. **Fechar a fase:**
   - rodar `flutter analyze` e `flutter test` e relatar o resultado real (se
     falhou, mostrar o erro);
   - listar o que o usuário precisa testar com F5 (as linhas de "Testes" do
     plano que valem para esta fase);
   - explicar em poucas linhas, em português simples, o que foi feito e por quê;
   - sugerir a mensagem de commit. **O commit é do usuário**, depois de testar.

A Fase 0 é só leitura: o passo 2 vira o relatório pedido no plano e o fluxo
para aí.

Se qualquer parte da instrução estiver ambígua, perguntar antes de ler os
arquivos.

---

## Regras

**Fazer:**

- Seguir os padrões existentes do projeto (nomes, organização dos arquivos,
  estilo dos widgets)
- Ficar no escopo estrito da fase pedida
- Relatar qualquer problema encontrado fora do escopo antes de agir sobre ele
- Conferir que toda chave de texto usada existe nos três `.arb` e que o
  `flutter gen-l10n` roda sem aviso de mensagem não traduzida
- Avisar se uma mudança pode quebrar algo em outro lugar

**Não fazer:**

- Alterar arquivos sem apresentar o plano e aguardar confirmação
- Assumir qualquer coisa que não tenha sido dita explicitamente — na dúvida
  sobre a regra da conta, perguntar
- Refatorar código além do que foi pedido
- Renomear, mover ou apagar arquivos sem aprovação explícita
- Instalar dependências novas sem informar e aguardar confirmação
- Criar arquivos novos sem avisar antes
- Reescrever lógica funcional ao corrigir algo simples
- Rodar `git commit`, `git checkout .` ou qualquer comando que descarte
  mudanças
- Mexer em `build/`, `.dart_tool/`, `.idea/`, nos `.dart` gerados de l10n, no
  `pubspec.lock` à mão ou no `android/local.properties`
