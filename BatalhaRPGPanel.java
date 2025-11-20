/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.sound.sampled.*;

public class BatalhaRPGPanel extends JPanel {

    private final JLabel fundoLabel = new JLabel();
    private final JLabel textoBatalha = new JLabel("", SwingConstants.LEFT);
    private final JLabel efeitoAtaque = new JLabel();
    private final JLabel personagemJogadorLabel;
    private final JLabel inimigoLabel;
    private final JLabel labelNomeJogador = new JLabel();

    private final JButton btnAtacar = new JButton("Atacar");
    private final JButton btnHabilidade = new JButton("Habilidade");
    private final JButton btnItem = new JButton("Item");
    private final JButton btnFugir = new JButton("Fugir");
    private final JButton btnReiniciar = new JButton("Reiniciar");

    private final JProgressBar barraVidaJogador;
    private final JProgressBar barraManaJogador;
    private final JProgressBar barraVidaInimigo;
    private final JProgressBar barraXP;
    private final JLabel labelLevel = new JLabel();

    private int level = 1;
    private int xp = 0;
    private int maxVidaJogador;
    private int maxManaJogador;

    private Personagem player;
    private Personagem inimigo;

    private final Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
    private final String fundoPath = "/imagem/batalha_castelo.jpg";

    public BatalhaRPGPanel(Personagem player, Personagem inimigo) {
        this.player = player;
        this.inimigo = inimigo;

        this.player.setVida(100);
        this.player.setMana(100);

        setLayout(null);
        setPreferredSize(tela);

        personagemJogadorLabel = new JLabel();
        inimigoLabel = new JLabel();

        maxVidaJogador = player.getVida();
        maxManaJogador = player.getMana();

        barraVidaJogador = criarBarra((int)(tela.width*0.05), (int)(tela.height*0.75),
                (int)(tela.width*0.2), 25, Color.GREEN, player.getVida(), maxVidaJogador);
        barraManaJogador = criarBarra((int)(tela.width*0.05), (int)(tela.height*0.8),
                (int)(tela.width*0.2), 20, Color.BLUE, player.getMana(), maxManaJogador);
        barraVidaInimigo = criarBarra((int)(tela.width*0.7), (int)(tela.height*0.1),
                (int)(tela.width*0.2), 25, Color.RED, inimigo.getVida(), Math.max(inimigo.getVida(), 1));
        barraXP = criarBarra((int)(tela.width*0.05), (int)(tela.height*0.85),
                (int)(tela.width*0.2), 15, Color.ORANGE, xp, 100);

        initUI();
        atualizarBarras();

        // Início da música de fundo
        tocarMusicaFundo("/som/batalha_theme.wav"); // <--- aqui você colocará sua música
    }

    private void initUI() {
        // Fundo
        try {
            URL u = getClass().getResource(fundoPath);
            if (u != null) {
                ImageIcon icon = new ImageIcon(u);
                fundoLabel.setIcon(scaleIcon(icon, tela.width, tela.height));
            } else {
                fundoLabel.setOpaque(true);
                fundoLabel.setBackground(Color.BLACK);
            }
        } catch (Exception e) {
            fundoLabel.setOpaque(true);
            fundoLabel.setBackground(Color.BLACK);
        }
        fundoLabel.setBounds(0, 0, tela.width, tela.height);
        fundoLabel.setLayout(null);
        add(fundoLabel);

        // Texto de batalha
        textoBatalha.setBounds((int)(tela.width*0.05), (int)(tela.height*0.02),
                (int)(tela.width*0.9), (int)(tela.height*0.15));
        textoBatalha.setOpaque(true);
        textoBatalha.setBackground(new Color(0,0,0,150));
        textoBatalha.setForeground(Color.WHITE);
        textoBatalha.setFont(new Font("Serif", Font.BOLD, 24));
        textoBatalha.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
        fundoLabel.add(textoBatalha);

        // Barras
        fundoLabel.add(barraVidaJogador);
        fundoLabel.add(barraManaJogador);
        fundoLabel.add(barraVidaInimigo);
        fundoLabel.add(barraXP);

        // Level
        labelLevel.setBounds((int)(tela.width*0.3), (int)(tela.height*0.75), 120, 25);
        labelLevel.setForeground(Color.WHITE);
        labelLevel.setFont(new Font("Serif", Font.BOLD, 18));
        fundoLabel.add(labelLevel);

        // Personagens
        setupCharacterLabel(personagemJogadorLabel, "/imagem/player_sprite.png",
                (int)(tela.width*0.05), (int)(tela.height*0.5), 150, 150, player.getNome());
        fundoLabel.add(personagemJogadorLabel);

        labelNomeJogador.setText(player.getNome());
        labelNomeJogador.setBounds((int)(tela.width*0.05), (int)(tela.height*0.45), 200, 25);
        labelNomeJogador.setForeground(Color.WHITE);
        labelNomeJogador.setFont(new Font("Serif", Font.BOLD, 18));
        fundoLabel.add(labelNomeJogador);

        setupCharacterLabel(inimigoLabel, "/imagem/dragao.png",
                (int)(tela.width*0.7), (int)(tela.height*0.3), 200, 200, inimigo.getNome());
        fundoLabel.add(inimigoLabel);

        // efeito
        efeitoAtaque.setBounds(0,0,100,100);
        efeitoAtaque.setVisible(false);
        fundoLabel.add(efeitoAtaque);

        // Botões
        int btnWidth = (int)(tela.width*0.15);
        int btnHeight = (int)(tela.height*0.07);
        int spacing = (int)(tela.width*0.02);
        int baseY = (int)(tela.height*0.85);

        btnAtacar.setBounds((int)(tela.width*0.05), baseY, btnWidth, btnHeight);
        btnHabilidade.setBounds((int)(tela.width*0.05)+btnWidth+spacing, baseY, btnWidth, btnHeight);
        btnItem.setBounds((int)(tela.width*0.05)+2*(btnWidth+spacing), baseY, btnWidth, btnHeight);
        btnFugir.setBounds((int)(tela.width*0.05)+3*(btnWidth+spacing), baseY, btnWidth, btnHeight);
        btnReiniciar.setBounds((int)(tela.width*0.05)+4*(btnWidth+spacing), baseY, btnWidth, btnHeight);

        fundoLabel.add(btnAtacar);
        fundoLabel.add(btnHabilidade);
        fundoLabel.add(btnItem);
        fundoLabel.add(btnFugir);
        fundoLabel.add(btnReiniciar);

        // Listeners com espaços para sons
        btnAtacar.addActionListener(e -> {
            // TODO: tocar som de ataque
            runActionWithLock(this::executarAtaqueFisico);
        });
        btnHabilidade.addActionListener(e -> {
            // TODO: tocar som de habilidade
            runActionWithLock(this::executarHabilidade);
        });
        btnItem.addActionListener(e -> {
            // TODO: tocar som de item
            runActionWithLock(this::executarUsarItem);
        });
        btnFugir.addActionListener(e -> {
            // TODO: tocar som de fuga
            runActionWithLock(this::executarFugir);
        });
        btnReiniciar.addActionListener(e -> {
            // TODO: tocar som de reinício
            reiniciarPartida();
        });
    }

