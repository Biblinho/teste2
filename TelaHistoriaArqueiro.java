/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.meujogorpg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class TelaHistoriaArqueiro extends JFrame {

    private String nomeJogador;
    private Dimension tela;

    public TelaHistoriaArqueiro(String nomeJogador) {
        super("História do Arqueiro");
        this.nomeJogador = nomeJogador;

        tela = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setLayout(null);

        initUI();
    }

    private void initUI() {
        // Fundo
        URL bgUrl = getClass().getResource("/imagem/FundoArqueiro.jpg");
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
        fundo.setBounds(0,0,tela.width,tela.height);
        fundo.setLayout(null);
        add(fundo);

        // Texto da história com fundo semi-transparente
        JTextArea historia = new JTextArea(
                "O Arqueiro nasceu entre as sombras das florestas ancestrais,\n" +
"onde cada árvore guarda segredos e cada vento sussurra perigos.\n" +
"Anos de treino lapidaram sua mira e seu instinto mortal.\n" +
"Agora, " + nomeJogador + ", a hora chegou: cada flecha lançada pode decidir o destino de reinos,\n" +
"e o coração do inimigo está na sua mira. Prepare-se para se tornar uma lenda!"

        );
        historia.setBounds((int)(tela.width*0.05), (int)(tela.height*0.05),
                           (int)(tela.width*0.6), (int)(tela.height*0.3));
        historia.setOpaque(true);
        historia.setBackground(new Color(0, 0, 0, 150));
        historia.setForeground(Color.WHITE);
        historia.setFont(new Font("Serif", Font.BOLD, (int)(tela.height*0.025)));
        historia.setLineWrap(true);
        historia.setWrapStyleWord(true);
        historia.setEditable(false);
        fundo.add(historia);

        // Botão Continuar para batalha
        JButton btnContinuar = new JButton("Continuar para Batalha");
        btnContinuar.setBounds((int)(tela.width*0.35), (int)(tela.height*0.8),
                               (int)(tela.width*0.25), (int)(tela.height*0.07));
        estilizarBotaoRPG(btnContinuar, new Color(34,139,34), new Color(60,179,113));
        btnContinuar.addActionListener(e -> iniciarBatalha());
        fundo.add(btnContinuar);

        // Botão Voltar para escolher personagem
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds((int)(tela.width*0.05), (int)(tela.height*0.8),
                            (int)(tela.width*0.15), (int)(tela.height*0.07));
        estilizarBotaoRPG(btnVoltar, new Color(105,105,105), new Color(169,169,169));
        btnVoltar.addActionListener(e -> {
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

    private void iniciarBatalha() {
        Personagem jogador = new Arqueiro(nomeJogador);
        Personagem inimigo = new Personagem("Dragão") {
            { vida = 50; mana = 5; forca = 10; agilidade = 5; }
            @Override public int atacar(Personagem alvo) {
                int dano = this.forca + (int)(Math.random()*6 + 1);
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
    }
}


