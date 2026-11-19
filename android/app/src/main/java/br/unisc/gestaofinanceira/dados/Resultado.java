package br.unisc.gestaofinanceira.dados;

import java.util.Collections;
import java.util.Map;

/**
 * Resposta de uma chamada à API: ou os dados (sucesso), ou o tipo do erro com a mensagem
 * e os erros de campo que vieram do servidor.
 */
public final class Resultado<T> {

    private final T dados;
    private final TipoErro erro;
    private final String mensagem;
    private final Map<String, String> campos;

    private Resultado(T dados, TipoErro erro, String mensagem, Map<String, String> campos) {
        this.dados = dados;
        this.erro = erro;
        this.mensagem = mensagem;
        this.campos = campos != null ? campos : Collections.emptyMap();
    }

    public static <T> Resultado<T> sucesso(T dados) {
        return new Resultado<>(dados, null, null, null);
    }

    public static <T> Resultado<T> erro(TipoErro tipo, String mensagem, Map<String, String> campos) {
        return new Resultado<>(null, tipo, mensagem, campos);
    }

    public boolean isSucesso() {
        return erro == null;
    }

    /** Os dados da resposta. Pode ser nulo em sucesso sem corpo (ex.: 204). */
    public T getDados() {
        return dados;
    }

    /** Nulo quando deu certo. */
    public TipoErro getErro() {
        return erro;
    }

    /** Mensagem que a API mandou. Nula em falha de rede ou quando o servidor não mandou. */
    public String getMensagem() {
        return mensagem;
    }

    /** Erros por campo (400). Nunca nulo. */
    public Map<String, String> getCampos() {
        return campos;
    }
}
