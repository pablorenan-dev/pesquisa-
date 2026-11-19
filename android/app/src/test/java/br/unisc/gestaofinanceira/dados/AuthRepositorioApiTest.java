package br.unisc.gestaofinanceira.dados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import br.unisc.gestaofinanceira.modelo.LoginResposta;
import br.unisc.gestaofinanceira.sessao.PreferenciasEmMemoria;
import br.unisc.gestaofinanceira.sessao.SessaoUsuario;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class AuthRepositorioApiTest {

    private MockWebServer servidor;
    private SessaoUsuario sessao;
    private AuthRepositorioApi repositorio;

    @Before
    public void iniciar() throws IOException {
        servidor = new MockWebServer();
        servidor.start();
        sessao = new SessaoUsuario(new PreferenciasEmMemoria());
        ApiServico api = ClienteApi.criarRetrofit(servidor.url("/api/").toString(), new OkHttpClient())
                .create(ApiServico.class);
        repositorio = new AuthRepositorioApi(api, sessao);
    }

    @After
    public void parar() throws IOException {
        servidor.shutdown();
    }

    @Test
    public void loginCerto_salvaASessao() throws Exception {
        servidor.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"token\":\"abc123\",\"expiraEm\":\"2026-11-19T18:30:00-03:00\","
                        + "\"usuario\":{\"id\":1,\"nome\":\"Ana\",\"perfil\":\"GESTOR\"}}"));

        Resultado<LoginResposta> resultado = entrar();

        assertTrue(resultado.isSucesso());
        assertTrue(sessao.estaLogado());
        assertEquals("abc123", sessao.getToken());
        assertEquals("Ana", sessao.getNome());
    }

    @Test
    public void loginErrado_naoSalvaSessao() throws Exception {
        servidor.enqueue(new MockResponse().setResponseCode(401).setBody("{\"mensagem\":\"Senha incorreta\"}"));

        Resultado<LoginResposta> resultado = entrar();

        assertFalse(resultado.isSucesso());
        assertFalse(sessao.estaLogado());
    }

    @Test
    public void mandaEmailESenhaNoCorpoDoPostDoContrato() throws Exception {
        servidor.enqueue(new MockResponse().setResponseCode(401).setBody("{}"));

        entrar();

        RecordedRequest pedido = servidor.takeRequest();
        assertEquals("POST", pedido.getMethod());
        assertEquals("/api/auth/login", pedido.getPath());
        String corpo = pedido.getBody().readUtf8();
        assertTrue(corpo, corpo.contains("\"email\":\"ana@unisc.br\""));
        assertTrue(corpo, corpo.contains("\"senha\":\"123456\""));
    }

    private Resultado<LoginResposta> entrar() throws InterruptedException {
        CountDownLatch terminou = new CountDownLatch(1);
        AtomicReference<Resultado<LoginResposta>> recebido = new AtomicReference<>();
        repositorio.entrar("ana@unisc.br", "123456", resultado -> {
            recebido.set(resultado);
            terminou.countDown();
        });
        assertTrue("a resposta não chegou", terminou.await(5, TimeUnit.SECONDS));
        return recebido.get();
    }
}
