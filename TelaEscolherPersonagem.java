/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.sound.sampled.*;
import java.io.IOException;

public class TelaEscolherPersonagem extends JFrame {

    private Clip musicaClip; 
    private String nomeJogador; 
    private Dimension tela;

    public TelaEscolherPersonagem(String nomeJogador) {
        super("Escolha seu Personagem");
        this.nomeJogador = nomeJogador;

        tela = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setLayout(null);

        tocarMusica();
        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Fundo
        URL bgUrl = getClass().getResource("/imagem/escolha_personagem.jpg");
        JLabel fundo = new JLabel();
        if (bgUrl != null) {
            ImageIcon icon = new ImageIcon(bgUrl);
            Image scaled = icon.getImage().getScaledInstance(tela.width, tela.height, Image.SCALE_SMOOTH);
            fundo.setIcon(new ImageIcon(scaled));
        } else {
            fundo.setOpaque(true);
            fundo.setBackground(new Color(30, 20, 10));
            fundo.setText("Fundo /imagem/escolha_personagem.jpg não encontrado");
            fundo.setForeground(Color.WHITE);
            fundo.setHorizontalAlignment(SwingConstants.CENTER);
        }
        fundo.setBounds(0, 0, tela.width, tela.height);
        fundo.setLayout(null);
        add(fundo);

        // Botões de classes
        int btnWidth = (int)(tela.width * 0.15);
        int btnHeight = (int)(tela.height * 0.07);
        int spacingX = (int)(tela.width * 0.05);
        int startY = (int)(tela.height * 0.7);

        JButton bBarbaro = criarBotaoRPG("Bárbaro", spacingX, startY, btnWidth, btnHeight);
        JButton bMago = criarBotaoRPG("Mago", 2*spacingX + btnWidth, startY, btnWidth, btnHeight);
        JButton bArqueiro = criarBotaoRPG("Arqueiro", 3*spacingX + 2*btnWidth, startY, btnWidth, btnHeight);
        JButton bLadino = criarBotaoRPG("Ladino", 4*spacingX + 3*btnWidth, startY, btnWidth, btnHeight);

        fundo.add(bBarbaro);
        fundo.add(bMago);
        fundo.add(bArqueiro);
        fundo.add(bLadino);

        // Abrir telas de classe
        bBarbaro.addActionListener(e -> abrirTelaClasse(new TelaBarbaro(nomeJogador)));
        bMago.addActionListener(e -> abrirTelaClasse(new TelaMago(nomeJogador)));
        bArqueiro.addActionListener(e -> abrirTelaClasse(new TelaArqueiro(nomeJogador)));
        bLadino.addActionListener(e -> abrirTelaClasse(new TelaLadino(nomeJogador)));

        // Botão Voltar
        JButton btnVoltar = criarBotaoRPG("Voltar", (int)(tela.width*0.02), (int)(tela.height*0.85), (int)(tela.width*0.1), (int)(tela.height*0.05));
        btnVoltar.addActionListener(e -> {
            cleanupAudio();
            dispose();
            SwingUtilities.invokeLater(() -> new TelaHistoria(nomeJogador).setVisible(true));
        });
        fundo.add(btnVoltar);
    }

    private JButton criarBotaoRPG(String texto, int x, int y, int largura, int altura) {
        JButton botao = new JButton(texto);
        botao.setBounds(x, y, largura, altura);
        botao.setFont(new Font("Serif", Font.BOLD, 22));
        botao.setForeground(Color.ORANGE);
        botao.setBackground(new Color(50, 25, 0));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3, true));

        // Hover e pressed
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(80, 40, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(50, 25, 0));
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(120, 60, 0));
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(80, 40, 0));
            }
        });

        return botao;
    }

    private void abrirTelaClasse(JFrame telaClasse) {
        if (musicaClip != null && musicaClip.isRunning()) {
            musicaClip.stop();
            musicaClip.close();
        }
        SwingUtilities.invokeLater(() -> telaClasse.setVisible(true));
        dispose();
    }

    private void tocarMusica() {
        try {
            URL url = getClass().getResource("/musica/tela_escolher_personagem_theme.wav");
            if (url == null) return;

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            musicaClip = AudioSystem.getClip();
            musicaClip.open(audio);
            musicaClip.start();
            musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    private void cleanupAudio() {
        try {
            if (musicaClip != null) {
                musicaClip.stop();
                musicaClip.close();
                musicaClip = null;
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void dispose() {
        cleanupAudio();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEscolherPersonagem("JogadorTeste").setVisible(true));
    }
}

