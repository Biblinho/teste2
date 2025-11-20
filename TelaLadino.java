/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
public class TelaLadino extends JFrame {

    private Clip musicaClip;
    private String nomeJogador;
    private Dimension tela;

    public TelaLadino(String nomeJogador) {
        super("Classe: Ladino");
        this.nomeJogador = nomeJogador;

        tela = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setLayout(null);

        initUI();
        tocarMusica();
    }

    private void initUI() {
        URL bg = getClass().getResource("/imagem/ladino_fundo.jpg");
        JLabel fundo = new JLabel();
        if (bg != null) {
            ImageIcon icon = new ImageIcon(bg);
            Image scaled = icon.getImage().getScaledInstance(tela.width, tela.height, Image.SCALE_SMOOTH);
            fundo.setIcon(new ImageIcon(scaled));
        } else {
            fundo.setOpaque(true);
            fundo.setBackground(Color.DARK_GRAY);
            fundo.setText("Fundo não encontrado");
            fundo.setForeground(Color.WHITE);
            fundo.setHorizontalAlignment(SwingConstants.CENTER);
        }
        fundo.setBounds(0,0,tela.width,tela.height);
        fundo.setLayout(null);
        add(fundo);

        JTextArea desc = new JTextArea(
                "LADINO\n\n- Vida Média\n- Força Média\n- Mana Média\n\nHabilidade Especial: ATAQUE FURTIVO"
        );
        desc.setEditable(false);
        desc.setOpaque(true);
        desc.setBackground(new Color(0, 0, 0, 150));
        desc.setForeground(Color.WHITE);
        desc.setFont(new Font("Serif", Font.BOLD, (int)(tela.height*0.022)));
        desc.setBounds((int)(tela.width*0.03),(int)(tela.height*0.05),(int)(tela.width*0.45),(int)(tela.height*0.25));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        fundo.add(desc);

        // Botão Confirmar com efeito Ladino
        JButton btnConfirm = new JButton("Confirmar Ladino");
        btnConfirm.setBounds((int)(tela.width*0.35),(int)(tela.height*0.75),(int)(tela.width*0.2),(int)(tela.height*0.08));
        estilizarBotaoLadino(btnConfirm, new Color(40, 40, 40), new Color(200, 180, 50));
        btnConfirm.addActionListener(e -> {
            if (musicaClip != null && musicaClip.isRunning()) {
                musicaClip.stop();
                musicaClip.close();
            }

            Ladino jogador = new Ladino(nomeJogador, new Dados());
            GameSession.setJogador(jogador);

            JOptionPane.showMessageDialog(this, "Você escolheu Ladino!");
            SwingUtilities.invokeLater(() -> new TelaHistoriaLadino(nomeJogador).setVisible(true));
            dispose();
        });
        fundo.add(btnConfirm);

        // Botão Voltar com efeito Ladino
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds((int)(tela.width*0.05),(int)(tela.height*0.75),(int)(tela.width*0.15),(int)(tela.height*0.08));
        estilizarBotaoLadino(btnVoltar, new Color(50, 50, 50), new Color(150, 150, 150));
        btnVoltar.addActionListener(e -> {
            if (musicaClip != null && musicaClip.isRunning()) {
                musicaClip.stop();
                musicaClip.close();
            }
            SwingUtilities.invokeLater(() -> new TelaEscolherPersonagem(nomeJogador).setVisible(true));
            dispose();
        });
        fundo.add(btnVoltar);
    }

    private void estilizarBotaoLadino(JButton btn, Color fundo, Color brilho) {
        btn.setFont(new Font("Trajan Pro", Font.BOLD, 24));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(brilho, 3, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(brilho);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
                btn.setForeground(brilho.darker());
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
    }

    private void tocarMusica() {
        try {
            URL url = getClass().getResource("/musica/ladino_theme.wav");
            if (url == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            musicaClip = AudioSystem.getClip();
            musicaClip.open(ais);
            musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicaClip.start();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void dispose() {
        try {
            if (musicaClip != null) {
                musicaClip.stop();
                musicaClip.close();
            }
        } catch (Exception ignored) {}
        super.dispose();
    }
}

