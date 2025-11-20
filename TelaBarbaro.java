/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TelaBarbaro extends JFrame {

    private Clip musicaClip;
    private String nomeJogador;
    private Dimension tela;

    public TelaBarbaro(String nomeJogador) {
        super("Classe: Bárbaro");
        this.nomeJogador = nomeJogador;

        // Tela cheia
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
        URL bg = getClass().getResource("/imagem/barbaro_fundo.jpg");
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
        fundo.setBounds(0, 0, tela.width, tela.height);
        fundo.setLayout(null);
        add(fundo);

        // Descrição com fundo semitransparente e texto bruto
        JTextArea desc = new JTextArea(
                "BÁRBARO\n\n" +
                "- Vida: Muito Alta\n" +
                "- Força: Descomunal\n" +
                "- Mana: Baixa\n\n" +
                "Habilidade Especial: FÚRIA AVASSALADORA\n\n" +
                "Prepare-se para esmagar inimigos com golpes brutais!"
        );
        desc.setEditable(false);
        desc.setOpaque(true);
        desc.setBackground(new Color(0, 0, 0, 180));
        desc.setForeground(new Color(255, 180, 0)); // tom laranja-bruto

        // 🔻 Fonte reduzida (opção A)
        desc.setFont(new Font("Trajan Pro", Font.BOLD, (int)(tela.height*0.018)));

        desc.setBounds((int)(tela.width*0.03), (int)(tela.height*0.05),
                       (int)(tela.width*0.45), (int)(tela.height*0.25));
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        fundo.add(desc);

        // Botão Confirmar com estilo bárbaro
        JButton btnConfirm = new JButton("CONFIRMAR BÁRBARO");
        estilizarBotao(btnConfirm, new Color(139, 69, 19), new Color(205, 133, 63));
        btnConfirm.setBounds((int)(tela.width*0.35), (int)(tela.height*0.75),
                             (int)(tela.width*0.2), (int)(tela.height*0.08));
        btnConfirm.addActionListener(e -> {
            if (musicaClip != null && musicaClip.isRunning()) {
                musicaClip.stop();
                musicaClip.close();
            }

            Barbaro jogador = new Barbaro(nomeJogador, new Dados());
            GameSession.setJogador(jogador);

            JOptionPane.showMessageDialog(this, "Você escolheu Bárbaro! Prepare-se para a batalha!");
            SwingUtilities.invokeLater(() -> new TelaHistoriaBarbaro(nomeJogador).setVisible(true));
            dispose();
        });
        fundo.add(btnConfirm);

        // Botão Voltar
        JButton btnVoltar = new JButton("VOLTAR");
        estilizarBotao(btnVoltar, new Color(105, 105, 105), new Color(169, 169, 169));
        btnVoltar.setBounds((int)(tela.width*0.05), (int)(tela.height*0.50),
                            (int)(tela.width*0.15), (int)(tela.height*0.08));
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

    private void estilizarBotao(JButton btn, Color fundo, Color borda) {
        btn.setFont(new Font("Trajan Pro", Font.BOLD, 24));
        btn.setBackground(fundo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(borda, 4));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(borda);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(fundo);
            }
        });
    }

    private void tocarMusica() {
        try {
            URL url = getClass().getResource("/musica/barbaro_theme.wav");
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


