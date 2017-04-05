package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class LiteralRef extends Expr {
    public String id;
    public LiteralRef(String text){
        this.id = text;
    }
}
