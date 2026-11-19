package br.unisc.gestaofinanceira.dados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import br.unisc.gestaofinanceira.api.ApiServico;
import br.unisc.gestaofinanceira.api.ClienteApi;
import br.unisc.gestaofinanceira.modelo.LoginRequisicao;
import br.unisc.gestaofinanceira.modelo.LoginResposta;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/** A chamada assíncrona (enqueue) entrega o Resultado já mapeado. */
public class ChamadaApiTest {

    private MockWebServer servidor;
    private ApiServico api;

    @Before
    public void iniciar() throws IOException {
        servidor = new MockWebServer();
        servidor.start();
        api = ClienteApi.criarRetrofit(servidor.url("/api/").toString(), new OkHttpClient())
                .create(ApiServico.class);
    }

    @After
    public void parar() throws IOException {
        servidor.shutdown();
    }

    @Test
    public void enfileirar_entregaSucesso() throws InterruptedException {
        servidor.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"token\":\"abc123\",\"expiraEm\":\"2026-11-19T18:30:00-03:00\","
                        + "\"usuario\":{\"id\":1,\"nome\":\"Ana\",\"perfil\":\"GESTOR\"}}"));

        Resultado<LoginResposta> resultado = esperar();

        assertTrue(resultado.isSucesso());
        assertEquals("abc123", resultado.getDados().getToken());
    }

    @Test
    public void enfileirar_entregaErroHttpMapeado() throws InterruptedException {
        servidor.enqueue(new MockResponse().setResponseCode(401).setBody("{\"mensagem\":\"Senha incorreta\"}"));

        Resultado<LoginResposta> resultado = esperar();

        assertEquals(TipoErro.NAO_AUTORIZADO, resultado.getErro());
        assertEquals("Senha incorreta", resultado.getMensagem());
    }

    private Resultado<LoginResposta> esperar() throws InterruptedException {
        CountDownLatch terminou = new CountDownLatch(1);
        AtomicReference<Resultado<LoginResposta>> recebido = new AtomicReference<>();

        ChamadaApi.enfileirar(api.login(new LoginRequisicao("ana@unisc.br", "123456")), resultado -> {
            recebido.set(resultado);
            terminou.countDown();
        });

        assertTrue("a resposta não chegou", terminou.await(5, TimeUnit.SECONDS));
        return recebido.get();
    }
}
