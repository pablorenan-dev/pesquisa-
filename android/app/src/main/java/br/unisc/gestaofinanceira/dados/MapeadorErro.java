package br.unisc.gestaofinanceira.dados;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import br.unisc.gestaofinanceira.modelo.ErroApi;
import okhttp3.ResponseBody;
import retrofit2.Response;

/** Converte a resposta ou a falha do Retrofit num Resultado. Um lugar só para o app inteiro. */
public final class MapeadorErro {

    private static final String TAG = "MapeadorErro";
    private static final Gson GSON = new Gson();

    private MapeadorErro() {
    }

    /** Resposta HTTP recebida: 2xx é sucesso, o resto vira erro pelo status. */
    public static <T> Resultado<T> deResposta(Response<T> resposta) {
        if (resposta.isSuccessful()) {
            return Resultado.sucesso(resposta.body());
        }
        ErroApi corpo = lerCorpoErro(resposta.errorBody());
        return Resultado.erro(tipoPorStatus(resposta.code()), corpo.getMensagem(), corpo.getCampos());
    }

    /** A chamada nem chegou a ter resposta (rede, timeout ou JSON inválido). */
    public static <T> Resultado<T> deFalha(Throwable falha) {
        // SocketTimeoutException também é IOException, por isso vem antes.
        if (falha instanceof SocketTimeoutException) {
            return Resultado.erro(TipoErro.TIMEOUT, null, null);
        }
        if (falha instanceof IOException) {
            return Resultado.erro(TipoErro.SEM_CONEXAO, null, null);
        }
        Log.e(TAG, "Falha inesperada na chamada à API", falha);
        return Resultado.erro(TipoErro.SERVIDOR, null, null);
    }

    private static TipoErro tipoPorStatus(int status) {
        switch (status) {
            case 400:
                return TipoErro.VALIDACAO;
            case 401:
                return TipoErro.NAO_AUTORIZADO;
            case 403:
                return TipoErro.PROIBIDO;
            case 404:
                return TipoErro.NAO_ENCONTRADO;
            case 422:
                return TipoErro.REGRA_NEGOCIO;
            default:
                return TipoErro.SERVIDOR;
        }
    }

    /** Lê o {mensagem, campos} do erro. Corpo vazio ou que não é JSON vira um erro sem mensagem. */
    private static ErroApi lerCorpoErro(ResponseBody corpo) {
        if (corpo == null) {
            return new ErroApi();
        }
        try {
            ErroApi erro = GSON.fromJson(corpo.string(), ErroApi.class);
            return erro != null ? erro : new ErroApi();
        } catch (IOException | JsonParseException falha) {
            Log.d(TAG, "Corpo de erro fora do formato da API", falha);
            return new ErroApi();
        }
    }
}
