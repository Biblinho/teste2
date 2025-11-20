/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;
import java.util.Random;
/**
 *
 * @author damar
 */


/**
 * TelaInimigo.java

Versão corrigida: mantém o nome TelaInimigo (conforme seu padrão),
mas estende a classe de domínio Personagem para manter a lógica do jogo.
 */
public class TelaInimigo extends Personagem {

    private final Random rnd = new Random();

    public TelaInimigo(String nome, int vida, int forca, int agi, int mana) {
        super(nome);
        this.vida = vida;
        this.forca = forca;
        this.agilidade = agi;
        this.mana = mana;
    }

    @Override
    public int atacar(Personagem inimigo) {
        int chance = calcularChanceDeAcerto(inimigo);
        int roll = rnd.nextInt(101);
        if (roll <= chance) {
            int dano = forca + rnd.nextInt(6) + 1;
            inimigo.receberDano(dano);
            return dano;
        } else {
            return 0;
        }
    }

    @Override
    public int usarHabilidade(Personagem inimigo) {
        // inimigo simples: usar ataque normal
        return atacar(inimigo);
    }

    @Override
    public void usarItem() {
        // inimigo não usa item por padrão
    }
}
