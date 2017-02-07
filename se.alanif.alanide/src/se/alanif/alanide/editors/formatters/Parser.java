package se.alanif.alanide.editors.formatters;

import se.alanif.alanide.model.*;
import java.util.ArrayList;



public class Parser {
	public static final int _EOF = 0;
	public static final int _Identifier = 1;
	public static final int _Integer = 2;
	public static final int _STRING = 3;
	public static final int _opaque = 4;
	public static final int _container = 5;
	public static final int _not = 6;
	public static final int maxT = 146;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	public AlanModel model;

	private boolean notOpaqueContainer() {
		Token next = scanner.Peek();
		return la.kind != _opaque || next.kind != _container;
	}
	
	private class AlanModelList extends ArrayList<AlanModel> {
		private static final long serialVersionUID = 1L;
	}



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void adventure() {
		model = new AlanModel(); 
		if (la.kind == 7 || la.kind == 8) {
			options();
		}
		while (StartOf(1)) {
			AlanModel declaration = declaration();
			if (declaration != null) model.add(declaration); 
		}
		if (la.kind == 70) {
			AlanModel start = start();
			if (start != null) model.add(start); 
		}
	}

	void options() {
		if (la.kind == 7) {
			Get();
		} else if (la.kind == 8) {
			Get();
		} else SynErr(147);
		option();
		while (StartOf(2)) {
			option();
		}
	}

	AlanModel  declaration() {
		AlanModel  declaration;
		declaration = null; 
		switch (la.kind) {
		case 11: {
			AlanModel importDeclaration = importstatement();
			declaration = importDeclaration; 
			break;
		}
		case 10: {
			prompt();
			break;
		}
		case 18: {
			AlanModel messageDeclaration = messages();
			declaration = messageDeclaration; 
			break;
		}
		case 36: {
			AlanModel classDeclaration = classdeclaration();
			declaration = classDeclaration; 
			break;
		}
		case 39: {
			AlanModel instanceDeclaration = instance();
			declaration = instanceDeclaration; 
			break;
		}
		case 32: {
			AlanModel ruleDeclaration = rule();
			declaration = ruleDeclaration; 
			break;
		}
		case 16: {
			AlanModel synonymsDeclaration = synonyms();
			declaration = synonymsDeclaration; 
			break;
		}
		case 20: {
			AlanModel syntaxDeclaration = syntax();
			declaration = syntaxDeclaration; 
			break;
		}
		case 31: {
			AlanModel verbDeclaration = verb();
			declaration = verbDeclaration; 
			break;
		}
		case 37: {
			AlanModel additionDeclaration = addition();
			declaration = additionDeclaration; 
			break;
		}
		case 63: {
			AlanModel eventDeclaration = event();
			declaration = eventDeclaration; 
			break;
		}
		default: SynErr(148); break;
		}
		return declaration;
	}

	AlanModel  start() {
		AlanModel  startsection;
		int start; 
		Expect(70);
		start = t.pos; 
		where();
		Expect(9);
		if (StartOf(3)) {
			statements();
		}
		startsection = new AlanStart(start, t.pos-start+t.val.length()); 
		return startsection;
	}

	void option() {
		ID();
		if (StartOf(4)) {
			optionvalue();
		}
		Expect(9);
	}

	void ID() {
		switch (la.kind) {
		case 1: {
			Get();
			break;
		}
		case 131: {
			Get();
			break;
		}
		case 130: {
			Get();
			break;
		}
		case 4: {
			Get();
			break;
		}
		case 104: {
			Get();
			break;
		}
		case 78: {
			Get();
			break;
		}
		case 124: {
			Get();
			break;
		}
		case 94: {
			Get();
			break;
		}
		case 100: {
			Get();
			break;
		}
		case 57: {
			Get();
			break;
		}
		default: SynErr(149); break;
		}
	}

	void optionvalue() {
		if (StartOf(2)) {
			ID();
		} else if (la.kind == 2) {
			Get();
		} else SynErr(150);
	}

	AlanImport  importstatement() {
		AlanImport  importDeclaration;
		int start; String name; 
		Expect(11);
		start = t.pos; 
		ID();
		if (t.val.charAt(0) == '\'') name = t.val.substring(1, t.val.length()-1); else name = t.val; 
		Expect(9);
		importDeclaration = new AlanImport(name, start, t.pos-start+t.val.length()); 
		return importDeclaration;
	}

	void prompt() {
		Expect(10);
		statements();
	}

	AlanMessage  messages() {
		AlanMessage  message;
		int start; 
		Expect(18);
		start = t.pos; 
		AlanMessage message2 = message();
		message = new AlanMessage("Messages", start, 0); message.add(message2); 
		while (StartOf(2)) {
			AlanMessage message3 = message();
			message.add(message3); 
		}
		message.length = t.pos-start+t.val.length(); 
		return message;
	}

	AlanClass  classdeclaration() {
		AlanClass  declaration;
		int start; 
		Expect(36);
		start = t.pos; 
		ID();
		declaration = new AlanClass(t.val, start, 0); 
		if (la.kind == 28) {
			AlanHeritage heritage = heritage();
			declaration.add(heritage); 
		}
		while (StartOf(5)) {
			AlanModelList property = property();
			declaration.add(property); 
		}
		classtail();
		declaration.length = t.pos-start+t.val.length(); 
		return declaration;
	}

