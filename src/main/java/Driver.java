import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class Driver {
    public static void main(String[] args) throws IOException {
        step2(System.in);
    }
    public static String step2(InputStream is) throws IOException {
        CommonTokenStream cts = new CommonTokenStream(new LittleLexer(new ANTLRInputStream(is)));
        LittleParser parse = new LittleParser(cts);
        parse.removeErrorListeners();
        parse.removeParseListeners();
        parse.addErrorListener(new LittleErrorListener());

        String computedResult = null;
        try {
            parse.program();
            computedResult = "Accepted";
        }
        catch (Exception ex) {
            computedResult = "Not accepted";
        }

        if(is.equals(System.in)) {
            System.out.println(computedResult);
        }
        return computedResult;
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