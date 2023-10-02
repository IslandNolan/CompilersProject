// Generated from Little.g4 by ANTLR 4.9.3
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LittleLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PROGRAM=1, BEGIN=2, END=3, FUNCTION=4, READ=5, WRITE=6, IF=7, ELSE=8, 
		ENDIF=9, WHILE=10, ENDWHILE=11, CONTINUE=12, BREAK=13, RETURN=14, INT=15, 
		VOID=16, STRING=17, FLOAT=18, ASSIGN=19, PLUS=20, MINUS=21, MULTIPLY=22, 
		DIVIDE=23, EQUAL=24, NOTEQUAL=25, LESS=26, GREATER=27, LPAREN=28, RPAREN=29, 
		SEMICOLON=30, COMMA=31, LESSEQUAL=32, GREATEREQUAL=33, WS=34, COMMENT=35, 
		INTLITERAL=36, FLOATLITERAL=37, STRINGLITERAL=38, IDENTIFIER=39;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"PROGRAM", "BEGIN", "END", "FUNCTION", "READ", "WRITE", "IF", "ELSE", 
			"ENDIF", "WHILE", "ENDWHILE", "CONTINUE", "BREAK", "RETURN", "INT", "VOID", 
			"STRING", "FLOAT", "ASSIGN", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "EQUAL", 
			"NOTEQUAL", "LESS", "GREATER", "LPAREN", "RPAREN", "SEMICOLON", "COMMA", 
			"LESSEQUAL", "GREATEREQUAL", "WS", "COMMENT", "INTLITERAL", "FLOATLITERAL", 
			"STRINGLITERAL", "IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'PROGRAM'", "'BEGIN'", "'END'", "'FUNCTION'", "'READ'", "'WRITE'", 
			"'IF'", "'ELSE'", "'ENDIF'", "'WHILE'", "'ENDWHILE'", "'CONTINUE'", "'BREAK'", 
			"'RETURN'", "'INT'", "'VOID'", "'STRING'", "'FLOAT'", "':='", "'+'", 
			"'-'", "'*'", "'/'", "'='", "'!='", "'<'", "'>'", "'('", "')'", "';'", 
			"','", "'<='", "'>='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PROGRAM", "BEGIN", "END", "FUNCTION", "READ", "WRITE", "IF", "ELSE", 
			"ENDIF", "WHILE", "ENDWHILE", "CONTINUE", "BREAK", "RETURN", "INT", "VOID", 
			"STRING", "FLOAT", "ASSIGN", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "EQUAL", 
			"NOTEQUAL", "LESS", "GREATER", "LPAREN", "RPAREN", "SEMICOLON", "COMMA", 
			"LESSEQUAL", "GREATEREQUAL", "WS", "COMMENT", "INTLITERAL", "FLOATLITERAL", 
			"STRINGLITERAL", "IDENTIFIER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public LittleLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Little.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2)\u0113\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b"+
		"\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3"+
		"\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25"+
		"\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\34"+
		"\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3\"\3#\6#\u00e4"+
		"\n#\r#\16#\u00e5\3#\3#\3$\3$\3$\3$\7$\u00ee\n$\f$\16$\u00f1\13$\3$\3$"+
		"\3%\6%\u00f6\n%\r%\16%\u00f7\3&\7&\u00fb\n&\f&\16&\u00fe\13&\3&\3&\6&"+
		"\u0102\n&\r&\16&\u0103\3\'\3\'\7\'\u0108\n\'\f\'\16\'\u010b\13\'\3\'\3"+
		"\'\3(\6(\u0110\n(\r(\16(\u0111\2\2)\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n"+
		"\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)\3\2\7\5\2\13"+
		"\f\17\17\"\"\4\2\f\f\17\17\3\2\62;\3\2$$\5\2\62;C\\c|\2\u0119\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3"+
		"\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3"+
		"\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2"+
		"=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3"+
		"\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\3Q\3\2\2\2\5Y\3\2\2\2\7_\3\2\2"+
		"\2\tc\3\2\2\2\13l\3\2\2\2\rq\3\2\2\2\17w\3\2\2\2\21z\3\2\2\2\23\177\3"+
		"\2\2\2\25\u0085\3\2\2\2\27\u008b\3\2\2\2\31\u0094\3\2\2\2\33\u009d\3\2"+
		"\2\2\35\u00a3\3\2\2\2\37\u00aa\3\2\2\2!\u00ae\3\2\2\2#\u00b3\3\2\2\2%"+
		"\u00ba\3\2\2\2\'\u00c0\3\2\2\2)\u00c3\3\2\2\2+\u00c5\3\2\2\2-\u00c7\3"+
		"\2\2\2/\u00c9\3\2\2\2\61\u00cb\3\2\2\2\63\u00cd\3\2\2\2\65\u00d0\3\2\2"+
		"\2\67\u00d2\3\2\2\29\u00d4\3\2\2\2;\u00d6\3\2\2\2=\u00d8\3\2\2\2?\u00da"+
		"\3\2\2\2A\u00dc\3\2\2\2C\u00df\3\2\2\2E\u00e3\3\2\2\2G\u00e9\3\2\2\2I"+
		"\u00f5\3\2\2\2K\u00fc\3\2\2\2M\u0105\3\2\2\2O\u010f\3\2\2\2QR\7R\2\2R"+
		"S\7T\2\2ST\7Q\2\2TU\7I\2\2UV\7T\2\2VW\7C\2\2WX\7O\2\2X\4\3\2\2\2YZ\7D"+
		"\2\2Z[\7G\2\2[\\\7I\2\2\\]\7K\2\2]^\7P\2\2^\6\3\2\2\2_`\7G\2\2`a\7P\2"+
		"\2ab\7F\2\2b\b\3\2\2\2cd\7H\2\2de\7W\2\2ef\7P\2\2fg\7E\2\2gh\7V\2\2hi"+
		"\7K\2\2ij\7Q\2\2jk\7P\2\2k\n\3\2\2\2lm\7T\2\2mn\7G\2\2no\7C\2\2op\7F\2"+
		"\2p\f\3\2\2\2qr\7Y\2\2rs\7T\2\2st\7K\2\2tu\7V\2\2uv\7G\2\2v\16\3\2\2\2"+
		"wx\7K\2\2xy\7H\2\2y\20\3\2\2\2z{\7G\2\2{|\7N\2\2|}\7U\2\2}~\7G\2\2~\22"+
		"\3\2\2\2\177\u0080\7G\2\2\u0080\u0081\7P\2\2\u0081\u0082\7F\2\2\u0082"+
		"\u0083\7K\2\2\u0083\u0084\7H\2\2\u0084\24\3\2\2\2\u0085\u0086\7Y\2\2\u0086"+
		"\u0087\7J\2\2\u0087\u0088\7K\2\2\u0088\u0089\7N\2\2\u0089\u008a\7G\2\2"+
		"\u008a\26\3\2\2\2\u008b\u008c\7G\2\2\u008c\u008d\7P\2\2\u008d\u008e\7"+
		"F\2\2\u008e\u008f\7Y\2\2\u008f\u0090\7J\2\2\u0090\u0091\7K\2\2\u0091\u0092"+
		"\7N\2\2\u0092\u0093\7G\2\2\u0093\30\3\2\2\2\u0094\u0095\7E\2\2\u0095\u0096"+
		"\7Q\2\2\u0096\u0097\7P\2\2\u0097\u0098\7V\2\2\u0098\u0099\7K\2\2\u0099"+
		"\u009a\7P\2\2\u009a\u009b\7W\2\2\u009b\u009c\7G\2\2\u009c\32\3\2\2\2\u009d"+
		"\u009e\7D\2\2\u009e\u009f\7T\2\2\u009f\u00a0\7G\2\2\u00a0\u00a1\7C\2\2"+
		"\u00a1\u00a2\7M\2\2\u00a2\34\3\2\2\2\u00a3\u00a4\7T\2\2\u00a4\u00a5\7"+
		"G\2\2\u00a5\u00a6\7V\2\2\u00a6\u00a7\7W\2\2\u00a7\u00a8\7T\2\2\u00a8\u00a9"+
		"\7P\2\2\u00a9\36\3\2\2\2\u00aa\u00ab\7K\2\2\u00ab\u00ac\7P\2\2\u00ac\u00ad"+
		"\7V\2\2\u00ad \3\2\2\2\u00ae\u00af\7X\2\2\u00af\u00b0\7Q\2\2\u00b0\u00b1"+
		"\7K\2\2\u00b1\u00b2\7F\2\2\u00b2\"\3\2\2\2\u00b3\u00b4\7U\2\2\u00b4\u00b5"+
		"\7V\2\2\u00b5\u00b6\7T\2\2\u00b6\u00b7\7K\2\2\u00b7\u00b8\7P\2\2\u00b8"+
		"\u00b9\7I\2\2\u00b9$\3\2\2\2\u00ba\u00bb\7H\2\2\u00bb\u00bc\7N\2\2\u00bc"+
		"\u00bd\7Q\2\2\u00bd\u00be\7C\2\2\u00be\u00bf\7V\2\2\u00bf&\3\2\2\2\u00c0"+
		"\u00c1\7<\2\2\u00c1\u00c2\7?\2\2\u00c2(\3\2\2\2\u00c3\u00c4\7-\2\2\u00c4"+
		"*\3\2\2\2\u00c5\u00c6\7/\2\2\u00c6,\3\2\2\2\u00c7\u00c8\7,\2\2\u00c8."+
		"\3\2\2\2\u00c9\u00ca\7\61\2\2\u00ca\60\3\2\2\2\u00cb\u00cc\7?\2\2\u00cc"+
		"\62\3\2\2\2\u00cd\u00ce\7#\2\2\u00ce\u00cf\7?\2\2\u00cf\64\3\2\2\2\u00d0"+
		"\u00d1\7>\2\2\u00d1\66\3\2\2\2\u00d2\u00d3\7@\2\2\u00d38\3\2\2\2\u00d4"+
		"\u00d5\7*\2\2\u00d5:\3\2\2\2\u00d6\u00d7\7+\2\2\u00d7<\3\2\2\2\u00d8\u00d9"+
		"\7=\2\2\u00d9>\3\2\2\2\u00da\u00db\7.\2\2\u00db@\3\2\2\2\u00dc\u00dd\7"+
		">\2\2\u00dd\u00de\7?\2\2\u00deB\3\2\2\2\u00df\u00e0\7@\2\2\u00e0\u00e1"+
		"\7?\2\2\u00e1D\3\2\2\2\u00e2\u00e4\t\2\2\2\u00e3\u00e2\3\2\2\2\u00e4\u00e5"+
		"\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7"+
		"\u00e8\b#\2\2\u00e8F\3\2\2\2\u00e9\u00ea\7/\2\2\u00ea\u00eb\7/\2\2\u00eb"+
		"\u00ef\3\2\2\2\u00ec\u00ee\n\3\2\2\u00ed\u00ec\3\2\2\2\u00ee\u00f1\3\2"+
		"\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f2\3\2\2\2\u00f1"+
		"\u00ef\3\2\2\2\u00f2\u00f3\b$\2\2\u00f3H\3\2\2\2\u00f4\u00f6\t\4\2\2\u00f5"+
		"\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8\3\2"+
		"\2\2\u00f8J\3\2\2\2\u00f9\u00fb\t\4\2\2\u00fa\u00f9\3\2\2\2\u00fb\u00fe"+
		"\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00ff\3\2\2\2\u00fe"+
		"\u00fc\3\2\2\2\u00ff\u0101\7\60\2\2\u0100\u0102\t\4\2\2\u0101\u0100\3"+
		"\2\2\2\u0102\u0103\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104"+
		"L\3\2\2\2\u0105\u0109\7$\2\2\u0106\u0108\n\5\2\2\u0107\u0106\3\2\2\2\u0108"+
		"\u010b\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a\u010c\3\2"+
		"\2\2\u010b\u0109\3\2\2\2\u010c\u010d\7$\2\2\u010dN\3\2\2\2\u010e\u0110"+
		"\t\6\2\2\u010f\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u010f\3\2\2\2\u0111"+
		"\u0112\3\2\2\2\u0112P\3\2\2\2\n\2\u00e5\u00ef\u00f7\u00fc\u0103\u0109"+
		"\u0111\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}