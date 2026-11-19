package br.unisc.gestaofinanceira.sessao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import br.unisc.gestaofinanceira.modelo.LoginResposta;
import br.unisc.gestaofinanceira.modelo.Usuario;

public class SessaoUsuarioTest {

    private PreferenciasEmMemoria preferencias;
    private SessaoUsuario sessao;

    @Before
    public void iniciar() {
        preferencias = new PreferenciasEmMemoria();
        sessao = new SessaoUsuario(preferencias);
    }

    @Test
    public void semLogin_naoEstaLogado() {
        assertFalse(sessao.estaLogado());
        assertNull(sessao.getToken());
    }

    @Test
    public void salvar_guardaTokenNomePerfilEExpiracao() {
        sessao.salvar(resposta("COORDENADOR"));

        assertTrue(sessao.estaLogado());
        assertEquals("abc123", sessao.getToken());
        assertEquals("Ana", sessao.getNome());
        assertEquals(Perfil.COORDENADOR, sessao.getPerfil());
        assertEquals("2026-11-19T18:30:00-03:00", sessao.getExpiraEm());
    }

    @Test
    public void salvar_naoGuardaNadaAlemDoQueOContratoDevolve() {
        sessao.salvar(resposta("GESTOR"));

        // Só token, nome, perfil e expiração: a senha nunca chega à sessão.
        assertEquals(4, preferencias.getAll().size());
    }

    @Test
    public void limpar_apagaTudo() {
        sessao.salvar(resposta("GESTOR"));

        sessao.limpar();

        assertFalse(sessao.estaLogado());
        assertNull(sessao.getNome());
        assertNull(sessao.getPerfil());
        assertTrue(preferencias.getAll().isEmpty());
    }

    @Test
    public void coordenador_podeTudo() {
        sessao.salvar(resposta("COORDENADOR"));

        assertTrue(sessao.pode(Acao.EDITAR_PROJETO));
        assertTrue(sessao.pode(Acao.EDITAR_LANCAMENTO));
        assertTrue(sessao.pode(Acao.ANEXAR_COMPROVANTE));
        assertTrue(sessao.pode(Acao.CANCELAR_LANCAMENTO));
    }

    @Test
    public void gestor_podeLancarEAnexar_masNaoProjetoNemCancelar() {
        sessao.salvar(resposta("GESTOR"));

        assertFalse(sessao.pode(Acao.EDITAR_PROJETO));
        assertTrue(sessao.pode(Acao.EDITAR_LANCAMENTO));
        assertTrue(sessao.pode(Acao.ANEXAR_COMPROVANTE));
        assertFalse(sessao.pode(Acao.CANCELAR_LANCAMENTO));
    }

    @Test
    public void consulta_naoPodeNada() {
        sessao.salvar(resposta("CONSULTA"));

        for (Acao acao : Acao.values()) {
            assertFalse(acao.name(), sessao.pode(acao));
        }
    }

    @Test
    public void perfilDesconhecido_naoPodeNada() {
        sessao.salvar(resposta("ADMIN"));

        assertNull(sessao.getPerfil());
        for (Acao acao : Acao.values()) {
            assertFalse(acao.name(), sessao.pode(acao));
        }
    }

    @Test
    public void semSessao_naoPodeNada() {
        for (Acao acao : Acao.values()) {
            assertFalse(acao.name(), sessao.pode(acao));
        }
    }

    private LoginResposta resposta(String perfil) {
        return new LoginResposta("abc123", "2026-11-19T18:30:00-03:00", new Usuario(1L, "Ana", perfil));
    }
}
