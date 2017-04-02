package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/1/2017.
 */
public class VarDef extends OutputModelObject {
//    @ModelElement
    public String type, id;
    public VarDef (String t, String i){
        type = t;
        id = i;
    }
}