	AlanInstance  instance() {
		AlanInstance  declaration;
		int start; 
		Expect(39);
		start = t.pos; 
		ID();
		declaration = new AlanInstance(t.val, start, 0); 
		if (la.kind == 28) {
			AlanHeritage heritage = heritage();
			declaration.add(heritage); 
		}
		while (StartOf(5)) {
			AlanModelList property = property();
			declaration.add(property); 
		}
		instancetail();
		declaration.length = t.pos-start+t.val.length(); 
		return declaration;
	}

	AlanRule  rule() {
		AlanRule  rule;
		int start; 
		Expect(32);
		start = t.pos; 
		expression();
		then();
		statements();
		if (la.kind == 33) {
			endwhen();
		}
		rule = new AlanRule(start, t.pos-start+t.val.length()); 
		return rule;
	}

	AlanSynonym  synonyms() {
		AlanSynonym  synonyms;
		int start; 
		Expect(16);
		start = t.pos; synonyms = new AlanSynonym("Synonyms", start, 0);
		AlanSynonym synonym = synonym();
		synonyms.add(synonym); 
		while (StartOf(2)) {
			AlanSynonym synonym2 = synonym();
			synonyms.add(synonym2); 
		}
		synonyms.length = t.pos-start+t.val.length(); 
		return synonyms;
	}

	AlanSyntax  syntax() {
		AlanSyntax  syntaxList;
		int start; 
		Expect(20);
		start = t.pos; syntaxList = new AlanSyntax("Syntax", start, 0);
		AlanSyntax syntax = syntaxitem();
		syntaxList.add(syntax); 
		while (StartOf(2)) {
			AlanSyntax syntax2 = syntaxitem();
			syntaxList.add(syntax2); 
		}
		syntaxList.length = t.pos-start+t.val.length(); 
		return syntaxList;
	}

	AlanVerb  verb() {
		AlanVerb  verb;
		int start; String name; 
		Expect(31);
		start = t.pos; 
		Token firstId = idlist();
		name = firstId.val; 
		verbbody();
		verbtail();
		verb = new AlanVerb(name, start, t.pos-start+t.val.length()); 
		return verb;
	}

	AlanAddition  addition() {
		AlanAddition  addition;
		int start; 
		Expect(37);
		start = t.pos; 
		Expect(38);
		if (la.kind == 36) {
			Get();
		}
		ID();
		addition = new AlanAddition(t.val, start, 0); 
		if (la.kind == 28) {
			AlanHeritage heritage = heritage();
			addition.add(heritage); 
		}
		while (StartOf(5)) {
			AlanModelList property = property();
			addition.add(property); 
		}
		addtail();
		addition.length = t.pos-start+t.val.length(); 
		return addition;
	}

	AlanEvent  event() {
		AlanEvent  event;
		String name; int start; 
		Expect(63);
		start = t.pos; 
		ID();
		name = t.val; 
		statements();
		eventtail();
		event = new AlanEvent(name, start, t.pos-start+t.val.length()); 
		return event;
	}

	void statements() {
		statement();
		while (StartOf(3)) {
			statement();
		}
	}

	AlanModelList  attributes() {
		AlanModelList  attributes;
		attributes = new AlanModelList(); 
		AlanAttribute attribute = attributedeclaration();
		attributes.add(attribute); 
		while (StartOf(6)) {
			AlanAttribute attribute2 = attributedeclaration();
			attributes.add(attribute2); 
		}
		return attributes;
	}

	AlanAttribute  attributedeclaration() {
		AlanAttribute  attribute;
		AlanAttribute definition = attributedefinition();
		attribute = definition; 
		Expect(9);
		return attribute;
	}

	AlanAttribute  attributedefinition() {
		AlanAttribute  attribute;
		boolean not = false; int start = 0; String name; String kind = "boolean"; 
		if (la.kind == 6) {
			Get();
			not = true; start = t.pos; 
		}
		ID();
		if (!not) start = t.pos; name = t.val; 
		if (StartOf(7)) {
			String valueKind = attributevalue();
			kind = valueKind; 
		}
		attribute = new AlanAttribute(name, kind, start, t.pos-start+t.val.length()); 
		return attribute;
	}

	String  attributevalue() {
		String  kind;
		kind = ""; 
		if (la.kind == 2 || la.kind == 12) {
			if (la.kind == 12) {
				Get();
			}
			Expect(2);
			kind = "integer"; 
		} else if (la.kind == 3) {
			Get();
			kind = "string"; 
		} else if (StartOf(2)) {
			ID();
			kind = "reference"; 
		} else if (la.kind == 13) {
			Get();
			if (StartOf(8)) {
				setmembers();
			}
			Expect(14);
			kind = "set"; 
		} else SynErr(151);
		return kind;
	}

	void setmembers() {
		setmember();
		while (la.kind == 15) {
			Get();
			setmember();
		}
	}

	void setmember() {
		if (StartOf(9)) {
			what();
		} else if (la.kind == 2 || la.kind == 12) {
			if (la.kind == 12) {
				Get();
			}
			Expect(2);
		} else if (la.kind == 3) {
			Get();
		} else SynErr(152);
	}

	void what() {
		simplewhat();
		while (la.kind == 19 || la.kind == 124) {
			if (la.kind == 19) {
				Get();
			} else {
				Get();
			}
			what();
		}
	}

	AlanSynonym  synonym() {
		AlanSynonym  synonym;
		int start; 
		Token firstId = idlist();
		start = firstId.pos; 
		Expect(17);
		ID();
		synonym = new AlanSynonym("for " + t.val, start, t.pos-start+t.val.length()); 
		Expect(9);
		return synonym;
	}

