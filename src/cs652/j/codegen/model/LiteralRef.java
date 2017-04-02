package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class LiteralRef extends Expr {
    @ModelElement String lit;
    public LiteralRef(String text){
        lit = text;
    }
}
