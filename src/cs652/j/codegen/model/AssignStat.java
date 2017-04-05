package cs652.j.codegen.model;

/**
 * Created by Anjani Bajaj on 4/2/2017.
 */
public class AssignStat extends Stat {
    //Stat a = new AssignStat();
    public @ModelElement Expr left;
    public @ModelElement Expr right;
    public AssignStat(Expr l, Expr r){
        this.left = l;
        this.right = r;
    }
}
