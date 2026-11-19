# Prompts do João Vitor — Gestão Financeira de Projetos

Nov 19, 2026 · @João Vitor

## Como usar

Suas fases são 1B, 2B, 3B e a sua parte da 4 (falhas de rede e APK). Todas seguem o mesmo ciclo, e você só passa pro passo seguinte quando o anterior fecha sem pendência.

1. **Branch nova** a partir da `main` atualizada: `git checkout main`, `git pull`, `git checkout -b <nome-da-fase>`
2. **Plano** em plan mode (Shift+Tab), sem código. Cole o Bloco de regras + o prompt de plano da fase. Leia o plano e corrija o que não fizer sentido antes de aprovar.
3. **Implementar em pedaços**, um prompt por pedaço (modelo abaixo). Olhe o diff de cada pedaço antes de aceitar.
4. **Testes automáticos** com o prompt T1 (última seção).
5. **`/clear` e revisão com contexto limpo** com o prompt R1. O Claude que escreveu o código tende a achar que está tudo certo, então a revisão precisa começar do zero.
6. **Teste manual** no emulador com o checklist da fase.
7. **PR**: prompt P1 pra descrição, commit, push e pedir revisão pro Pablo ou pro João Pedro.

Todo dia: `git merge main` na sua branch, pra os conflitos chegarem pequenos.

### Bloco de regras (colar no começo do plano de toda fase)

```text
Regras para esta fase inteira:
- Siga o CLAUDE.md, o AGENTS.md e o contrato da API. Se algo não estiver definido lá, pare e me pergunte em vez de inventar.
- Mexa só no que é da minha fase. Se precisar mudar código de outro integrante, me avise antes e explique por quê.
- strings.xml: só os meus blocos (prefixos login_ e lanc_). ApiServico.java: só o meu bloco comentado. AndroidManifest.xml e libs.versions.toml: me avise antes de mexer.
- Use o que a 1C já criou (Dinheiro, Datas, Validacao, layout de estado da tela) em vez de criar outra coisa parecida.
- Dinheiro sempre em BigDecimal, nunca double ou float.
- Não adicione biblioteca nova sem me perguntar.
- Trabalhe em pedaços pequenos. No fim de cada pedaço rode os testes, me mostre o resumo do que mudou e espere eu dizer "pode seguir".
- Toda regra nova (validação, cálculo, filtro, tratamento de erro) vem com teste.
- Comentários curtos em português. Sem código morto.
- Não faça commit nem push, isso é comigo.
```

### Modelo de prompt de pedaço

```text
Plano aprovado. Faça só o pedaço <N>: <nome do pedaço>. Escreva os testes primeiro, depois o código. Rode <comando de teste> e me mostre: arquivos criados ou alterados, resultado dos testes e o que eu devo conferir na tela. Depois pare.
```

Comandos de teste: backend `mvnw.cmd test`, app `gradlew.bat testDebugUnitTest`. Pedaço que só mexe em tela: `gradlew.bat assembleDebug` e você confere no emulador.

## Fase 1B — App base e login

Branch: `1b-app-login`. O ponto que mais trava o grupo aqui é que a 1C do João Pedro também mexe em `android/`, então o esqueleto do projeto (pedaço 1) vai pra `main` num PR pequeno antes do resto. Nesta fase você pode mexer no `AndroidManifest.xml` e no `libs.versions.toml`, porque é você quem cria os dois. Só avise o grupo.

### Plano (plan mode)

```text
[cole aqui o Bloco de regras]

Fase 1B (minha): app base e login. Leia o CLAUDE.md, o AGENTS.md e o PLANO-TRABALHO.md, principalmente o contrato de POST /api/auth/login, a tabela de perfis e as dependências do app.

Monte um plano em pedaços pequenos, cada um testável sozinho:
1. Esqueleto do projeto Android em android/ (Java, minSdk decidido na Fase 0, dependências do AGENTS.md) compilando com gradlew.bat assembleDebug. Esse pedaço vai pra main primeiro porque a 1C depende dele.
2. Camada de rede: Retrofit com URL base fácil de trocar (10.0.2.2:8080 no emulador), permissão de internet e liberação de HTTP só pra desenvolvimento, Resultado (sucesso ou erro com tipo: sem conexão, timeout, não autorizado, proibido, validação, servidor), SessaoUsuario guardando token, nome e perfil, e AuthInterceptor colocando o Bearer.
3. Tela de login com ViewModel: campos, validação no campo, carregando, mensagens de erro e navegação pra uma tela de projetos ainda vazia.
4. Sessão: manter o login ao reabrir o app, sair, e token vencido (401 em qualquer chamada) voltando ao login com aviso.

Para cada pedaço diga os arquivos que vai criar, os testes JUnit que vai escrever e como eu confiro na tela. Liste também o que está ambíguo no AGENTS.md ou no contrato. Não escreva código ainda.
```

