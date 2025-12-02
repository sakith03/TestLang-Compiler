package ast;

public class Assertion implements Statement {
    public String type; // "status", "header", "body"
    public String operator; // "equals", "contains"
    public String key; // for header assertions
    public Object expected; // Integer for status, String for others

    public Assertion(String type, String operator, String key, Object expected) {
        this.type = type;
        this.operator = operator;
        this.key = key;
        this.expected = expected;
    }
}