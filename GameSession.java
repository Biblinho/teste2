/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

public class GameSession {
    private static Personagem jogador;

    public static void setJogador(Personagem p) { jogador = p; }
    public static Personagem getJogador() { return jogador; }
    public static void clear() { jogador = null; }
}