### Pedaços (use o modelo de prompt de pedaço e acrescente a nota de cada um)

1. **Esqueleto.** "Rode gradlew.bat assembleDebug e confirme que abre no emulador com uma tela em branco." Depois disso: commit, PR pequeno e avise o João Pedro.
2. **Rede.** "Escreva primeiro os testes do mapeamento de respostas pro Resultado: 200, 400 com erro de validação, 401, 403, 500, sem conexão e timeout. Se o login do Pablo ainda não existir, teste com um servidor falso seguindo o contrato (me pergunte antes de adicionar a biblioteca), sem mudar o contrato."
3. **Login.** "O estado fica no ViewModel, então girar a tela não apaga o que foi digitado nem refaz a requisição. Botão Entrar desabilitado enquanto carrega, pra não mandar duas vezes. Teste do ViewModel com sucesso, senha errada, campo vazio e backend fora do ar."
4. **Sessão.** "401 em qualquer tela limpa a sessão e volta ao login com aviso, num lugar só e não tela por tela. A senha nunca é salva, só o token."

### Checklist manual

- [ ] Login certo abre a tela de projetos
- [ ] Senha errada mostra mensagem e o app continua aberto
- [ ] Campo vazio mostra erro no próprio campo e nem chama a API
- [ ] Backend desligado mostra aviso amigável, sem crash
- [ ] Girar a tela com o formulário preenchido mantém o texto
- [ ] Tocar duas vezes rápido em Entrar manda uma requisição só (olhe o log do backend)
- [ ] Fechar e abrir o app mantém o login
- [ ] Token vencido volta ao login com aviso (peça pro Pablo um jeito de gerar token de vida curta)

## Fase 2B — Lançamentos: lista, filtros e detalhe

Branch: `2b-lancamentos-lista`. Começa quando 1A, 1B e 1C estiverem na `main`. Cobre GP-07 e a parte de autoria e data do GP-09.

### Plano (plan mode)

```text
[cole aqui o Bloco de regras]

Fase 2B (minha): lançamentos, com lista, filtros e detalhe. Cobre GP-07 e a autoria e data do GP-09. Leia o CLAUDE.md, o AGENTS.md, o PLANO-TRABALHO.md e o que já está na main: rede e login (1B), utilitários e layout de estado (1C), entidades e dados de exemplo (1A).

Monte um plano em pedaços:
1. Backend: GET de lançamentos do projeto com filtros opcionais (data inicial e final, rubrica, tipo, situação), ordenado por data, respeitando o perfil e o acesso do usuário ao projeto. Os filtros são feitos no servidor.
2. Backend: GET do detalhe e do histórico do lançamento, com quem criou, quando, e cada alteração com autor e data.
3. App: lista com RecyclerView, valores pelo Dinheiro, datas pelo Datas, e estados de carregando, vazio e erro com "tentar de novo" usando o layout da 1C.
4. App: filtros por período, rubrica, tipo e situação, com botão de limpar, mantidos ao girar a tela e ao voltar do detalhe.
5. App: detalhe com todos os campos, quem lançou e quando, histórico, e um espaço reservado e comentado pra seção de comprovantes da 3C.

Para cada pedaço: arquivos, testes e como eu confiro. Confirme no contrato os nomes exatos dos parâmetros e dos campos e liste o que estiver faltando ou ambíguo, por exemplo se filtrar por uma rubrica traz também as subrubricas dela. Não escreva código ainda.
```

### Pedaços (modelo de pedaço + nota)

1. **Filtros no backend.** "Testes de integração com os dados de exemplo da 1A pra cada filtro sozinho e combinado, período incluindo o último dia (lançamento do dia 31 aparece num filtro de 01 a 31), data inicial maior que a final dando 400, e usuário sem acesso ao projeto dando 403."
2. **Detalhe e histórico.** "Teste que o detalhe traz autor e data de criação, e que lançamento de outro projeto ou inexistente dá 404 ou 403 conforme o AGENTS.md."
3. **Lista.** "Girar a tela não refaz a busca. Teste do ViewModel pros estados carregando, lista, vazio e erro."
4. **Filtros no app.** "O app só monta os parâmetros e chama a API, não filtra a lista localmente. Teste da montagem dos parâmetros, incluindo filtro limpo."
5. **Detalhe.** "Deixe a seção de comprovantes como um container vazio com comentário dizendo que é da 3C, pro João Pedro encaixar sem mexer no resto."

### Checklist manual

