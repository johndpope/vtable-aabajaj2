package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 4/4/2017.
 */
public class MethodDef extends OutputModelObject {
    public @ModelElement List<VarDef> args = new ArrayList<>();
    public @ModelElement Block body;
    public @ModelElement FuncName funcName;
    public @ModelElement TypeSpec returnType;
}
