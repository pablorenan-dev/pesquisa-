package br.unisc.gestaofinanceira.telas.login;

/**
 * O que a tela de login mostra. As mensagens são ids do strings.xml (0 = sem mensagem),
 * assim o ViewModel não precisa de Context e dá para testar.
 */
public final class EstadoLogin {

    public enum Etapa { OCIOSO, CARREGANDO, SUCESSO, ERRO }

    private final Etapa etapa;
    private final int erroEmail;
    private final int erroSenha;
    private final int mensagemErro;

    private EstadoLogin(Etapa etapa, int erroEmail, int erroSenha, int mensagemErro) {
        this.etapa = etapa;
        this.erroEmail = erroEmail;
        this.erroSenha = erroSenha;
        this.mensagemErro = mensagemErro;
    }

    static EstadoLogin ocioso() {
        return new EstadoLogin(Etapa.OCIOSO, 0, 0, 0);
    }

    static EstadoLogin carregando() {
        return new EstadoLogin(Etapa.CARREGANDO, 0, 0, 0);
    }

    static EstadoLogin sucesso() {
        return new EstadoLogin(Etapa.SUCESSO, 0, 0, 0);
    }

    /** Erro em campo: aparece no próprio campo. */
    static EstadoLogin erroNosCampos(int erroEmail, int erroSenha) {
        return new EstadoLogin(Etapa.ERRO, erroEmail, erroSenha, 0);
    }

    /** Erro geral (rede, senha errada, servidor): aparece abaixo dos campos. */
    static EstadoLogin erro(int mensagem) {
        return new EstadoLogin(Etapa.ERRO, 0, 0, mensagem);
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public boolean isCarregando() {
        return etapa == Etapa.CARREGANDO;
    }

    public int getErroEmail() {
        return erroEmail;
    }

    public int getErroSenha() {
        return erroSenha;
    }

    public int getMensagemErro() {
        return mensagemErro;
    }
}
