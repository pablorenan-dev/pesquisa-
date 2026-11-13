# Plano de trabalho — Balaio de Lenha (divisão da conta)

Disciplina: Programação para Dispositivos Móveis (UNISC)
Stack: Flutter + Dart, rodando e visualizando direto pelo VS Code

## Objetivo

Recriar em Flutter a interface do Balaio de Lenha da aula de Gerenciadores de Layout e fazer ela funcionar:

1. Funcionalidades já previstas na tela (divisão da conta por pessoas)
2. Dois rótulos novos:
   - valor total da conta convertido para dólar (campo `bid` da API)
   - data da cotação usada (campo `create_date` da API)
3. API: `https://economia.awesomeapi.com.br/json/last/USD`

## Antes de começar

- [ ] Flutter SDK instalado (`flutter doctor` sem erro no que for usar)
- [ ] VS Code com as extensões **Flutter**, **Dart** e **Claude Code**
- [ ] Criar o projeto: `flutter create balaio_de_lenha`
- [ ] Abrir a pasta `balaio_de_lenha` no VS Code
- [ ] Colocar `CLAUDE.md`, `AGENTS.md` e este arquivo na raiz do projeto
- [ ] Criar a pasta `referencia/` e colocar lá o print da interface da aula (e o XML do layout, se tiver)
- [ ] `git init` + commit inicial

### Onde rodar pra visualizar

No canto inferior direito do VS Code tem o seletor de dispositivo. Opções:

| Dispositivo | Observação |
|---|---|
| **Chrome** | mais fácil, abre na hora. Se a API der erro de CORS, usar outra opção |
| **Windows (desktop)** | precisa do Visual Studio com "Desenvolvimento para desktop com C++" |
| **Emulador Android** | o mais fiel a um celular. Pode abrir pelo VS Code se o Android SDK estiver instalado |

Depois é só **F5** (ou "Run > Start Debugging"). Com o app aberto, salvar o arquivo já aplica o **hot reload**.

### Quem faz o quê

| Quem | O quê |
|---|---|
| Claude Code | escreve o código, roda `flutter analyze` e `flutter test` |
| Você | roda com F5, olha a tela e testa |

## Fases

Cada fase é uma conversa curta. No fim de cada uma: rodar com F5, testar, commit e `/clear` antes da próxima.

### Fase 0 — Reconhecimento (sem editar nada)

Ative o plan mode (Shift+Tab) e mande:

> Leia o CLAUDE.md, o AGENTS.md e o PLANO-TRABALHO.md. Depois olhe a pasta referencia/ e me descreva a interface do Balaio de Lenha: quais campos, botões e resultados ela tem, e quais widgets do Flutter equivalem aos gerenciadores de layout usados. Não altere nada.

Checkpoint: a descrição bate com a interface da aula? Decida aqui o que ficar em dúvida (ex.: taxa de serviço entra ou não no total).

### Fase 1 — Estrutura e dependências

> Fase 1: adicione as dependências do AGENTS.md, configure a internacionalização pt/en/es e crie a estrutura de pastas. O main.dart deve abrir uma tela vazia do Balaio de Lenha.

Checkpoint: F5 abre o app sem erro.

### Fase 2 — Layout

> Fase 2: monte a interface da tela igual à da referencia/, só o visual, já com os dois rótulos novos (total em dólar e data da cotação). Textos nos arquivos .arb das três línguas.

Checkpoint: comparar lado a lado com o print da aula. Ajustar com hot reload até ficar parecido.

### Fase 3 — Lógica da divisão

> Fase 3: implemente o cálculo como funções puras em lib/utils/calculo.dart, com testes em test/. Ligue na tela com validação dos campos.

Checkpoint: `flutter test` passando e a tabela de testes abaixo conferida na tela.

### Fase 4 — Cotação da API

> Fase 4: implemente o serviço de cotação conforme o AGENTS.md e mostre a data formatada na tela.

Checkpoint: abrir o app com internet e ver a data. Depois desligar a internet e ver o aviso (sem crash).

### Fase 5 — Conversão para dólar e casos de borda

> Fase 5: use o bid pra converter o total e trate todos os casos de borda do AGENTS.md.

Checkpoint: conferir na calculadora (total em R$ ÷ bid).

### Fase 6 — Revisão e entrega

> Fase 6: revise o código, rode flutter analyze, remova o que não é usado, deixe comentários curtos em português e me explique em tópicos o fluxo do app pra eu apresentar pro professor.

- [ ] Ler e entender cada parte do código (o professor pode perguntar)
- [ ] Prints do app em pt, en, es e no caso sem internet
- [ ] Commit final e zip do projeto (sem `build/` e `.dart_tool/`)

## Testes

| Total (R$) | Pessoas | Esperado |
|---|---|---|
| 100,00 | 4 | 25,00 por pessoa |
| 99,90 | 3 | 33,30 por pessoa |
| 0 | 2 | mensagem pedindo valor válido |
| 150,00 | 0 | mensagem pedindo nº de pessoas válido |
| vazio | vazio | mensagem de erro, sem crash |

Também testar:

- [ ] Aceita vírgula e ponto como separador decimal
- [ ] Total em dólar = total em R$ ÷ bid (conferir abrindo a API no navegador)
- [ ] Data da cotação aparece certa
- [ ] Sem internet → aviso amigável, o resto da tela funciona
- [ ] Idioma em pt, en e es (dá pra forçar trocando o `locale` no `MaterialApp`)

## Dicas pro Claude Code no VS Code

- **Shift+Tab** alterna entre modo normal, aceitar edições automaticamente e plan mode
- **@arquivo** no prompt pra apontar um arquivo
- **Esc** interrompe se ele sair do rumo
- **/clear** entre fases
- Sempre olhar o diff antes de aceitar
- Commit no fim de cada fase. Se algo quebrar, `git checkout .` volta ao último commit
