import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
public class Driver {
    public static void main(String[] args) {
        new Driver().step4(System.in);
    }
    public ResultContext step1(InputStream is) {

        final String DISPLAY = "Token Type: %s\nValue: %s\n";
        final HashSet<String> keyword_text = new HashSet<String>() {{
            add("PROGRAM"); add("BEGIN"); add("END"); add("FUNCTION"); add("READ"); add("WRITE");
            add("IF"); add("ELSE"); add("ENDIF"); add("WHILE"); add("ENDWHILE"); add("CONTINUE"); add("BREAK");
            add("RETURN"); add("INT"); add("VOID"); add("STRING"); add("FLOAT");
        }};
        final HashSet<String> op_text = new HashSet<String>() {{
            add("ASSIGN"); add("PLUS"); add("MINUS"); add("MULTIPLY"); add("DIVIDE");
            add("EQUAL"); add("NOTEQUAL"); add("LESS"); add("GREATER"); add("LPAREN");
            add("RPAREN"); add("SEMICOLON"); add("COMMA"); add("LESSEQUAL"); add("GREATEREQUAL");
        }};

        StringBuilder computedResult = new StringBuilder();
        try {
            LittleLexer lexer = new LittleLexer(new ANTLRInputStream(is));
            Token t;
            while ((t = lexer.nextToken()) != null && !lexer._hitEOF) {
                String name = lexer.getVocabulary().getSymbolicName(t.getType());
                if (keyword_text.contains(name))
                    name = "KEYWORD";
                else if (op_text.contains(name))
                    name = "OPERATOR";
               computedResult.append(String.format(DISPLAY, name, t.getText()));
            }
        }
        catch (Exception ex) {
            return new ResultContext(is.getClass()).withSuccess(false);
        }
        return new ResultContext(is.getClass()).withContent(computedResult.toString()).displayIfApplicable();
    }
    public ResultContext step2(InputStream is) {
        String result = "";
        try {
            CommonTokenStream cts = new CommonTokenStream(new LittleLexer(new ANTLRInputStream(is)));
            LittleParser parse = new LittleParser(cts);
            parse.removeErrorListeners();
            parse.removeParseListeners();
            parse.addErrorListener(new LittleErrorListener());

            parse.program();
            result = "Accepted";

        } catch (RuntimeException ex) {
            //Place failure message here.
            result = "Not accepted";
        } catch (IOException ex) {
            return new ResultContext(is.getClass()).withSuccess(false);
        }

        return new ResultContext(is.getClass()).withContent(result).displayIfApplicable();
    }

    public ResultContext step3(InputStream is) {
        try {
            LittleParser parse = new LittleParser(new CommonTokenStream(new LittleLexer(new ANTLRInputStream(is))));
            parse.removeErrorListeners();
            parse.removeParseListeners();
            parse.addErrorListener(new LittleErrorListener());

            ParseTreeWalker walker = new ParseTreeWalker();
            CustomLittleListener listener = new CustomLittleListener();
            walker.walk(listener, parse.program());


            if (Objects.nonNull(listener.getDeclError())) {
                return new ResultContext(is.getClass()).withContent(String.format("DECLARATION ERROR %s", listener.getDeclError())).displayIfApplicable();
            } else
                return new ResultContext(is.getClass()).withContent(listener.getSymbolTableString()).displayIfApplicable();
        } catch (RuntimeException | IOException ex) {
            return new ResultContext(is.getClass()).withSuccess(false);
        }
    }

    public ResultContext step4(InputStream is) {

        try {
            LittleParser parse = new LittleParser(new CommonTokenStream(new LittleLexer(new ANTLRInputStream(is))));
            parse.removeErrorListeners();
            parse.removeParseListeners();
            parse.addErrorListener(new LittleErrorListener());

            ParseTreeWalker walker = new ParseTreeWalker();
            IRGenerator gen = new IRGenerator();
            walker.walk(gen, parse.program());

            for(String tinyLine : gen.irToTiny()) {
                System.out.println(tinyLine);
            }

        } catch (IOException e) {

            return new ResultContext(is.getClass());
        }
        return new ResultContext(is.getClass());
    }
}
class IRGenerator extends LittleBaseListener {
    HashMap<String,String> types = new HashMap<>();
    LinkedList<CodeObject> irList = new LinkedList<>();
    Integer latestTemp = 1;
    HashMap<String,String> tToR = new HashMap<>();
    LinkedHashMap<String,String> assigned_content = new LinkedHashMap<>();
    static class CodeObject {
        String dest = new String();
        String s1 = new String();
        String s2 = new String();
        String opcode = new String();
        public CodeObject(String opcode, String s1, String s2, String dest) {

            String ir = "";
            if(opcode!=null) {
                this.opcode = opcode;
                ir+=";"+opcode+" ";
            }
            if(s2!=null) {
                this.s1 = s1;
                ir+=(s1+" ");
            }
            if(s2!=null) {
                this.s2 = s2;
                ir+=(s2+" ");
            }
            if(dest!=null) {
                this.dest = dest;
                ir+=(dest+" ");
            }
            ir = ir.trim().replaceAll("  "," ");
            System.out.println(ir);
        }
    }

