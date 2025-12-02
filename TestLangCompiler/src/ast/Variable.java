package ast;

public class Variable {
    public String name;
    public Object value; // String or Integer

    public Variable(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
