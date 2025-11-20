/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import java.util.Random;

public abstract class Personagem {
    protected String nome;
    protected int vida;
    protected int mana;
    protected int forca;
    protected int agilidade;
    protected final Random rnd = new Random();

    public Personagem(String nome) {
        this.nome = nome;
    }

    // RN01: abstrata - não instanciar Personagem diretamente
    public abstract int atacar(Personagem inimigo);

    public abstract int usarHabilidade(Personagem inimigo);

    public abstract void usarItem();

    public boolean fugir() {
        return rnd.nextInt(100) < 40;
    }

    public int rolarDados(int quantidade, int lados) {
        int total = 0;
        for (int i = 0; i < quantidade; i++) total += rnd.nextInt(lados) + 1;
        return total;
    }

    // RN05: calcula chance de acerto conforme regra
    public int calcularChanceDeAcerto(Personagem inimigo) {
        int base = 50 + (this.agilidade - inimigo.agilidade) * 5;
        if (base < 10) base = 10;
        if (base > 95) base = 95;
        return base;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    // getters / setters
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public int getMana() { return mana; }
    public int getForca() { return forca; }
    public int getAgilidade() { return agilidade; }

    public void receberDano(int d) {
        vida = Math.max(0, vida - d);
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setForca(int forca) {
        this.forca = forca;
    }

    public void setAgilidade(int agilidade) {
        this.agilidade = agilidade;
    }

    public void curar(int q) { vida += q; }

    @Override
    public String toString() {
        return String.format("%s (HP:%d MP:%d FOR:%d AGI:%d)", nome, vida, mana, forca, agilidade);
    }
}


