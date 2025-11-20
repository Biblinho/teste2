/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.scientificcalculator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/**
 * ScientificCalculator - Calculadora científica com Swing
 * Recursos adicionados (nova versão):
 * - Parser de expressões (shunting-yard -> RPN) com suporte a funções multiaridade (pow(x,y))
 * - Funções: sin, cos, tan, asin, acos, atan, sqrt, ln, log, abs, pow (pow aceita 2 args)
 * - Histórico (JList) que salva/ler em arquivo local automaticamente
 * - Botões: Limpar, Usar, Salvar, Carregar
 * - Tema claro / escuro
 *
 * Cole em ScientificCalculator.java e rode.
 */
public class ScientificCalculator extends JFrame {
    private final JTextField display;
    private final DefaultListModel<String> historyModel;
    private final JList<String> historyList;
    private boolean darkMode = false;

    // arquivo de histórico no diretório do usuário
    private static final Path HISTORY_PATH = Paths.get(System.getProperty("user.home"), ".scientific_calc_history.txt");

    public ScientificCalculator() {
        super("Calculadora Científica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setMinimumSize(new Dimension(760, 480));
        setLocationRelativeTo(null);

        // Main layout
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // Top: display + theme toggle
        JPanel top = new JPanel(new BorderLayout(8, 8));
        display = new JTextField();
        display.setFont(new Font("Consolas", Font.PLAIN, 28));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setFocusable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        top.add(display, BorderLayout.CENTER);

        JButton themeBtn = new JButton("Tema: Claro");
        themeBtn.addActionListener(e -> {
            darkMode = !darkMode;
            applyTheme(darkMode);
            themeBtn.setText(darkMode ? "Tema: Escuro" : "Tema: Claro");
        });
        top.add(themeBtn, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // Center: keypad + functions
        JPanel center = new JPanel(new BorderLayout(10,10));
        root.add(center, BorderLayout.CENTER);

        JPanel keypad = createKeypad();
        center.add(keypad, BorderLayout.CENTER);

        // Right side: history
        JPanel right = new JPanel(new BorderLayout(6,6));
        right.setPreferredSize(new Dimension(320, 0));
        right.setBorder(BorderFactory.createTitledBorder("Histórico"));

        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane histPane = new JScrollPane(historyList);
        right.add(histPane, BorderLayout.CENTER);

        JPanel histButtons = new JPanel(new GridLayout(2,2,6,6));
        JButton clearHist = new JButton("Limpar");
        clearHist.addActionListener(e -> historyModel.clear());
        JButton useHist = new JButton("Usar");
        useHist.addActionListener(e -> {
            String sel = historyList.getSelectedValue();
            if (sel != null) {
                int eq = sel.indexOf(" = ");
                if (eq > 0) {
                    display.setText(sel.substring(0, eq));
                } else {
                    display.setText(sel);
                }
            }
        });
        JButton saveHist = new JButton("Salvar");
        saveHist.addActionListener(e -> {
            try {
                saveHistoryToFile();
                JOptionPane.showMessageDialog(this, "Histórico salvo em:\n" + HISTORY_PATH.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar histórico: " + ex.getMessage());
            }
        });
        JButton loadHist = new JButton("Carregar");
        loadHist.addActionListener(e -> {
            try {
                loadHistoryFromFile();
                JOptionPane.showMessageDialog(this, "Histórico carregado.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar histórico: " + ex.getMessage());
            }
        });

        histButtons.add(clearHist);
        histButtons.add(useHist);
        histButtons.add(saveHist);
        histButtons.add(loadHist);
        right.add(histButtons, BorderLayout.SOUTH);

        center.add(right, BorderLayout.EAST);

        // Bottom: quick tips
        JLabel tips = new JLabel("Dica: use ^ para potência ou use pow(x,y). Funções: sin(), cos(), tan(), ln(), log(), sqrt(), abs()");
        tips.setFont(new Font("SansSerif", Font.PLAIN, 12));
        root.add(tips, BorderLayout.SOUTH);

        // Keyboard support
        setupKeyboardShortcuts();

        // load history on startup if exists
        try { loadHistoryFromFile(); } catch (IOException ignored) {}

        // autosave on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try { saveHistoryToFile(); } catch (IOException ignored) {}
            }
        });

        applyTheme(false);
    }

