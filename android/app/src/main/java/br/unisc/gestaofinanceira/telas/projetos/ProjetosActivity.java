package br.unisc.gestaofinanceira.telas.projetos;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import br.unisc.gestaofinanceira.R;

/** Tela de projetos. Casca criada na 1B só para o login ter para onde ir: a lista é da 2A. */
public class ProjetosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projetos);
    }
}
