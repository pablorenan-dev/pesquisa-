package br.unisc.gestaofinanceira.telas.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import br.unisc.gestaofinanceira.R;
import br.unisc.gestaofinanceira.api.ClienteApi;
import br.unisc.gestaofinanceira.dados.AuthRepositorioApi;
import br.unisc.gestaofinanceira.sessao.SessaoUsuario;
import br.unisc.gestaofinanceira.telas.projetos.ProjetosActivity;

/** Tela de login (GP-01). Só mostra o estado do LoginViewModel e repassa o toque em Entrar. */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout campoEmail;
    private TextInputLayout campoSenha;
    private TextInputEditText textoEmail;
    private TextInputEditText textoSenha;
    private TextView textoErro;
    private MaterialButton botaoEntrar;
    private View carregando;

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ligarViews();

        AuthRepositorioApi repositorio = new AuthRepositorioApi(ClienteApi.obter(this), SessaoUsuario.de(this));
        viewModel = new ViewModelProvider(this, LoginViewModel.fabrica(repositorio)).get(LoginViewModel.class);
        viewModel.getEstado().observe(this, this::mostrar);

        botaoEntrar.setOnClickListener(v -> entrar());
        textoSenha.setOnEditorActionListener((v, acao, evento) -> {
            if (acao == EditorInfo.IME_ACTION_DONE) {
                entrar();
                return true;
            }
            return false;
        });
    }

    private void ligarViews() {
        campoEmail = findViewById(R.id.campo_email);
        campoSenha = findViewById(R.id.campo_senha);
        textoEmail = findViewById(R.id.texto_email);
        textoSenha = findViewById(R.id.texto_senha);
        textoErro = findViewById(R.id.texto_erro);
        botaoEntrar = findViewById(R.id.botao_entrar);
        carregando = findViewById(R.id.carregando);
    }

    private void entrar() {
        viewModel.entrar(texto(textoEmail), texto(textoSenha));
    }

    private void mostrar(EstadoLogin estado) {
        if (estado.getEtapa() == EstadoLogin.Etapa.SUCESSO) {
            abrirProjetos();
            return;
        }
        boolean ocupado = estado.isCarregando();
        botaoEntrar.setEnabled(!ocupado);
        carregando.setVisibility(ocupado ? View.VISIBLE : View.GONE);

        campoEmail.setError(textoOuNulo(estado.getErroEmail()));
        campoSenha.setError(textoOuNulo(estado.getErroSenha()));

        int mensagem = estado.getMensagemErro();
        textoErro.setVisibility(mensagem != 0 ? View.VISIBLE : View.GONE);
        if (mensagem != 0) {
            textoErro.setText(mensagem);
        }
    }

    private void abrirProjetos() {
        startActivity(new Intent(this, ProjetosActivity.class));
        finish();
    }

    private CharSequence textoOuNulo(int idTexto) {
        return idTexto != 0 ? getString(idTexto) : null;
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() != null ? campo.getText().toString() : "";
    }
}
