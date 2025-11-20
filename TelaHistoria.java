/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// TelaHistoria.java
package com.mycompany.meujogorpg;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class TelaHistoria extends JFrame {

    private Clip musicClip;
    private boolean isMusicPlaying = false;

    private JLabel backgroundLabel;
    private JTextPane storyTextPane;
    private JButton btnPlayMusic;

    private ImageIcon[] images;
    private int currentImageIndex = 0;
    private Timer slideshowTimer;

    private static final String[] IMAGE_PATHS = {
        "/imagem/aldeia.jpg",
        "/imagem/imagem1.jpg",
        "/imagem/imagem2.jpg",
        "/imagem/imagem3.jpg",
        "/imagem/imagem4.jpg",
        "/imagem/imagem5.jpg",
        "/imagem/imagem6.jpg",
        "/imagem/imagem7.jpg",
        "/imagem/imagem8.jpg"
    };

    private static final String MUSIC_PATH = "/musica/sacred-garden-10377.wav";

    private String nomeJogador;
    private Dimension tela;

    public TelaHistoria(String nomeJogador) {
        super("História - MeuJogoRPG");
        this.nomeJogador = nomeJogador;

        tela = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);

        loadImages();
        initComponents();
        prepareAndPlayMusic(MUSIC_PATH);
        startSlideshow();
        setVisible(true);
    }

    private void loadImages() {
        images = new ImageIcon[IMAGE_PATHS.length];
        for (int i = 0; i < IMAGE_PATHS.length; i++) {
            URL url = getClass().getResource(IMAGE_PATHS[i]);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                images[i] = new ImageIcon(icon.getImage().getScaledInstance(tela.width, tela.height, Image.SCALE_SMOOTH));
            }
        }
    }

    private void initComponents() {
        JPanel root = new JPanel(null);
        root.setPreferredSize(tela);
        setContentPane(root);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, tela.width, tela.height);
        root.add(layeredPane);

        // Background
        backgroundLabel = new JLabel();
        if (images.length > 0 && images[0] != null) {
            backgroundLabel.setIcon(images[0]);
        } else {
            backgroundLabel.setOpaque(true);
            backgroundLabel.setBackground(new Color(20, 20, 20));
        }
        backgroundLabel.setBounds(0, 0, tela.width, tela.height);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        // Overlay semi-transparente
        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setBackground(new Color(0, 0, 0, 80));
        overlay.setBounds((int)(tela.width*0.05), (int)(tela.height*0.05),
                          (int)(tela.width*0.9), (int)(tela.height*0.75));
        overlay.setBorder(new EmptyBorder(10, 10, 10, 10));
        layeredPane.add(overlay, Integer.valueOf(1));

        // Texto da história
        storyTextPane = new JTextPane();
        storyTextPane.setContentType("text/html");
        storyTextPane.setEditable(false);
        storyTextPane.setOpaque(false);
        storyTextPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        storyTextPane.setFont(new Font("Serif", Font.PLAIN, (int)(tela.height*0.025)));
        storyTextPane.setText(buildStoryHtml());
        overlay.add(storyTextPane, BorderLayout.CENTER);

        // Painel inferior para botões
        JPanel controls = new JPanel(null);
        controls.setOpaque(false);
        controls.setBounds((int)(tela.width*0.05), (int)(tela.height*0.82),
                           (int)(tela.width*0.9), (int)(tela.height*0.1));
        layeredPane.add(controls, Integer.valueOf(2));

        int btnWidth = (int)(controls.getWidth() * 0.2);
        int btnHeight = (int)(controls.getHeight() * 0.6);
        int spacing = (int)(controls.getWidth() * 0.05);
        int baseY = (int)(controls.getHeight() * 0.2);

        JButton btnVoltar = criarBotaoRPG("Voltar", spacing, baseY, btnWidth, btnHeight);
        btnVoltar.addActionListener(e -> {
            cleanupAudio();
            SwingUtilities.invokeLater(() -> new TelaIntroducao().setVisible(true));
            dispose();
        });

        btnPlayMusic = criarBotaoRPG("Pausar Música", 2*spacing + btnWidth, baseY, btnWidth, btnHeight);
        btnPlayMusic.addActionListener(e -> toggleMusic());

        JButton btnIniciar = criarBotaoRPG("Iniciar Jogo", 3*spacing + 2*btnWidth, baseY, btnWidth, btnHeight);
        btnIniciar.addActionListener(e -> {
            cleanupAudio();
            SwingUtilities.invokeLater(() -> new TelaEscolherPersonagem(nomeJogador).setVisible(true));
            dispose();
        });

        controls.add(btnVoltar);
        controls.add(btnPlayMusic);
        controls.add(btnIniciar);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAudio();
                stopSlideshow();
                super.windowClosing(e);
            }
        });
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

    private void startSlideshow() {
        slideshowTimer = new Timer(3000, e -> {
            currentImageIndex = (currentImageIndex + 1) % images.length;
            if (images[currentImageIndex] != null) {
                backgroundLabel.setIcon(images[currentImageIndex]);
            }
        });
        slideshowTimer.start();
    }

    private void stopSlideshow() {
        if (slideshowTimer != null) slideshowTimer.stop();
    }

    private String buildStoryHtml() {
        return "<html><head><style>"
                + "body{ font-family: 'Serif'; color:#f5f0e6; font-size:22px; line-height:1.5 }"
                + "h1{ text-align:center; color:#ffd27f; font-size:32px }"
                + "p{ margin:8px 0; text-indent:12px }"
                + ".lead{ font-size:26px; color:#ffecc9; margin-bottom:10px }"
                + "</style></head><body>"
                + "<h1>O Crepúsculo das Cinco Coroas</h1>"
                + "<div class='lead'>As chamas ainda fumegam, " + nomeJogador + ". O vento carrega cinzas e sussurros.</div>"
                + "<p>O imponente Castelo de Arvandor e a pacata aldeia que o servia jazem em ruínas...</p>"
                + "<p>Você sente que seu destino está ligado a este lugar. A jornada começa aqui.</p>"
                + "<p style='text-align:center; font-weight:bold; margin-top:10px;'>Clique em \"Iniciar Jogo\" para partir.</p>"
                + "</body></html>";
    }

    private void prepareAndPlayMusic(String musicResource) {
        new Thread(() -> {
            try {
                URL url = getClass().getResource(musicResource);
                if (url == null) return;

                AudioInputStream ais = AudioSystem.getAudioInputStream(url);
                AudioFormat base = ais.getFormat();
                AudioFormat decoded = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        base.getSampleRate(), 16, base.getChannels(),
                        base.getChannels() * 2, base.getSampleRate(), false);
                AudioInputStream dais = AudioSystem.getAudioInputStream(decoded, ais);

                musicClip = AudioSystem.getClip();
                musicClip.open(dais);
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                musicClip.start();
                isMusicPlaying = true;

                SwingUtilities.invokeLater(() -> btnPlayMusic.setText("Pausar Música"));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "MusicThread").start();
    }

    private void toggleMusic() {
        if (musicClip == null) return;
        if (isMusicPlaying) {
            musicClip.stop();
            isMusicPlaying = false;
            btnPlayMusic.setText("Tocar Música");
        } else {
            musicClip.start();
            isMusicPlaying = true;
            btnPlayMusic.setText("Pausar Música");
        }
    }

    private void cleanupAudio() {
        try {
            if (musicClip != null) {
                musicClip.stop();
                musicClip.close();
                musicClip = null;
            }
        } catch (Exception ignored) {}
    }
}


