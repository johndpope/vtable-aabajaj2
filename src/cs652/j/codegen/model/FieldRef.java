package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class FieldRef extends Expr {
    public String fieldName;
    @ModelElement public Expr object;

    public FieldRef(String fieldName, Expr object) {
        this.fieldName = fieldName;
        this.object = object;
    }
}
