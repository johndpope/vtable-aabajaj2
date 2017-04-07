package cs652.j.codegen.model;

import com.sun.xml.internal.bind.v2.util.TypeCast;

import java.util.List;

/**
 * Created by Anjani Bajaj on 4/5/2017.
 */
public class MethodCall extends Expr {
    public @ModelElement VarRef receiver;
    public @ModelElement
    TypeCast receiverType;
    public @ModelElement FuncPtrType fptrType;
    public @ModelElement
    List<VarRef> args;

}
