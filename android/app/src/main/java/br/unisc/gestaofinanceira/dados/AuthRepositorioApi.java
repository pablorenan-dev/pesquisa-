package br.unisc.gestaofinanceira.dados;

import br.unisc.gestaofinanceira.api.ApiServico;
import br.unisc.gestaofinanceira.modelo.LoginRequisicao;
import br.unisc.gestaofinanceira.modelo.LoginResposta;
import br.unisc.gestaofinanceira.sessao.SessaoUsuario;

/** Login de verdade: POST /auth/login e, se der certo, guarda token, nome e perfil na sessão. */
public class AuthRepositorioApi implements AuthRepositorio {

    private final ApiServico api;
    private final SessaoUsuario sessao;

    public AuthRepositorioApi(ApiServico api, SessaoUsuario sessao) {
        this.api = api;
        this.sessao = sessao;
    }

    @Override
    public void entrar(String email, String senha, ChamadaApi.Retorno<LoginResposta> retorno) {
        ChamadaApi.enfileirar(api.login(new LoginRequisicao(email, senha)), resultado -> {
            if (resultado.isSucesso() && resultado.getDados() != null) {
                sessao.salvar(resultado.getDados());
            }
            retorno.aoTerminar(resultado);
        });
    }
}
