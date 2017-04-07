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
    public OutputModelObject visitFieldRef(JParser.FieldRefContext ctx) {
        FieldRef fieldRef = new FieldRef();
        System.out.println("In fieldRef="+ctx.expression().getText());
        fieldRef.fieldName = ctx.ID().getText();

        System.out.println("Classname= "+currentScope.getEnclosingScope().getName());
//        if(ctx)
//        fieldRef.object = currentClass.resolve(ctx)
        return fieldRef;
    }

    @Override
	public OutputModelObject visitFile(JParser.FileContext ctx) {
		MainMethod main = (MainMethod) visit(ctx.main());
		CFile file = new CFile(fileName);
        currentScope = ctx.scope;
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
        currentScope = ctx.scope;
        mainMethod.body = (Block) visit(ctx.block());
        return mainMethod;
	}

	@Override
	public OutputModelObject visitBlock(JParser.BlockContext ctx) {
        Block block = new Block();
        currentScope = ctx.scope;
        for (JParser.StatementContext stat : ctx.statement()) {
            if(visit(stat) instanceof VarDef) {
                OutputModelObject smt = visit(stat);
                block.locals.add(smt);
            }else {
                OutputModelObject smt = visit(stat);
                block.instrs.add(smt);
            }
        }
        currentScope = currentScope.getEnclosingScope();
        return block;
    }

    @Override
    public OutputModelObject visitLocalVarStat(JParser.LocalVarStatContext ctx) {
        return visitLocalVariableDeclaration(ctx.localVariableDeclaration());
    }

    @Override
    public OutputModelObject visitLocalVariableDeclaration(JParser.LocalVariableDeclarationContext ctx) {
        VarDef varDef;
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
        if (ctx.expression()!=null) {
            returnStat.expr = (Expr) visit(ctx.expression());
            return returnStat;
        }else {

            return returnStat;
        }
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
        currentScope = ctx.scope;
        String typename;
        FuncName funcName = new FuncName();
        funcName.className = ctx.scope.getEnclosingScope().getName();
        funcName.methodName = ctx.ID().getText();
        if (ctx.jType()!=null) {
            typename = ctx.jType().getText();
        }else {
            typename = "void";
        }
        if (isClassName(typename)) {
            methodDef.returnType = new ObjectTypeSpec(typename);
        } else {
            methodDef.returnType = new PrimitiveTypeSpec(typename);
        }
        methodDef.body = (Block) visit(ctx.methodBody());
        methodDef.funcName = funcName;
        TypeSpec t;
        VarDef varDef;
        String tName;
        if (ctx.jType()!=null) {
            tName = ctx.scope.getEnclosingScope().getName();
        }else {
            tName = "void";
        }
        if ( isClassName(tName) ) {
            t =  new ObjectTypeSpec(tName);
        }
        else {
            t = new PrimitiveTypeSpec(tName);
        }
        varDef = new VarDef(t,"this");
        methodDef.args.add(varDef);
        if(ctx.formalParameters().formalParameterList()!=null){
            for(JParser.FormalParameterContext c: ctx.formalParameters().formalParameterList().formalParameter()){
//                System.out.println("Formal= "+c.getText());
                methodDef.args.add((VarDef) visit(c));
            }
        }
        return methodDef;
    }

    @Override
    public OutputModelObject visitMethodBody(JParser.MethodBodyContext ctx) {
        return visit(ctx.block());
    }

    @Override
    public OutputModelObject visitClassDeclaration(JParser.ClassDeclarationContext ctx) {
        ClassDef classDef = new ClassDef(ctx.scope);
        currentScope = ctx.scope;
        currentClass = ctx.scope;
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

    @Override
    public OutputModelObject visitFormalParameter(JParser.FormalParameterContext ctx) {
        VarDef varDef;
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

    //    @Override
//    public OutputModelObject visitQMethodCall(JParser.QMethodCallContext ctx) {
//        MethodCall methodCall = new MethodCall();
//        FuncPtrType funcPtrType = new FuncPtrType();
//        JClass jClass = getClassfromCtx(ctx).scope.
//        JMethod jMethod = (JMethod) currentClass.resolveMethod(ctx.ID().getText());
//        funcPtrType.returnType = ctx.type;
//        System.out.println("in Qmethod call= "+currentScope.getEnclosingScope());
//        System.out.println("in Qmethod call= "+ctx.expression().getText());
//
//        return methodCall;
//    }

//    private JParser.BlockContext getClassfromCtx(JParser.QMethodCallContext ctx) {
//        return getClassfromCtx((JParser.BlockContext) ctx.getParent());
//    }
}