    private void tocarMusicaFundo(String caminho){
        // TODO: colocar música de fundo looping aqui
        // Exemplo:
        // new Thread(() -> { ... }).start();
    }

    private void tocarSom(String caminho){
        // TODO: tocar efeito de som único aqui
        // Exemplo:
        // new Thread(() -> { ... }).start();
    }

    private ImageIcon scaleIcon(ImageIcon icon, int maxW, int maxH){
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(maxW, maxH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private JProgressBar criarBarra(int x,int y,int w,int h,Color cor,int valor,int max){
        JProgressBar barra = new JProgressBar(0,max);
        barra.setValue(valor);
        barra.setForeground(cor);
        barra.setBounds(x,y,w,h);
        barra.setStringPainted(true);
        return barra;
    }

    private void setupCharacterLabel(JLabel lbl, String resource, int x,int y,int w,int h,String fallback){
        try{
            URL u = getClass().getResource(resource);
            if(u != null){
                ImageIcon icon = new ImageIcon(u);
                lbl.setIcon(scaleIcon(icon,w,h));
            } else {
                lbl.setText(fallback);
                lbl.setForeground(Color.WHITE);
            }
            lbl.setBounds(x,y,w,h);
        } catch(Exception e){
            lbl.setText(fallback);
            lbl.setForeground(Color.WHITE);
            lbl.setBounds(x,y,w,h);
        }
    }

    private void executarAtaqueFisico(){
        // TODO: tocar som de ataque
        int dano = player.atacar(inimigo);
        mostrarTexto(player.getNome() + " causou " + dano + " de dano!");
        atualizarBarras();
        SwingUtilities.invokeLater(this::inimigoAtaca);
    }

    private void executarHabilidade(){
        // TODO: tocar som de habilidade
        int dano = player.usarHabilidade(inimigo);
        mostrarTexto(player.getNome() + " usou habilidade causando " + dano + " de dano!");
        atualizarBarras();
        SwingUtilities.invokeLater(this::inimigoAtaca);
    }

    private void executarUsarItem(){
        // TODO: tocar som de item
        player.usarItem();
        mostrarTexto(player.getNome() + " usou um item!");
        atualizarBarras();
        SwingUtilities.invokeLater(this::inimigoAtaca);
    }

    private void executarFugir(){
        // TODO: tocar som de fuga
        boolean fugiu = player.fugir();
        mostrarTexto(fugiu ? player.getNome()+" fugiu!" : player.getNome()+" tentou fugir mas falhou!");
        if(!fugiu) SwingUtilities.invokeLater(this::inimigoAtaca);
    }

    private void reiniciarPartida(){
        // TODO: tocar som de reinício
        player.setVida(100);
        player.setMana(100);
        atualizarBarras();
        mostrarTexto("Partida reiniciada!");
    }

    private void atualizarBarras(){
        barraVidaJogador.setValue(player.getVida());
        barraManaJogador.setValue(player.getMana());
        barraVidaInimigo.setValue(inimigo.getVida());
        barraXP.setValue(xp);
        labelLevel.setText("Level: " + level);
    }

    private void mostrarTexto(String msg){
        textoBatalha.setText("<html><left>"+msg+"</left></html>");
    }

    private void inimigoAtaca(){
        if(!inimigo.estaVivo()) return;
        // TODO: tocar som de ataque inimigo
        int dano = inimigo.atacar(player);
        mostrarTexto("O inimigo causa " + dano + " de dano!");
        atualizarBarras();
    }

    private void runActionWithLock(Runnable action){
        btnAtacar.setEnabled(false);
        btnHabilidade.setEnabled(false);
        btnItem.setEnabled(false);
        btnFugir.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try{ action.run(); } finally {
                if(player.estaVivo()) {
                    btnAtacar.setEnabled(true);
                    btnHabilidade.setEnabled(true);
                    btnItem.setEnabled(true);
                    btnFugir.setEnabled(true);
                }
            }
        });
    }
}


