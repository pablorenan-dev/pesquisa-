package br.unisc.gestaofinanceira.modelo;

import java.util.Collections;
import java.util.Map;

/** Corpo de erro padrão da API: {mensagem, campos: {campo: mensagem}}. */
public class ErroApi {

    private String mensagem;
    private Map<String, String> campos;

    public String getMensagem() {
        return mensagem;
    }

    /** Nunca nulo: sem erro de campo, devolve um mapa vazio. */
    public Map<String, String> getCampos() {
        return campos != null ? campos : Collections.emptyMap();
    }
}
