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

public class TelaHistoriaMago extends JFrame {

    private String nomeJogador;
    private Dimension tela;

    public TelaHistoriaMago(String nomeJogador) {
        super("História do Mago");
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
        URL bgUrl = getClass().getResource("/imagem/FundoMago.jpg");
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

        // Texto da história
        JTextArea historia = new JTextArea(
                "O Mago passou anos estudando nas torres ancestrais, onde os livros respiram magia e os ventos sussurram segredos proibidos.\n" +
"Ele dominou feitiços que podem mover montanhas, acender infernos e dobrar o tempo.\n" +
"Mas cada poder tem seu preço, e o mundo sente a sua energia pulsando no ar.\n\n" +
"Hoje, " + nomeJogador + ", o destino o convoca: as sombras de Arvandor se agitam, monstros antigos despertam e segredos esquecidos imploram por revelação.\n" +
"Empunhe sua varinha, concentre sua mente e prepare-se — cada feitiço lançado pode decidir a vitória ou a perdição da humanidade!"

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

        // Botão Continuar
        JButton btnContinuar = new JButton("Continuar para Batalha");
        btnContinuar.setBounds((int)(tela.width*0.35), (int)(tela.height*0.8),
                               (int)(tela.width*0.25), (int)(tela.height*0.07));
        estilizarBotaoMagia(btnContinuar, new Color(80, 0, 200)); // roxo mágico
        btnContinuar.addActionListener(e -> iniciarBatalha());
        fundo.add(btnContinuar);

        // Botão Voltar
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds((int)(tela.width*0.05), (int)(tela.height*0.8),
                            (int)(tela.width*0.15), (int)(tela.height*0.07));
        estilizarBotaoMagia(btnVoltar, new Color(120, 120, 120)); // cinza mágico
        btnVoltar.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new TelaEscolherPersonagem(nomeJogador).setVisible(true));
            dispose();
        });
        fundo.add(btnVoltar);
    }

    // Método para estilizar os botões com efeito mágico
    private void estilizarBotaoMagia(JButton btn, Color corBase) {
        btn.setFont(new Font("Trajan Pro", Font.BOLD, 22));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBase.darker(), 3, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(corBase.brighter());
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                btn.setForeground(corBase.darker());
                btn.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
        });
    }

    private void iniciarBatalha() {
        Personagem jogador = new Mago(nomeJogador);
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



