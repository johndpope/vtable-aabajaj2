package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class AssignStat extends Stat {
    //Stat a = new AssignStat();
    public @ModelElement
    String left;
    public @ModelElement
    String right;
}
