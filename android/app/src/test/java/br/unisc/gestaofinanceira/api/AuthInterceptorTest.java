package br.unisc.gestaofinanceira.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import br.unisc.gestaofinanceira.modelo.LoginRequisicao;
import br.unisc.gestaofinanceira.modelo.LoginResposta;
import br.unisc.gestaofinanceira.modelo.Usuario;
import br.unisc.gestaofinanceira.sessao.PreferenciasEmMemoria;
import br.unisc.gestaofinanceira.sessao.SessaoUsuario;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/** O cliente HTTP do app coloca o Bearer quando há sessão e usa o timeout de 15 s. */
public class AuthInterceptorTest {

    private MockWebServer servidor;
    private SessaoUsuario sessao;
    private OkHttpClient cliente;

    @Before
    public void iniciar() throws IOException {
        servidor = new MockWebServer();
        servidor.start();
        sessao = new SessaoUsuario(new PreferenciasEmMemoria());
        cliente = ClienteApi.criarClienteHttp(sessao);
    }

    @After
    public void parar() throws IOException {
        servidor.shutdown();
    }

    @Test
    public void comSessao_mandaAuthorizationBearer() throws Exception {
        sessao.salvar(new LoginResposta("abc123", "2026-11-19T18:30:00-03:00", new Usuario(1L, "Ana", "GESTOR")));

        assertEquals("Bearer abc123", fazerChamada().getHeader("Authorization"));
    }

    @Test
    public void semSessao_naoMandaAuthorization() throws Exception {
        assertNull(fazerChamada().getHeader("Authorization"));
    }

    @Test
    public void timeoutsSaoDe15Segundos() {
        assertEquals(15_000, cliente.connectTimeoutMillis());
        assertEquals(15_000, cliente.readTimeoutMillis());
        assertEquals(15_000, cliente.writeTimeoutMillis());
    }

    private RecordedRequest fazerChamada() throws Exception {
        servidor.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        ApiServico api = ClienteApi.criarRetrofit(servidor.url("/api/").toString(), cliente)
                .create(ApiServico.class);
        api.login(new LoginRequisicao("ana@unisc.br", "123456")).execute();
        return servidor.takeRequest();
    }
}
