package br.unisc.gestaofinanceira.dados;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Faz a chamada fora da thread principal (enqueue) e entrega um Resultado já mapeado. */
public final class ChamadaApi {

    /** Quem recebe o resultado. No app, roda na thread principal. */
    public interface Retorno<T> {
        void aoTerminar(Resultado<T> resultado);
    }

    private ChamadaApi() {
    }

    public static <T> void enfileirar(Call<T> chamada, Retorno<T> retorno) {
        chamada.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> resposta) {
                retorno.aoTerminar(MapeadorErro.deResposta(resposta));
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable falha) {
                retorno.aoTerminar(MapeadorErro.deFalha(falha));
            }
        });
    }
}
