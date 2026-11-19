package br.unisc.gestaofinanceira.modelo;

/** Corpo do POST /auth/login. A senha só existe aqui, na hora de enviar: nunca é salva. */
public class LoginRequisicao {

    private final String email;
    private final String senha;

    public LoginRequisicao(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
