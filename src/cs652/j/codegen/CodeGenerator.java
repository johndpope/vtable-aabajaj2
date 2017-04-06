package cs652.j.codegen;

import cs652.j.codegen.model.*;
import cs652.j.parser.JBaseVisitor;
import cs652.j.parser.JParser;
import cs652.j.semantics.JClass;
import cs652.j.semantics.JPrimitiveType;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.lang.reflect.Method;

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
        for (JParser.ClassDeclarationContext c : ctx.classDeclaration()) {
            ClassDef clazz = (ClassDef) visit(c);
            file.classes.add(clazz);
        }
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
        VarDef varDef;//= new VarDef((TypeSpec) visit(ctx.jType()), ctx.ID().getText());
        System.out.println("VarDef= "+ctx.jType().getText());
        TypeSpec t;
        String typename = ctx.jType().getText();
        if ( isClassName(typename) ) {
            t =  new ObjectTypeSpec(typename);
        }
        else {
            t = new PrimitiveTypeSpec(typename);
        }
        varDef = new VarDef(t,ctx.ID().getText());
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
//        System.out.println("Expr = "+ctx.expression(0).getText());
        AssignStat as = new AssignStat((Expr) visit(ctx.expression(0)),(Expr) visit(ctx.expression(1)));
        return as;
    }

    @Override
    public OutputModelObject visitIdRef(JParser.IdRefContext ctx) {
        VarRef vr = new VarRef(ctx.ID().getText());
        return vr;
    }

    @Override
    public OutputModelObject visitLiteralRef(JParser.LiteralRefContext ctx) {
        LiteralRef lr = new LiteralRef(ctx.getText());
        return  lr;
    }

    @Override
    public OutputModelObject visitIfStat(JParser.IfStatContext ctx) {
        IfStat ifs = new IfStat();
        IfElseStat ifElseStat = new IfElseStat();
        ifs.condition = ctx.parExpression().getText();
        for (JParser.StatementContext stat : ctx.statement()) {
            OutputModelObject smt = visit(stat);
            ifs.stat.add(smt);
        }
        if(ctx.statement(1) == null) {
            return ifs;
        }else {
            ifElseStat.condition = ctx.parExpression().getText();
            OutputModelObject smt = visit(ctx.statement(0));
            ifElseStat.stat.add(smt);
            OutputModelObject smt2 = visit(ctx.statement(1));
            ifElseStat.elseStat.add(smt2);
            return ifElseStat;
        }
    }

    @Override
    public OutputModelObject visitReturnStat(JParser.ReturnStatContext ctx) {
        ReturnStat returnStat = new ReturnStat();
        returnStat.e = visit(ctx.expression());
        return returnStat;
    }

    @Override
    public OutputModelObject visitWhileStat(JParser.WhileStatContext ctx) {
        WhileStat whileStat = new WhileStat();
        whileStat.condition = ctx.parExpression().getText();
        whileStat.stat = ctx.statement().getText();
        return whileStat;
    }

    @Override
    public OutputModelObject visitCtorCall(JParser.CtorCallContext ctx) {
        CtorCall ctorCall = new CtorCall();
        ctorCall.name = ctx.ID().getText();
        return ctorCall;
    }

    @Override
    public OutputModelObject visitMethodDeclaration(JParser.MethodDeclarationContext ctx) {
        MethodDef methodDef = new MethodDef();
        FuncName funcName = new FuncName();
        funcName.slotNumber = ctx.scope.getSlotNumber();
//        funcName.name = ctx.scope.getEnclosingScope().getName()+"_"+ctx.scope.getName();
//        System.out.println("Funcname  = "+funcName.name);
//        methodDef.args = visit(ctx.getText())
        return methodDef;
    }

    @Override
    public OutputModelObject visitClassDeclaration(JParser.ClassDeclarationContext ctx) {
        ClassDef classDef = new ClassDef(ctx.scope);
        currentScope = ctx.scope;
        for (JParser.ClassBodyDeclarationContext c : ctx.classBody().classBodyDeclaration()){
            MethodDef methodDef;
            methodDef = (MethodDef) visit(c);
            classDef.methods.add(methodDef);
        }
        for(MethodSymbol m : ctx.scope.getMethods()){
            FuncName funcName = new FuncName();
            funcName.methodName = m.getName();
            funcName.className = ctx.scope.resolve(m.getEnclosingScope().getName()).getName();
            if(!classDef.vtable.contains(funcName)){
                classDef.vtable.add(funcName);
                classDef.define.add("#define "+ funcName.className+"_"+funcName.methodName +"_SLOT "+String.valueOf(m.getSlotNumber()));
            }
        }

        return classDef;
    }
    public boolean isClassName(String typename) {
        return Character.isUpperCase(typename.charAt(0));
    }
}