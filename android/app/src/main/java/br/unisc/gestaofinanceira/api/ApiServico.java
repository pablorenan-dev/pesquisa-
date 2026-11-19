package br.unisc.gestaofinanceira.api;

import br.unisc.gestaofinanceira.modelo.LoginRequisicao;
import br.unisc.gestaofinanceira.modelo.LoginResposta;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Rotas da API (contrato no AGENTS.md). Cada área mexe só no seu bloco.
 * Os caminhos são relativos à URL base, que já termina em /api/.
 */
public interface ApiServico {

    // ===== login (1B) =====

    @POST("auth/login")
    Call<LoginResposta> login(@Body LoginRequisicao requisicao);

}
