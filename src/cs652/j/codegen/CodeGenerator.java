package cs652.j.codegen;

import cs652.j.codegen.model.*;
import cs652.j.parser.JBaseVisitor;
import cs652.j.parser.JParser;
import cs652.j.semantics.JClass;
import cs652.j.semantics.JField;
import org.antlr.symtab.FieldSymbol;
import org.antlr.symtab.MethodSymbol;
import org.antlr.symtab.Scope;
import org.antlr.symtab.TypedSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.Collection;
import java.util.Collections;


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
        return (CFile)visit(tree);
    }

    @Override
    public OutputModelObject visitThisRef(JParser.ThisRefContext ctx) {
        return new ThisRef();
    }

    @Override
    public OutputModelObject visitFieldRef(JParser.FieldRefContext ctx) {
        return new FieldRef(ctx.ID().getText(),(Expr) visit(ctx.expression()));
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
        MainMethod mainMethod = new MainMethod((Block) visit(ctx.block()));
        currentScope = ctx.scope;
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
        t = getTypeSpec(ctx);
        varDef = new VarDef(t,ctx.ID().getText());
        return varDef;
    }

    private TypeSpec getTypeSpec(JParser.LocalVariableDeclarationContext ctx) {
        TypeSpec t;
        String typename = ctx.jType().getText();
        t = getTypeSpec(typename);
        return t;
    }

    private TypeSpec getTypeSpec(String typename) {
        TypeSpec t;
        if ( isClassName(typename) ) {
            t =  new ObjectTypeSpec(typename);
        }
        else {
            t = new PrimitiveTypeSpec(typename);
        }
        return t;
    }

    @Override
    public OutputModelObject visitPrintStringStat(JParser.PrintStringStatContext ctx) {
        return new PrintStringStat(ctx.STRING().getText());
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
        AssignStat as ;
        Expr l = (Expr) visit(ctx.expression(0));
        Expr r = (Expr) visit(ctx.expression(1));
        OutputModelObject right = visit(ctx.expression(1));
        if(!(right instanceof LiteralRef)){
            TypeCast typeCast = new TypeCast();

            String tn = ctx.expression(0).type.getName();
            if ( isClassName(tn)) {
                typeCast.type =  new ObjectTypeSpec(tn);
                typeCast.expr = (Expr) right;
                as = new AssignStat(l,typeCast);
            }
            else {
                as = new AssignStat(l,(Expr) right);
            }
        } else {
            as = new AssignStat(l,r);
        }
        return as;
    }

    @Override
    public OutputModelObject visitIdRef(JParser.IdRefContext ctx) {
        TypedSymbol typedSymbol = (TypedSymbol) currentScope.resolve(ctx.ID().getText());
        if(typedSymbol instanceof JField){
            return new VarRef("this->"+ctx.ID().getText());
        }else {
            return new VarRef(ctx.ID().getText());
        }
    }


    @Override
    public OutputModelObject visitLiteralRef(JParser.LiteralRefContext ctx) {
        return new LiteralRef(ctx.getText());
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
        FuncName funcName = new FuncName(ctx.scope.getEnclosingScope().getName(),ctx.ID().getText(),0);
        if (ctx.jType()!=null) {
            typename = ctx.jType().getText();
        }else {
            typename = "void";
        }
        methodDef.returnType = getTypeSpec(typename);
        methodDef.body = (Block) visit(ctx.methodBody());
        methodDef.funcName = funcName;

        TypeSpec t;
        VarDef varDef;
        String tName = ctx.scope.getEnclosingScope().getName();
        t = getTypeSpec(tName);
        varDef = new VarDef(t,"this");
        methodDef.args.add(varDef);
        if(ctx.formalParameters().formalParameterList()!=null){
            for(JParser.FormalParameterContext c: ctx.formalParameters().formalParameterList().formalParameter()){
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
    public OutputModelObject visitFieldDeclaration(JParser.FieldDeclarationContext ctx) {
        VarDef varDef;
        TypeSpec t;
        String typename = ctx.jType().getText();
        t = getTypeSpec(typename);
        varDef = new VarDef(t,ctx.ID().getText());
        return varDef;
    }

    @Override
    public OutputModelObject visitClassDeclaration(JParser.ClassDeclarationContext ctx) {
        ClassDef classDef = new ClassDef(ctx.scope);
        currentScope = ctx.scope;
        currentClass = ctx.scope;

        for (JParser.ClassBodyDeclarationContext c : ctx.classBody().classBodyDeclaration()){
            if(visit(c) instanceof MethodDef){
                MethodDef methodDef;
                methodDef = (MethodDef) visit(c);
                classDef.methods.add(methodDef);
            }
        }

        for(MethodSymbol m : ctx.scope.getMethods()){
            FuncName funcName = new FuncName(ctx.scope.resolve(m.getEnclosingScope().getName()).getName(),m.getName(),m.getSlotNumber());
            classDef.vtable.add(funcName);
            funcName.slotNumber = m.getSlotNumber();
        }
        for(FieldSymbol a: ctx.scope.getFields() ){
            VarDef varDef;
            TypeSpec t;
            String typename =  a.getType().getName();
            t = getTypeSpec(typename);
            varDef = new VarDef(t,a.getName());
            classDef.fields.add(varDef);
        }
        Collections.sort(classDef.getVtable(),(o1, o2) -> o1.slotNumber - o2.slotNumber);
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
        t = getTypeSpec(typename);
        varDef = new VarDef(t,ctx.ID().getText());
        return varDef;
    }
    @Override
    public OutputModelObject visitQMethodCall(JParser.QMethodCallContext ctx) {
        MethodCall methodCall = new MethodCall();
        FuncPtrType funcPtrType = new FuncPtrType();
        JClass jClass = (JClass) currentScope.resolve(ctx.expression().type.getName());
        MethodSymbol methodSymbol = (MethodSymbol) jClass.resolve(ctx.ID().getText());
        methodCall.className = jClass.getName();
        methodCall.receiverType = methodSymbol.getEnclosingScope().getName();
        methodCall.name = ctx.ID().getText();

        TypeSpec t;
        String typename = methodSymbol.getType().getName();
        t = getTypeSpec(typename);
        funcPtrType.returnType = t;
        methodCall.fptrType = funcPtrType;
        if(visit(ctx.expression()) instanceof VarRef) {
            methodCall.receiver = (VarRef) visit(ctx.expression());
        }else if(visit(ctx.expression()) instanceof  FieldRef) {
            methodCall.receiver = (FieldRef) visit(ctx.expression());
        } else {
            methodCall.receiver = (ThisRef) visit(ctx.expression());
        }
        funcPtrType.argTypes.add(new ObjectTypeSpec(methodSymbol.getEnclosingScope().getName()));
        if(ctx.expression().type!=null && ctx.expressionList()!=null) {
            for (JParser.ExpressionContext e : ctx.expressionList().expression()) {
                String ty = e.type.getName();
                if(isClassName(ty))
                {
                    funcPtrType.argTypes.add(new ObjectTypeSpec(ty));
                }
                else {
                    funcPtrType.argTypes.add(new PrimitiveTypeSpec(ty));
                }
                if (visit(e) instanceof VarRef) {
                    VarRef varRef = (VarRef) visit(e);
                    TypeCast typeCast = new TypeCast();
                    String ty5 = ctx.expression().type.getName();
                    typeCast.type = getTypeSpec(ty5);
                    typeCast.expr = varRef;
                    methodCall.args.add(typeCast);
                } else if(visit(e) instanceof LiteralRef){
                    LiteralRef literalRef = (LiteralRef) visit(e);
                    methodCall.args.add(literalRef);
                } else {
                    CtorCall ctorCall = (CtorCall) visit(e);
                    TypeCast typeCast = new TypeCast();
                    typeCast.type = new ObjectTypeSpec(e.type.getName());
                    typeCast.expr = ctorCall;
                    methodCall.args.add(typeCast);
                }
            }
        }
        return methodCall;
    }

    @Override
    public OutputModelObject visitCallStat(JParser.CallStatContext ctx) {
        CallStat callStat = new CallStat();
        if(visit(ctx.expression()) instanceof  Expr) {
            callStat.call = visit(ctx.expression());
        }else {
            callStat.call = visit(ctx.expression());
        }
        return callStat;
    }

    @Override
    public OutputModelObject visitMethodCall(JParser.MethodCallContext ctx) {
        MethodCall methodCall = new MethodCall();
        FuncPtrType funcPtrType = new FuncPtrType();
        methodCall.name = ctx.ID().getText();
        methodCall.className = currentClass.getName();
        MethodSymbol methodSymbol = (MethodSymbol) currentClass.resolve(ctx.ID().getText());
        methodCall.receiverType = methodSymbol.getEnclosingScope().getName();
        TypeSpec t;
        String typename = methodSymbol.getType().getName();
        t = getTypeSpec(typename);
        funcPtrType.returnType = t;
        methodCall.receiver = new  VarRef("this");
        methodCall.fptrType = funcPtrType;
        funcPtrType.argTypes.add(new ObjectTypeSpec(methodSymbol.getEnclosingScope().getName()));
        if(ctx.expressionList()!=null) {
            for (JParser.ExpressionContext e : ctx.expressionList().expression()) {
                String ty = e.type.getName();
                if(isClassName(ty))
                {
                    funcPtrType.argTypes.add(new ObjectTypeSpec(ty));
                }
                else {
                    funcPtrType.argTypes.add(new PrimitiveTypeSpec(ty));
                }
                if (visit(e) instanceof VarRef) {
                    VarRef varRef2 = (VarRef) visit(e);
                    TypeCast typeCast = new TypeCast();
                    String ty2 = e.type.getName();
                    typeCast.type = getTypeSpec(ty2);
                    typeCast.expr = varRef2;
                    methodCall.args.add(typeCast);
                } else if(visit(e) instanceof LiteralRef){
                    LiteralRef literalRef = (LiteralRef) visit(e);
                    methodCall.args.add(literalRef);
                } else {
                    CtorCall ctorCall = (CtorCall) visit(e);
                    TypeCast typeCast = new TypeCast();
                    typeCast.type = new ObjectTypeSpec(e.type.getName());
                    typeCast.expr = ctorCall;
                    methodCall.args.add(typeCast);
                }
            }
        }
        return methodCall;
    }
}