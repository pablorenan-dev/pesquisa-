# CLAUDE.md

@AGENTS.md
@PLANO-TRABALHO.md

As regras do projeto estão no AGENTS.md e valem inteiras, inclusive o fluxo
obrigatório. Aqui fica só o que é específico de trabalhar com o Claude Code
neste projeto.

## Uma fase por conversa

- "Fase X" no pedido (0, 1A a 3C, 4) = o escopo daquela fase do plano e nada
  além. A maioria das fases mexe no backend e no app: fazer os dois e testar
  os dois.
- Se o pedido não disser a fase, perguntar qual é. Cada integrante tem as
  suas (tabela "Divisão do trabalho" no plano).
- Entre fases o usuário roda `/clear`. O que precisa sobreviver (decisões,
  mudanças no contrato da API) vai para o AGENTS.md, depois que o usuário
  aprovar. Oferecer isso ao fim da Fase 0.
- A Fase 0 roda em plan mode: só leitura, nenhum arquivo alterado, nem o
  AGENTS.md.
- Três pessoas usam este repositório. Antes de mexer, conferir com
  `git status` e `git branch` que a branch é a da fase e não a `main`.

## O que fica com o usuário

O Claude Code não abre o emulador nem vê a tela.

- Não rodar `mvnw spring-boot:run` nem nada que suba o app: fica preso
  esperando. Quem sobe o backend e roda o app (Run ▶ no Android Studio) é o
  usuário.
- O máximo que dá para afirmar é "testes e build passaram". Nunca dizer que a
  tela funciona sem o teste do usuário.
- Mudou layout → pedir para conferir no emulador em pé e deitado, com o
  teclado aberto.
- Mudou rede → pedir o teste com o backend ligado e desligado.
- Mudou perfil → pedir o teste logado como coordenador e como consulta.

## Ambiente

- Windows. Comandos com `gradlew.bat` (dentro de `android/`) e `mvnw.cmd`
  (dentro de `backend/`).
- O Android SDK vem com o Android Studio. Se o build reclamar de SDK ou de
  `local.properties`, parar e pedir para o usuário abrir o projeto no Android
  Studio uma vez. Não instalar SDK nem mexer no PATH ou no `JAVA_HOME`.
- O primeiro build demora (baixa o Gradle e as dependências): usar timeout
  longo.
- Falhou → achar o primeiro erro real (o resto costuma ser cascata) e mostrar
  ao usuário antes de mexer em qualquer configuração.
- Git só para leitura (`status`, `diff`, `log`, `branch`). Commit, push e
  `checkout` são do usuário.
