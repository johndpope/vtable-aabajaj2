package cs652.j.codegen.model;

public abstract class TypeSpec extends OutputModelObject {
    public String type;
    public TypeSpec(String t){
        this.type = t;
    }
}
