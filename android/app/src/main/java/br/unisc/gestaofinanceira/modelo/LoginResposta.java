package br.unisc.gestaofinanceira.modelo;

/** Resposta do login: {token, expiraEm, usuario}. */
public class LoginResposta {

    private String token;
    /** Data e hora ISO-8601 com fuso, ex.: 2026-11-19T18:30:00-03:00. */
    private String expiraEm;
    private Usuario usuario;

    /** Usado pelo Gson. */
    public LoginResposta() {
    }

    public LoginResposta(String token, String expiraEm, Usuario usuario) {
        this.token = token;
        this.expiraEm = expiraEm;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public String getExpiraEm() {
        return expiraEm;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
