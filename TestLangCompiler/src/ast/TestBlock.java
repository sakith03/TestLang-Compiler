package ast;

import java.util.ArrayList;
import java.util.List;

public class TestBlock {

    public String name;
    public List<Statement> statements;
    public boolean active;
    public String description;


    public TestBlock(String name, boolean active, String description, List<Statement> statements) {
        this.name = name;
        this.statements = statements != null ? statements : new ArrayList<>();
        this.description = description;
        this.active = active;
    }

    public TestBlock(String name, String description, List<Statement> statements) {
        this(name, false, description, statements);
    }

    public TestBlock(String name, boolean active, List<Statement> statements) {
        this(name, active, "", statements);
    }

    public TestBlock(String name, List<Statement> statements) {
        this(name, false, "", statements);
    }
}
