package ast;

import java.util.*;

public class Program {
    public Config config;
    public List<Variable> variables;
    public List<TestBlock> tests;

    public Program(Config config, List<Variable> variables, List<TestBlock> tests) {
        this.config = config;
        this.variables = variables != null ? variables : new ArrayList<>();
        this.tests = tests != null ? tests : new ArrayList<>();
    }
}