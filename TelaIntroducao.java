/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// TelaIntroducao.java
package com.mycompany.meujogorpg;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.net.URL;
import java.io.BufferedInputStream;

public class TelaIntroducao extends JFrame {

    private Clip clipMusica;
    private JButton btnPlayPause;
    private JTextField txtNomeJogador;

    public TelaIntroducao() {
        super("Tela de Introdução - MeuJogoRPG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // tela cheia
        setUndecorated(false);
        initUI();
        prepararMusica("/musica/medieval-opener-270568.wav");
        setVisible(true);
    }

    private void initUI() {
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        JPanel painel = new JPanel(null);
        setContentPane(painel);

        // Fundo
        URL urlImg = getClass().getResource("/imagem/castelo.jpg");
        JLabel fundo;
        if (urlImg != null) {
            ImageIcon ic = new ImageIcon(urlImg);
            Image scaled = ic.getImage().getScaledInstance(tela.width, tela.height, Image.SCALE_SMOOTH);
            fundo = new JLabel(new ImageIcon(scaled));
        } else {
            fundo = new JLabel("Fundo não encontrado");
            fundo.setOpaque(true);
            fundo.setBackground(Color.BLACK);
        }
        fundo.setBounds(0, 0, tela.width, tela.height);
        fundo.setLayout(null);
        painel.add(fundo);

        // Nome do jogador
        JLabel lblNome = new JLabel("Digite seu nome:");
        lblNome.setForeground(Color.WHITE);
        lblNome.setFont(new Font("Serif", Font.BOLD, 24));
        lblNome.setBounds((int)(tela.width*0.05), (int)(tela.height*0.05), 250, 30);
        fundo.add(lblNome);

        txtNomeJogador = new JTextField();
        txtNomeJogador.setBounds((int)(tela.width*0.25), (int)(tela.height*0.05), 250, 30);
        fundo.add(txtNomeJogador);

        // Botões estilizados
        JButton btnHistoria = criarBotao("História", (int)(tela.width*0.05), (int)(tela.height*0.12), 180, 50);
        btnHistoria.addActionListener(e -> abrirHistoria());
        fundo.add(btnHistoria);

        btnPlayPause = criarBotao("Pausar Música", (int)(tela.width*0.05), (int)(tela.height*0.2), 200, 50);
        btnPlayPause.addActionListener(e -> toggleMusica());
        fundo.add(btnPlayPause);
    }

    private JButton criarBotao(String texto, int x, int y, int largura, int altura) {
        JButton botao = new JButton(texto);
        botao.setBounds(x, y, largura, altura);
        botao.setFont(new Font("Serif", Font.BOLD, 20));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(45, 45, 45));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));

        // Hover e pressed
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(70, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(45, 45, 45));
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(100, 100, 100));
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(70, 70, 70));
            }
        });

        return botao;
    }

    private void abrirHistoria() {
        String nomeJogador = txtNomeJogador.getText().trim();
        if (nomeJogador.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite seu nome para continuar!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (clipMusica != null && clipMusica.isRunning()) {
            clipMusica.stop();
            clipMusica.close();
        }

        dispose();
        SwingUtilities.invokeLater(() -> {
            TelaHistoria telaHistoria = new TelaHistoria(nomeJogador);
            telaHistoria.setVisible(true);
        });
    }

    private void prepararMusica(String resourcePath) {
        new Thread(() -> {
            try {
                URL url = getClass().getResource(resourcePath);
                if (url == null) return;

                AudioInputStream audioBase;
                try {
                    audioBase = AudioSystem.getAudioInputStream(url);
                } catch (Exception e) {
                    audioBase = AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream()));
                }

                AudioFormat formatoBase = audioBase.getFormat();
                AudioFormat formatoDecodificado = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        formatoBase.getSampleRate(),
                        16,
                        formatoBase.getChannels(),
                        formatoBase.getChannels() * 2,
                        formatoBase.getSampleRate(),
                        false
                );

                AudioInputStream audioFinal = AudioSystem.getAudioInputStream(formatoDecodificado, audioBase);

                clipMusica = AudioSystem.getClip();
                clipMusica.open(audioFinal);
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
                clipMusica.start();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void toggleMusica() {
        if (clipMusica == null) return;
        if (clipMusica.isRunning()) {
            clipMusica.stop();
            btnPlayPause.setText("Tocar Música");
        } else {
            clipMusica.start();
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
            btnPlayPause.setText("Pausar Música");
        }
    }

    @Override
    public void dispose() {
        try {
            if (clipMusica != null) {
                clipMusica.stop();
                clipMusica.close();
            }
        } catch (Exception ignored) {}
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaIntroducao::new);
    }
}