	Token  idlist() {
		Token  firstId;
		ID();
		firstId = t; 
		while (la.kind == 15) {
			Get();
			ID();
		}
		return firstId;
	}

	AlanMessage  message() {
		AlanMessage  message;
		int start; String name; 
		ID();
		start = t.pos; name = t.val; 
		Expect(19);
		statements();
		message = new AlanMessage(name, start, t.pos-start+t.val.length()); 
		return message;
	}

	AlanSyntax  syntaxitem() {
		AlanSyntax  syntax;
		int start; String name; 
		ID();
		start = t.pos; name = t.val; 
		Expect(17);
		syntaxelements();
		optionalsyntaxrestrictions();
		syntax = new AlanSyntax(name, start, t.pos-start+t.val.length()); 
		return syntax;
	}

	void syntaxelements() {
		syntaxelement();
		while (StartOf(10)) {
			syntaxelement();
		}
	}

	void optionalsyntaxrestrictions() {
		if (la.kind == 9) {
			Get();
		} else if (la.kind == 30) {
			Get();
			syntaxrestrictionclauses();
		} else SynErr(153);
	}

	void syntaxelement() {
		if (StartOf(2)) {
			ID();
		} else if (la.kind == 21) {
			Get();
			ID();
			Expect(22);
			if (StartOf(11)) {
				if (la.kind == 23) {
					Get();
				} else if (la.kind == 24) {
					Get();
				} else if (la.kind == 25) {
					Get();
				} else {
					Get();
				}
			}
		} else SynErr(154);
	}

	void syntaxrestrictionclauses() {
		syntaxrestriction();
		while (la.kind == 27) {
			Get();
			syntaxrestriction();
		}
	}

	void syntaxrestriction() {
		ID();
		Expect(28);
		restrictionclass();
		Expect(29);
		statements();
	}

	void restrictionclass() {
		if (StartOf(2)) {
			ID();
		} else if (la.kind == 5) {
			Get();
		} else SynErr(155);
	}

	void verbbody() {
		if (la.kind == 33 || la.kind == 34 || la.kind == 35) {
			simpleverbbody();
		} else if (la.kind == 32) {
			verbalternatives();
		} else SynErr(156);
	}

	void verbtail() {
		Expect(33);
		Expect(31);
		if (StartOf(2)) {
			ID();
		}
		Expect(9);
	}

	void simpleverbbody() {
		if (la.kind == 34) {
			checks();
		}
		if (la.kind == 35) {
			does();
		}
	}

	void verbalternatives() {
		verbalternative();
		while (la.kind == 32) {
			verbalternative();
		}
	}

	void verbalternative() {
		Expect(32);
		ID();
		simpleverbbody();
	}

	void checks() {
		Expect(34);
		if (StartOf(3)) {
			statements();
		} else if (StartOf(12)) {
			checklist();
		} else SynErr(157);
	}

	void does() {
		Expect(35);
		optionalqual();
		statements();
	}

	void checklist() {
		check();
		while (la.kind == 27) {
			Get();
			check();
		}
	}

	void check() {
		expression();
		Expect(29);
		statements();
	}

	void expression() {
		term();
		while (la.kind == 120) {
			Get();
			expression();
		}
	}

	void optionalqual() {
		if (StartOf(3)) {
		} else if (la.kind == 144) {
			Get();
		} else if (la.kind == 66) {
			Get();
		} else if (la.kind == 145) {
			Get();
		} else SynErr(158);
	}

	AlanHeritage  heritage() {
		AlanHeritage  heritage;
		int start; 
		Expect(28);
		start = t.pos; 
		ID();
		heritage = new AlanHeritage(t.val, start, t.pos-start+t.val.length()); 
		if (la.kind == 9) {
			Get();
		}
		return heritage;
	}

	AlanModelList  property() {
		AlanModelList  property;
		property = null; 
		switch (la.kind) {
		case 83: case 123: case 132: case 133: case 134: case 135: {
			where();
			if (la.kind == 9) {
				Get();
			}
			break;
		}
		case 44: case 45: case 46: case 47: {
			is();
			AlanModelList attributes = attributes();
			property = attributes; 
			break;
		}
		case 48: {
			description();
			break;
		}
		case 54: {
			name();
			break;
		}
		case 55: {
			pronoun();
			break;
		}
		case 52: {
			initialize();
			break;
		}
		case 53: {
			mentioned();
			break;
		}
		case 40: {
			Get();
			articleorform();
			break;
		}
		case 41: case 49: case 50: {
			if (la.kind == 41) {
				Get();
			}
			articleorform();
			break;
		}
		case 42: {
			Get();
			articleorform();
			break;
		}
		case 4: case 5: case 56: {
			containerproperties();
			break;
		}
		case 31: {
			AlanVerb verb = verb();
			break;
		}
		case 64: {
			script();
			break;
		}
		case 51: {
			entered();
			break;
		}
		case 43: {
			exit();
			break;
		}
		default: SynErr(159); break;
		}
		return property;
	}

	void classtail() {
		Expect(33);
		Expect(36);
		if (StartOf(2)) {
			ID();
		}
		if (la.kind == 9) {
			Get();
		}
	}

	void addtail() {
		Expect(33);
		Expect(37);
		if (la.kind == 38) {
			Get();
		}
		if (StartOf(2)) {
			ID();
		}
		if (la.kind == 9) {
			Get();
		}
	}

