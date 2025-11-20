/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.sound.sampled.*;
import java.awt.event.MouseEvent;

public class TelaMago extends JFrame {

    private Clip musicaClip;
    private String nomeJogador;
    private Dimension tela;

    public TelaMago(String nomeJogador) {
        super("Classe: Mago");
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
        // Fundo
        URL bg = getClass().getResource("/imagem/mago_fundo.jpg");
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

        // Descrição do personagem (mantendo texto original)
        JTextArea desc = new JTextArea(
                "MAGO\n\n- Vida Média\n- Força Baixa\n- Mana Alta\n\nHabilidade Especial: BOLA DE FOGO"
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

        // Botão Confirmar Mago
        JButton btnConfirm = new JButton("Confirmar Mago");
        btnConfirm.setBounds((int)(tela.width*0.35),(int)(tela.height*0.75),(int)(tela.width*0.2),(int)(tela.height*0.08));
        btnConfirm.addActionListener(e -> {
            if (musicaClip != null && musicaClip.isRunning()) {
                musicaClip.stop();
                musicaClip.close();
            }

            Mago jogador = new Mago(nomeJogador, new Dados());
            GameSession.setJogador(jogador);

            JOptionPane.showMessageDialog(this, "Você escolheu Mago!");
            SwingUtilities.invokeLater(() -> new TelaHistoriaMago(nomeJogador).setVisible(true));
            dispose();
        });
        estilizarBotaoMagia(btnConfirm);
        fundo.add(btnConfirm);

        // Botão Voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds((int)(tela.width*0.05),(int)(tela.height*0.75),(int)(tela.width*0.15),(int)(tela.height*0.08));
        btnVoltar.addActionListener(e -> {
            if (musicaClip != null && musicaClip.isRunning()) {
                musicaClip.stop();
                musicaClip.close();
            }
            SwingUtilities.invokeLater(() -> new TelaEscolherPersonagem(nomeJogador).setVisible(true));
            dispose();
        });
        estilizarBotaoMagia(btnVoltar);
        fundo.add(btnVoltar);
    }

    // Método para estilizar os botões com visual mágico
    private void estilizarBotaoMagia(JButton btn) {
        btn.setFont(new Font("Trajan Pro", Font.BOLD, 24));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        // Bordas com efeito mágico roxo
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 50, 255), 3, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(new Color(200, 150, 255)); // brilho mágico
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                btn.setForeground(new Color(150, 50, 255)); // escurece ao clicar
                btn.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
        });
    }

    private void tocarMusica() {
        try {
            URL url = getClass().getResource("/musica/mago_theme.wav");
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
