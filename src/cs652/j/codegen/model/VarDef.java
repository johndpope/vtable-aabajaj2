package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/1/2017.
 */
public class VarDef extends OutputModelObject {
//    @ModelElement
    @ModelElement public TypeSpec type;
    public String id;
    public VarDef (TypeSpec t, String i){
        type = t;
        id = i;
    }
}
