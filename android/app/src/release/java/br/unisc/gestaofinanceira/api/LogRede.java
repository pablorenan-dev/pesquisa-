package br.unisc.gestaofinanceira.api;

import okhttp3.OkHttpClient;

/** Versão de release: não registra nada (o logging-interceptor nem entra no APK de release). */
final class LogRede {

    private LogRede() {
    }

    static void adicionar(OkHttpClient.Builder construtor) {
        // de propósito vazio
    }
}