    public String createTemp() {
        return "$T"+latestTemp++;
    }
    public String[] processCompound(String s,String delimeter) {
        return s.replace("(","").replace(")","").split(delimeter);
    }
    public String findPostfix(String... args) {
        for(String s : args) {
            //If contains a decimal
            if(s.contains(".")) { return "F"; }
            if(types.containsKey(s) && types.get(s).equals("FLOAT")) { return "F"; }
        }
        return "I";
    }
    public String getTemp(String s) {
        if(s.contains("$T")) {
            String x = tToR.getOrDefault(s, "r"+latestTemp);
            if(!tToR.containsKey(s)) {
                tToR.put(s,x);
                latestTemp++;
            }
            return x;
        } else return s;
    }
    public List<String> irToTiny() {

        LinkedList<String> lines = new LinkedList<>();
        latestTemp=0;
        for(String key : types.keySet().stream().sorted().collect(Collectors.toList())) {
            switch(types.get(key)) {
                case "FLOAT":
                case "INT": {
                    if(assigned_content.containsKey(key)) {
                        lines.add(new String("var "+key+" "+assigned_content.get(key)));
                    } else { lines.add(new String("var "+key)); }
                    break;
                }
                case "STRING": {
                    if(assigned_content.containsKey(key)) {
                        lines.add(new String("str "+key+" "+assigned_content.get(key)));
                    } else { lines.add(new String("str "+key)); }
                    break;
                }
            }
        }
        ListIterator<CodeObject> itr = irList.listIterator();
        while(itr.hasNext()) {
            CodeObject x = itr.next();
            CodeObject nextElement;
            switch(x.opcode) {
                case "STOREF":
                case "STOREI":
                    nextElement = irList.listIterator(itr.nextIndex()).next();
                    if(nextElement.opcode.equals(x.opcode) && nextElement.s1.equals(x.dest)) {
                        lines.add("move "+getTemp(x.s1)+" "+nextElement.dest);
                        itr.next();
                        itr.remove();
                        break;
                    }
                    else lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    break;
                case "MULTI":
                    lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    lines.add("muli "+ getTemp(x.s2)+" "+ getTemp(x.dest));
                    break;
                case "MULTF":
                    lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    lines.add("mulr "+ getTemp(x.s2)+" "+ getTemp(x.dest));
                    break;
                case "DIVI":
                    lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    lines.add("divi "+ getTemp(x.s2)+" "+ getTemp(x.dest));
                    break;
                case "DIVF":
                    lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    lines.add("divr "+ getTemp(x.s2)+" "+ getTemp(x.dest));
                    break;
                case "READF":
                    lines.add("sys readr "+ getTemp(x.dest));
                    break;
                case "READI":
                    lines.add("sys readi "+ getTemp(x.dest));
                    break;
                case "ADDI":
                    lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    lines.add("addi "+ getTemp(x.s2)+" "+ getTemp(x.dest));
                    break;
                case "ADDF":
                    lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    lines.add("addr "+ getTemp(x.s2)+" "+ getTemp(x.dest));
                    break;
                case "SUBI":
                    lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    lines.add("subi "+ getTemp(x.s2)+" "+ getTemp(x.dest));
                    break;
                case "SUBF":
                    lines.add("move "+ getTemp(x.s1)+" "+ getTemp(x.dest));
                    lines.add("subr "+ getTemp(x.s2)+" "+ getTemp(x.dest));
                    break;
                case "WRITEF":
                    lines.add("sys writer "+getTemp(x.dest));
                    break;
                case "WRITES":
                case "WRITEI":
                    lines.add("sys "+x.opcode.toLowerCase()+" "+ getTemp(x.dest));
                    break;
                case "":

                    break;

            }
        }
        return lines;
    }