	void instancetail() {
		Expect(33);
		Expect(39);
		if (StartOf(2)) {
			ID();
		}
		if (la.kind == 9) {
			Get();
		}
	}

	void where() {
		if (la.kind == 123) {
			Get();
		}
		properwhere();
	}

	void is() {
		if (la.kind == 44) {
			Get();
		} else if (la.kind == 45) {
			Get();
		} else if (la.kind == 46) {
			Get();
		} else if (la.kind == 47) {
			Get();
		} else SynErr(160);
	}

	void description() {
		Expect(48);
		if (StartOf(13)) {
			if (la.kind == 34) {
				checks();
			}
			if (la.kind == 35) {
				does();
			}
		} else if (StartOf(3)) {
			statements();
		} else SynErr(161);
	}

	void name() {
		Expect(54);
		ids();
		if (la.kind == 9) {
			Get();
		}
	}

	void pronoun() {
		Expect(55);
		Token firstId = idlist();
		if (la.kind == 9) {
			Get();
		}
	}

	void initialize() {
		Expect(52);
		statements();
	}

	void mentioned() {
		Expect(53);
		statements();
	}

	void articleorform() {
		if (la.kind == 49) {
			article();
		} else if (la.kind == 50) {
			form();
		} else SynErr(162);
	}

	void containerproperties() {
		if (la.kind == 56) {
			Get();
		}
		if (la.kind == 4) {
			Get();
		}
		Expect(5);
		containerbody();
	}

	void script() {
		Expect(64);
		ID();
		if (la.kind == 9) {
			Get();
		}
		if (la.kind == 48) {
			description();
		}
		steplist();
	}

	void entered() {
		Expect(51);
		statements();
	}

	void exit() {
		Expect(43);
		Token firstId = idlist();
		Expect(38);
		ID();
		if (la.kind == 33 || la.kind == 34 || la.kind == 35) {
			exitbody();
		}
		Expect(9);
	}

	void exitbody() {
		if (la.kind == 34) {
			checks();
		}
		if (la.kind == 35) {
			does();
		}
		Expect(33);
		Expect(43);
		if (StartOf(2)) {
			ID();
		}
	}

	void article() {
		Expect(49);
		if (StartOf(3)) {
			statements();
		}
	}

	void form() {
		Expect(50);
		if (StartOf(3)) {
			statements();
		}
	}

	void ids() {
		ID();
		while (StartOf(2)) {
			ID();
		}
	}

	void containerbody() {
		if (StartOf(14)) {
			if (la.kind == 57) {
				taking();
			}
			if (la.kind == 58) {
				limits();
			}
			if (la.kind == 61) {
				header();
			}
			if (la.kind == 29) {
				empty();
			}
			if (la.kind == 62) {
				extract();
			}
		} else if (la.kind == 9) {
			Get();
		} else SynErr(163);
	}

	void taking() {
		Expect(57);
		ID();
		Expect(9);
	}

	void limits() {
		Expect(58);
		limitlist();
	}

	void header() {
		Expect(61);
		statements();
	}

	void empty() {
		Expect(29);
		statements();
	}

	void extract() {
		Expect(62);
		if (StartOf(15)) {
			if (la.kind == 34) {
				checks();
			}
			if (la.kind == 35) {
				does();
			}
		} else if (StartOf(3)) {
			statements();
		} else SynErr(164);
	}

	void limitlist() {
		limit();
		while (StartOf(16)) {
			limit();
		}
	}

	void limit() {
		limitattribute();
		elseorthen();
		statements();
	}

	void limitattribute() {
		if (StartOf(6)) {
			AlanAttribute attribute = attributedefinition();
		} else if (la.kind == 60) {
			Get();
			Expect(2);
		} else SynErr(165);
	}

	void elseorthen() {
		if (la.kind == 29) {
			Get();
		} else if (la.kind == 59) {
			Get();
		} else SynErr(166);
	}

	void eventtail() {
		Expect(33);
		Expect(63);
		if (StartOf(2)) {
			ID();
		}
		Expect(9);
	}

	void steplist() {
		step();
		while (la.kind == 65) {
			step();
		}
	}

	void step() {
		Expect(65);
		if (la.kind == 66 || la.kind == 67) {
			stepcondition();
		}
		statements();
	}

	void stepcondition() {
		if (la.kind == 66) {
			Get();
			expression();
		} else if (la.kind == 67) {
			Get();
			Expect(68);
			expression();
		} else SynErr(167);
	}

	void then() {
		if (la.kind == 69) {
			Get();
		} else if (la.kind == 59) {
			Get();
		} else SynErr(168);
	}

	void endwhen() {
		Expect(33);
		Expect(32);
		if (la.kind == 9) {
			Get();
		}
	}

	void statement() {
		switch (la.kind) {
		case 3: case 71: case 72: case 73: case 74: case 75: case 76: {
			outputstatement();
			break;
		}
		case 112: case 113: case 114: case 115: case 116: case 117: case 118: case 119: {
			specialstatement();
			break;
		}
		case 80: case 81: case 82: case 84: {
			manipulationstatement();
			break;
		}
		case 110: case 111: {
			actorstatement();
			break;
		}
		case 86: case 87: {
			eventstatement();
			break;
		}
		case 88: case 89: case 90: case 92: case 93: {
			assignmentstatement();
			break;
		}
		case 108: case 109: {
			repetitionstatement();
			break;
		}
		case 101: case 103: {
			conditionalstatement();
			break;
		}
		default: SynErr(169); break;
		}
	}

