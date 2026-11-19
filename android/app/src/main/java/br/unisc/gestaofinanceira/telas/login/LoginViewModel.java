package br.unisc.gestaofinanceira.telas.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.unisc.gestaofinanceira.R;
import br.unisc.gestaofinanceira.dados.AuthRepositorio;
import br.unisc.gestaofinanceira.dados.Resultado;
import br.unisc.gestaofinanceira.dados.TipoErro;
import br.unisc.gestaofinanceira.modelo.LoginResposta;

/**
 * Estado da tela de login. Sobrevive ao giro da tela, então a requisição em andamento
 * não é refeita e o resultado chega na tela nova.
 */
public class LoginViewModel extends ViewModel {

    private final AuthRepositorio repositorio;
    private final MutableLiveData<EstadoLogin> estado = new MutableLiveData<>(EstadoLogin.ocioso());

    public LoginViewModel(AuthRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /** Cria o ViewModel com o repositório (o ViewModelProvider só sabe criar sem parâmetro). */
    public static ViewModelProvider.Factory fabrica(AuthRepositorio repositorio) {
        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> classe) {
                return (T) new LoginViewModel(repositorio);
            }
        };
    }

    public LiveData<EstadoLogin> getEstado() {
        return estado;
    }

    public void entrar(String email, String senha) {
        // Toque duplo: enquanto carrega, ignora.
        if (estado.getValue() != null && estado.getValue().isCarregando()) {
            return;
        }
        String emailLimpo = email == null ? "" : email.trim();
        String senhaDigitada = senha == null ? "" : senha;

        int erroEmail = emailLimpo.isEmpty() ? R.string.login_erro_email_vazio : 0;
        int erroSenha = senhaDigitada.isEmpty() ? R.string.login_erro_senha_vazia : 0;
        if (erroEmail != 0 || erroSenha != 0) {
            estado.setValue(EstadoLogin.erroNosCampos(erroEmail, erroSenha));
            return;
        }

        estado.setValue(EstadoLogin.carregando());
        repositorio.entrar(emailLimpo, senhaDigitada, this::aoResponder);
    }

    private void aoResponder(Resultado<LoginResposta> resultado) {
        if (resultado.isSucesso()) {
            estado.setValue(EstadoLogin.sucesso());
        } else {
            estado.setValue(EstadoLogin.erro(mensagemPara(resultado.getErro())));
        }
    }

    private static int mensagemPara(TipoErro erro) {
        switch (erro) {
            case SEM_CONEXAO:
                return R.string.login_erro_sem_conexao;
            case TIMEOUT:
                return R.string.login_erro_timeout;
            case NAO_AUTORIZADO:
                return R.string.login_erro_credenciais;
            case VALIDACAO:
                return R.string.login_erro_validacao;
            default:
                return R.string.login_erro_servidor;
        }
    }
}