    private JPanel createKeypad() {
        JPanel panel = new JPanel(new BorderLayout(8,8));

        // Left: numeric + basic ops
        JPanel left = new JPanel(new BorderLayout(8,8));
        JPanel numPad = new JPanel(new GridLayout(4,4,6,6));
        String[] keys = {
                "7","8","9","/",
                "4","5","6","*",
                "1","2","3","-",
                "0",".","(",")"
        };
        for (String k : keys) {
            JButton b = createButton(k, e -> appendToDisplay(k));
            numPad.add(b);
        }
        left.add(numPad, BorderLayout.CENTER);

        // basic ops on right of numpad
        JPanel ops = new JPanel(new GridLayout(4,1,6,6));
        JButton plus = createButton("+", e -> appendToDisplay("+"));
        JButton eq = createButton("=", e -> evaluate());
        JButton del = createButton("DEL", e -> backspace());
        JButton clear = createButton("C", e -> display.setText(""));
        ops.add(plus);
        ops.add(eq);
        ops.add(del);
        ops.add(clear);
        left.add(ops, BorderLayout.EAST);

        // Top row for memory/functions
        JPanel topRow = new JPanel(new GridLayout(1,4,6,6));
        topRow.add(createButton("ANS", e -> useLastAnswer()));
        topRow.add(createButton("PI", e -> appendToDisplay(String.valueOf(Math.PI))));
        topRow.add(createButton("E", e -> appendToDisplay(String.valueOf(Math.E))));
        topRow.add(createButton("x^2", e -> appendToDisplay("^2")));
        left.add(topRow, BorderLayout.NORTH);

        panel.add(left, BorderLayout.CENTER);

        // Right: scientific buttons
        JPanel sci = new JPanel(new GridLayout(6,2,6,6));
        String[] sciButtons = {
                "sin(", "cos(",
                "tan(", "asin(",
                "acos(", "atan(",
                "sqrt(", "ln(",
                "log(", "abs(",
                "^", "pow("
        };
        for (String s : sciButtons) {
            JButton b = createButton(s, e -> appendToDisplay(s));
            sci.add(b);
        }

        panel.add(sci, BorderLayout.EAST);

        return panel;
    }

    private JButton createButton(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.addActionListener(al);
        b.setFocusable(false);
        return b;
    }

    private void appendToDisplay(String s) {
        display.setText(display.getText() + s);
    }

    private void backspace() {
        String t = display.getText();
        if (!t.isEmpty()) {
            display.setText(t.substring(0, t.length() - 1));
        }
    }

    private void useLastAnswer() {
        if (!historyModel.isEmpty()) {
            String last = historyModel.get(historyModel.size()-1);
            int eq = last.indexOf(" = ");
            if (eq > 0) {
                String res = last.substring(eq + 3);
                appendToDisplay(res);
            } else {
                appendToDisplay(last);
            }
        }
    }

    private void evaluate() {
        String expr = display.getText().trim();
        if (expr.isEmpty()) return;
        try {
            double result = ExpressionEvaluator.evaluate(expr);
            String resStr = (result == Math.rint(result)) ? String.valueOf((long) Math.rint(result)) : String.valueOf(result);
            historyModel.addElement(expr + " = " + resStr);
            display.setText(resStr);
        } catch (Exception ex) {
            display.setText("Erro");
        }
    }