	void outputstatement() {
		switch (la.kind) {
		case 3: {
			Get();
			break;
		}
		case 71: {
			Get();
			what();
			Expect(9);
			break;
		}
		case 72: {
			Get();
			if (StartOf(17)) {
				sayform();
			}
			expression();
			Expect(9);
			break;
		}
		case 73: {
			Get();
			primary();
			Expect(9);
			break;
		}
		case 74: {
			Get();
			ID();
			Expect(9);
			break;
		}
		case 75: {
			Get();
			ID();
			Expect(9);
			break;
		}
		case 76: {
			Get();
			ID();
			Expect(9);
			break;
		}
		default: SynErr(170); break;
		}
	}

	void specialstatement() {
		switch (la.kind) {
		case 112: {
			Get();
			Expect(9);
			break;
		}
		case 113: {
			Get();
			Expect(9);
			break;
		}
		case 114: {
			Get();
			Expect(9);
			break;
		}
		case 115: {
			Get();
			Expect(9);
			break;
		}
		case 116: {
			Get();
			Expect(9);
			break;
		}
		case 117: {
			Get();
			if (la.kind == 2) {
				Get();
			}
			Expect(9);
			break;
		}
		case 118: {
			Get();
			Expect(2);
			Expect(9);
			break;
		}
		case 119: {
			Get();
			Expect(3);
			Expect(9);
			break;
		}
		default: SynErr(171); break;
		}
	}

	void manipulationstatement() {
		if (la.kind == 80) {
			Get();
			primary();
			if (StartOf(18)) {
				where();
			}
			Expect(9);
		} else if (la.kind == 81) {
			Get();
			primary();
			where();
			Expect(9);
		} else if (la.kind == 82) {
			Get();
			primary();
			Expect(83);
			what();
			Expect(9);
		} else if (la.kind == 84) {
			Get();
			primary();
			Expect(85);
			what();
			Expect(9);
		} else SynErr(172);
	}

	void actorstatement() {
		if (la.kind == 110) {
			Get();
			what();
			Expect(9);
		} else if (la.kind == 111) {
			Get();
			Expect(64);
			ID();
			if (la.kind == 108) {
				foractor();
			}
			Expect(9);
		} else SynErr(173);
	}

	void eventstatement() {
		if (la.kind == 86) {
			Get();
			what();
			Expect(9);
		} else if (la.kind == 87) {
			Get();
			what();
			if (StartOf(18)) {
				where();
			}
			Expect(66);
			expression();
			Expect(9);
		} else SynErr(174);
	}

	void assignmentstatement() {
		if (la.kind == 88) {
			Get();
			primary();
			something();
			Expect(9);
		} else if (la.kind == 89) {
			Get();
			if (la.kind == 94 || la.kind == 95) {
				firstorlast();
			}
			if (StartOf(12)) {
				expression();
			}
			if (StartOf(19)) {
				wordorcharacter();
			}
			Expect(85);
			expression();
			if (la.kind == 100) {
				into();
			}
			Expect(9);
		} else if (la.kind == 90) {
			Get();
			what();
			if (la.kind == 91) {
				Get();
				expression();
			}
			Expect(9);
		} else if (la.kind == 92) {
			Get();
			what();
			if (la.kind == 91) {
				Get();
				expression();
			}
			Expect(9);
		} else if (la.kind == 93) {
			Get();
			what();
			Expect(38);
			expression();
			Expect(9);
		} else SynErr(175);
	}

	void repetitionstatement() {
		foreach();
		ID();
		if (StartOf(20)) {
			loopfilters();
		}
		Expect(106);
		statements();
		Expect(33);
		foreach();
		if (la.kind == 9) {
			Get();
		}
	}

	void conditionalstatement() {
		if (la.kind == 101) {
			ifstatement();
		} else if (la.kind == 103) {
			dependingstatement();
		} else SynErr(176);
	}

	void sayform() {
		if (la.kind == 39) {
			Get();
		} else if (la.kind == 77) {
			Get();
		} else if (la.kind == 78) {
			Get();
		} else if (la.kind == 79) {
			Get();
		} else SynErr(177);
	}

	void primary() {
		switch (la.kind) {
		case 2: case 12: {
			if (la.kind == 12) {
				Get();
			}
			Expect(2);
			break;
		}
		case 3: {
			Get();
			break;
		}
		case 1: case 4: case 57: case 78: case 94: case 100: case 104: case 124: case 128: case 129: case 130: case 131: {
			what();
			break;
		}
		case 117: {
			Get();
			break;
		}
		case 13: {
			Get();
			if (StartOf(8)) {
				setmembers();
			}
			Expect(14);
			break;
		}
		case 21: {
			Get();
			expression();
			Expect(22);
			break;
		}
		case 122: {
			Get();
			randomwhat();
			break;
		}
		default: SynErr(178); break;
		}
	}

	void something() {
		if (la.kind == 6) {
			Get();
		}
		ID();
	}

	void firstorlast() {
		if (la.kind == 94) {
			Get();
		} else if (la.kind == 95) {
			Get();
		} else SynErr(179);
	}

	void wordorcharacter() {
		if (la.kind == 96) {
			Get();
		} else if (la.kind == 97) {
			Get();
		} else if (la.kind == 98) {
			Get();
		} else if (la.kind == 99) {
			Get();
		} else SynErr(180);
	}

	void into() {
		Expect(100);
		expression();
	}

