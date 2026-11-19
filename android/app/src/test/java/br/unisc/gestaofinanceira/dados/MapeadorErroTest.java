package br.unisc.gestaofinanceira.dados;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.unisc.gestaofinanceira.api.ApiServico;
import br.unisc.gestaofinanceira.api.ClienteApi;
import br.unisc.gestaofinanceira.modelo.LoginRequisicao;
import br.unisc.gestaofinanceira.modelo.LoginResposta;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

/** Respostas do servidor falso (seguindo o contrato) viram o Resultado certo. */
public class MapeadorErroTest {

    private MockWebServer servidor;
    private OkHttpClient cliente;

    @Before
    public void iniciar() throws IOException {
        servidor = new MockWebServer();
        servidor.start();
        // Timeout curto só no teste, para o caso de timeout não demorar 15 s.
        cliente = new OkHttpClient.Builder().readTimeout(1, TimeUnit.SECONDS).build();
    }

    @After
    public void parar() throws IOException {
        servidor.shutdown();
    }

    @Test
    public void status200_viraSucessoComOsDadosDoContrato() {
        responder(200, "{\"token\":\"abc123\",\"expiraEm\":\"2026-11-19T18:30:00-03:00\","
                + "\"usuario\":{\"id\":1,\"nome\":\"Ana\",\"perfil\":\"COORDENADOR\"}}");

        Resultado<LoginResposta> resultado = chamarLogin(urlServidor());

        assertTrue(resultado.isSucesso());
        assertEquals("abc123", resultado.getDados().getToken());
        assertEquals("2026-11-19T18:30:00-03:00", resultado.getDados().getExpiraEm());
        assertEquals("Ana", resultado.getDados().getUsuario().getNome());
        assertEquals("COORDENADOR", resultado.getDados().getUsuario().getPerfil());
    }

    @Test
    public void status400_viraValidacaoComOsCampos() {
        responder(400, "{\"mensagem\":\"Dados inválidos\",\"campos\":{\"email\":\"Informe o e-mail\"}}");

        Resultado<LoginResposta> resultado = chamarLogin(urlServidor());

        assertErro(resultado, TipoErro.VALIDACAO, "Dados inválidos");
        assertEquals("Informe o e-mail", resultado.getCampos().get("email"));
    }

    @Test
    public void status401_viraNaoAutorizado() {
        responder(401, "{\"mensagem\":\"E-mail ou senha incorretos\"}");

        assertErro(chamarLogin(urlServidor()), TipoErro.NAO_AUTORIZADO, "E-mail ou senha incorretos");
    }

    @Test
    public void status403_viraProibido() {
        responder(403, "{\"mensagem\":\"Sem permissão\"}");

        assertErro(chamarLogin(urlServidor()), TipoErro.PROIBIDO, "Sem permissão");
    }

    @Test
    public void status404_viraNaoEncontrado() {
        responder(404, "{\"mensagem\":\"Não encontrado\"}");

        assertErro(chamarLogin(urlServidor()), TipoErro.NAO_ENCONTRADO, "Não encontrado");
    }

    @Test
    public void status422_viraRegraDeNegocio() {
        responder(422, "{\"mensagem\":\"Saldo insuficiente na rubrica\"}");

        assertErro(chamarLogin(urlServidor()), TipoErro.REGRA_NEGOCIO, "Saldo insuficiente na rubrica");
    }

    @Test
    public void status500ComCorpoQueNaoEJson_viraServidorSemQuebrar() {
        responder(500, "<html>Internal Server Error</html>");

        Resultado<LoginResposta> resultado = chamarLogin(urlServidor());

        assertErro(resultado, TipoErro.SERVIDOR, null);
        assertTrue(resultado.getCampos().isEmpty());
    }

    @Test
    public void statusDesconhecido_viraServidor() {
        responder(409, "{\"mensagem\":\"Conflito\"}");

        assertErro(chamarLogin(urlServidor()), TipoErro.SERVIDOR, "Conflito");
    }

    @Test
    public void backendDesligado_viraSemConexao() {
        // Porta 1: ninguém escuta, a conexão é recusada.
        assertErro(chamarLogin("http://127.0.0.1:1/api/"), TipoErro.SEM_CONEXAO, null);
    }

    @Test
    public void servidorQueNaoResponde_viraTimeout() {
        servidor.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

        assertErro(chamarLogin(urlServidor()), TipoErro.TIMEOUT, null);
    }

    @Test
    public void falhaQueNaoEDeRede_viraServidor() {
        Resultado<Object> resultado = MapeadorErro.deFalha(new IllegalStateException("JSON inesperado"));

        assertErro(resultado, TipoErro.SERVIDOR, null);
    }

    private void responder(int status, String corpo) {
        servidor.enqueue(new MockResponse().setResponseCode(status).setBody(corpo));
    }

    private String urlServidor() {
        return servidor.url("/api/").toString();
    }

    /** Faz o login de forma síncrona (só no teste) e passa pelo mesmo mapeamento do app. */
    private Resultado<LoginResposta> chamarLogin(String urlBase) {
        ApiServico api = ClienteApi.criarRetrofit(urlBase, cliente).create(ApiServico.class);
        try {
            return MapeadorErro.deResposta(api.login(new LoginRequisicao("ana@unisc.br", "123456")).execute());
        } catch (IOException falha) {
            return MapeadorErro.deFalha(falha);
        }
    }

    private static void assertErro(Resultado<?> resultado, TipoErro tipo, String mensagem) {
        assertFalse(resultado.isSucesso());
        assertEquals(tipo, resultado.getErro());
        if (mensagem == null) {
            assertNull(resultado.getMensagem());
        } else {
            assertEquals(mensagem, resultado.getMensagem());
        }
    }
}
