package codegen;

import ast.*;
import java.util.*;

public class CodeGenerator {
    private Program program;
    private Map<String, Object> variables;

    public CodeGenerator(Program program) {
        this.program = program;
        this.variables = new HashMap<>();
        // Load variables from program
        for (Variable var : program.variables) {
            variables.put(var.name, var.value);
        }
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();

        // ------------------- Imports -------------------
        sb.append("import org.junit.jupiter.api.*;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n");
        sb.append("import java.net.http.*;\n");
        sb.append("import java.net.*;\n");
        sb.append("import java.time.Duration;\n");
        sb.append("import java.nio.charset.StandardCharsets;\n");
        sb.append("import java.util.*;\n\n");

        // ------------------- Class declaration -------------------
        sb.append("public class GeneratedTests {\n");

        // Static fields
        String baseUrl = (program.config != null && program.config.baseUrl != null)
                ? program.config.baseUrl : "";
        sb.append("  static String BASE = \"").append(baseUrl).append("\";\n");
        sb.append("  static Map<String,String> DEFAULT_HEADERS = new HashMap<>();\n");
        sb.append("  static HttpClient client;\n\n");

        // Setup method
        sb.append("  @BeforeAll\n");
        sb.append("  static void setup() {\n");
        sb.append("    client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();\n");
        if (program.config != null) {
            for (Map.Entry<String, String> header : program.config.headers.entrySet()) {
                sb.append("    DEFAULT_HEADERS.put(\"").append(header.getKey())
                        .append("\",\"").append(header.getValue()).append("\");\n");
            }
        }
        sb.append("  }\n\n");

        // ------------------- Test methods -------------------
        for (TestBlock test : program.tests) {
            generateTest(sb, test);
        }

        sb.append("}\n");
        return sb.toString();
    }

    private void generateTest(StringBuilder sb, TestBlock test) {

        if (test.description != null && !test.description.isEmpty()) {
            sb.append("  @Description(\"").append(test.description).append("\")\n");
        }
        if (!test.active) {
            sb.append("  @Disabled(\"Test marked as inactive\")\n");
        }

        sb.append("  @Test\n");
        sb.append("  void test_").append(test.name).append("() throws Exception {\n");

        for (Statement stmt : test.statements) {
            if (stmt instanceof HttpRequest) {
                generateRequest(sb, (HttpRequest) stmt);
            } else if (stmt instanceof Assertion) {
                generateAssertion(sb, (Assertion) stmt);
            }
        }

        sb.append("  }\n\n");
    }

    private void generateRequest(StringBuilder sb, HttpRequest req) {
        String path = substituteVariables(req.path);
        String url = (path.startsWith("/") && program.config != null && program.config.baseUrl != null)
                ? "BASE + \"" + path + "\""
                : "\"" + path + "\"";

        sb.append("    HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(").append(url).append("))\n");
        sb.append("      .timeout(Duration.ofSeconds(10))");

        // Method and body
        switch (req.method) {
            case "GET": sb.append("\n      .GET()"); break;
            case "DELETE": sb.append("\n      .DELETE()"); break;
            case "POST":
            case "PUT":
                if (req.body != null) {
                    String body = substituteVariables(req.body);
                    sb.append("\n      .").append(req.method)
                            .append("(HttpRequest.BodyPublishers.ofString(\"")
                            .append(body)
                            .append("\"))");
                } else {
                    sb.append("\n      .").append(req.method)
                            .append("(HttpRequest.BodyPublishers.noBody())");
                }
                break;
        }
        sb.append(";\n");

        // Add request-specific headers
        if (req.headers != null && !req.headers.isEmpty()) {
            for (Map.Entry<String, String> h : req.headers.entrySet()) {
                sb.append("    b.header(\"").append(h.getKey()).append("\", \"")
                        .append(h.getValue()).append("\");\n");
            }
        }

        // Add default headers
        sb.append("    for (var e: DEFAULT_HEADERS.entrySet()) b.header(e.getKey(), e.getValue());\n");

        sb.append("    HttpResponse<String> resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));\n\n");
    }

    private void generateAssertion(StringBuilder sb, Assertion assertion) {
        switch (assertion.type) {
            case "status":
                if ("equals".equals(assertion.operator)) {
                    sb.append("    assertEquals(").append(assertion.expected)
                            .append(", resp.statusCode());\n");
                } else if ("in_range".equals(assertion.operator)) {     // Extra feature range check
                    // Parse range like "200..299"
                    String range = (String) assertion.expected;
                    String[] parts = range.split("\\.\\.");
                    int start = Integer.parseInt(parts[0]);
                    int end = Integer.parseInt(parts[1]);
                    sb.append("    assertTrue(resp.statusCode() >= ").append(start)
                            .append(" && resp.statusCode() <= ").append(end)
                            .append(", \"Expected status in range ").append(start)
                            .append("..").append(end).append("\");\n");
                }
                break;
            case "header":
                if ("equals".equals(assertion.operator)) {
                    sb.append("    assertEquals(\"").append(assertion.expected)
                            .append("\", resp.headers().firstValue(\"").append(assertion.key)
                            .append("\").orElse(\"\"));\n");
                } else if ("contains".equals(assertion.operator)) {
                    sb.append("    assertTrue(resp.headers().firstValue(\"").append(assertion.key)
                            .append("\").orElse(\"\").contains(\"").append(assertion.expected).append("\"));\n");
                }
                break;
            case "body":
                if ("contains".equals(assertion.operator)) {
                    // Remove spaces from both expected and actual body for matching
                    sb.append("    assertTrue(resp.body().replace(\" \", \"\").contains(\"")
                            .append(assertion.expected.toString().replace(" ", ""))
                            .append("\"));\n");
                }
                break;
        }
    }

    // ------------------- Helpers -------------------
    private String substituteVariables(String text) {
        String result = text;
        for (Map.Entry<String, Object> var : variables.entrySet()) {
            result = result.replace("$" + var.getKey(), var.getValue().toString());
        }
        return result;
    }
}
