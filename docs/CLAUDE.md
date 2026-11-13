# CLAUDE.md

@AGENTS.md
@PLANO-TRABALHO.md

As regras do projeto estão no AGENTS.md e valem inteiras, inclusive o fluxo
obrigatório. Aqui fica só o que é específico de trabalhar com o Claude Code
neste projeto.

## Uma fase por conversa

- "Fase N" no pedido = o escopo daquela fase do plano e nada além.
- Entre fases o usuário roda `/clear`. O que precisa sobreviver (widgets e
  nomes da tela, decisões da Fase 0) vai para o "Mapa da tela" do AGENTS.md,
  depois que o usuário aprovar a lista — oferecer isso ao fim da Fase 0.
- A Fase 0 roda em plan mode: só leitura, nenhum arquivo alterado, nem o
  AGENTS.md.

## O que fica com o usuário (F5 no VS Code)

O Claude Code não abre o app nem vê a tela.

- Não rodar `flutter run`: ele fica preso esperando o app fechar. Quem roda é
  o usuário, com F5.
- O máximo que dá para afirmar é "analyze e test passaram". Nunca dizer que a
  tela funciona sem o teste do usuário.
- Mudou layout → pedir para conferir com F5 e hot reload, lado a lado com a
  `referencia/`, em pt, en e es (forçando o `locale` no `MaterialApp`) e com a
  tela deitada, se for exigido.
- Mudou rede → pedir o teste com internet e sem (Wi-Fi desligado ou modo avião
  no emulador).

## Ambiente

- Windows + VS Code. O `flutter` precisa estar no PATH. Se o comando não
  existir, parar e avisar o usuário — não instalar SDK nem mexer no PATH.
- O primeiro `pub get` e o primeiro `flutter test` demoram: usar timeout longo.
- Falhou → achar o primeiro erro real (o resto costuma ser cascata) e mostrar
  ao usuário antes de mexer em qualquer configuração.
- Git só para leitura (`status`, `diff`, `log`). Commit e `checkout` são do
  usuário — é com eles que o plano volta atrás quando algo quebra.
