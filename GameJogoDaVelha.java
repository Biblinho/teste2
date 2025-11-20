/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.gamejogodavelha;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/**
 * GameJogoDaVelha.java
 *
 * Versão GAMER (estilo D) com:
 * - IA Minimax (Impossível) + dificuldades Fácil/Médio/Impossível
 * - Undo (desfazer)
 * - Persistência de placar em ~/.gamejogodavelha_score.txt
 * - Tema "GAMER": fundo com gradiente/textura-like, hover nos botões, cores fortes
 * - Sons sintetizados por código (click e vitória)
 *
 * O arquivo está ricamente comentado para facilitar leitura no GitHub.
 *
 * Cole em: com.mycompany.gamejogodavelha.GameJogoDaVelha
 */
public class GameJogoDaVelha extends JFrame {

    // ---------------------------
    // UI components (visíveis)
    // ---------------------------
    private final JButton[][] cells = new JButton[3][3];
    private final JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);

    // ---------------------------
    // Game state (lógica)
    // ---------------------------
    private char[][] board = new char[3][3]; // '\0' = vazio, 'X' ou 'O'
    private boolean xTurn = true;             // X inicia
    private boolean vsAI = true;              // modo padrão: vs IA
    private char aiChar = 'O';                // IA joga com 'O' por padrão
    private Difficulty difficulty = Difficulty.IMPOSSIBLE;

    // ---------------------------
    // Score e persistência
    // ---------------------------
    private int scoreX = 0, scoreO = 0, draws = 0;
    private static final Path SCORE_FILE = Paths.get(System.getProperty("user.home"), ".gamejogodavelha_score.txt");

    // ---------------------------
    // Undo (histórico de movimentos)
    // ---------------------------
    private final Deque<Point> moveHistory = new ArrayDeque<>();

    // ---------------------------
    // UI / Visuals
    // ---------------------------
    private boolean darkMode = false;         // ainda usamos tema escuro/claro dentro do estilo gamer
    private final Random rnd = new Random();  // para movimentos aleatórios (FÁCIL/MÉDIO)

    // ---------------------------
    // Construtor: monta interface e inicializa
    // ---------------------------
    public GameJogoDaVelha() {
        super("GameJogoDaVelha — Gamer Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(680, 740);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Top: título + controles (modo, dificuldade, lado da IA)
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false); // deixamos o painel transparente para o background estilizado

        JLabel title = new JLabel("⚔ GameJogoDaVelha ⚔", SwingConstants.LEFT);
        title.setFont(new Font("Orbitron", Font.BOLD, 28)); // se Orbitron não existir, a JVM vai usar fallback
        title.setForeground(new Color(220, 220, 255));
        top.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        // Modo: 1 jogador vs AI ou 2 jogadores local
        JComboBox<String> modeSelect = new JComboBox<>(new String[]{"1 Jogador (vs IA)", "2 Jogadores (Local)"});
        modeSelect.setSelectedIndex(0);
        modeSelect.addActionListener(e -> {
            vsAI = modeSelect.getSelectedIndex() == 0;
            resetBoard();
        });
        styleControl(modeSelect);
        controls.add(modeSelect);

        // Dificuldade: Fácil / Médio / Impossível
        JComboBox<String> diffSelect = new JComboBox<>(new String[]{"Fácil", "Médio", "Impossível"});
        diffSelect.setSelectedIndex(2);
        diffSelect.addActionListener(e -> {
            int idx = diffSelect.getSelectedIndex();
            difficulty = idx == 0 ? Difficulty.EASY : idx == 1 ? Difficulty.MEDIUM : Difficulty.IMPOSSIBLE;
            updateStatus();
        });
        styleControl(diffSelect);
        controls.add(diffSelect);

        // Escolha lado da IA
        JComboBox<String> aiSide = new JComboBox<>(new String[]{"IA joga: O (padrão)", "IA joga: X"});
        aiSide.setSelectedIndex(0);
        aiSide.addActionListener(e -> {
            aiChar = aiSide.getSelectedIndex() == 0 ? 'O' : 'X';
            resetBoard();
        });
        styleControl(aiSide);
        controls.add(aiSide);

        top.add(controls, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // --- Center: board com estilo gamer
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        // Painel personalizado para desenhar o fundo "gamer" (gradiente + textura simples)
        JPanel boardWrap = new JPanel(new GridLayout(3, 3, 8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                // Gradiente de fundo
                Graphics2D g2 = (Graphics2D) g.create();
                Color c1 = new Color(12, 12, 25);
                Color c2 = new Color(25, 5, 30);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Textura sutil: linhas diagonais
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(255, 255, 255, 6));
                for (int i = -getHeight(); i < getWidth(); i += 12) {
                    g2.drawLine(i, 0, i + getHeight(), getHeight());
                }
                g2.dispose();

                super.paintComponent(g);
            }
        };
        boardWrap.setOpaque(false);
        boardWrap.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Monta botões da grade 3x3 com estilo
        Font cellFont = new Font("Consolas", Font.BOLD, 72); // aparência "tech"
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton b = new JButton("");
                b.setFont(cellFont);
                b.setFocusPainted(false);
                styleCell(b); // aplica hover e estilos gamer
                final int rr = r, cc = c;
                b.addActionListener(e -> handleCellClick(rr, cc));
                cells[r][c] = b;
                boardWrap.add(b);
            }
        }

        center.add(boardWrap, BorderLayout.CENTER);

        // Right: painel com botões de ação (undo, novo jogo, export, som)
        JPanel side = new JPanel(new GridLayout(7, 1, 8, 8));
        side.setOpaque(false);

        JButton btnNew = new JButton("▶ Novo Jogo");
        styleBigButton(btnNew);
        btnNew.addActionListener(e -> {
            SoundPlayer.playClick();
            resetBoard();
        });
        side.add(btnNew);

        JButton btnUndo = new JButton("⟲ Desfazer (Undo)");
        styleBigButton(btnUndo);
        btnUndo.addActionListener(e -> {
            SoundPlayer.playClick();
            undoLastMove();
        });
        side.add(btnUndo);

        JButton btnResetScore = new JButton("♻ Reiniciar Placar");
        styleBigButton(btnResetScore);
        btnResetScore.addActionListener(e -> {
            SoundPlayer.playClick();
            scoreX = scoreO = draws = 0;
            updateScoreLabel();
            saveScoreToFile();
        });
        side.add(btnResetScore);

        JButton btnTheme = new JButton("🖤 Tema Gamer");
        styleBigButton(btnTheme);
        btnTheme.addActionListener(e -> {
            SoundPlayer.playClick();
            darkMode = !darkMode;
            applyTheme();
            btnTheme.setText(darkMode ? "♡ Tema Claro" : "🖤 Tema Gamer");
        });
        side.add(btnTheme);

        JButton btnExport = new JButton("⤓ Exportar Replay");
        styleBigButton(btnExport);
        btnExport.addActionListener(e -> {
            SoundPlayer.playClick();
            exportReplaySample();
        });
        side.add(btnExport);

        // Toggle som (on/off)
        JCheckBox chkSound = new JCheckBox("Som");
        chkSound.setSelected(true);
        styleControl(chkSound);
        chkSound.addActionListener(e -> SoundPlayer.enabled = chkSound.isSelected());
        side.add(chkSound);

        // Placeholder para futuro (ex.: ranking online)
        JButton btnPlaceholder = new JButton("★ Mais em breve");
        styleBigButton(btnPlaceholder);
        btnPlaceholder.setEnabled(false);
        side.add(btnPlaceholder);

        center.add(side, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);

        // --- Bottom: status e placar
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(new Color(200, 220, 255));
        bottom.add(statusLabel, BorderLayout.NORTH);

        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        scoreLabel.setForeground(new Color(200, 220, 255));
        bottom.add(scoreLabel, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);

        // Carrega placar salvo (se existir)
        loadScoreFromFile();

        // Inicializa tabuleiro e tema
        resetBoard();
        applyTheme();

        // Salva placar quando fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveScoreToFile();
            }
        });
    }

    // ---------------------------
    // Estilos (gamer look)
    // ---------------------------

    // Aplica estilo a um controle (JComboBox, JCheckBox etc)
    private void styleControl(JComponent c) {
        c.setBackground(new Color(20, 20, 30));
        c.setForeground(new Color(230, 230, 255));
        if (c instanceof JComboBox) ((JComboBox<?>) c).setOpaque(true);
        c.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    // Estilo geral para botões grandes do painel lateral
    private void styleBigButton(JButton b) {
        b.setBackground(new Color(30, 20, 60));
        b.setForeground(new Color(200, 230, 255));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(90, 20, 140), 2));
        b.setFont(new Font("Helvetica", Font.BOLD, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(55, 20, 120));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(30, 20, 60));
            }
        });
    }

    // Estilo para as células do tabuleiro: hover, border, cor
    private void styleCell(JButton b) {
        b.setBackground(new Color(12, 12, 20));
        b.setForeground(new Color(180, 230, 255));
        b.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 60), 3));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Hover effect
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) b.setBorder(BorderFactory.createLineBorder(new Color(120, 200, 255), 4));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (b.isEnabled()) b.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 60), 3));
            }
        });
    }

    // ---------------------------
    // Eventos do jogo (cliques)
    // ---------------------------

    /**
     * handleCellClick - dispara quando jogador humano clica em uma célula.
     * - garante que a célula esteja livre
     * - previne cliques quando é vez da IA
     * - grava movimento em moveHistory (para undo)
     * - chama IA quando necessário
     */
    private void handleCellClick(int r, int c) {
        if (board[r][c] != '\0') return; // já ocupado
        char current = xTurn ? 'X' : 'O';
        // Se modo vsIA e é turno da IA, ignora cliques humanos
        if (vsAI && current == aiChar) return;

        // marca na lógica e UI
        performMove(r, c, current);
        moveHistory.push(new Point(r, c));
        SoundPlayer.playClick();

        // checa fim de jogo
        char winner = checkWinner();
        if (winner != '\0') {
            SoundPlayer.playVictory();
            finishGame(winner);
            return;
        }
        if (isBoardFull()) {
            SoundPlayer.playVictory();
            finishDraw();
            return;
        }

        // muda turno
        xTurn = !xTurn;
        updateStatus();

        // se agora for o turno da IA, faz IA com um pequeno delay para "sentir" mais natural
        if (vsAI && (xTurn ? 'X' : 'O') == aiChar) {
            // delay curto para efeito UX (200ms) — usamos javax.swing.Timer explicitamente
            javax.swing.Timer t = new javax.swing.Timer(200, ev -> {
                performAIMoveWithDifficulty();
            });
            t.setRepeats(false);
            t.start();
        }
    }

    // Marca movimento na board[][] e atualiza botão correspondente
    private void performMove(int r, int c, char p) {
        board[r][c] = p;
        cells[r][c].setText(String.valueOf(p));
        cells[r][c].setEnabled(false);
    }

    // Reinicia tabuleiro para novo jogo (limpa UI e lógica)
    private void resetBoard() {
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) {
            board[r][c] = '\0';
            cells[r][c].setText("");
            cells[r][c].setEnabled(true);
            cells[r][c].setBackground(null);
        }
        moveHistory.clear();
        xTurn = true;
        updateStatus();

        // Se IA escolheu X e está vsIA, deixa IA jogar primeiro (pequeno delay para UX) — usamos javax.swing.Timer explicitamente
        if (vsAI && aiChar == 'X') {
            javax.swing.Timer t = new javax.swing.Timer(300, ev -> performAIMoveWithDifficulty());
            t.setRepeats(false);
            t.start();
        }
    }

    // Atualiza label de status e placar
    private void updateStatus() {
        String mode = vsAI ? "1 jogador (vs IA)" : "2 jogadores (local)";
        String diff = difficulty == Difficulty.EASY ? "Fácil" : difficulty == Difficulty.MEDIUM ? "Médio" : "Impossível";
        char turnChar = xTurn ? 'X' : 'O';
        statusLabel.setText(String.format("Modo: %s  |  Dificuldade: %s  |  Vez: %s", mode, diff, turnChar));
        updateScoreLabel();
    }

    private void updateScoreLabel() {
        scoreLabel.setText(String.format("Placar — X: %d    O: %d    Empates: %d", scoreX, scoreO, draws));
    }

    // ---------------------------
    // Final de jogo: vitória / empate
    // ---------------------------

    // Verifica vencedor e destaca linha vencedora
    private char checkWinner() {
        // linhas e colunas
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != '\0' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                highlightLine(new Point(i, 0), new Point(i, 1), new Point(i, 2));
                return board[i][0];
            }
            if (board[0][i] != '\0' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                highlightLine(new Point(0, i), new Point(1, i), new Point(2, i));
                return board[0][i];
            }
        }
        // diagonais
        if (board[0][0] != '\0' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            highlightLine(new Point(0, 0), new Point(1, 1), new Point(2, 2));
            return board[0][0];
        }
        if (board[0][2] != '\0' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            highlightLine(new Point(0, 2), new Point(1, 1), new Point(2, 0));
            return board[0][2];
        }
        return '\0';
    }

    // Pinta a linha vencedora com cor chamativa
    private void highlightLine(Point a, Point b, Point c) {
        // reset geral antes
        for (int r = 0; r < 3; r++) for (int col = 0; col < 3; col++)
            cells[r][col].setBackground(darkMode ? new Color(18, 18, 24) : new Color(230, 240, 255));

        Color winColor = new Color(255, 200, 50);
        cells[a.x][a.y].setBackground(winColor);
        cells[b.x][b.y].setBackground(winColor);
        cells[c.x][c.y].setBackground(winColor);
    }

    private boolean isBoardFull() {
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) if (board[r][c] == '\0') return false;
        return true;
    }

    private void finishGame(char winner) {
        // desabilita células
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) cells[r][c].setEnabled(false);

        // atualiza placar conforme o vencedor
        if (winner == 'X') scoreX++;
        else if (winner == 'O') scoreO++;
        updateScoreLabel();
        saveScoreToFile();
        JOptionPane.showMessageDialog(this, "Vitória: " + winner, "Fim de jogo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void finishDraw() {
        draws++;
        updateScoreLabel();
        saveScoreToFile();
        JOptionPane.showMessageDialog(this, "Empate!", "Fim de jogo", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------
    // Undo (desfazer último movimento)
    // ---------------------------
    private void undoLastMove() {
        if (moveHistory.isEmpty()) return;

        // Remove último movimento
        Point last = moveHistory.pop();
        board[last.x][last.y] = '\0';
        cells[last.x][last.y].setText("");
        cells[last.x][last.y].setEnabled(true);

        // Se estiver jogando vs IA, desfaz também o movimento anterior do jogador (para restaurar turno humano)
        if (vsAI && !moveHistory.isEmpty()) {
            Point prev = moveHistory.pop();
            board[prev.x][prev.y] = '\0';
            cells[prev.x][prev.y].setText("");
            cells[prev.x][prev.y].setEnabled(true);
        }

        // recalcula quem começa com base no número de movimentos
        int moves = countMoves();
        xTurn = (moves % 2 == 0);
        updateStatus();
    }

    private int countMoves() {
        int cnt = 0;
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) if (board[r][c] != '\0') cnt++;
        return cnt;
    }

    // ---------------------------
    // IA: Minimax + poda alfa-beta (usa board[][] para simular)
    // ---------------------------

    // Coordenada escolhida pela IA
    private Point getBestMove(char ai, char human) {
        double bestVal = Double.NEGATIVE_INFINITY;
        Point best = null;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == '\0') {
                    board[r][c] = ai;
                    double val = minimax(0, false, ai, human, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                    board[r][c] = '\0';
                    if (val > bestVal) {
                        bestVal = val;
                        best = new Point(r, c);
                    }
                }
            }
        }
        return best;
    }

    /**
     * minimax: algoritmo recursivo com poda alfa-beta.
     * - depth penaliza vitórias mais longas (ganhar antes é melhor)
     * - isMaximizing: se for a vez do maximizador (IA)
     */
    private double minimax(int depth, boolean isMaximizing, char ai, char human, double alpha, double beta) {
        char winner = evaluateWinnerQuick();
        if (winner == ai) return 10 - depth;
        if (winner == human) return depth - 10;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            double best = Double.NEGATIVE_INFINITY;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c] == '\0') {
                        board[r][c] = ai;
                        double val = minimax(depth + 1, false, ai, human, alpha, beta);
                        board[r][c] = '\0';
                        best = Math.max(best, val);
                        alpha = Math.max(alpha, best);
                        if (beta <= alpha) return best; // poda
                    }
                }
            }
            return best;
        } else {
            double best = Double.POSITIVE_INFINITY;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c] == '\0') {
                        board[r][c] = human;
                        double val = minimax(depth + 1, true, ai, human, alpha, beta);
                        board[r][c] = '\0';
                        best = Math.min(best, val);
                        beta = Math.min(beta, best);
                        if (beta <= alpha) return best; // poda
                    }
                }
            }
            return best;
        }
    }

    // Avalia vencedor rapidamente no estado atual do board
    private char evaluateWinnerQuick() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != '\0' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0];
            if (board[0][i] != '\0' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return board[0][i];
        }
        if (board[0][0] != '\0' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0];
        if (board[0][2] != '\0' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2];
        return '\0';
    }

    // Escolhe movimento aleatório (para nível Fácil)
    private Point randomMove() {
        List<Point> empties = new ArrayList<>();
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) if (board[r][c] == '\0') empties.add(new Point(r, c));
        if (empties.isEmpty()) return null;
        return empties.get(rnd.nextInt(empties.size()));
    }

    // Faz a jogada da IA considerando a dificuldade selecionada
    private void performAIMoveWithDifficulty() {
        char ai = aiChar;
        char human = (ai == 'X') ? 'O' : 'X';
        Point chosen = null;

        if (difficulty == Difficulty.EASY) {
            chosen = randomMove();
        } else if (difficulty == Difficulty.MEDIUM) {
            if (rnd.nextDouble() < 0.5) chosen = getBestMove(ai, human);
            else chosen = randomMove();
        } else {
            chosen = getBestMove(ai, human);
        }

        if (chosen == null) chosen = randomMove();
        if (chosen != null) {
            performMove(chosen.x, chosen.y, ai);
            moveHistory.push(new Point(chosen.x, chosen.y));
            SoundPlayer.playClick();

            char winner = checkWinner();
            if (winner != '\0') {
                SoundPlayer.playVictory();
                finishGame(winner);
                return;
            }
            if (isBoardFull()) {
                SoundPlayer.playVictory();
                finishDraw();
                return;
            }

            xTurn = !xTurn;
            updateStatus();
        }
    }

    // ---------------------------
    // Persistence: salvar / carregar placar
    // ---------------------------
    private void saveScoreToFile() {
        try {
            String line = scoreX + "," + scoreO + "," + draws;
            Files.write(SCORE_FILE, Collections.singletonList(line), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            System.err.println("Erro ao salvar placar: " + ex.getMessage());
        }
    }

    private void loadScoreFromFile() {
        try {
            if (!Files.exists(SCORE_FILE)) return;
            List<String> lines = Files.readAllLines(SCORE_FILE);
            if (lines.isEmpty()) return;
            String[] parts = lines.get(0).split(",");
            if (parts.length >= 3) {
                scoreX = Integer.parseInt(parts[0]);
                scoreO = Integer.parseInt(parts[1]);
                draws = Integer.parseInt(parts[2]);
            }
        } catch (Exception ex) {
            System.err.println("Erro ao ler placar: " + ex.getMessage());
        }
    }

    // ---------------------------
    // Tema / UI updates
    // ---------------------------
    private void applyTheme() {
        // no estilo GAMER o fundo é desenhado no painel do centro; aqui só atualizamos cores das células e labels
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) {
            cells[r][c].setForeground(darkMode ? new Color(220, 240, 255) : new Color(10, 30, 40));
            cells[r][c].setBackground(darkMode ? new Color(18, 18, 24) : new Color(240, 248, 255));
        }
        statusLabel.setForeground(darkMode ? new Color(200, 230, 255) : new Color(25, 25, 50));
        scoreLabel.setForeground(darkMode ? new Color(200, 230, 255) : new Color(25, 25, 50));
        SwingUtilities.updateComponentTreeUI(this);
    }

    // ---------------------------
    // Export replay (copia JSON-like para clipboard)
    // ---------------------------
    private void exportReplaySample() {
        if (moveHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum movimento para exportar.", "Exportar Replay", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"moves\": [");
        List<Point> seq = new ArrayList<>(moveHistory);
        Collections.reverse(seq); // moveHistory guarda em ordem de push (cronológica), mas deque -> reverse para garantir
        boolean x = true;
        for (int i = 0; i < seq.size(); i++) {
            Point p = seq.get(i);
            sb.append(String.format("\"(%d,%d):%s\"", p.x, p.y, x ? "X" : "O"));
            if (i < seq.size() - 1) sb.append(", ");
            x = !x;
        }
        sb.append("] }");
        String replay = sb.toString();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(replay), null);
        JOptionPane.showMessageDialog(this, "Replay copiado para a área de transferência.", "Exportar Replay", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------
    // Utilities / enums
    // ---------------------------
    enum Difficulty { EASY, MEDIUM, IMPOSSIBLE }

    // ---------------------------
    // SoundPlayer: sintetiza sons simples (click e vitória)
    // - usa SourceDataLine para gerar tom senoidal rápido
    // - executa em thread separada para não travar UI
    // ---------------------------
    static class SoundPlayer {
        // controla se som está ativo
        static volatile boolean enabled = true;

        // simples click: tom curto
        static void playClick() {
            if (!enabled) return;
            playToneAsync(880, 70, 0.12); // A5 curto
        }

        // vitória: sequência rápida
        static void playVictory() {
            if (!enabled) return;
            // toca três tons em sequência (com pequenos delays)
            new Thread(() -> {
                playTone(1046, 120, 0.16); // C6
                sleep(80);
                playTone(1318, 120, 0.16); // E6
                sleep(80);
                playTone(1568, 220, 0.18); // G6
            }).start();
        }

        // executa em thread
        private static void playToneAsync(int hz, int msecs, double vol) {
            new Thread(() -> playTone(hz, msecs, vol)).start();
        }

        // gera e toca um tom senoidal por msecs milisegundos
        private static void playTone(int hz, int msecs, double vol) {
            final float SAMPLE_RATE = 44100;
            byte[] buf = new byte[1];
            AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
                sdl.open(af);
                sdl.start();
                for (int i = 0; i < msecs * (float) SAMPLE_RATE / 1000; i++) {
                    double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
                    buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
                    sdl.write(buf, 0, 1);
                }
                sdl.drain();
            } catch (LineUnavailableException ex) {
                // se falhar no áudio, fallback para beep do sistema
                Toolkit.getDefaultToolkit().beep();
            } catch (Exception ignored) {}
        }

        private static void sleep(int ms) {
            try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
        }
    }

    // ---------------------------
    // main - ponto de entrada público
    // ---------------------------
    public static void main(String[] args) {
        // garante execução na Event Dispatch Thread (recomendado para Swing)
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameJogoDaVelha app = new GameJogoDaVelha();
            app.setVisible(true);
        });
    }
}
