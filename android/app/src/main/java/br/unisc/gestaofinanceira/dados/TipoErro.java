package br.unisc.gestaofinanceira.dados;

/** Tipos de falha de uma chamada à API. A tela escolhe a mensagem do strings.xml pelo tipo. */
public enum TipoErro {
    /** Sem internet ou backend desligado. */
    SEM_CONEXAO,
    /** O servidor não respondeu em 15 s. */
    TIMEOUT,
    /** 401: sem token, token vencido ou senha errada no login. */
    NAO_AUTORIZADO,
    /** 403: o perfil não tem permissão. */
    PROIBIDO,
    /** 404: o registro não existe. */
    NAO_ENCONTRADO,
    /** 400: algum campo inválido (ver os campos do Resultado). */
    VALIDACAO,
    /** 422: regra de negócio, ex.: saldo insuficiente. */
    REGRA_NEGOCIO,
    /** 500 ou qualquer outra resposta inesperada. */
    SERVIDOR
}
