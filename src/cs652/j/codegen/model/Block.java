package cs652.j.codegen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjani Bajaj on 4/1/2017.
 */
public class Block extends OutputModelObject {
    @ModelElement
    public
    List<OutputModelObject> locals = new ArrayList<>();
    public @ModelElement List<OutputModelObject> instrs = new ArrayList<>();
}
