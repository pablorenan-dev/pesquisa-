package br.unisc.gestaofinanceira.sessao;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** SharedPreferences falso, guardado num Map, para testar a sessão sem o Android. */
public class PreferenciasEmMemoria implements SharedPreferences {

    private final Map<String, Object> valores = new HashMap<>();

    @Override
    public Map<String, ?> getAll() {
        return new HashMap<>(valores);
    }

    @Override
    public String getString(String chave, String padrao) {
        Object valor = valores.get(chave);
        return valor != null ? (String) valor : padrao;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getStringSet(String chave, Set<String> padrao) {
        Object valor = valores.get(chave);
        return valor != null ? (Set<String>) valor : padrao;
    }

    @Override
    public int getInt(String chave, int padrao) {
        Object valor = valores.get(chave);
        return valor != null ? (Integer) valor : padrao;
    }

    @Override
    public long getLong(String chave, long padrao) {
        Object valor = valores.get(chave);
        return valor != null ? (Long) valor : padrao;
    }

    @Override
    public float getFloat(String chave, float padrao) {
        Object valor = valores.get(chave);
        return valor != null ? (Float) valor : padrao;
    }

    @Override
    public boolean getBoolean(String chave, boolean padrao) {
        Object valor = valores.get(chave);
        return valor != null ? (Boolean) valor : padrao;
    }

    @Override
    public boolean contains(String chave) {
        return valores.containsKey(chave);
    }

    @Override
    public Editor edit() {
        return new EditorEmMemoria();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener ouvinte) {
        // não usado nos testes
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener ouvinte) {
        // não usado nos testes
    }

    /** Junta as mudanças e aplica tudo de uma vez, como o Editor de verdade. */
    private class EditorEmMemoria implements Editor {

        private final Map<String, Object> novos = new HashMap<>();
        private final Set<String> removidos = new HashSet<>();
        private boolean limparTudo;

        @Override
        public Editor putString(String chave, String valor) {
            return colocar(chave, valor);
        }

        @Override
        public Editor putStringSet(String chave, Set<String> valor) {
            return colocar(chave, valor);
        }

        @Override
        public Editor putInt(String chave, int valor) {
            return colocar(chave, valor);
        }

        @Override
        public Editor putLong(String chave, long valor) {
            return colocar(chave, valor);
        }

        @Override
        public Editor putFloat(String chave, float valor) {
            return colocar(chave, valor);
        }

        @Override
        public Editor putBoolean(String chave, boolean valor) {
            return colocar(chave, valor);
        }

        @Override
        public Editor remove(String chave) {
            removidos.add(chave);
            return this;
        }

        @Override
        public Editor clear() {
            limparTudo = true;
            return this;
        }

        @Override
        public boolean commit() {
            if (limparTudo) {
                valores.clear();
            }
            for (String chave : removidos) {
                valores.remove(chave);
            }
            for (Map.Entry<String, Object> item : novos.entrySet()) {
                if (item.getValue() == null) {
                    valores.remove(item.getKey());
                } else {
                    valores.put(item.getKey(), item.getValue());
                }
            }
            return true;
        }

        @Override
        public void apply() {
            commit();
        }

        private Editor colocar(String chave, Object valor) {
            novos.put(chave, valor);
            return this;
        }
    }
}
