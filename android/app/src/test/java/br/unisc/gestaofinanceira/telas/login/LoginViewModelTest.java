package br.unisc.gestaofinanceira.telas.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import br.unisc.gestaofinanceira.R;
import br.unisc.gestaofinanceira.dados.AuthRepositorio;
import br.unisc.gestaofinanceira.dados.ChamadaApi;
import br.unisc.gestaofinanceira.dados.Resultado;
import br.unisc.gestaofinanceira.dados.TipoErro;
import br.unisc.gestaofinanceira.modelo.LoginResposta;
import br.unisc.gestaofinanceira.modelo.Usuario;

public class LoginViewModelTest {

    // Faz o LiveData entregar o valor na hora, sem a thread principal do Android.
    @Rule
    public InstantTaskExecutorRule executorInstantaneo = new InstantTaskExecutorRule();

    private RepositorioFalso repositorio;
    private LoginViewModel viewModel;

    @Before
    public void iniciar() {
        repositorio = new RepositorioFalso();
        viewModel = new LoginViewModel(repositorio);
    }

    @Test
    public void comecaOcioso() {
        assertEquals(EstadoLogin.Etapa.OCIOSO, estado().getEtapa());
    }

    @Test
    public void loginCerto_passaPorCarregandoETerminaEmSucesso() {
        viewModel.entrar("ana@unisc.br", "123456");

        assertTrue(estado().isCarregando());

        repositorio.responder(Resultado.sucesso(
                new LoginResposta("abc123", "2026-11-19T18:30:00-03:00", new Usuario(1L, "Ana", "GESTOR"))));

        assertEquals(EstadoLogin.Etapa.SUCESSO, estado().getEtapa());
        assertEquals("ana@unisc.br", repositorio.ultimoEmail);
        assertEquals("123456", repositorio.ultimaSenha);
    }

    @Test
    public void emailComEspacos_eEnviadoSemEspacos() {
        viewModel.entrar("  ana@unisc.br  ", "123456");

        assertEquals("ana@unisc.br", repositorio.ultimoEmail);
    }

    @Test
    public void senhaErrada_mostraMensagemDeCredenciais() {
        viewModel.entrar("ana@unisc.br", "errada");
        repositorio.responder(Resultado.erro(TipoErro.NAO_AUTORIZADO, "Senha incorreta", null));

        assertEquals(EstadoLogin.Etapa.ERRO, estado().getEtapa());
        assertEquals(R.string.login_erro_credenciais, estado().getMensagemErro());
        assertFalse(estado().isCarregando());
    }

    @Test
    public void emailVazio_mostraErroNoCampoENaoChamaApi() {
        viewModel.entrar("   ", "123456");

        assertEquals(R.string.login_erro_email_vazio, estado().getErroEmail());
        assertEquals(0, estado().getErroSenha());
        assertEquals(0, repositorio.chamadas);
    }

    @Test
    public void senhaVazia_mostraErroNoCampoENaoChamaApi() {
        viewModel.entrar("ana@unisc.br", "");

        assertEquals(0, estado().getErroEmail());
        assertEquals(R.string.login_erro_senha_vazia, estado().getErroSenha());
        assertEquals(0, repositorio.chamadas);
    }

    @Test
    public void osDoisVazios_mostraOsDoisErros() {
        viewModel.entrar(null, null);

        assertEquals(R.string.login_erro_email_vazio, estado().getErroEmail());
        assertEquals(R.string.login_erro_senha_vazia, estado().getErroSenha());
        assertEquals(0, repositorio.chamadas);
    }

    @Test
    public void backendForaDoAr_mostraSemConexao() {
        viewModel.entrar("ana@unisc.br", "123456");
        repositorio.responder(Resultado.erro(TipoErro.SEM_CONEXAO, null, null));

        assertEquals(R.string.login_erro_sem_conexao, estado().getMensagemErro());
    }

    @Test
    public void timeout_mostraMensagemDeTimeout() {
        viewModel.entrar("ana@unisc.br", "123456");
        repositorio.responder(Resultado.erro(TipoErro.TIMEOUT, null, null));

        assertEquals(R.string.login_erro_timeout, estado().getMensagemErro());
    }

    @Test
    public void erroDeValidacaoDoServidor_mostraMensagemDeValidacao() {
        viewModel.entrar("ana", "123456");
        repositorio.responder(Resultado.erro(TipoErro.VALIDACAO, "E-mail inválido", null));

        assertEquals(R.string.login_erro_validacao, estado().getMensagemErro());
    }

    @Test
    public void erro500_mostraErroDoServidor() {
        viewModel.entrar("ana@unisc.br", "123456");
        repositorio.responder(Resultado.erro(TipoErro.SERVIDOR, null, null));

        assertEquals(R.string.login_erro_servidor, estado().getMensagemErro());
    }

    @Test
    public void segundoToqueEnquantoCarrega_naoMandaOutraRequisicao() {
        viewModel.entrar("ana@unisc.br", "123456");
        viewModel.entrar("ana@unisc.br", "123456");

        assertEquals(1, repositorio.chamadas);
    }

    @Test
    public void depoisDeUmErro_podeTentarDeNovo() {
        viewModel.entrar("ana@unisc.br", "123456");
        repositorio.responder(Resultado.erro(TipoErro.SEM_CONEXAO, null, null));

        viewModel.entrar("ana@unisc.br", "123456");

        assertEquals(2, repositorio.chamadas);
        assertTrue(estado().isCarregando());
    }

    private EstadoLogin estado() {
        return viewModel.getEstado().getValue();
    }

    /** Guarda a chamada e só responde quando o teste mandar, como a rede de verdade. */
    private static class RepositorioFalso implements AuthRepositorio {

        int chamadas;
        String ultimoEmail;
        String ultimaSenha;
        private ChamadaApi.Retorno<LoginResposta> retornoPendente;

        @Override
        public void entrar(String email, String senha, ChamadaApi.Retorno<LoginResposta> retorno) {
            chamadas++;
            ultimoEmail = email;
            ultimaSenha = senha;
            retornoPendente = retorno;
        }

        void responder(Resultado<LoginResposta> resultado) {
            retornoPendente.aoTerminar(resultado);
        }
    }
}
