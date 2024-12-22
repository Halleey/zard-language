package editor.globals;

public class GlobalClass {
    boolean foundReturn;
    boolean whileFunctionEnd;


    public void setFoundReturn(boolean foundReturn) {
        this.foundReturn = foundReturn;
    }

    public boolean isFoundReturn() {
        return foundReturn;
    }

    public boolean isFunctionWhile() {return whileFunctionEnd; }

    public void setFunctionWhile(boolean whileFunctionEnd) {
        this.whileFunctionEnd = whileFunctionEnd;
    }



}
