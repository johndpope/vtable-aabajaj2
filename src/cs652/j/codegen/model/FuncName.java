package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/4/2017.
 */
public class FuncName extends OutputModelObject {
    public  String className;
    public  String methodName;
    public int slotNumber;
    public String getName(){
        return this.className+"_"+this.methodName;
    }

    public FuncName(String className, String methodName, int slotNumber) {
        this.className = className;
        this.methodName = methodName;
        this.slotNumber = slotNumber;
    }
}
