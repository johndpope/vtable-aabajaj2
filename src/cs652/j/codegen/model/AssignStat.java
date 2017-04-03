package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class AssignStat extends Stat {
    //Stat a = new AssignStat();
    public String left;
    public String right;
    public AssignStat(String l, String r){
        this.left = l;
        this.right = r;
    }
}
