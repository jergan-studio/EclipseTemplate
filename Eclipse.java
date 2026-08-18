import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class Eclipse {

    private static final String VERSION = "Eclipse ECS Compiler 1.0";

    private static final String GENERATED_CLASS =
            "EclipseProgram";


    public static void main(String[] args) {

        System.out.println();
        System.out.println("================================");
        System.out.println("        ECLIPSE COMPILER");
        System.out.println("================================");
        System.out.println(VERSION);
        System.out.println();


        if (args.length == 0) {

            System.out.println("Usage:");
            System.out.println();
            System.out.println("  java Eclipse game.ecs");
            System.out.println();

            return;
        }


        Path ecsFile =
                Paths.get(args[0])
                        .toAbsolutePath()
                        .normalize();


        if (!Files.exists(ecsFile)) {

            error(
                    "ECS file not found: "
                    + ecsFile
            );

            return;
        }


        if (
                !ecsFile
                        .toString()
                        .toLowerCase()
                        .endsWith(".ecs")
        ) {

            error(
                    "The file must use the .ecs extension."
            );

            return;
        }


        try {

            List<String> lines =
                    Files.readAllLines(
                            ecsFile
                    );


            String generated =
                    compile(lines);


            Path projectFolder =
                    ecsFile.getParent();


            Path buildFolder =
                    projectFolder.resolve(
                            ".eclipse-build"
                    );


            Files.createDirectories(
                    buildFolder
            );


            Path javaFile =
                    buildFolder.resolve(
                            GENERATED_CLASS
                            + ".java"
                    );


            Files.writeString(
                    javaFile,
                    generated
            );


            System.out.println(
                    "Source:"
            );

            System.out.println(
                    "  "
                    + ecsFile
            );

            System.out.println();


            System.out.println(
                    "Generating Java..."
            );


            System.out.println(
                    "  "
                    + javaFile
            );

            System.out.println();


            Process javac =
                    new ProcessBuilder(
                            "javac",
                            javaFile.toString()
                    )
                    .inheritIO()
                    .start();


            int result =
                    javac.waitFor();


            if (result != 0) {

                error(
                        "Generated Java compilation failed."
                );

                return;
            }


            System.out.println(
                    "ECS compilation successful!"
            );

            System.out.println();


            System.out.println(
                    "Launching ECS program..."
            );

            System.out.println();


            Process program =
                    new ProcessBuilder(
                            "java",
                            "-cp",
                            buildFolder.toString(),
                            GENERATED_CLASS
                    )
                    .inheritIO()
                    .start();


            program.waitFor();


        } catch (Exception e) {

            error(
                    e.getClass()
                            .getSimpleName()
                    + ": "
                    + e.getMessage()
            );
        }
    }


    /*
     * ============================================================
     * COMPILER
     * ============================================================
     */

    static String compile(
            List<String> lines
    ) {

        StringBuilder main =
                new StringBuilder();


        StringBuilder functions =
                new StringBuilder();


        boolean functionMode =
                false;


        String functionName =
                "";


        List<String> functionLines =
                new ArrayList<>();


        for (String raw : lines) {

            String line =
                    raw.trim();


            if (line.isEmpty()) {
                continue;
            }


            if (line.startsWith("#")) {
                continue;
            }


            /*
             * func shoot()
             */

            if (
                    line.startsWith(
                            "func "
                    )
            ) {

                functionMode =
                        true;


                functionName =
                        line.substring(
                                5
                        ).trim();


                if (
                        functionName
                                .endsWith(
                                        "()"
                                )
                ) {

                    functionName =
                            functionName.substring(
                                    0,
                                    functionName.length()
                                            - 2
                            );
                }


                functionLines.clear();


                continue;
            }


            /*
             * End function.
             */

            if (
                    functionMode
                    &&
                    line.equals("end")
            ) {

                compileFunction(
                        functionName,
                        functionLines,
                        functions
                );


                functionMode =
                        false;


                functionName =
                        "";


                continue;
            }


            if (functionMode) {

                functionLines.add(
                        line
                );

            } else {

                main.append(
                        statement(
                                line,
                                2
                        )
                );
            }
        }


        StringBuilder out =
                new StringBuilder();


        /*
         * Imports
         */

        out.append(
                "import java.awt.Color;\n"
        );

        out.append(
                "import java.awt.Dimension;\n"
        );

        out.append(
                "import java.awt.Graphics;\n"
        );

        out.append(
                "import java.awt.Image;\n"
        );

        out.append(
                "import java.awt.Rectangle;\n"
        );

        out.append(
                "import java.awt.event.KeyAdapter;\n"
        );

        out.append(
                "import java.awt.event.KeyEvent;\n"
        );

        out.append(
                "import java.io.File;\n"
        );

        out.append(
                "import java.io.InputStream;\n"
        );

        out.append(
                "import javax.swing.JFrame;\n"
        );

        out.append(
                "import javax.swing.JPanel;\n"
        );

        out.append(
                "import javax.swing.SwingUtilities;\n"
        );

        out.append(
                "import java.util.HashMap;\n"
        );

        out.append(
                "import java.util.HashSet;\n"
        );

        out.append("\n");


        /*
         * Class
         */

        out.append(
                "public class EclipseProgram {\n\n"
        );


        /*
         * Variables
         */

        out.append(
                "    static JFrame window;\n"
        );

        out.append(
                "    static GamePanel panel;\n"
        );

        out.append(
                "    static boolean game = true;\n"
        );

        out.append(
                "    static String title = \"Eclipse\";\n"
        );

        out.append(
                "    static HashMap<String, GameObject> objects "
                + "= new HashMap<>();\n"
        );

        out.append(
                "    static HashSet<Integer> keys "
                + "= new HashSet<>();\n"
        );

        out.append(
                "    static HashMap<String, Integer> vars "
                + "= new HashMap<>();\n\n"
        );


        /*
         * Game object
         */

        out.append("""
    static class GameObject {

        String name;

        int x = 0;
        int y = 0;

        int width = 50;
        int height = 50;

        boolean visible = true;

        GameObject(String name) {
            this.name = name;
        }
    }

""");


        /*
         * Game panel
         */

        out.append("""
    static class GamePanel extends JPanel {

        GamePanel() {

            setFocusable(true);

            addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    keys.add(e.getKeyCode());
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    keys.remove(e.getKeyCode());
                }
            });
        }


        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            g.setColor(Color.BLACK);

            g.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );


            for (GameObject object : objects.values()) {

                if (!object.visible) {
                    continue;
                }


                if (object.name.equals("player")) {

                    g.setColor(Color.CYAN);

                }
                else if (object.name.equals("enemy")) {

                    g.setColor(Color.RED);

                }
                else if (object.name.equals("bullet")) {

                    g.setColor(Color.YELLOW);

                }
                else {

                    g.setColor(Color.WHITE);
                }


                g.fillRect(
                        object.x,
                        object.y,
                        object.width,
                        object.height
                );
            }
        }
    }

""");


        /*
         * Window
         */

        out.append("""
    static void createWindow(
            int width,
            int height
    ) {

        SwingUtilities.invokeLater(() -> {

            window =
                    new JFrame(title);


            /*
             * Eclipse icon.
             *
             * Expected location:
             *
             * Assets/Eclipse.ico
             */

            loadEclipseIcon();


            panel =
                    new GamePanel();


            panel.setPreferredSize(
                    new Dimension(
                            width,
                            height
                    )
            );


            window.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );


            window.add(panel);


            window.pack();


            window.setLocationRelativeTo(
                    null
            );


            window.setVisible(
                    true
            );


            panel.requestFocusInWindow();

        });


        sleep(300);
    }

""");


        /*
         * Icon loader
         */

        out.append("""
    static void loadEclipseIcon() {

        try {

            File iconFile =
                    new File(
                            "Assets/Eclipse.ico"
                    );


            if (!iconFile.exists()) {

                System.out.println(
                        "[Eclipse Warning] "
                        + "Assets/Eclipse.ico not found."
                );

                return;
            }


            /*
             * Java ImageIO does not consistently
             * support ICO files.
             *
             * Try ImageIO first.
             */

            Image icon =
                    ImageIO.read(
                            iconFile
                    );


            if (icon != null) {

                window.setIconImage(
                        icon
                );

                return;
            }


            /*
             * Windows fallback.
             *
             * Uses PowerShell/.NET to convert the
             * exact ICO into a temporary PNG.
             */

            File png =
                    new File(
                            System.getProperty(
                                    "java.io.tmpdir"
                            ),
                            "eclipse-icon.png"
                    );


            String icoPath =
                    iconFile
                            .getAbsolutePath()
                            .replace(
                                    "'",
                                    "''"
                            );


            String pngPath =
                    png
                            .getAbsolutePath()
                            .replace(
                                    "'",
                                    "''"
                            );


            String command =
                    "Add-Type -AssemblyName System.Drawing; "
                    + "$i=[System.Drawing.Icon]::ExtractAssociatedIcon('"
                    + icoPath
                    + "'); "
                    + "$b=$i.ToBitmap(); "
                    + "$b.Save('"
                    + pngPath
                    + "', "
                    + "[System.Drawing.Imaging.ImageFormat]::Png)";


            Process process =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-Command",
                            command
                    )
                    .redirectErrorStream(
                            true
                    )
                    .start();


            process.waitFor();


            if (png.exists()) {

                Image converted =
                        ImageIO.read(
                                png
                        );


                if (converted != null) {

                    window.setIconImage(
                            converted
                    );

                    return;
                }
            }


            System.out.println(
                    "[Eclipse Warning] "
                    + "Unable to decode Eclipse.ico."
            );


        } catch (Exception e) {

            System.out.println(
                    "[Eclipse Warning] "
                    + "Icon error: "
                    + e.getMessage()
            );
        }
    }

""");


        /*
         * Object commands
         */

        out.append("""
    static void createObject(
            String name
    ) {

        objects.put(
                name,
                new GameObject(name)
        );
    }


    static void setX(
            String name,
            int value
    ) {

        GameObject object =
                objects.get(name);

        if (object != null) {
            object.x = value;
        }
    }


    static void setY(
            String name,
            int value
    ) {

        GameObject object =
                objects.get(name);

        if (object != null) {
            object.y = value;
        }
    }


    static void moveX(
            String name,
            int value
    ) {

        GameObject object =
                objects.get(name);

        if (object != null) {
            object.x += value;
        }
    }


    static void moveY(
            String name,
            int value
    ) {

        GameObject object =
                objects.get(name);

        if (object != null) {
            object.y += value;
        }
    }


    static void draw() {

        if (panel != null) {
            panel.repaint();
        }
    }

""");


        /*
         * Keyboard
         */

        out.append("""
    static boolean keyDown(
            String key
    ) {

        switch (key.toUpperCase()) {

            case "LEFT":
                return keys.contains(
                        KeyEvent.VK_LEFT
                );

            case "RIGHT":
                return keys.contains(
                        KeyEvent.VK_RIGHT
                );

            case "UP":
                return keys.contains(
                        KeyEvent.VK_UP
                );

            case "DOWN":
                return keys.contains(
                        KeyEvent.VK_DOWN
                );

            case "SPACE":
                return keys.contains(
                        KeyEvent.VK_SPACE
                );

            case "ENTER":
                return keys.contains(
                        KeyEvent.VK_ENTER
                );

            case "R":
                return keys.contains(
                        KeyEvent.VK_R
                );

            default:
                return false;
        }
    }

""");


        /*
         * Collision
         */

        out.append("""
    static boolean collision(
            String firstName,
            String secondName
    ) {

        GameObject first =
                objects.get(
                        firstName
                );


        GameObject second =
                objects.get(
                        secondName
                );


        if (
                first == null
                ||
                second == null
        ) {

            return false;
        }


        Rectangle firstRect =
                new Rectangle(
                        first.x,
                        first.y,
                        first.width,
                        first.height
                );


        Rectangle secondRect =
                new Rectangle(
                        second.x,
                        second.y,
                        second.width,
                        second.height
                );


        return firstRect.intersects(
                secondRect
        );
    }

""");


        /*
         * Variables
         */

        out.append("""
    static void setVar(
            String name,
            int value
    ) {

        vars.put(
                name,
                value
        );
    }


    static int getVar(
            String name
    ) {

        return vars.getOrDefault(
                name,
                0
        );
    }


    static void sleep(
            long milliseconds
    ) {

        try {

            Thread.sleep(
                    milliseconds
            );

        }
        catch (InterruptedException ignored) {

        }
    }

""");


        /*
         * Functions
         */

        out.append(
                functions
        );


        /*
         * Main
         */

        out.append(
                "    public static void main("
                + "String[] args"
                + ") throws Exception {\n\n"
        );


        out.append(
                main
        );


        out.append(
                "    }\n"
        );


        out.append(
                "}\n"
        );


        return out.toString();
    }


    static void compileFunction(
            String name,
            List<String> lines,
            StringBuilder output
    ) {

        output.append(
                "    static void "
                + name
                + "() throws Exception {\n"
        );


        for (String line : lines) {

            output.append(
                    statement(
                            line,
                            2
                    )
            );
        }


        output.append(
                "    }\n\n"
        );
    }


    static String statement(
            String line,
            int indent
    ) {

        String spaces =
                "    ".repeat(
                        indent
                );


        /*
         * console.log()
         */

        if (
                line.startsWith(
                        "console.log("
                )
        ) {

            String value =
                    inside(line);


            return spaces
                    + "System.out.println("
                    + value
                    + ");\n";
        }


        /*
         * title
         */

        if (
                line.startsWith(
                        "title ="
                )
        ) {

            String value =
                    line.substring(
                            7
                    ).trim();


            return spaces
                    + "title = "
                    + value
                    + ";\n";
        }


        /*
         * create window
         */

        if (
                line.startsWith(
                        "create window("
                )
        ) {

            String value =
                    inside(line);


            String[] parts =
                    value.split(",");


            if (
                    parts.length == 2
            ) {

                return spaces
                        + "createWindow("
                        + parts[0].trim()
                        + ", "
                        + parts[1].trim()
                        + ");\n";
            }


            return "";
        }


        /*
         * game
         */

        if (
                line.startsWith(
                        "game ="
                )
        ) {

            String value =
                    line.substring(
                            6
                    ).trim();


            return spaces
                    + "game = "
                    + value
                    + ";\n";
        }


        /*
         * obj
         */

        if (
                line.startsWith(
                        "obj "
                )
        ) {

            String name =
                    line.substring(
                            4
                    ).trim();


            return spaces
                    + "createObject(\""
                    + name
                    + "\");\n";
        }


        /*
         * set object x/y
         */

        if (
                line.startsWith(
                        "set "
                )
        ) {

            String[] parts =
                    line.split(
                            "\\s+"
                    );


            if (
                    parts.length >= 4
            ) {

                String object =
                        parts[1];

                String axis =
                        parts[2];

                String value =
                        parts[3];


                if (
                        axis.equalsIgnoreCase(
                                "x"
                        )
                ) {

                    return spaces
                            + "setX(\""
                            + object
                            + "\", "
                            + value
                            + ");\n";
                }


                if (
                        axis.equalsIgnoreCase(
                                "y"
                        )
                ) {

                    return spaces
                            + "setY(\""
                            + object
                            + "\", "
                            + value
                            + ");\n";
                }
            }


            return "";
        }


        /*
         * move object x/y
         */

        if (
                line.startsWith(
                        "move "
                )
        ) {

            String[] parts =
                    line.split(
                            "\\s+"
                    );


            if (
                    parts.length >= 4
            ) {

                String object =
                        parts[1];

                String axis =
                        parts[2];

                String value =
                        parts[3];


                if (
                        axis.equalsIgnoreCase(
                                "x"
                        )
                ) {

                    return spaces
                            + "moveX(\""
                            + object
                            + "\", "
                            + value
                            + ");\n";
                }


                if (
                        axis.equalsIgnoreCase(
                                "y"
                        )
                ) {

                    return spaces
                            + "moveY(\""
                            + object
                            + "\", "
                            + value
                            + ");\n";
                }
            }


            return "";
        }


        /*
         * draw object
         */

        if (
                line.startsWith(
                        "draw "
                )
        ) {

            return spaces
                    + "draw();\n";
        }


        /*
         * keydown
         */

        if (
                line.startsWith(
                        "if keydown("
                )
                &&
                line.endsWith(
                        "then"
                )
        ) {

            String key =
                    inside(
                            line.substring(
                                    3,
                                    line.length() - 5
                            )
                    );


            return spaces
                    + "if (keyDown("
                    + key
                    + ")) {\n";
        }


        /*
         * collision
         */

        if (
                line.startsWith(
                        "if collision("
                )
                &&
                line.endsWith(
                        "then"
                )
        ) {

            String value =
                    inside(
                            line.substring(
                                    3,
                                    line.length() - 5
                            )
                    );


            String[] parts =
                    value.split(",");


            if (
                    parts.length == 2
            ) {

                return spaces
                        + "if (collision(\""
                        + parts[0].trim()
                        + "\", \""
                        + parts[1].trim()
                        + "\")) {\n";
            }


            return "";
        }


        /*
         * while
         *
         * while [game] = true
         */

        if (
                line.startsWith(
                        "while "
                )
        ) {

            String condition =
                    line.substring(
                            6
                    ).trim();


            condition =
                    convertCondition(
                            condition
                    );


            return spaces
                    + "while ("
                    + condition
                    + ") {\n";
        }


        /*
         * normal if
         */

        if (
                line.startsWith(
                        "if "
                )
                &&
                line.endsWith(
                        "then"
                )
        ) {

            String condition =
                    line.substring(
                            3,
                            line.length() - 5
                    ).trim();


            condition =
                    convertCondition(
                            condition
                    );


            return spaces
                    + "if ("
                    + condition
                    + ") {\n";
        }


        /*
         * end
         */

        if (
                line.equals(
                        "end"
                )
        ) {

            return spaces
                    + "}\n";
        }


        /*
         * function call
         *
         * Example:
         *
         * shoot()
         */

        if (
                line.matches(
                        "[A-Za-z_][A-Za-z0-9_]*\\(\\)"
                )
        ) {

            return spaces
                    + line
                    + ";\n";
        }


        /*
         * Unknown command
         */

        System.out.println(
                "[Eclipse Warning] Unknown command: "
                + line
        );


        return "";
    }


    static String convertCondition(
            String condition
    ) {

        condition =
                condition.replace(
                        "[game]",
                        "game"
                );


        condition =
                condition.replace(
                        " = ",
                        " == "
                );


        return condition;
    }


    static String inside(
            String text
    ) {

        int start =
                text.indexOf(
                        "("
                );


        int end =
                text.lastIndexOf(
                        ")"
                );


        if (
                start == -1
                ||
                end == -1
        ) {

            return "";
        }


        return text.substring(
                start + 1,
                end
        ).trim();
    }


    static void error(
            String message
    ) {

        System.out.println(
                "[Eclipse Error] "
                + message
        );
    }
}
