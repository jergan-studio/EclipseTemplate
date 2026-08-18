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

        Path ecsFile = Paths.get(args[0]);

        if (!Files.exists(ecsFile)) {
            error("File does not exist: " + ecsFile);
            return;
        }

        if (!ecsFile.toString().toLowerCase().endsWith(".ecs")) {
            error("Eclipse files must use the .ecs extension.");
            return;
        }

        try {
            List<String> source = Files.readAllLines(ecsFile);

            String javaCode = compile(source);

            Path outputDirectory = ecsFile.toAbsolutePath()
                    .getParent()
                    .resolve(".eclipse-build");

            Files.createDirectories(outputDirectory);

            Path javaFile = outputDirectory.resolve(GENERATED_CLASS + ".java");

            Files.writeString(javaFile, javaCode);

            System.out.println("Eclipse: " + ecsFile.getFileName());
            System.out.println("Generating Java...");
            System.out.println("Generated: " + javaFile);

            Process javac = new ProcessBuilder(
                    "javac",
                    javaFile.toString()
            )
                    .inheritIO()
                    .start();

            int compileResult = javac.waitFor();

            if (compileResult != 0) {
                error("Java compilation failed.");
                return;
            }

            System.out.println("Running Eclipse program...");

            Process java = new ProcessBuilder(
                    "java",
                    "-cp",
                    outputDirectory.toString(),
                    GENERATED_CLASS
            )
                    .inheritIO()
                    .start();

            int result = java.waitFor();

            if (result != 0) {
                error("Program exited with code " + result);
            }

        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    static String compile(List<String> source) {
        StringBuilder java = new StringBuilder();

        java.append("import java.io.*;\n");
        java.append("import java.util.*;\n");
        java.append("import javax.swing.*;\n");
        java.append("import java.awt.*;\n");
        java.append("\n");

        java.append("public class ").append(GENERATED_CLASS).append(" {\n");

        java.append("    public static void main(String[] args) throws Exception {\n");

        compileLines(source, java, 2);

        java.append("    }\n");
        java.append("}\n");

        return java.toString();
    }

    static void compileLines(
            List<String> lines,
            StringBuilder java,
            int indent
    ) {
        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i).trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("#")) {
                continue;
            }

            /*
             * console.log("Hello")
             */
            if (line.startsWith("console.log(")) {

                String value = getInsideParentheses(line);

                java.append(spaces(indent))
                        .append("System.out.println(")
                        .append(convertExpression(value))
                        .append(");\n");

                continue;
            }

            /*
             * x = 10
             * name = "Eclipse"
             */
            if (line.matches(
                    "[A-Za-z_][A-Za-z0-9_]*\\s*=\\s*.+"
            )) {

                String[] parts = line.split("=", 2);

                String name = parts[0].trim();
                String value = parts[1].trim();

                java.append(spaces(indent))
                        .append("var ")
                        .append(name)
                        .append(" = ")
                        .append(convertExpression(value))
                        .append(";\n");

                continue;
            }

            /*
             * if [x] = 3 then
             */
            if (line.startsWith("if ") && line.endsWith(" then")) {

                String condition = line
                        .substring(3, line.length() - 5)
                        .trim();

                java.append(spaces(indent))
                        .append("if (")
                        .append(convertCondition(condition))
                        .append(") {\n");

                i = compileBlock(lines, i + 1, java, indent + 1);

                java.append(spaces(indent))
                        .append("}\n");

                continue;
            }

            /*
             * repeat 5
             */
            if (line.startsWith("repeat ")) {

                String amount = line
                        .substring(7)
                        .trim();

                java.append(spaces(indent))
                        .append("for (int eclipse_i = 0; eclipse_i < ")
                        .append(convertExpression(amount))
                        .append("; eclipse_i++) {\n");

                i = compileBlock(lines, i + 1, java, indent + 1);

                java.append(spaces(indent))
                        .append("}\n");

                continue;
            }

            /*
             * openapp file("program.exe")
             */
            if (line.startsWith("openapp")) {

                Matcher matcher = Pattern.compile(
                        "openapp\\s+file\\((.*)\\)"
                ).matcher(line);

                if (matcher.matches()) {

                    String file = convertExpression(
                            matcher.group(1)
                    );

                    java.append(spaces(indent))
                            .append("new ProcessBuilder(")
                            .append(file)
                            .append(").start();\n");

                } else {
                    error("Invalid openapp command: " + line);
                }

                continue;
            }

            /*
             * create file("index.html")
             */
            if (line.startsWith("create file(")) {

                String file = getInsideParentheses(line);

                java.append(spaces(indent))
                        .append("new File(")
                        .append(convertExpression(file))
                        .append(").createNewFile();\n");

                continue;
            }

            /*
             * Unknown command
             */
            error("Unknown Eclipse command: " + line);
        }
    }

    static int compileBlock(
            List<String> lines,
            int start,
            StringBuilder java,
            int indent
    ) {
        List<String> block = new ArrayList<>();

        int i = start;

        while (i < lines.size()) {

            String line = lines.get(i).trim();

            if (line.equals("end")) {
                break;
            }

            block.add(lines.get(i));

            i++;
        }

        compileLines(block, java, indent);

        return i;
    }

    static String convertCondition(String condition) {

        condition = condition.replace("[", "");
        condition = condition.replace("]", "");

        condition = condition.replace("==", "==");
        condition = condition.replace("=", "==");

        return condition;
    }

    static String convertExpression(String value) {

        value = value.trim();

        /*
         * Eclipse variable:
         * [x]
         */
        if (value.matches("\\[[A-Za-z_][A-Za-z0-9_]*\\]")) {
            return value.substring(1, value.length() - 1);
        }

        return value;
    }

    static String getInsideParentheses(String line) {

        int start = line.indexOf("(");
        int end = line.lastIndexOf(")");

        if (start == -1 || end == -1) {
            return "";
        }

        return line.substring(start + 1, end);
    }

    static String spaces(int amount) {
        return "    ".repeat(amount);
    }

    static void error(String message) {
        System.err.println("[Eclipse Error] " + message);
    }
}
