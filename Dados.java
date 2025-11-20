/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import java.util.Random;

/**
 * Utilitário de rolagem de dados.
 * Métodos de instância para compatibilidade e métodos estáticos convenientes.
 */
public class Dados {
    private final Random rnd = new Random();
    private static final Random RND = new Random();

    // instância (compat)
    public int rolar(int quantidade, int lados) {
        int total = 0;
        for (int i = 0; i < quantidade; i++) total += rnd.nextInt(lados) + 1;
        return total;
    }

    // utilitário rápido de instância
    public int d6() { return rnd.nextInt(6) + 1; }

    // --- métodos estáticos para conveniência/compatibilidade ---
    public static int rolarStatic(int quantidade, int lados) {
        int total = 0;
        for (int i = 0; i < quantidade; i++) total += RND.nextInt(lados) + 1;
        return total;
    }

    // alias com nome diferente (evita duplicar assinatura)
    public static int rolarDados(int quantidade, int lados) {
        return rolarStatic(quantidade, lados);
    }

    public static int d6Static() {
        return RND.nextInt(6) + 1;
    }
}




