package br.unisc.gestaofinanceira.sessao;

/** Ações que dependem do perfil (tabela de perfis do AGENTS.md). Ver tudo, todos podem. */
public enum Acao {
    /** Criar e editar projeto e rubricas. */
    EDITAR_PROJETO,
    /** Criar e editar lançamentos. */
    EDITAR_LANCAMENTO,
    /** Anexar comprovante a um lançamento. */
    ANEXAR_COMPROVANTE,
    /** Cancelar (exclusão lógica) um lançamento. */
    CANCELAR_LANCAMENTO
}
