package br.unisc.gestaofinanceira.sessao;

import android.content.Context;
import android.content.SharedPreferences;

import br.unisc.gestaofinanceira.modelo.LoginResposta;

/**
 * Sessão do usuário logado, no SharedPreferences privado do app.
 * Guarda só token, nome, perfil e expiração. A senha nunca é guardada.
 */
public class SessaoUsuario {

    private static final String ARQUIVO = "sessao";
    private static final String CHAVE_TOKEN = "token";
    private static final String CHAVE_EXPIRA_EM = "expiraEm";
    private static final String CHAVE_NOME = "nome";
    private static final String CHAVE_PERFIL = "perfil";

    private final SharedPreferences preferencias;

    public SessaoUsuario(SharedPreferences preferencias) {
        this.preferencias = preferencias;
    }

    public static SessaoUsuario de(Context contexto) {
        return new SessaoUsuario(contexto.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE));
    }

    public void salvar(LoginResposta resposta) {
        preferencias.edit()
                .putString(CHAVE_TOKEN, resposta.getToken())
                .putString(CHAVE_EXPIRA_EM, resposta.getExpiraEm())
                .putString(CHAVE_NOME, resposta.getUsuario().getNome())
                .putString(CHAVE_PERFIL, resposta.getUsuario().getPerfil())
                .apply();
    }

    /** Sair ou token vencido: apaga tudo. */
    public void limpar() {
        preferencias.edit().clear().apply();
    }

    public boolean estaLogado() {
        return getToken() != null;
    }

    public String getToken() {
        return preferencias.getString(CHAVE_TOKEN, null);
    }

    public String getExpiraEm() {
        return preferencias.getString(CHAVE_EXPIRA_EM, null);
    }

    public String getNome() {
        return preferencias.getString(CHAVE_NOME, null);
    }

    /** Nulo se não há sessão ou se o perfil é desconhecido. */
    public Perfil getPerfil() {
        return Perfil.deTexto(preferencias.getString(CHAVE_PERFIL, null));
    }

    /** Único lugar que decide o que cada perfil pode fazer. Quem garante de verdade é o 403 do servidor. */
    public boolean pode(Acao acao) {
        Perfil perfil = getPerfil();
        if (perfil == null) {
            return false;
        }
        switch (perfil) {
            case COORDENADOR:
                return true;
            case GESTOR:
                return acao == Acao.EDITAR_LANCAMENTO || acao == Acao.ANEXAR_COMPROVANTE;
            default:
                return false;
        }
    }
}
