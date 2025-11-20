/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author damar
 */
package com.mycompany.meujogorpg;

/**
 * Factory segura (usa reflection) — evita dependência direta em tempo de compilação.
 * Retorna null se não conseguir criar a classe; ideal como fallback rápido.
 */
public final class PersonagemFactory {

    private PersonagemFactory() {}

    public static Personagem criar(String tipo, String nome) {
        if (tipo == null) tipo = "Barbaro";
        String t = tipo.trim();
        // tenta nomes comuns
        String[][] candidates = {
            { "Barbaro", "com.mycompany.meujogorpg.Barbaro" },
            { "Mago", "com.mycompany.meujogorpg.Mago" },
            { "Arqueiro", "com.mycompany.meujogorpg.Arqueiro" },
            { "Ladino", "com.mycompany.meujogorpg.Ladino" },
            { t, "com.mycompany.meujogorpg." + t }
        };

        for (String[] c : candidates) {
            try {
                Class<?> cls = Class.forName(c[1]);
                // tenta construtor (String, Dados) depois (String) e depois (String, Object)
                try {
                    return (Personagem) cls.getConstructor(String.class).newInstance(nome);
                } catch (NoSuchMethodException ex) {
                    try {
                        // tenta construtor com Dados
                        return (Personagem) cls.getConstructor(String.class, com.mycompany.meujogorpg.Dados.class)
                                               .newInstance(nome, new com.mycompany.meujogorpg.Dados());
                    } catch (NoSuchMethodException ex2) {
                        // tenta construtor sem args (raro)
                        try {
                            Personagem p = (Personagem) cls.getConstructor().newInstance();
                            // tenta setar nome via reflexão (se houver setter)
                            try {
                                cls.getMethod("setNome", String.class).invoke(p, nome);
                            } catch (Exception ign) {}
                            return p;
                        } catch (Exception ign3) {}
                    } catch (Exception e2) { /* ignora e continua */ }
                } catch (Exception e) {
                    // falhou criar com String, tenta próxima candidate
                }
            } catch (ClassNotFoundException cnf) {
                // tenta próximo candidate
            } catch (Exception ex) {
                // ignora
            }
        }
        // fallback: tenta criar um Bárbaro diretamente (se a classe estiver disponível)
        try {
            Class<?> cb = Class.forName("com.mycompany.meujogorpg.Barbaro");
            return (Personagem) cb.getConstructor(String.class).newInstance(nome);
        } catch (Exception e) {
            // não conseguiu criar nada — retorna null (tratamento na UI necessário)
            return null;
        }
    }
}