    private void applyTheme(boolean dark) {
        Color bg = dark ? new Color(40, 43, 48) : Color.WHITE;
        Color fg = dark ? new Color(220, 220, 220) : Color.BLACK;
        Color panelBg = dark ? new Color(50, 54, 59) : new Color(245, 245, 245);

        getContentPane().setBackground(panelBg);
        for (Component c : getContentPane().getComponents()) {
            c.setBackground(panelBg);
            c.setForeground(fg);
        }

        display.setBackground(dark ? new Color(30, 33, 36) : Color.WHITE);
        display.setForeground(fg);
        historyList.setBackground(dark ? new Color(30,33,36) : Color.WHITE);
        historyList.setForeground(fg);
        updateComponentTreeUI(this.getContentPane(), panelBg, fg, dark);
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void updateComponentTreeUI(Component comp, Color bg, Color fg, boolean dark) {
        if (comp instanceof JPanel) {
            comp.setBackground(bg);
            comp.setForeground(fg);
        } else if (comp instanceof JScrollPane) {
            comp.setBackground(bg);
            comp.setForeground(fg);
            JScrollPane sp = (JScrollPane) comp;
            updateComponentTreeUI(sp.getViewport().getView(), bg, fg, dark);
        } else if (comp instanceof JButton) {
            JButton b = (JButton) comp;
            b.setBackground(dark ? new Color(70, 73, 78) : new Color(230, 230, 230));
            b.setForeground(fg);
        } else if (comp instanceof JList) {
            comp.setBackground(bg);
            comp.setForeground(fg);
        } else if (comp instanceof JToolBar || comp instanceof JLabel) {
            comp.setForeground(fg);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                updateComponentTreeUI(child, bg, fg, dark);
            }
        }
    }

