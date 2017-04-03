package cs652.j.codegen;

import cs652.j.codegen.model.*;
import cs652.j.parser.JBaseVisitor;
import cs652.j.parser.JParser;
import cs652.j.semantics.JClass;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class CodeGenerator extends JBaseVisitor<OutputModelObject> {
	public STGroup templates;
	public String fileName;

	public Scope currentScope;
	public JClass currentClass;

	public CodeGenerator(String fileName) {
		this.fileName = fileName;
		templates = new STGroupFile("cs652/j/templates/C.stg");
	}

	public CFile generate(ParserRuleContext tree) {
		CFile file = (CFile)visit(tree);
		return file;
	}

	@Override
	public OutputModelObject visitFile(JParser.FileContext ctx) {
		MainMethod main = (MainMethod) visit(ctx.main());
		CFile file = new CFile(fileName);
        file.main = main;
		return file;
	}

	@Override
	public OutputModelObject visitMain(JParser.MainContext ctx) {
		MainMethod mainMethod = new MainMethod();
        mainMethod.body = (Block) visit(ctx.block());
        return mainMethod;
	}

	@Override
	public OutputModelObject visitBlock(JParser.BlockContext ctx) {
        Block block = new Block();
        for (JParser.StatementContext stat : ctx.statement()) {
            OutputModelObject smt = visit(stat);
            block.locals.add(smt);
        }
        return block;
    }

    @Override
    public OutputModelObject visitLocalVarStat(JParser.LocalVarStatContext ctx) {
        return visitLocalVariableDeclaration(ctx.localVariableDeclaration());
    }

    @Override
    public OutputModelObject visitLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {
        VarDef varDef = new VarDef(ctx.jType().getText(), ctx.ID().getText());
        return varDef;
    }

    @Override
    public OutputModelObject visitPrintStringStat(JParser.PrintStringStatContext ctx) {
        PrintStringStat pss = new PrintStringStat(ctx.STRING().getText());
        return pss;
    }

    @Override
    public OutputModelObject visitPrintStat(JParser.PrintStatContext ctx) {
        PrintStat ps = new PrintStat(ctx.STRING().getText());
        for (JParser.ExpressionContext e : ctx.expressionList().expression()) {
            OutputModelObject args = visit(e);
            ps.args.add(args);
        }
        return ps;
    }

    @Override
    public OutputModelObject visitAssignStat(JParser.AssignStatContext ctx) {
        AssignStat as = new AssignStat(ctx.expression(0).getText(),ctx.expression(1).getText());
//        as.left =  ctx.expression(0).getText();
//        System.out.println("Visiting tree = "+ctx.expression(1).getText());
//        as.right = ctx.expression(1).getText();
        return as;
    }

    @Override
    public OutputModelObject visitIdRef(JParser.IdRefContext ctx) {
        VarRef vr = new VarRef(ctx.ID().getText());
        return vr;
    }

    @Override
    public OutputModelObject visitLiteralRef(JParser.LiteralRefContext ctx) {
        LiteralRef lr = new LiteralRef(ctx.INT().getText());
        return  lr;
    }

    @Override
    public OutputModelObject visitIfStat(JParser.IfStatContext ctx) {
        IfStat ifs = new IfStat();
        ifs.condition = ctx.parExpression().getText();
        for (JParser.StatementContext stat : ctx.statement()) {
            OutputModelObject smt = visit(stat);
            ifs.stat.add(smt);
        }
        return ifs;
    }

    @Override
    public OutputModelObject visitReturnStat(JParser.ReturnStatContext ctx) {
        ReturnStat returnStat = new ReturnStat();
        returnStat.e = visit(ctx.expression());
        System.out.println("Return expression = "+ctx.expression());
        return returnStat;
    }
//    @Override
//    public OutputModelObject visitParExpression(JParser.ParExpressionContext ctx) {
//
//        return
//    }
}