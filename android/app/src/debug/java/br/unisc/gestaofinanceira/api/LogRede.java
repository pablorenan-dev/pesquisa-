package br.unisc.gestaofinanceira.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/** Versão de debug: mostra no Logcat (tag "OkHttp") o método, a URL e o status de cada chamada. */
final class LogRede {

    private LogRede() {
    }

    static void adicionar(OkHttpClient.Builder construtor) {
        // BASIC não mostra corpo nem cabeçalhos: senha e token não aparecem no log.
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BASIC);
        log.redactHeader("Authorization");
        construtor.addInterceptor(log);
    }
}
