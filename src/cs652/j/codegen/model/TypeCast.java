package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/7/2017.
 */
public class TypeCast extends Expr {
    public @ModelElement TypeSpec typeSpec;
    public @ModelElement Expr expr;
}
