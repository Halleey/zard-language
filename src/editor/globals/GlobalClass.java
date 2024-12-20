package editor.globals;

public class GlobalClass {
    boolean foundReturn;
    boolean functionWhile;
    public void setFoundReturn(boolean foundReturn) {
        this.foundReturn = foundReturn;
    }

    public boolean isFoundReturn() {
        return foundReturn;
    }

    public boolean isFunctionWhile() {return functionWhile; }

    public void setFunctionWhile(boolean functionWhile) {
        this.functionWhile = functionWhile;
    }
}