    @Override
    public void exitAssign_stmt(LittleParser.Assign_stmtContext ctx) {

        //Expression
        String compound = ctx.assign_expr().expr().getText();

        if(compound.contains("/")){

            String assignExpr = "/";
            String assignee = ctx.assign_expr().id().getText();
            String[] arr = processCompound(ctx.assign_expr().expr().getText(),assignExpr);
            String postfix = findPostfix(arr);
            String temp1 = createTemp();

            if (Objects.equals(postfix, "F")) {

                String temp2 = createTemp();

                //FLOAT DIVISION, DIFF INSTRUCTIONS
                irList.add(new CodeObject("STORE"+postfix,arr[1],"",temp1));
                irList.add(new CodeObject("DIV"+postfix,arr[0],temp1,temp2));
                irList.add(new CodeObject("STORE"+postfix,temp2,"",assignee));

            }
            else {
                //SIMPLE INTEGER DIVISION
                irList.add(new CodeObject("DIV"+postfix,arr[0],arr[1],temp1));
                irList.add(new CodeObject("STORE"+postfix,temp1,"",assignee));
            }

        }
        else if(compound.contains("+")){

            String assignExpr = "\\+";
            String[] arr = processCompound(ctx.assign_expr().expr().getText(),assignExpr);
            String assignee = ctx.assign_expr().id().getText();
            String postfix = findPostfix(arr);
            String temp = createTemp();

            irList.add(new CodeObject("ADD"+postfix,arr[0],arr[1],temp));
            irList.add(new CodeObject("STORE"+postfix,temp,"",assignee));

        }
        else if(compound.contains("-")){
            String assignExpr = "-";
            String[] arr = processCompound(ctx.assign_expr().expr().getText(),assignExpr);
            String assignee = ctx.assign_expr().id().getText();
            String postfix = findPostfix(arr);
            String temp = createTemp();

            irList.add(new CodeObject("SUB"+postfix,arr[0],arr[1],temp));
            irList.add(new CodeObject("STORE"+postfix,temp,"",assignee));

        }
        else if(compound.contains("*")){

            String assignExpr = "\\*";
            String[] arr = processCompound(ctx.assign_expr().expr().getText(),assignExpr);
            String assignee = ctx.assign_expr().id().getText();
            String postfix = findPostfix(arr);
            String temp = createTemp();

            irList.add(new CodeObject("MULT"+postfix,arr[0],arr[1],temp));
            irList.add(new CodeObject("STORE"+postfix,temp,"",assignee));
        }
        else {
            if(ctx.assign_expr().expr().getText().matches("[0-9]*") || ctx.assign_expr().expr().getText().matches("[0-9]*.[0-9]+")) {
                Character postfix = (types.get(ctx.assign_expr().id().getText()).equals("INT")) ? 'I' : 'F';
                irList.add(new CodeObject("STORE" + postfix, ctx.assign_expr().expr().getText(), "", "$T" + latestTemp++));
                irList.add(new CodeObject("STORE" + postfix, irList.getLast().dest, "", ctx.assign_expr().id().getText()));
            }
            else {
                Character postfix = (types.get(ctx.assign_expr().id().getText()).equals("INT")) ? 'I' : 'F';
                irList.add(new CodeObject("STORE" + postfix, irList.getLast().dest, "", ctx.assign_expr().id().getText()));
            }
        }
    }
    @Override
    public void enterString_decl(LittleParser.String_declContext ctx) {
        types.put(ctx.id().getText(),"STRING");
        assigned_content.put(ctx.id().getText(),ctx.str().getText());
    }
    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        Arrays.stream(ctx.id_list().getText().split(",")).forEach(id -> {
                types.put(id, ctx.var_type().getText());
            }
        );
    }
    @Override
    public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
        Arrays.stream(ctx.id_list().getText().split(",")).forEach(read -> {
            switch(types.get(read)) {
                case "FLOAT": {
                    irList.add(new CodeObject("READF", null, null, read));
                    break;
                }
                case "INT": {
                    irList.add(new CodeObject("READI", null, null, read));
                }
                case "STRING": {
                    irList.add(new CodeObject("READS", null, null, read));
                }
            }
        });
    }
    @Override
    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        Arrays.stream(ctx.id_list().getText().split(",")).forEach(write -> {
            if(types.get(write).equalsIgnoreCase("string")) {
                irList.add(new CodeObject("WRITES", null, null, write));
            }
            else if(types.get(write).equals("FLOAT")) {
                irList.add(new CodeObject("WRITEF",null,null,write));
            }
            else {
                irList.add(new CodeObject("WRITEI", null, null, write));
            }
        });
    }
    @Override
    public void exitPgm_body(LittleParser.Pgm_bodyContext ctx) {
        irList.add(new CodeObject("RET", null, null, null));
    }
}