	void ifstatement() {
		Expect(101);
		expression();
		Expect(59);
		statements();
		if (la.kind == 102) {
			elsiflist();
		}
		if (la.kind == 29) {
			elsepart();
		}
		Expect(33);
		Expect(101);
		Expect(9);
	}

	void dependingstatement() {
		Expect(103);
		Expect(104);
		primary();
		dependcases();
		Expect(33);
		genSym12();
		Expect(9);
	}

	void elsiflist() {
		Expect(102);
		expression();
		Expect(59);
		statements();
		while (la.kind == 102) {
			Get();
			expression();
			Expect(59);
			statements();
		}
	}

	void elsepart() {
		Expect(29);
		statements();
	}

	void dependcases() {
		dependcase();
		while (StartOf(21)) {
			dependcase();
		}
	}

	void genSym12() {
		if (la.kind == 105) {
			Get();
		} else if (la.kind == 103) {
			Get();
		} else SynErr(181);
	}

	void dependcase() {
		if (la.kind == 29) {
			Get();
			statements();
		} else if (StartOf(22)) {
			righthandside();
			Expect(59);
			statements();
		} else SynErr(182);
	}

	void righthandside() {
		if (StartOf(23)) {
			filter();
		} else if (StartOf(24)) {
			if (la.kind == 6) {
				Get();
			}
			realrighthandside();
		} else SynErr(183);
	}

	void foreach() {
		if (la.kind == 108) {
			Get();
			if (la.kind == 109) {
				Get();
			}
		} else if (la.kind == 109) {
			Get();
		} else SynErr(184);
	}

	void loopfilters() {
		if (StartOf(23)) {
			filters();
		} else if (la.kind == 107) {
			Get();
			factor();
			Expect(27);
			factor();
		} else SynErr(185);
	}

	void filters() {
		filter();
		while (la.kind == 15) {
			Get();
			filter();
		}
	}

	void factor() {
		factorleft();
		if (StartOf(25)) {
			if (la.kind == 6) {
				Get();
			}
			factorright();
		}
	}

	void foractor() {
		Expect(108);
		what();
	}

	void term() {
		factor();
		while (la.kind == 27) {
			Get();
			term();
		}
	}

	void factorleft() {
		if (StartOf(26)) {
			primary();
		} else if (StartOf(27)) {
			aggregate();
			filters();
		} else SynErr(186);
	}

	void factorright() {
		switch (la.kind) {
		case 28: {
			Get();
			ID();
			break;
		}
		case 44: case 45: case 46: case 47: {
			is();
			something();
			break;
		}
		case 12: case 23: case 136: case 137: {
			binop();
			factor();
			break;
		}
		case 83: case 123: case 132: case 133: case 134: case 135: {
			where();
			break;
		}
		case 17: case 138: case 139: case 140: case 141: case 142: case 143: {
			relop();
			factor();
			break;
		}
		case 121: {
			Get();
			factor();
			break;
		}
		case 107: {
			Get();
			factor();
			Expect(27);
			factor();
			break;
		}
		default: SynErr(187); break;
		}
	}

	void aggregate() {
		if (la.kind == 60) {
			Get();
		} else if (la.kind == 125 || la.kind == 126 || la.kind == 127) {
			aggregator();
			Expect(124);
			ID();
		} else SynErr(188);
	}

	void binop() {
		if (la.kind == 136) {
			Get();
		} else if (la.kind == 12) {
			Get();
		} else if (la.kind == 23) {
			Get();
		} else if (la.kind == 137) {
			Get();
		} else SynErr(189);
	}

	void relop() {
		switch (la.kind) {
		case 138: {
			Get();
			break;
		}
		case 17: {
			Get();
			break;
		}
		case 139: {
			Get();
			break;
		}
		case 140: {
			Get();
			break;
		}
		case 141: {
			Get();
			break;
		}
		case 142: {
			Get();
			break;
		}
		case 143: {
			Get();
			break;
		}
		default: SynErr(190); break;
		}
	}

	void filter() {
		if (StartOf(28)) {
			if (la.kind == 6) {
				Get();
			}
			if (StartOf(18)) {
				where();
			} else if (la.kind == 28) {
				Get();
				ID();
			} else SynErr(191);
		} else if (StartOf(29)) {
			is();
			something();
		} else SynErr(192);
	}

	void realrighthandside() {
		if (StartOf(30)) {
			relop();
			primary();
		} else if (la.kind == 121) {
			Get();
			factor();
		} else if (la.kind == 107) {
			Get();
			factor();
			Expect(27);
			factor();
		} else SynErr(193);
	}

	void randomwhat() {
		if (la.kind == 83 || la.kind == 123) {
			if (la.kind == 123) {
				Get();
			}
			Expect(83);
			primary();
		} else if (StartOf(26)) {
			primary();
			Expect(38);
			primary();
		} else SynErr(194);
	}

	void aggregator() {
		if (la.kind == 125) {
			Get();
		} else if (la.kind == 126) {
			Get();
		} else if (la.kind == 127) {
			Get();
		} else SynErr(195);
	}

	void simplewhat() {
		if (StartOf(2)) {
			ID();
		} else if (la.kind == 128) {
			Get();
		} else if (la.kind == 129) {
			Get();
			if (la.kind == 130) {
				Get();
			} else if (la.kind == 131) {
				Get();
			} else SynErr(196);
		} else SynErr(197);
	}

