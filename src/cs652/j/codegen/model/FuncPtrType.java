package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 4/5/2017.
 */
public class FuncPtrType extends OutputModelObject {
    public @ModelElement TypeSpec returnType;
    public @ModelElement List<TypeSpec> argTypes = new ArrayList<>();
}