- [ ] Filtrar por uma rubrica mostra só ela, e bate com o mesmo GET no Postman
- [ ] Período de 01 a 31 inclui lançamento do dia 31
- [ ] Filtros combinados e o botão de limpar funcionam
- [ ] Filtro sem resultado mostra estado vazio, não tela em branco
- [ ] Girar a tela não perde os filtros nem refaz a busca
- [ ] Voltar do detalhe mantém filtros e posição na lista
- [ ] Detalhe mostra quem lançou e quando
- [ ] Valores em `R$ 1.234,56` e datas no formato brasileiro
- [ ] Backend desligado mostra erro com "tentar de novo", e tentar de novo funciona quando ele volta

## Fase 3B — Lançamentos: formulário e edição

Branch: `3b-lancamentos-form`. Começa quando a rodada 2 estiver na `main`. Cobre GP-04 e GP-09. O saldo aqui tem que usar a mesma regra do resumo da 2C, senão o formulário aceita uma despesa que o dashboard mostra como estourada. Combine com o João Pedro de usar o mesmo método no backend.

### Plano (plan mode)

```text
[cole aqui o Bloco de regras]

Fase 3B (minha): lançamentos, com formulário, edição e cancelamento. Cobre GP-04 e GP-09. Leia o CLAUDE.md, o AGENTS.md (principalmente a regra de saldo, as situações que contam no saldo e a tabela de perfis decididas na Fase 0), o PLANO-TRABALHO.md e o código da 2B e da 2C na main.

Monte um plano em pedaços:
1. Backend: criar lançamento. Validação de data, descrição, rubrica, tipo, situação, quantidade > 0 e valor unitário > 0. Total calculado no servidor (quantidade × valor unitário em BigDecimal, 2 casas). Regra de saldo da Fase 0 usando o mesmo cálculo do resumo da 2C. Autor e data gravados. 403 para perfil sem permissão.
2. Backend: editar e cancelar, gravando no histórico o que mudou, quem e quando. Cancelar muda a situação e não apaga o registro, conforme o AGENTS.md. Na edição, a checagem de saldo desconta o valor antigo do próprio lançamento.
3. App: formulário com prévia do total enquanto digita, aceitando vírgula e ponto, validação no próprio campo, erros de validação do servidor mostrados no campo certo, e tudo mantido ao girar a tela.
4. App: confirmação antes de salvar e de cancelar, aviso de saldo conforme a regra, botão desabilitado durante o envio, edição abrindo com os dados preenchidos, volta pra lista ou detalhe atualizado, e criar, editar e cancelar escondidos pro perfil de consulta.

Os testes têm que cobrir as linhas da 3B na tabela de testes do PLANO-TRABALHO.md: 5 × 320,00 = 1.600,00, 2 × 100,00 = 200,00, quantidade 0, valor vazio ou só vírgula, sem rubrica ou sem data, despesa maior que o saldo da rubrica, e perfil de consulta tomando 403 pela API.

Para cada pedaço: arquivos, testes e como eu confiro. Liste o que estiver ambíguo, por exemplo se dá pra editar um lançamento cancelado e se receita também passa pela regra de saldo. Não escreva código ainda.
```

### Pedaços (modelo de pedaço + nota)

1. **Criar no backend.** "Testes primeiro pra cada linha da tabela de testes que é da 3B, e um teste de limite: despesa exatamente igual ao saldo."
2. **Editar e cancelar.** "Teste que editar uma despesa de 500 pra 600 numa rubrica com 550 de saldo livre passa (sobra 50 + 500 do valor antigo), e que o histórico grava autor, data e valor antes e depois."
3. **Formulário.** "A prévia do total no app é só informativa, o total que vale é o que o servidor devolve. Use o Dinheiro e o Validacao da 1C. Teste do ViewModel com os mesmos casos do backend."
4. **Confirmação e permissão.** "Esconder botão é só conforto, a permissão de verdade é o 403 do servidor. Trate o 403 com mensagem clara se ele vier mesmo assim."

### Checklist manual

- [ ] 5 × 320,00 mostra prévia de R$ 1.600,00 e salva 1.600,00
- [ ] 2 × 100,00 dá R$ 200,00
- [ ] "320.5" e "320,5" dão o mesmo total
- [ ] Quantidade 0, valor vazio ou só "," mostram erro no campo, sem crash
- [ ] Sem rubrica ou sem data não envia
- [ ] Despesa maior que o saldo da rubrica avisa ou bloqueia conforme a regra da Fase 0
- [ ] Salvar e cancelar pedem confirmação
- [ ] Tocar duas vezes em Salvar cria um lançamento só
- [ ] Depois de editar, o histórico do detalhe mostra a edição com autor e data
- [ ] Girar a tela no meio do formulário não apaga nada
- [ ] Usuário de consulta não vê os botões, e um POST com o token dele no Postman dá 403
- [ ] Depois de salvar, o saldo no dashboard (2C) bate com o novo lançamento

## Fase 4 — Falhas de rede e APK

