package br.unisc.gestaofinanceira.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import br.unisc.gestaofinanceira.BuildConfig;
import br.unisc.gestaofinanceira.sessao.SessaoUsuario;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Monta o Retrofit uma vez só para o app inteiro.
 * A URL base vem do BuildConfig.API_URL (app/build.gradle.kts): é lá que se troca.
 */
public final class ClienteApi {

    private static final int TIMEOUT_SEGUNDOS = 15;

    private static ApiServico servico;

    private ClienteApi() {
    }

    public static synchronized ApiServico obter(Context contexto) {
        if (servico == null) {
            SessaoUsuario sessao = SessaoUsuario.de(contexto.getApplicationContext());
            servico = criarRetrofit(BuildConfig.API_URL, criarClienteHttp(sessao)).create(ApiServico.class);
        }
        return servico;
    }

    /** Cliente HTTP com timeout de 15 s, o Bearer e o log (só no build de debug). */
    public static OkHttpClient criarClienteHttp(SessaoUsuario sessao) {
        OkHttpClient.Builder construtor = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(sessao));
        LogRede.adicionar(construtor);
        return construtor.build();
    }

    public static Retrofit criarRetrofit(String urlBase, OkHttpClient cliente) {
        return new Retrofit.Builder()
                .baseUrl(urlBase)
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
