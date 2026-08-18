import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Eclipse {

    static final String GENERATED_CLASS = "EclipseProgram";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Eclipse Compiler");
            System.out.println("Usage: java Eclipse <file.ecs>");
            return;
        }

        Path ecs = Paths.get(args[0]);

        if (!Files.exists(ecs)) {
            error("File not found: " + ecs);
            return;
        }

        if (!ecs.toString().toLowerCase().endsWith(".ecs")) {
            error("Only .ecs files can be compiled.");
            return;
        }

        try {
            List<String> source = Files.readAllLines(ecs);
            String java = compile(source);

            Path folder = ecs.toAbsolutePath()
                    .getParent()
                    .resolve(".eclipse-build");

            Files.createDirectories(folder);

            Path javaFile =
                    folder.resolve(GENERATED_CLASS + ".java");

            Files.writeString(javaFile, java);

            System.out.println("Eclipse");
            System.out.println("-------");
            System.out.println("Compiling: " + ecs.getFileName());
            System.out.println("Generating Java...");

            Process javac = new ProcessBuilder(
                    "javac",
                    javaFile.toString()
            ).inheritIO().start();

            int result = javac.waitFor();

            if (result != 0) {
                error("Java compilation failed.");
                return;
            }

            System.out.println("Running...");
            System.out.println();

            Process javaProcess = new ProcessBuilder(
                    "java",
                    "-cp",
                    folder.toString(),
                    GENERATED_CLASS
            ).inheritIO().start();

            javaProcess.waitFor();

        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    static String compile(List<String> source) {

        StringBuilder out = new StringBuilder();

        out.append("import java.io.*;\n");
        out.append("import java.util.*;\n");
        out.append("import javax.swing.*;\n");
        out.append("import java.awt.*;\n");
        out.append("import java.awt.event.*;\n");
        out.append("\n");

        out.append("public class ")
                .append(GENERATED_CLASS)
                .append(" {\n\n");

        out.append("""
            static JFrame window;
            static GamePanel panel;

            static boolean game = true;

            static HashMap<String, GameObject> objects =
                    new HashMap<>();

            static HashSet<Integer> keys =
                    new HashSet<>();

            static String title = "Eclipse";

            static class GameObject {
                String name;
                int x = 0;
                int y = 0;
                int width = 50;
                int height = 50;

                GameObject(String name) {
                    this.name = name;
                }
            }

            static class GamePanel extends JPanel {

                GamePanel() {
                    setFocusable(true);

                    addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent e) {
                            keys.add(e.getKeyCode());
                        }

                        public void keyReleased(KeyEvent e) {
                            keys.remove(e.getKeyCode());
                        }
                    });
                }

                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    g.setColor(Color.BLACK);
                    g.fillRect(
                        0,
                        0,
                        getWidth(),
                        getHeight()
                    );

                    for (GameObject obj : objects.values()) {

                        if (obj.name.equals("player")) {
                            g.setColor(Color.CYAN);
                        }
                        else if (obj.name.equals("enemy")) {
                            g.setColor(Color.RED);
                        }
                        else if (obj.name.equals("bullet")) {
                            g.setColor(Color.YELLOW);
                        }
                        else {
                            g.setColor(Color.WHITE);
                        }

                        g.fillRect(
                            obj.x,
                            obj.y,
                            obj.width,
                            obj.height
                        );
                    }
                }
            }

            static void createWindow(int width, int height) {

                SwingUtilities.invokeLater(() -> {

                    window = new JFrame(title);

                    panel = new GamePanel();

                    panel.setPreferredSize(
                        new Dimension(width, height)
                    );

                    window.setDefaultCloseOperation(
                        JFrame.EXIT_ON_CLOSE
                    );

                    window.add(panel);
                    window.pack();
                    window.setLocationRelativeTo(null);
                    window.setVisible(true);

                    panel.requestFocusInWindow();
                });

                sleep(100);
            }

            static void createObject(String name) {
                objects.put(
                    name,
                    new GameObject(name)
                );
            }

            static void setX(String name, int x) {
                GameObject obj = objects.get(name);

                if (obj != null) {
                    obj.x = x;
                }
            }

            static void setY(String name, int y) {
                GameObject obj = objects.get(name);

                if (obj != null) {
                    obj.y = y;
                }
            }

            static void moveX(String name, int amount) {
                GameObject obj = objects.get(name);

                if (obj != null) {
                    obj.x += amount;
                }
            }

            static void moveY(String name, int amount) {
                GameObject obj = objects.get(name);

                if (obj != null) {
                    obj.y += amount;
                }
            }

            static boolean keyDown(String key) {

                switch (key.toUpperCase()) {

                    case "LEFT":
                        return keys.contains(KeyEvent.VK_LEFT);

                    case "RIGHT":
                        return keys.contains(KeyEvent.VK_RIGHT);

                    case "UP":
                        return keys.contains(KeyEvent.VK_UP);

                    case "DOWN":
                        return keys.contains(KeyEvent.VK_DOWN);

                    case "SPACE":
                        return keys.contains(KeyEvent.VK_SPACE);

                    case "ENTER":
                        return keys.contains(KeyEvent.VK_ENTER);

                    case "R":
                        return keys.contains(KeyEvent.VK_R);

                    default:
                        return false;
                }
            }

            static boolean collision(
                    String first,
                    String second
            ) {

                GameObject a = objects.get(first);
                GameObject b = objects.get(second);

                if (a == null || b == null) {
                    return false;
                }

                Rectangle ra = new Rectangle(
                    a.x,
                    a.y,
                    a.width,
                    a.height
                );

                Rectangle rb = new Rectangle(
                    b.x,
                    b.y,
                    b.width,
                    b.height
                );

                return ra.intersects(rb);
            }

            static void draw() {

                if (panel != null) {
                    panel.repaint();
                }
            }

            static void sleep(long ms) {
                try {
                    Thread.sleep(ms);
                }
                catch (InterruptedException ignored) {
                }
            }

            """);

        out.append(
                "    public static void main(String[] args) throws Exception {\n"
        );

        compileLines(source, out, 2);

        out.append("    }\n");
        out.append("}\n");

        return out.toString();
    }

    static void compileLines(
            List<String> lines,
            StringBuilder out,
            int indent
    ) {

        for (int i = 0; i < lines.size(); i++) {

            String line = clean(lines.get(i));

            if (line.isEmpty()) {
                continue;
            }

            // console.log(...)
            if (line.startsWith("console.log(")) {

                String value = inside(line);

                out.append(spaces(indent))
                        .append("System.out.println(")
                        .append(expression(value))
                        .append(");\n");

                continue;
            }

            // create window(800, 600)
            if (line.startsWith("create window(")) {

                String value = inside(line);
                String[] p = value.split(",");

                if (p.length == 2) {

                    out.append(spaces(indent))
                            .append("createWindow(")
                            .append(expression(p[0]))
                            .append(",")
                            .append(expression(p[1]))
                            .append(");\n");
                }

                continue;
            }

            // title = "Eclipse Shooter"
            if (line.startsWith("title =")) {

                String value =
                        line.substring(7).trim();

                out.append(spaces(indent))
                        .append("title = ")
                        .append(expression(value))
                        .append(";\n");

                continue;
            }

            // obj player
            if (line.startsWith("obj ")) {

                String name =
                        line.substring(4).trim();

                out.append(spaces(indent))
                        .append("createObject(\"")
                        .append(name)
                        .append("\");\n");

                continue;
            }

            // set player x 400
            if (line.startsWith("set ")) {

                String[] p =
                        line.split("\\s+");

                if (p.length >= 4) {

                    String object = p[1];
                    String axis = p[2];
                    String value = p[3];

                    if (axis.equalsIgnoreCase("x")) {

                        out.append(spaces(indent))
                                .append("setX(\"")
                                .append(object)
                                .append("\",")
                                .append(expression(value))
                                .append(");\n");
                    }

                    if (axis.equalsIgnoreCase("y")) {

                        out.append(spaces(indent))
                                .append("setY(\"")
                                .append(object)
                                .append("\",")
                                .append(expression(value))
                                .append(");\n");
                    }
                }

                continue;
            }

            // move player x 5
            if (line.startsWith("move ")) {

                String[] p =
                        line.split("\\s+");

                if (p.length >= 4) {

                    String object = p[1];
                    String axis = p[2];
                    String value = p[3];

                    if (axis.equalsIgnoreCase("x")) {

                        out.append(spaces(indent))
                                .append("moveX(\"")
                                .append(object)
                                .append("\",")
                                .append(expression(value))
                                .append(");\n");
                    }

                    if (axis.equalsIgnoreCase("y")) {

                        out.append(spaces(indent))
                                .append("moveY(\"")
                                .append(object)
                                .append("\",")
                                .append(expression(value))
                                .append(");\n");
                    }
                }

                continue;
            }

            // draw player
            if (line.startsWith("draw ")) {

                out.append(spaces(indent))
                        .append("draw();\n");

                continue;
            }

            // if keydown("SPACE") then
            if (line.startsWith("if keydown(")
                    && line.endsWith("then")) {

                String value =
                        line.substring(
                                3,
                                line.length() - 5
                        );

                String key =
                        inside(value);

                out.append(spaces(indent))
                        .append("if (keyDown(")
                        .append(expression(key))
                        .append(")) {\n");

                int end = compileBlock(
                        lines,
                        i + 1,
                        out,
                        indent + 1
                );

                out.append(spaces(indent))
                        .append("}\n");

                i = end;

                continue;
            }

            // if collision(player, enemy) then
            if (line.startsWith("if collision(")
                    && line.endsWith("then")) {

                String value =
                        inside(
                                line.substring(
                                        3,
                                        line.length() - 5
                                )
                        );

                String[] p = value.split(",");

                if (p.length == 2) {

                    out.append(spaces(indent))
                            .append("if (collision(\"")
                            .append(p[0].trim())
                            .append("\",\"")
                            .append(p[1].trim())
                            .append("\")) {\n");

                    int end = compileBlock(
                            lines,
                            i + 1,
                            out,
                            indent + 1
                    );

                    out.append(spaces(indent))
                            .append("}\n");

                    i = end;
                }

                continue;
            }

            // if [x] = 3 then
            if (line.startsWith("if ")
                    && line.endsWith("then")) {

                String condition =
                        line.substring(
                                3,
                                line.length() - 5
                        ).trim();

                out.append(spaces(indent))
                        .append("if (")
                        .append(condition(condition))
                        .append(") {\n");

                int end = compileBlock(
                        lines,
                        i + 1,
                        out,
                        indent + 1
                );

                out.append(spaces(indent))
                        .append("}\n");

                i = end;

                continue;
            }

            // while [game] = true
            if (line.startsWith("while ")) {

                String value =
                        line.substring(6).trim();

                out.append(spaces(indent))
                        .append("while (")
                        .append(condition(value))
                        .append(") {\n");

                int end = compileBlock(
                        lines,
                        i + 1,
                        out,
                        indent + 1
                );

                out.append(spaces(indent))
                        .append("}\n");

                i = end;

                continue;
            }

            // repeat 5
            if (line.startsWith("repeat ")) {

                String amount =
                        line.substring(7).trim();

                out.append(spaces(indent))
                        .append("for (int eclipse_i = 0; ")
                        .append("eclipse_i < ")
                        .append(expression(amount))
                        .append("; eclipse_i++) {\n");

                int end = compileBlock(
                        lines,
                        i + 1,
                        out,
                        indent + 1
                );

                out.append(spaces(indent))
                        .append("}\n");

                i = end;

                continue;
            }

            // func name()
            if (line.startsWith("func ")) {

                String name =
                        line.substring(5).trim();

                if (name.endsWith("()")) {
                    name = name.substring(
                            0,
                            name.length() - 2
                    );
                }

                out.append(spaces(indent))
                        .append("// Eclipse function: ")
                        .append(name)
                        .append("\n");

                continue;
            }

            // function call
            if (line.matches(
                    "[A-Za-z_][A-Za-z0-9_]*\\(\\)"
            )) {

                String name =
                        line.substring(
                                0,
                                line.indexOf("(")
                        );

                out.append(spaces(indent))
                        .append(name)
                        .append("();\n");

                continue;
            }

            // variable
            if (line.matches(
                    "[A-Za-z_][A-Za-z0-9_]*\\s*=.*"
            )) {

                String[] p =
                        line.split("=", 2);

                String name = p[0].trim();
                String value = p[1].trim();

                out.append(spaces(indent))
                        .append("var ")
                        .append(name)
                        .append(" = ")
                        .append(expression(value))
                        .append(";\n");

                continue;
            }

            // end
            if (line.equals("end")) {
                continue;
            }

            error("Unknown Eclipse command: " + line);
        }
    }

    static int compileBlock(
            List<String> lines,
            int start,
            StringBuilder out,
            int indent
    ) {

        int depth = 0;

        for (int i = start; i < lines.size(); i++) {

            String line = clean(lines.get(i));

            if (line.startsWith("if ")
                    || line.startsWith("while ")
                    || line.startsWith("repeat ")
                    || line.startsWith("func ")) {

                depth++;
            }

            if (line.equals("end")) {

                if (depth == 0) {
                    return i;
                }

                depth--;
            }

            List<String> one =
                    new ArrayList<>();

            one.add(lines.get(i));

            compileLines(one, out, indent);
        }

        return lines.size();
    }

    static String expression(String value) {

        value = value.trim();

        value = value.replaceAll(
                "\\[([A-Za-z_][A-Za-z0-9_]*)\\]",
                "$1"
        );

        return value;
    }

    static String condition(String value) {

        value = expression(value);

        value = value.replace(
                " = ",
                " == "
        );

        return value;
    }

    static String inside(String value) {

        int start = value.indexOf("(");
        int end = value.lastIndexOf(")");

        if (start == -1 || end == -1) {
            return "";
        }

        return value.substring(
                start + 1,
                end
        ).trim();
    }

    static String clean(String line) {

        line = line.trim();

        if (line.startsWith("#")) {
            return "";
        }

        return line;
    }

    static String spaces(int n) {
        return "    ".repeat(n);
    }

    static void error(String message) {
        System.err.println(
                "[Eclipse Error] " + message
        );
    }
}
