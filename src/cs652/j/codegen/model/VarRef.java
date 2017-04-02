package cs652.j.codegen.model;

import org.antlr.symtab.VariableSymbol;

/**
 * Created by Anjani Bajaj on 4/1/2017.
 */
public class VarRef extends Expr {
    @ModelElement String id;
    public  VarRef(String text){
        id =text;
    }
}