class LittleErrorListener implements ANTLRErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        try {
            throw new Exception("Parser Error at symbol: "+offendingSymbol.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {

    }
    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {

    }
    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {

    }
}
class SymbolEntry {
    String name;
    String type;
    String value;
    public SymbolEntry(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getType() { return type; }
    public String getName() { return name; }
    public String getValue() { return value; }
    @Override
    public String toString() {
        if(Objects.isNull(value)) return String.format("name %s type %s%n",getName(),getType());
        else return String.format("name %s type %s value %s%n",getName(),getType(),getValue());
    }
}
class CustomLittleListener extends LittleBaseListener {
    // Keeps track of order the variables have seen
    private Stack<Map<String, SymbolEntry>> symbolTableStack = new Stack<>();
    // Stores current symbols as they are seen (LinkedHashMap to store order)
    private Map<String, SymbolEntry> currentSymbolTable = new LinkedHashMap<>();
    // Saves what the stack has done as it pops scopes, scopSymbolTables is the resulting structure
    private Map<String, Map<String, SymbolEntry>> scopeSymbolTables = new LinkedHashMap<>();
    // Number that increments when a new scope has been entered
    private int blockNum = 1;
    private List<String> declError = new ArrayList<>();

    public CustomLittleListener() {
        symbolTableStack.push(new LinkedHashMap<>());
        currentSymbolTable = symbolTableStack.peek();
        scopeSymbolTables.put("GLOBAL", currentSymbolTable);
    }
    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        currentSymbolTable = new LinkedHashMap<>();
        symbolTableStack.push(currentSymbolTable);
        scopeSymbolTables.put(ctx.id().getText(), currentSymbolTable);
    }

    @Override
    public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        symbolTableStack.pop();
        currentSymbolTable = symbolTableStack.peek();
    }


    @Override
    public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        currentSymbolTable = new LinkedHashMap<>();
        symbolTableStack.push(currentSymbolTable);
        scopeSymbolTables.put("BLOCK " + blockNum, currentSymbolTable);
        blockNum++;

        LittleParser.Else_partContext elsePart = ctx.else_part();
        if (elsePart != null && elsePart.stmt_list() != null && !elsePart.stmt_list().isEmpty()) {
            currentSymbolTable = new LinkedHashMap<>();
            symbolTableStack.push(currentSymbolTable);
            scopeSymbolTables.put("BLOCK " + blockNum, currentSymbolTable);
            blockNum++;
        }
    }

    @Override
    public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
        symbolTableStack.pop();
        currentSymbolTable = symbolTableStack.peek();
    }

    @Override
    public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        currentSymbolTable = new LinkedHashMap<>();
        symbolTableStack.push(currentSymbolTable);
        scopeSymbolTables.put("BLOCK " + blockNum, currentSymbolTable);
        blockNum++;
    }

    @Override
    public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
        symbolTableStack.pop();
        currentSymbolTable = symbolTableStack.peek();
    }

    @Override
    public void enterParam_decl(LittleParser.Param_declContext ctx) {
        String type = ctx.var_type().getText();
        String name = ctx.id().getText();
        String value = null;
        if(searchForKeyName(name)) {
            declError.add(name);
            return;
        }
        currentSymbolTable.put(name, new SymbolEntry(name, type, value));
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        String type = ctx.var_type().getText();
        String[] variableNames = ctx.id_list().getText().split(",");
        for (String variableName : variableNames) {
            String name = variableName.trim();
            String value = null;
            if(searchForKeyName(name)) {
                declError.add(name);
                return;
            }
            currentSymbolTable.put(name, new SymbolEntry(name, type, value));
        }
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx) {
        String value = ctx.str().getText();
        String name = ctx.id().getText();
        String type = "STRING";
        if(searchForKeyName(name)) {
            declError.add(name);
            return;
        }
        currentSymbolTable.put(name, new SymbolEntry(name, type, value));
    }
    public String getSymbolTableString() {
        final String SYMBOL_TABLE = "Symbol table ";
        StringBuilder result = new StringBuilder();
        for (String scopeName : scopeSymbolTables.keySet()) {
            result.append(SYMBOL_TABLE).append(scopeName).append("\n");
            Map<String, SymbolEntry> symbolTable = scopeSymbolTables.get(scopeName);
            symbolTable.values().forEach(symbol -> result.append(symbol.toString()));
            result.append("\n");
        }
        return result.toString();
    }
    public Boolean searchForKeyName(String newKey) {
        return currentSymbolTable.containsKey(newKey);
    }
    public String getDeclError() {
        if(declError.isEmpty()) {
            return null;
        } else return declError.get(0);
    }
}
class ResultContext {
    Boolean success = true;
    String content = null;
    Class<? extends InputStream> cl;

    public ResultContext(Class<? extends InputStream> isC) {
        cl = isC;
    }
    public ResultContext withSuccess(Boolean outcome) {
        this.success = outcome;
        return this;
    }
    public ResultContext withContent(String sb) {
        content = sb;
        return this;
    }
    public ResultContext displayIfApplicable() {
        if(cl.equals(BufferedInputStream.class)) {
            System.out.print(content);
        }
        return this;
    }
}