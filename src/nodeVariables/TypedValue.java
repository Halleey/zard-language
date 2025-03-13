package nodeVariables;

public class TypedValue {
    private String type;
    private Object value;

    public TypedValue(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object newValue) {
        this.value = newValue;
    }
}