/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TelaHistoriaLadino extends JFrame {

    private String nomeJogador;
    private Dimension tela;

    public TelaHistoriaLadino(String nomeJogador) {
        super("História do Ladino");
        this.nomeJogador = nomeJogador;

        // Tela cheia
        tela = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setLayout(null);

        initUI();
    }

    private void initUI() {
        // Fundo
        URL bgUrl = getClass().getResource("/imagem/FundoLadino.jpg");
        JLabel fundo = new JLabel();
        if (bgUrl != null) {
            ImageIcon icon = new ImageIcon(bgUrl);
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

        // Texto da história
        JTextArea historia = new JTextArea(
                "O Ladino surgiu nas sombras da cidade, onde cada beco esconde segredos e perigos mortais.\n" +
"Ágil como o vento e silencioso como a noite, ele espreita, observa e ataca sem aviso.\n" +
"Cada movimento é calculado, cada golpe é letal, e nada escapa de seus olhos atentos.\n" +
"Prepare-se, " + nomeJogador + ", a escuridão é sua aliada, e a vitória pertence àqueles que dominam a arte do furtivo!"

        );
        historia.setBounds((int)(tela.width * 0.05), (int)(tela.height * 0.05),
                (int)(tela.width * 0.6), (int)(tela.height * 0.3));
        historia.setOpaque(true);
        historia.setBackground(new Color(0, 0, 0, 150));
        historia.setForeground(Color.WHITE);
        historia.setFont(new Font("Serif", Font.BOLD, (int)(tela.height * 0.025)));
        historia.setLineWrap(true);
        historia.setWrapStyleWord(true);
        historia.setEditable(false);
        fundo.add(historia);

        // Botão Continuar com estilo
        JButton btnContinuar = new JButton("Continuar para Batalha");
        btnContinuar.setBounds((int)(tela.width * 0.35), (int)(tela.height * 0.8),
                (int)(tela.width * 0.25), (int)(tela.height * 0.07));
        estilizarBotaoLadino(btnContinuar, new Color(40, 40, 40), new Color(200, 180, 50));
        btnContinuar.addActionListener(e -> {
            Personagem jogador = new Ladino(nomeJogador);
            Personagem inimigo = new Personagem("Dragão") {
                { vida = 50; mana = 5; forca = 10; agilidade = 5; }
                @Override public int atacar(Personagem alvo) {
                    int dano = this.forca + (int)(Math.random() * 6 + 1);
                    alvo.receberDano(dano);
                    return dano;
                }
                @Override public int usarHabilidade(Personagem alvo) { return 0; }
                @Override public void usarItem() {}
            };

            JFrame frameBatalha = new JFrame("Batalha RPG");
            frameBatalha.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frameBatalha.setContentPane(new BatalhaRPGPanel(jogador, inimigo));
            frameBatalha.pack();
            frameBatalha.setLocationRelativeTo(null);
            frameBatalha.setVisible(true);

            dispose();
        });
        fundo.add(btnContinuar);

        // Botão Voltar com estilo
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds((int)(tela.width * 0.05), (int)(tela.height * 0.8),
                (int)(tela.width * 0.15), (int)(tela.height * 0.07));
        estilizarBotaoLadino(btnVoltar, new Color(50, 50, 50), new Color(150, 150, 150));
        btnVoltar.addActionListener(e -> {
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

        // Bordas com efeito metálico
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(brilho, 3, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // Transparente para aplicar o efeito customizado
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        // Mouse effects
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(brilho);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                btn.setForeground(brilho.darker());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
    }
}