	void properwhere() {
		if (la.kind == 132) {
			Get();
		} else if (la.kind == 133) {
			Get();
		} else if (la.kind == 134) {
			Get();
			primary();
		} else if (la.kind == 83) {
			Get();
			primary();
		} else if (la.kind == 135) {
			Get();
			what();
		} else SynErr(198);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		adventure();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,T,T, x,x,x,x, T,x,T,x, T,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, T,T,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, T,x,x,x, T,T,T,x, T,x,T,T, T,T,T,x, T,T,x,x, x,x,x,x, x,T,x,T, x,x,x,x, T,T,T,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,T, T,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,T, T,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,T, T,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,T,x, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,T,T,T, x,x,x,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,T, x,T,x,x, x,x,x,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,T,T,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,T,T,T, x,x,x,x, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,T,T, T,T,T,T, x,x,x,x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,T,T, T,T,T,T, x,x,x,x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, x,x,x,x},
		{x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,T, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,T, x,x,x,x, x,x,x,x, T,T,T,T, T,T,T,T, T,T,T,T, x,x,x,x},
		{x,T,T,T, T,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,T,x, T,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, x,x,x,x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "Identifier expected"; break;
			case 2: s = "Integer expected"; break;
			case 3: s = "STRING expected"; break;
			case 4: s = "opaque expected"; break;
			case 5: s = "container expected"; break;
			case 6: s = "not expected"; break;
			case 7: s = "\"option\" expected"; break;
			case 8: s = "\"options\" expected"; break;
			case 9: s = "\".\" expected"; break;
			case 10: s = "\"prompt\" expected"; break;
			case 11: s = "\"import\" expected"; break;
			case 12: s = "\"-\" expected"; break;
			case 13: s = "\"{\" expected"; break;
			case 14: s = "\"}\" expected"; break;
			case 15: s = "\",\" expected"; break;
			case 16: s = "\"synonyms\" expected"; break;
			case 17: s = "\"=\" expected"; break;
			case 18: s = "\"message\" expected"; break;
			case 19: s = "\":\" expected"; break;
			case 20: s = "\"syntax\" expected"; break;
			case 21: s = "\"(\" expected"; break;
			case 22: s = "\")\" expected"; break;
			case 23: s = "\"*\" expected"; break;
			case 24: s = "\"!\" expected"; break;
			case 25: s = "\"*!\" expected"; break;
			case 26: s = "\"!*\" expected"; break;
			case 27: s = "\"and\" expected"; break;
			case 28: s = "\"isa\" expected"; break;
			case 29: s = "\"else\" expected"; break;
			case 30: s = "\"where\" expected"; break;
			case 31: s = "\"verb\" expected"; break;
			case 32: s = "\"when\" expected"; break;
			case 33: s = "\"end\" expected"; break;
			case 34: s = "\"check\" expected"; break;
			case 35: s = "\"does\" expected"; break;
			case 36: s = "\"every\" expected"; break;
			case 37: s = "\"add\" expected"; break;
			case 38: s = "\"to\" expected"; break;
			case 39: s = "\"the\" expected"; break;
			case 40: s = "\"definite\" expected"; break;
			case 41: s = "\"indefinite\" expected"; break;
			case 42: s = "\"negative\" expected"; break;
			case 43: s = "\"exit\" expected"; break;
			case 44: s = "\"is\" expected"; break;
			case 45: s = "\"are\" expected"; break;
			case 46: s = "\"has\" expected"; break;
			case 47: s = "\"can\" expected"; break;
			case 48: s = "\"description\" expected"; break;
			case 49: s = "\"article\" expected"; break;
			case 50: s = "\"form\" expected"; break;
			case 51: s = "\"entered\" expected"; break;
			case 52: s = "\"initialize\" expected"; break;
			case 53: s = "\"mentioned\" expected"; break;
			case 54: s = "\"name\" expected"; break;
			case 55: s = "\"pronoun\" expected"; break;
			case 56: s = "\"with\" expected"; break;
			case 57: s = "\"taking\" expected"; break;
			case 58: s = "\"limits\" expected"; break;
			case 59: s = "\"then\" expected"; break;
			case 60: s = "\"count\" expected"; break;
			case 61: s = "\"header\" expected"; break;
			case 62: s = "\"extract\" expected"; break;
			case 63: s = "\"event\" expected"; break;
			case 64: s = "\"script\" expected"; break;
			case 65: s = "\"step\" expected"; break;
			case 66: s = "\"after\" expected"; break;
			case 67: s = "\"wait\" expected"; break;
			case 68: s = "\"until\" expected"; break;
			case 69: s = "\"=>\" expected"; break;
			case 70: s = "\"start\" expected"; break;
			case 71: s = "\"describe\" expected"; break;
			case 72: s = "\"say\" expected"; break;
			case 73: s = "\"list\" expected"; break;
			case 74: s = "\"show\" expected"; break;
			case 75: s = "\"play\" expected"; break;
			case 76: s = "\"style\" expected"; break;
			case 77: s = "\"an\" expected"; break;
			case 78: s = "\"it\" expected"; break;
			case 79: s = "\"no\" expected"; break;
			case 80: s = "\"empty\" expected"; break;
			case 81: s = "\"locate\" expected"; break;
			case 82: s = "\"include\" expected"; break;
			case 83: s = "\"in\" expected"; break;
			case 84: s = "\"exclude\" expected"; break;
			case 85: s = "\"from\" expected"; break;
			case 86: s = "\"cancel\" expected"; break;
			case 87: s = "\"schedule\" expected"; break;
			case 88: s = "\"make\" expected"; break;
			case 89: s = "\"strip\" expected"; break;
			case 90: s = "\"increase\" expected"; break;
			case 91: s = "\"by\" expected"; break;
			case 92: s = "\"decrease\" expected"; break;
			case 93: s = "\"set\" expected"; break;
			case 94: s = "\"first\" expected"; break;
			case 95: s = "\"last\" expected"; break;
			case 96: s = "\"word\" expected"; break;
			case 97: s = "\"words\" expected"; break;
			case 98: s = "\"character\" expected"; break;
			case 99: s = "\"characters\" expected"; break;
			case 100: s = "\"into\" expected"; break;
			case 101: s = "\"if\" expected"; break;
			case 102: s = "\"elsif\" expected"; break;
			case 103: s = "\"depending\" expected"; break;
			case 104: s = "\"on\" expected"; break;
			case 105: s = "\"depend\" expected"; break;
			case 106: s = "\"do\" expected"; break;
			case 107: s = "\"between\" expected"; break;
			case 108: s = "\"for\" expected"; break;
			case 109: s = "\"each\" expected"; break;
			case 110: s = "\"stop\" expected"; break;
			case 111: s = "\"use\" expected"; break;
			case 112: s = "\"quit\" expected"; break;
			case 113: s = "\"look\" expected"; break;
			case 114: s = "\"save\" expected"; break;
			case 115: s = "\"restore\" expected"; break;
			case 116: s = "\"restart\" expected"; break;
			case 117: s = "\"score\" expected"; break;
			case 118: s = "\"visits\" expected"; break;
			case 119: s = "\"system\" expected"; break;
			case 120: s = "\"or\" expected"; break;
			case 121: s = "\"contains\" expected"; break;
			case 122: s = "\"random\" expected"; break;
			case 123: s = "\"directly\" expected"; break;
			case 124: s = "\"of\" expected"; break;
			case 125: s = "\"max\" expected"; break;
			case 126: s = "\"min\" expected"; break;
			case 127: s = "\"sum\" expected"; break;
			case 128: s = "\"this\" expected"; break;
			case 129: s = "\"current\" expected"; break;
			case 130: s = "\"actor\" expected"; break;
			case 131: s = "\"location\" expected"; break;
			case 132: s = "\"here\" expected"; break;
			case 133: s = "\"nearby\" expected"; break;
			case 134: s = "\"at\" expected"; break;
			case 135: s = "\"near\" expected"; break;
			case 136: s = "\"+\" expected"; break;
			case 137: s = "\"/\" expected"; break;
			case 138: s = "\"<>\" expected"; break;
			case 139: s = "\"==\" expected"; break;
			case 140: s = "\">=\" expected"; break;
			case 141: s = "\"<=\" expected"; break;
			case 142: s = "\">\" expected"; break;
			case 143: s = "\"<\" expected"; break;
			case 144: s = "\"before\" expected"; break;
			case 145: s = "\"only\" expected"; break;
			case 146: s = "??? expected"; break;
			case 147: s = "invalid options"; break;
			case 148: s = "invalid declaration"; break;
			case 149: s = "invalid ID"; break;
			case 150: s = "invalid optionvalue"; break;
			case 151: s = "invalid attributevalue"; break;
			case 152: s = "invalid setmember"; break;
			case 153: s = "invalid optionalsyntaxrestrictions"; break;
			case 154: s = "invalid syntaxelement"; break;
			case 155: s = "invalid restrictionclass"; break;
			case 156: s = "invalid verbbody"; break;
			case 157: s = "invalid checks"; break;
			case 158: s = "invalid optionalqual"; break;
			case 159: s = "invalid property"; break;
			case 160: s = "invalid is"; break;
			case 161: s = "invalid description"; break;
			case 162: s = "invalid articleorform"; break;
			case 163: s = "invalid containerbody"; break;
			case 164: s = "invalid extract"; break;
			case 165: s = "invalid limitattribute"; break;
			case 166: s = "invalid elseorthen"; break;
			case 167: s = "invalid stepcondition"; break;
			case 168: s = "invalid then"; break;
			case 169: s = "invalid statement"; break;
			case 170: s = "invalid outputstatement"; break;
			case 171: s = "invalid specialstatement"; break;
			case 172: s = "invalid manipulationstatement"; break;
			case 173: s = "invalid actorstatement"; break;
			case 174: s = "invalid eventstatement"; break;
			case 175: s = "invalid assignmentstatement"; break;
			case 176: s = "invalid conditionalstatement"; break;
			case 177: s = "invalid sayform"; break;
			case 178: s = "invalid primary"; break;
			case 179: s = "invalid firstorlast"; break;
			case 180: s = "invalid wordorcharacter"; break;
			case 181: s = "invalid genSym12"; break;
			case 182: s = "invalid dependcase"; break;
			case 183: s = "invalid righthandside"; break;
			case 184: s = "invalid foreach"; break;
			case 185: s = "invalid loopfilters"; break;
			case 186: s = "invalid factorleft"; break;
			case 187: s = "invalid factorright"; break;
			case 188: s = "invalid aggregate"; break;
			case 189: s = "invalid binop"; break;
			case 190: s = "invalid relop"; break;
			case 191: s = "invalid filter"; break;
			case 192: s = "invalid filter"; break;
			case 193: s = "invalid realrighthandside"; break;
			case 194: s = "invalid randomwhat"; break;
			case 195: s = "invalid aggregator"; break;
			case 196: s = "invalid simplewhat"; break;
			case 197: s = "invalid simplewhat"; break;
			case 198: s = "invalid properwhere"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
