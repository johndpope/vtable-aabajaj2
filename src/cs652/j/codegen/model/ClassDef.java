package cs652.j.codegen.model;

import cs652.j.semantics.JClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 3/31/2017.
 */
public class ClassDef extends OutputModelObject{
    public JClass clazz;
    @ModelElement public List<VarDef> fields = new ArrayList<>();
    @ModelElement public List<MethodDef> methods = new ArrayList<>();
    @ModelElement public List<FuncName> vtable = new ArrayList<>();

    public ClassDef(JClass jClass){ this.clazz = jClass;}

    public String getName(){return clazz.getName();}

}