Branch: `4b-falhas-rede-apk`. Aqui você mexe em telas dos outros, mas só no tratamento de falha. Avise o dono de cada tela que mudou. Primeiro um levantamento sem mudar nada, depois as correções.

### Levantamento (plan mode)

```text
[cole aqui o Bloco de regras]

Fase 4, minha parte: falhas de rede em todas as telas e APK. Nesta fase posso mexer em telas de outros integrantes, mas só no tratamento de falha.

Antes de mudar qualquer coisa, monte uma tabela: uma linha por tela (login, projetos, cadastro de projeto, rubricas, dashboard, lançamentos, detalhe, formulário, comprovantes, relatório) e uma coluna por situação: sem internet, backend desligado, timeout, 401 token vencido, 403, 404, 400 com erro de validação, 500, e girar a tela durante o carregamento. Em cada célula: ok, falta tratamento ou trava o app. Leia o código pra preencher, sem chutar. Não altere nada ainda.
```

### Correções

```text
Corrija um grupo de telas por vez, começando pelo que trava o app. Use o Resultado e o layout de estado da tela em vez de tratar cada tela de um jeito. O 401 volta ao login num lugar só. Pra cada tela de outro integrante, me diga o que mudou pra eu avisar o dono. Rode gradlew.bat testDebugUnitTest no fim de cada grupo e pare.
```

### APK

```text
Gere o APK de debug com gradlew.bat assembleDebug e me diga onde ele fica. Depois me explique o que preciso fazer pra ele falar com o backend no meu PC usando um celular de verdade: URL base com o IP do PC na rede, liberação de HTTP pra esse IP, firewall do Windows na porta 8080 e o backend aceitando conexão de fora. Deixe a URL num lugar fácil de trocar, nunca espalhada pelo código.
```

### Checklist manual

- [ ] Modo avião em cada tela: aviso amigável, e "tentar de novo" funciona quando a internet volta
- [ ] Desligar o backend no meio do uso não fecha o app
- [ ] Token vencido em qualquer tela volta ao login com aviso
- [ ] Girar a tela durante um carregamento não duplica a busca nem trava
- [ ] APK instalado num celular de verdade, com login e uma lista funcionando pelo Wi-Fi
- [ ] Prints da tela sem internet pros entregáveis

## Prompts reutilizáveis

Servem pra todas as fases. Troque `<X>` pelo código da fase (1B, 2B, 3B ou 4).

### T1 — Testes e lint (depois do último pedaço)

```text
Rode mvnw.cmd test (se a fase mexeu no backend), gradlew.bat testDebugUnitTest, gradlew.bat lintDebug e gradlew.bat assembleDebug. Corrija o que falhar e os erros de lint nos arquivos da minha fase. Me diga quais avisos ficaram e por quê.
```

### R1 — Revisão com contexto limpo (depois do `/clear`)

```text
Revise a minha branch como um revisor exigente que não escreveu esse código. Veja o diff com git diff main...HEAD e leia o AGENTS.md e a Fase <X> do PLANO-TRABALHO.md.

Procure: bugs, casos não tratados (vazio, nulo, sem rede, girar a tela, clique duplo), regra diferente do AGENTS.md, dinheiro fora de BigDecimal, permissão checada só no app e não no servidor, teste que passa sem testar nada de verdade, código duplicado com o que já existe na main, e mudança fora da minha fase.

Liste por gravidade (quebra, importante, detalhe), com arquivo, linha e o cenário que dá errado. Não corrija nada ainda.
```

Depois de ler a lista:

```text
Corrija os itens de quebra e os importantes. Pra cada um, escreva antes um teste que falhe por causa do problema, quando der. Rode os testes e pare.
```

### P1 — Descrição do pull request

```text
Escreva a descrição do pull request da Fase <X>: o que foi feito, requisitos cobertos (GP-xx), como testar passo a passo, o que ficou pendente e o que os colegas precisam saber (arquivos compartilhados que mudei, mudança no contrato da API). Não faça commit.
```

Depois: `git add .`, `git commit -m "Fase <X>: ..."`, `git push -u origin <branch>`, abrir o PR no GitHub e marcar um colega pra revisar.

### Quando travar

```text
Pare. Antes de tentar de novo, me explique o que está dando errado, o que você já tentou e duas hipóteses pra causa. Não mude código até eu escolher.
```

### E1 — Estudar pra apresentação (no fim de cada fase)

```text
Me explique a minha Fase <X> do botão na tela até o banco, em tópicos curtos e linguagem simples: qual tela e layout, qual ViewModel, qual chamada no ApiServico, qual endpoint e controller, qual regra no service e qual tabela. Depois me faça 5 perguntas que o professor poderia fazer sobre esse código, uma por vez, e corrija minhas respostas.
```
