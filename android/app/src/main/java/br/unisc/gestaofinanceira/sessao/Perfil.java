package br.unisc.gestaofinanceira.sessao;

/** Perfis do contrato da API. O perfil é global e vem no login. */
public enum Perfil {
    COORDENADOR,
    GESTOR,
    CONSULTA;

    /** Converte o texto da API. Perfil desconhecido ou nulo vira nulo (e não pode nada). */
    public static Perfil deTexto(String texto) {
        if (texto == null) {
            return null;
        }
        for (Perfil perfil : values()) {
            if (perfil.name().equals(texto)) {
                return perfil;
            }
        }
        return null;
    }
}
