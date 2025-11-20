/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.sound.sampled.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaArqueiro extends JFrame {

    private Clip musicaClip;
    private String nomeJogador;
    private Dimension tela;

    public TelaArqueiro(String nomeJogador) {
        super("Classe: Arqueiro");
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
        URL bg = getClass().getResource("/imagem/arqueiro_fundo.jpg");
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
                "ARQUEIRO\n\n- Vida Média\n- Força Média\n- Mana Média\n\nHabilidade Especial: TIRO PRECISO"
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

        // Botão Confirmar Arqueiro
        JButton btnConfirm = new JButton("Confirmar Arqueiro");
        btnConfirm.setBounds((int)(tela.width*0.35),(int)(tela.height*0.75),(int)(tela.width*0.2),(int)(tela.height*0.08));
        estilizarBotaoRPG(btnConfirm, new Color(34,139,34), new Color(60,179,113)); // verde arqueiro
        btnConfirm.addActionListener(e -> {
            if (musicaClip != null && musicaClip.isRunning()) {
                musicaClip.stop();
                musicaClip.close();
            }

            Arqueiro jogador = new Arqueiro(nomeJogador, new Dados());
            GameSession.setJogador(jogador);

            JOptionPane.showMessageDialog(this, "Você escolheu Arqueiro!");
            SwingUtilities.invokeLater(() -> new TelaHistoriaArqueiro(nomeJogador).setVisible(true));
            dispose();
        });
        fundo.add(btnConfirm);

        // Botão Voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds((int)(tela.width*0.05),(int)(tela.height*0.75),(int)(tela.width*0.15),(int)(tela.height*0.08));
        estilizarBotaoRPG(btnVoltar, new Color(105,105,105), new Color(169,169,169)); // cinza elegante
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

    private void estilizarBotaoRPG(JButton btn, Color fundo, Color borda) {
        btn.setFont(new Font("Trajan Pro", Font.BOLD, 24));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Borda elegante
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borda.darker(), 3, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // Efeito degradê no fundo
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        // Mouse hover e pressed
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(borda.brighter());
                btn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setForeground(borda.darker());
                btn.repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
        });

        // Custom painting para efeito metálico
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gp;
                if (btn.getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, fundo.darker(), 0, btn.getHeight(), fundo.darker().darker());
                } else if (btn.getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, fundo.brighter(), 0, btn.getHeight(), fundo);
                } else {
                    gp = new GradientPaint(0, 0, fundo, 0, btn.getHeight(), fundo.darker());
                }
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, btn.getWidth(), btn.getHeight(), 15, 15);
                g2d.dispose();
                super.paint(g, c);
            }
        });
    }

    private void tocarMusica() {
        try {
            URL url = getClass().getResource("/musica/arqueiro_theme.wav");
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

