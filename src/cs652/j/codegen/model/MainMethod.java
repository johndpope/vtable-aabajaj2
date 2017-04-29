package cs652.j.codegen.model;

import org.antlr.symtab.Scope;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 3/31/2017.
 */
public class MainMethod extends OutputModelObject{
    public @ModelElement
    Block body;
    public @ModelElement
    String returnType;
    public @ModelElement
    String funcName;
    public @ModelElement
    String args;

    public MainMethod(Block body) {
        this.body = body;
    }
}
