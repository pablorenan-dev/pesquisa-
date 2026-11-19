package br.unisc.gestaofinanceira.dados;

import br.unisc.gestaofinanceira.modelo.LoginResposta;

/** Login na API. É uma interface para o ViewModel poder ser testado com um repositório falso. */
public interface AuthRepositorio {

    /** Chama o login fora da thread principal e entrega o Resultado. No sucesso, a sessão já está salva. */
    void entrar(String email, String senha, ChamadaApi.Retorno<LoginResposta> retorno);
}
