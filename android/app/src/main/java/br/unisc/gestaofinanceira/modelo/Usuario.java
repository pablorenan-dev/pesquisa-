package br.unisc.gestaofinanceira.modelo;

/** Usuário logado: {id, nome, perfil}. O perfil chega como texto (COORDENADOR, GESTOR ou CONSULTA). */
public class Usuario {

    private Long id;
    private String nome;
    private String perfil;

    /** Usado pelo Gson. */
    public Usuario() {
    }

    public Usuario(Long id, String nome, String perfil) {
        this.id = id;
        this.nome = nome;
        this.perfil = perfil;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPerfil() {
        return perfil;
    }
}
