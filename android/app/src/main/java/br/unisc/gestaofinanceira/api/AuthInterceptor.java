package br.unisc.gestaofinanceira.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import br.unisc.gestaofinanceira.sessao.SessaoUsuario;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** Coloca o "Authorization: Bearer <token>" em toda chamada quando há sessão. */
public class AuthInterceptor implements Interceptor {

    private final SessaoUsuario sessao;

    public AuthInterceptor(SessaoUsuario sessao) {
        this.sessao = sessao;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain cadeia) throws IOException {
        Request pedido = cadeia.request();
        String token = sessao.getToken();
        if (token == null) {
            return cadeia.proceed(pedido);
        }
        Request comToken = pedido.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return cadeia.proceed(comToken);
    }
}