    private void setupKeyboardShortcuts() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) return false;
            char c = e.getKeyChar();
            int code = e.getKeyCode();

            if (Character.isDigit(c) || c == '.' ) {
                appendToDisplay(String.valueOf(c));
                return true;
            }
            switch (code) {
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_ADD:
                case KeyEvent.VK_EQUALS:
                case KeyEvent.VK_ENTER:
                    if (e.isShiftDown() && code == KeyEvent.VK_EQUALS) { appendToDisplay("+"); return true; }
                    evaluate(); return true;
                case KeyEvent.VK_MINUS: appendToDisplay("-"); return true;
                case KeyEvent.VK_SLASH: appendToDisplay("/"); return true;
                case KeyEvent.VK_ASTERISK: appendToDisplay("*"); return true;
                case KeyEvent.VK_BACK_SPACE: backspace(); return true;
                case KeyEvent.VK_ESCAPE: display.setText(""); return true;
                case KeyEvent.VK_C: if (e.isControlDown()) display.setText(""); return true;
                default:
                    if (Character.isLetter(c)) appendToDisplay(String.valueOf(c));
            }
            return false;
        });
    }

    private void saveHistoryToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < historyModel.size(); i++) lines.add(historyModel.get(i));
        Files.write(HISTORY_PATH, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void loadHistoryFromFile() throws IOException {
        if (!Files.exists(HISTORY_PATH)) return;
        List<String> lines = Files.readAllLines(HISTORY_PATH);
        historyModel.clear();
        for (String l : lines) if (!l.isBlank()) historyModel.addElement(l);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScientificCalculator calc = new ScientificCalculator();
            calc.setVisible(true);
        });
    }

    // -------------------------
    // Expression evaluator class
    // -------------------------
    static class ExpressionEvaluator {
        private static final Set<String> FUNCTIONS = new HashSet<>(Arrays.asList(
                "sin","cos","tan","asin","acos","atan","sqrt","ln","log","abs","pow"
        ));
        private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
        static {
            PRECEDENCE.put("+", 2);
            PRECEDENCE.put("-", 2);
            PRECEDENCE.put("*", 3);
            PRECEDENCE.put("/", 3);
            PRECEDENCE.put("^", 4); // right-associative
            PRECEDENCE.put("u-", 5); // unary minus (highest)
        }

        public static double evaluate(String expr) {
            List<Token> tokens = tokenize(expr);
            List<Token> rpn = shuntingYard(tokens);
            return evalRPN(rpn);
        }

        // Tokenization
        private static List<Token> tokenize(String s) {
            List<Token> out = new ArrayList<>();
            int i = 0;
            while (i < s.length()) {
                char c = s.charAt(i);
                if (Character.isWhitespace(c)) { i++; continue; }
                if (c == '(') { out.add(new Token(TokenType.LPAREN, "(")); i++; continue; }
                if (c == ')') { out.add(new Token(TokenType.RPAREN, ")")); i++; continue; }
                if (c == ',') { out.add(new Token(TokenType.COMMA, ",")); i++; continue; }

                // operator
                if ("+-*/^".indexOf(c) >= 0) {
                    out.add(new Token(TokenType.OP, String.valueOf(c)));
                    i++; continue;
                }

                // number (including decimal and exponent)
                if (Character.isDigit(c) || c == '.') {
                    int j = i+1;
                    while (j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j)=='.' || s.charAt(j)=='e' || s.charAt(j)=='E' || ((s.charAt(j)=='+' || s.charAt(j)=='-') && (s.charAt(j-1)=='e' || s.charAt(j-1)=='E')))) {
                        j++;
                    }
                    String num = s.substring(i,j);
                    out.add(new Token(TokenType.NUMBER, num));
                    i = j;
                    continue;
                }

                // functions/identifiers
                if (Character.isLetter(c)) {
                    int j = i+1;
                    while (j < s.length() && (Character.isLetterOrDigit(s.charAt(j)) || s.charAt(j) == '_')) j++;
                    String name = s.substring(i, j);
                    out.add(new Token(TokenType.FUNC_OR_VAR, name));
                    i = j;
                    continue;
                }

                // unknown char
                throw new IllegalArgumentException("Caracter inválido na expressão: " + c);
            }

            // adjust unary minus tokens: replace - with u- when appropriate
            List<Token> normalized = new ArrayList<>();
            Token prev = null;
            for (Token t : out) {
                if (t.type == TokenType.OP && t.text.equals("-")) {
                    if (prev == null || prev.type == TokenType.OP || prev.type == TokenType.LPAREN || prev.type == TokenType.COMMA) {
                        normalized.add(new Token(TokenType.OP, "u-")); // unary minus
                    } else {
                        normalized.add(t);
                    }
                } else {
                    normalized.add(t);
                }
                prev = normalized.isEmpty() ? null : normalized.get(normalized.size()-1);
            }
            return normalized;
        }

        // shunting-yard
        private static List<Token> shuntingYard(List<Token> tokens) {
            List<Token> output = new ArrayList<>();
            Deque<Token> stack = new ArrayDeque<>();

            for (Token token : tokens) {
                switch (token.type) {
                    case NUMBER:
                        output.add(token);
                        break;
                    case FUNC_OR_VAR:
                        if (FUNCTIONS.contains(token.text.toLowerCase())) {
                            stack.push(token);
                        } else {
                            output.add(token);
                        }
                        break;
                    case COMMA:
                        while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN) {
                            output.add(stack.pop());
                        }
                        if (stack.isEmpty() || stack.peek().type != TokenType.LPAREN) {
                            throw new IllegalArgumentException("Vírgula fora de funções ou parênteses");
                        }
                        break;
                    case OP:
                        String op1 = token.text;
                        while (!stack.isEmpty() && stack.peek().type == TokenType.OP) {
                            String op2 = stack.peek().text;
                            if ((isLeftAssociative(op1) && precedence(op1) <= precedence(op2)) ||
                                    (!isLeftAssociative(op1) && precedence(op1) < precedence(op2))) {
                                output.add(stack.pop());
                                continue;
                            }
                            break;
                        }
                        stack.push(token);
                        break;
                    case LPAREN:
                        stack.push(token);
                        break;
                    case RPAREN:
                        while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN) {
                            output.add(stack.pop());
                        }
                        if (stack.isEmpty()) throw new IllegalArgumentException("Parêntese ) sem par correspondente");
                        stack.pop(); // pop '('
                        if (!stack.isEmpty() && stack.peek().type == TokenType.FUNC_OR_VAR) {
                            output.add(stack.pop());
                        }
                        break;
                }
            }

            while (!stack.isEmpty()) {
                Token t = stack.pop();
                if (t.type == TokenType.LPAREN || t.type == TokenType.RPAREN) {
                    throw new IllegalArgumentException("Parênteses desencontrados");
                }
                output.add(t);
            }

            return output;
        }

        private static boolean isLeftAssociative(String op) {
            if (op.equals("^")) return false; // right-assoc
            return !op.equals("u-");
        }

        private static int precedence(String op) {
            return PRECEDENCE.getOrDefault(op, 0);
        }

        // Evaluate RPN
        private static double evalRPN(List<Token> rpn) {
            Deque<Double> st = new ArrayDeque<>();
            for (Token t : rpn) {
                if (t.type == TokenType.NUMBER) {
                    st.push(Double.parseDouble(t.text));
                } else if (t.type == TokenType.FUNC_OR_VAR) {
                    String name = t.text.toLowerCase();
                    if (name.equals("pi")) st.push(Math.PI);
                    else if (name.equals("e")) st.push(Math.E);
                    else if (FUNCTIONS.contains(name)) {
                        if (name.equals("pow")) {
                            // pow expects 2 args: a ^ b => pow(a,b)
                            if (st.size() < 2) throw new IllegalArgumentException("Argumentos insuficientes para pow");
                            double b = st.pop();
                            double a = st.pop();
                            st.push(Math.pow(a, b));
                        } else {
                            if (st.isEmpty()) throw new IllegalArgumentException("Argumentos insuficientes para função " + name);
                            double a = st.pop();
                            double res = applyFunc(name, a);
                            st.push(res);
                        }
                    } else {
                        throw new IllegalArgumentException("Identificador desconhecido: " + name);
                    }
                } else if (t.type == TokenType.OP) {
                    String op = t.text;
                    if (op.equals("u-")) {
                        if (st.isEmpty()) throw new IllegalArgumentException("Operação inválida (u-)");
                        st.push(-st.pop());
                    } else if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("^")) {
                        if (st.size() < 2) throw new IllegalArgumentException("Operands insuficientes para " + op);
                        double b = st.pop();
                        double a = st.pop();
                        double res = 0;
                        switch (op) {
                            case "+": res = a + b; break;
                            case "-": res = a - b; break;
                            case "*": res = a * b; break;
                            case "/": res = a / b; break;
                            case "^": res = Math.pow(a, b); break;
                        }
                        st.push(res);
                    } else {
                        throw new IllegalArgumentException("Operador desconhecido: " + op);
                    }
                }
            }
            if (st.size() != 1) throw new IllegalArgumentException("Expressão inválida");
            return st.pop();
        }

        private static double applyFunc(String name, double a) {
            switch (name) {
                case "sin": return Math.sin(a);
                case "cos": return Math.cos(a);
                case "tan": return Math.tan(a);
                case "asin": return Math.asin(a);
                case "acos": return Math.acos(a);
                case "atan": return Math.atan(a);
                case "sqrt": return Math.sqrt(a);
                case "ln": return Math.log(a);
                case "log": return Math.log10(a);
                case "abs": return Math.abs(a);
                default: throw new IllegalArgumentException("Função não suportada: " + name);
            }
        }

        // Token types
        private enum TokenType { NUMBER, FUNC_OR_VAR, OP, LPAREN, RPAREN, COMMA }
        private static class Token {
            final TokenType type;
            final String text;
            Token(TokenType type, String text) { this.type = type; this.text = text; }
            public String toString(){ return text; }
        }
    }
}
