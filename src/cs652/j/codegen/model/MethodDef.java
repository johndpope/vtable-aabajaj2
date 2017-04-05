package cs652.j.codegen.model;

import java.util.List;

/**
 * Created by Anjani Bajaj on 4/4/2017.
 */
public class MethodDef extends OutputModelObject {
    @ModelElement String args;
    @ModelElement Block body;
}
