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
	public static final int maxT = 151;

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
		if (la.kind == 72) {
			AlanModel start = start();
			if (start != null) model.add(start); 
		}
	}

	void options() {
		if (la.kind == 7) {
			Get();
		} else if (la.kind == 8) {
			Get();
		} else SynErr(152);
		option();
		while (StartOf(2)) {
			option();
		}
	}

	AlanModel  declaration() {
		AlanModel  declaration;
		declaration = null; 
		switch (la.kind) {
		case 12: {
			AlanModel importDeclaration = importstatement();
			declaration = importDeclaration; 
			break;
		}
		case 11: {
			prompt();
			break;
		}
		case 19: {
			AlanModel messageDeclaration = messages();
			declaration = messageDeclaration; 
			break;
		}
		case 38: {
			AlanModel classDeclaration = classdeclaration();
			declaration = classDeclaration; 
			break;
		}
		case 41: {
			AlanModel instanceDeclaration = instance();
			declaration = instanceDeclaration; 
			break;
		}
		case 34: {
			AlanModel ruleDeclaration = rule();
			declaration = ruleDeclaration; 
			break;
		}
		case 17: {
			AlanModel synonymsDeclaration = synonyms();
			declaration = synonymsDeclaration; 
			break;
		}
		case 21: {
			AlanModel syntaxDeclaration = syntax();
			declaration = syntaxDeclaration; 
			break;
		}
		case 32: case 33: {
			AlanModel verbDeclaration = verb();
			declaration = verbDeclaration; 
			break;
		}
		case 39: {
			AlanModel additionDeclaration = addition();
			declaration = additionDeclaration; 
			break;
		}
		case 65: {
			AlanModel eventDeclaration = event();
			declaration = eventDeclaration; 
			break;
		}
		default: SynErr(153); break;
		}
		return declaration;
	}

	AlanModel  start() {
		AlanModel  startsection;
		int start; 
		Expect(72);
		start = t.pos; 
		where();
		Expect(10);
		if (StartOf(3)) {
			statements();
		}
		startsection = new AlanStart(start, t.pos-start+t.val.length()); 
		return startsection;
	}

	void option() {
		if (la.kind == 9) {
			Get();
		}
		ID();
		if (StartOf(4)) {
			optionvalue();
		}
		Expect(10);
	}

	void ID() {
		switch (la.kind) {
		case 1: {
			Get();
			break;
		}
		case 133: {
			Get();
			break;
		}
		case 132: {
			Get();
			break;
		}
		case 4: {
			Get();
			break;
		}
		case 121: {
			Get();
			break;
		}
		case 124: {
			Get();
			break;
		}
		case 105: {
			Get();
			break;
		}
		case 80: {
			Get();
			break;
		}
		case 126: {
			Get();
			break;
		}
		case 95: {
			Get();
			break;
		}
		case 101: {
			Get();
			break;
		}
		case 59: {
			Get();
			break;
		}
		case 122: {
			Get();
			break;
		}
		default: SynErr(154); break;
		}
	}

	void optionvalue() {
		if (StartOf(5)) {
			ID();
		} else if (la.kind == 2) {
			Get();
		} else SynErr(155);
	}

	AlanImport  importstatement() {
		AlanImport  importDeclaration;
		int start; String name; 
		Expect(12);
		start = t.pos; 
		ID();
		if (t.val.charAt(0) == '\'') name = t.val.substring(1, t.val.length()-1); else name = t.val; 
		Expect(10);
		importDeclaration = new AlanImport(name, start, t.pos-start+t.val.length()); 
		return importDeclaration;
	}

	void prompt() {
		Expect(11);
		statements();
	}

	AlanMessage  messages() {
		AlanMessage  message;
		int start; 
		Expect(19);
		start = t.pos; 
		AlanMessage message2 = message();
		message = new AlanMessage("Messages", start, 0); message.add(message2); 
		while (StartOf(5)) {
			AlanMessage message3 = message();
			message.add(message3); 
		}
		message.length = t.pos-start+t.val.length(); 
		return message;
	}

	AlanClass  classdeclaration() {
		AlanClass  declaration;
		int start; 
		Expect(38);
		start = t.pos; 
		ID();
		declaration = new AlanClass(t.val, start, 0); 
		if (la.kind == 29) {
			AlanHeritage heritage = heritage();
			declaration.add(heritage); 
		}
		while (StartOf(6)) {
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
		Expect(41);
		start = t.pos; 
		ID();
		declaration = new AlanInstance(t.val, start, 0); 
		if (la.kind == 29) {
			AlanHeritage heritage = heritage();
			declaration.add(heritage); 
		}
		while (StartOf(6)) {
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
		Expect(34);
		start = t.pos; 
		expression();
		then();
		statements();
		if (la.kind == 35) {
			endwhen();
		}
		rule = new AlanRule(start, t.pos-start+t.val.length()); 
		return rule;
	}

	AlanSynonym  synonyms() {
		AlanSynonym  synonyms;
		int start; 
		Expect(17);
		start = t.pos; synonyms = new AlanSynonym("Synonyms", start, 0);
		AlanSynonym synonym = synonym();
		synonyms.add(synonym); 
		while (StartOf(5)) {
			AlanSynonym synonym2 = synonym();
			synonyms.add(synonym2); 
		}
		synonyms.length = t.pos-start+t.val.length(); 
		return synonyms;
	}

	AlanSyntax  syntax() {
		AlanSyntax  syntaxList;
		int start; 
		Expect(21);
		start = t.pos; syntaxList = new AlanSyntax("Syntax", start, 0);
		AlanSyntax syntax = syntaxitem();
		syntaxList.add(syntax); 
		while (StartOf(5)) {
			AlanSyntax syntax2 = syntaxitem();
			syntaxList.add(syntax2); 
		}
		syntaxList.length = t.pos-start+t.val.length(); 
		return syntaxList;
	}

	AlanVerb  verb() {
		AlanVerb  verb;
		int start; String name; 
		if (la.kind == 32) {
			Get();
		}
		Expect(33);
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
		Expect(39);
		start = t.pos; 
		Expect(40);
		if (la.kind == 38) {
			Get();
		}
		ID();
		addition = new AlanAddition(t.val, start, 0); 
		if (la.kind == 29) {
			AlanHeritage heritage = heritage();
			addition.add(heritage); 
		}
		while (StartOf(6)) {
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
		Expect(65);
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
		while (StartOf(7)) {
			AlanAttribute attribute2 = attributedeclaration();
			attributes.add(attribute2); 
		}
		return attributes;
	}

	AlanAttribute  attributedeclaration() {
		AlanAttribute  attribute;
		AlanAttribute definition = attributedefinition();
		attribute = definition; 
		Expect(10);
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
		if (StartOf(8)) {
			String valueKind = attributevalue();
			kind = valueKind; 
		}
		attribute = new AlanAttribute(name, kind, start, t.pos-start+t.val.length()); 
		return attribute;
	}

	String  attributevalue() {
		String  kind;
		kind = ""; 
		if (la.kind == 3) {
			Get();
			kind = "string"; 
		} else if (StartOf(5)) {
			ID();
			kind = "reference"; 
		} else if (la.kind == 2 || la.kind == 13) {
			if (la.kind == 13) {
				Get();
			}
			Expect(2);
			kind = "integer"; 
		} else if (la.kind == 14) {
			Get();
			if (StartOf(9)) {
				setmembers();
			}
			Expect(15);
			kind = "set"; 
		} else SynErr(156);
		return kind;
	}

	void setmembers() {
		setmember();
		while (la.kind == 16) {
			Get();
			setmember();
		}
	}

	void setmember() {
		if (StartOf(10)) {
			what();
		} else if (la.kind == 3) {
			Get();
		} else if (la.kind == 2 || la.kind == 13) {
			if (la.kind == 13) {
				Get();
			}
			Expect(2);
		} else SynErr(157);
	}

	void what() {
		simplewhat();
		while (la.kind == 20 || la.kind == 126) {
			if (la.kind == 20) {
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
		Expect(18);
		ID();
		synonym = new AlanSynonym("for " + t.val, start, t.pos-start+t.val.length()); 
		Expect(10);
		return synonym;
	}

	Token  idlist() {
		Token  firstId;
		ID();
		firstId = t; 
		while (la.kind == 16) {
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
		Expect(20);
		statements();
		message = new AlanMessage(name, start, t.pos-start+t.val.length()); 
		return message;
	}

	AlanSyntax  syntaxitem() {
		AlanSyntax  syntax;
		int start; String name; 
		ID();
		start = t.pos; name = t.val; 
		Expect(18);
		syntaxelements();
		optionalsyntaxrestrictions();
		syntax = new AlanSyntax(name, start, t.pos-start+t.val.length()); 
		return syntax;
	}

	void syntaxelements() {
		syntaxelement();
		while (StartOf(11)) {
			syntaxelement();
		}
	}

	void optionalsyntaxrestrictions() {
		if (la.kind == 10) {
			Get();
		} else if (la.kind == 31) {
			Get();
			syntaxrestrictionclauses();
		} else SynErr(158);
	}

	void syntaxelement() {
		if (StartOf(5)) {
			ID();
		} else if (la.kind == 22) {
			Get();
			ID();
			Expect(23);
			if (StartOf(12)) {
				if (la.kind == 24) {
					Get();
				} else if (la.kind == 25) {
					Get();
				} else if (la.kind == 26) {
					Get();
				} else {
					Get();
				}
			}
		} else SynErr(159);
	}

	void syntaxrestrictionclauses() {
		syntaxrestriction();
		while (la.kind == 28) {
			Get();
			syntaxrestriction();
		}
	}

	void syntaxrestriction() {
		ID();
		Expect(29);
		restrictionclass();
		Expect(30);
		statements();
	}

	void restrictionclass() {
		if (StartOf(5)) {
			ID();
		} else if (la.kind == 5) {
			Get();
		} else SynErr(160);
	}

	void verbbody() {
		if (la.kind == 35 || la.kind == 36 || la.kind == 37) {
			simpleverbbody();
		} else if (la.kind == 34) {
			verbalternatives();
		} else SynErr(161);
	}

	void verbtail() {
		Expect(35);
		Expect(33);
		if (StartOf(5)) {
			ID();
		}
		Expect(10);
	}

	void simpleverbbody() {
		if (la.kind == 36) {
			checks();
		}
		if (la.kind == 37) {
			does();
		}
	}

	void verbalternatives() {
		verbalternative();
		while (la.kind == 34) {
			verbalternative();
		}
	}

	void verbalternative() {
		Expect(34);
		ID();
		simpleverbbody();
	}

	void checks() {
		Expect(36);
		if (StartOf(3)) {
			statements();
		} else if (StartOf(13)) {
			checklist();
		} else SynErr(162);
	}

	void does() {
		Expect(37);
		optionalqual();
		statements();
	}

	void checklist() {
		check();
		while (la.kind == 28) {
			Get();
			check();
		}
	}

	void check() {
		expression();
		Expect(30);
		statements();
	}

	void expression() {
		term();
		while (la.kind == 123) {
			Get();
			expression();
		}
	}

	void optionalqual() {
		if (StartOf(3)) {
		} else if (la.kind == 149) {
			Get();
		} else if (la.kind == 68) {
			Get();
		} else if (la.kind == 150) {
			Get();
		} else SynErr(163);
	}

	AlanHeritage  heritage() {
		AlanHeritage  heritage;
		int start; 
		Expect(29);
		start = t.pos; 
		ID();
		heritage = new AlanHeritage(t.val, start, t.pos-start+t.val.length()); 
		if (la.kind == 10) {
			Get();
		}
		return heritage;
	}

	AlanModelList  property() {
		AlanModelList  property;
		property = null; 
		switch (la.kind) {
		case 84: case 134: case 135: case 136: case 137: case 138: case 139: case 140: {
			where();
			if (la.kind == 10) {
				Get();
			}
			break;
		}
		case 46: case 47: case 48: case 49: {
			is();
			AlanModelList attributes = attributes();
			property = attributes; 
			break;
		}
		case 50: {
			description();
			break;
		}
		case 56: {
			name();
			break;
		}
		case 57: {
			pronoun();
			break;
		}
		case 54: {
			initialize();
			break;
		}
		case 55: {
			mentioned();
			break;
		}
		case 42: {
			Get();
			articleorform();
			break;
		}
		case 43: case 51: case 52: {
			if (la.kind == 43) {
				Get();
			}
			articleorform();
			break;
		}
		case 44: {
			Get();
			articleorform();
			break;
		}
		case 4: case 5: case 58: {
			containerproperties();
			break;
		}
		case 32: case 33: {
			AlanVerb verb = verb();
			break;
		}
		case 66: {
			script();
			break;
		}
		case 53: {
			entered();
			break;
		}
		case 45: {
			exit();
			break;
		}
		default: SynErr(164); break;
		}
		return property;
	}

	void classtail() {
		Expect(35);
		Expect(38);
		if (StartOf(5)) {
			ID();
		}
		if (la.kind == 10) {
			Get();
		}
	}

	void addtail() {
		Expect(35);
		Expect(39);
		if (la.kind == 40) {
			Get();
		}
		if (StartOf(5)) {
			ID();
		}
		if (la.kind == 10) {
			Get();
		}
	}

	void instancetail() {
		Expect(35);
		Expect(41);
		if (StartOf(5)) {
			ID();
		}
		if (la.kind == 10) {
			Get();
		}
	}

	void where() {
		if (la.kind == 138 || la.kind == 139 || la.kind == 140) {
			transitivity();
		}
		properwhere();
	}

	void is() {
		if (la.kind == 46) {
			Get();
		} else if (la.kind == 47) {
			Get();
		} else if (la.kind == 48) {
			Get();
		} else if (la.kind == 49) {
			Get();
		} else SynErr(165);
	}

	void description() {
		Expect(50);
		if (StartOf(14)) {
			if (la.kind == 36) {
				checks();
			}
			if (la.kind == 37) {
				does();
			}
		} else if (StartOf(3)) {
			statements();
		} else SynErr(166);
	}

	void name() {
		Expect(56);
		ids();
		if (la.kind == 10) {
			Get();
		}
	}

	void pronoun() {
		Expect(57);
		Token firstId = idlist();
		if (la.kind == 10) {
			Get();
		}
	}

	void initialize() {
		Expect(54);
		statements();
	}

	void mentioned() {
		Expect(55);
		statements();
	}

	void articleorform() {
		if (la.kind == 51) {
			article();
		} else if (la.kind == 52) {
			form();
		} else SynErr(167);
	}

	void containerproperties() {
		if (la.kind == 58) {
			Get();
		}
		if (la.kind == 4) {
			Get();
		}
		Expect(5);
		containerbody();
	}

	void script() {
		Expect(66);
		ID();
		if (la.kind == 10) {
			Get();
		}
		if (la.kind == 50) {
			description();
		}
		steplist();
	}

	void entered() {
		Expect(53);
		statements();
	}

	void exit() {
		Expect(45);
		Token firstId = idlist();
		Expect(40);
		ID();
		if (la.kind == 35 || la.kind == 36 || la.kind == 37) {
			exitbody();
		}
		Expect(10);
	}

	void exitbody() {
		if (la.kind == 36) {
			checks();
		}
		if (la.kind == 37) {
			does();
		}
		Expect(35);
		Expect(45);
		if (StartOf(5)) {
			ID();
		}
	}

	void article() {
		Expect(51);
		if (StartOf(3)) {
			statements();
		}
	}

	void form() {
		Expect(52);
		if (StartOf(3)) {
			statements();
		}
	}

	void ids() {
		ID();
		while (StartOf(5)) {
			ID();
		}
	}

	void containerbody() {
		if (StartOf(15)) {
			if (la.kind == 59) {
				taking();
			}
			if (la.kind == 60) {
				limits();
			}
			if (la.kind == 63) {
				header();
			}
			if (la.kind == 30) {
				empty();
			}
			if (la.kind == 64) {
				extract();
			}
		} else if (la.kind == 10) {
			Get();
		} else SynErr(168);
	}

	void taking() {
		Expect(59);
		ID();
		Expect(10);
	}

	void limits() {
		Expect(60);
		limitlist();
	}

	void header() {
		Expect(63);
		statements();
	}

	void empty() {
		Expect(30);
		statements();
	}

	void extract() {
		Expect(64);
		if (StartOf(16)) {
			if (la.kind == 36) {
				checks();
			}
			if (la.kind == 37) {
				does();
			}
		} else if (StartOf(3)) {
			statements();
		} else SynErr(169);
	}

	void limitlist() {
		limit();
		while (StartOf(17)) {
			limit();
		}
	}

	void limit() {
		limitattribute();
		elseorthen();
		statements();
	}

	void limitattribute() {
		if (StartOf(7)) {
			AlanAttribute attribute = attributedefinition();
		} else if (la.kind == 62) {
			Get();
			Expect(2);
		} else SynErr(170);
	}

	void elseorthen() {
		if (la.kind == 30) {
			Get();
		} else if (la.kind == 61) {
			Get();
		} else SynErr(171);
	}

	void eventtail() {
		Expect(35);
		Expect(65);
		if (StartOf(5)) {
			ID();
		}
		Expect(10);
	}

	void steplist() {
		step();
		while (la.kind == 67) {
			step();
		}
	}

	void step() {
		Expect(67);
		if (la.kind == 68 || la.kind == 69) {
			stepcondition();
		}
		if (la.kind == 10) {
			Get();
		}
		statements();
	}

	void stepcondition() {
		if (la.kind == 68) {
			Get();
			expression();
		} else if (la.kind == 69) {
			Get();
			Expect(70);
			expression();
		} else SynErr(172);
	}

	void then() {
		if (la.kind == 71) {
			Get();
		} else if (la.kind == 61) {
			Get();
		} else SynErr(173);
	}

	void endwhen() {
		Expect(35);
		Expect(34);
		if (la.kind == 10) {
			Get();
		}
	}

	void statement() {
		switch (la.kind) {
		case 3: case 73: case 74: case 75: case 76: case 77: case 78: {
			outputstatement();
			break;
		}
		case 113: case 114: case 115: case 116: case 117: case 118: case 119: case 120: case 121: {
			specialstatement();
			break;
		}
		case 81: case 82: case 83: case 85: {
			manipulationstatement();
			break;
		}
		case 111: case 112: {
			actorstatement();
			break;
		}
		case 87: case 88: {
			eventstatement();
			break;
		}
		case 89: case 90: case 91: case 93: case 94: {
			assignmentstatement();
			break;
		}
		case 109: case 110: {
			repetitionstatement();
			break;
		}
		case 102: case 104: {
			conditionalstatement();
			break;
		}
		default: SynErr(174); break;
		}
	}

	void outputstatement() {
		switch (la.kind) {
		case 3: {
			Get();
			break;
		}
		case 73: {
			Get();
			what();
			Expect(10);
			break;
		}
		case 74: {
			Get();
			if (StartOf(18)) {
				sayform();
			}
			expression();
			Expect(10);
			break;
		}
		case 75: {
			Get();
			primary();
			Expect(10);
			break;
		}
		case 76: {
			Get();
			ID();
			Expect(10);
			break;
		}
		case 77: {
			Get();
			ID();
			Expect(10);
			break;
		}
		case 78: {
			Get();
			ID();
			Expect(10);
			break;
		}
		default: SynErr(175); break;
		}
	}

	void specialstatement() {
		switch (la.kind) {
		case 113: {
			Get();
			Expect(10);
			break;
		}
		case 114: {
			Get();
			Expect(10);
			break;
		}
		case 115: {
			Get();
			Expect(10);
			break;
		}
		case 116: {
			Get();
			Expect(10);
			break;
		}
		case 117: {
			Get();
			Expect(10);
			break;
		}
		case 118: {
			Get();
			if (la.kind == 2) {
				Get();
			}
			Expect(10);
			break;
		}
		case 119: {
			Get();
			onoroff();
			Expect(10);
			break;
		}
		case 120: {
			Get();
			Expect(3);
			Expect(10);
			break;
		}
		case 121: {
			Get();
			Expect(2);
			Expect(10);
			break;
		}
		default: SynErr(176); break;
		}
	}

	void manipulationstatement() {
		if (la.kind == 81) {
			Get();
			primary();
			if (StartOf(19)) {
				where();
			}
			Expect(10);
		} else if (la.kind == 82) {
			Get();
			primary();
			where();
			Expect(10);
		} else if (la.kind == 83) {
			Get();
			primary();
			Expect(84);
			what();
			Expect(10);
		} else if (la.kind == 85) {
			Get();
			primary();
			Expect(86);
			what();
			Expect(10);
		} else SynErr(177);
	}

	void actorstatement() {
		if (la.kind == 111) {
			Get();
			what();
			Expect(10);
		} else if (la.kind == 112) {
			Get();
			Expect(66);
			ID();
			if (la.kind == 109) {
				foractor();
			}
			Expect(10);
		} else SynErr(178);
	}

	void eventstatement() {
		if (la.kind == 87) {
			Get();
			what();
			Expect(10);
		} else if (la.kind == 88) {
			Get();
			what();
			if (StartOf(19)) {
				where();
			}
			Expect(68);
			expression();
			Expect(10);
		} else SynErr(179);
	}

	void assignmentstatement() {
		if (la.kind == 89) {
			Get();
			primary();
			something();
			Expect(10);
		} else if (la.kind == 90) {
			Get();
			if (la.kind == 95 || la.kind == 96) {
				firstorlast();
			}
			if (StartOf(13)) {
				expression();
			}
			if (StartOf(20)) {
				wordorcharacter();
			}
			Expect(86);
			expression();
			if (la.kind == 101) {
				into();
			}
			Expect(10);
		} else if (la.kind == 91) {
			Get();
			what();
			if (la.kind == 92) {
				Get();
				expression();
			}
			Expect(10);
		} else if (la.kind == 93) {
			Get();
			what();
			if (la.kind == 92) {
				Get();
				expression();
			}
			Expect(10);
		} else if (la.kind == 94) {
			Get();
			what();
			Expect(40);
			expression();
			Expect(10);
		} else SynErr(180);
	}

	void repetitionstatement() {
		foreach();
		ID();
		if (StartOf(21)) {
			loopfilters();
		}
		Expect(107);
		statements();
		Expect(35);
		foreach();
		if (la.kind == 10) {
			Get();
		}
	}

	void conditionalstatement() {
		if (la.kind == 102) {
			ifstatement();
		} else if (la.kind == 104) {
			dependingstatement();
		} else SynErr(181);
	}

	void sayform() {
		if (la.kind == 41) {
			Get();
		} else if (la.kind == 79) {
			Get();
		} else if (la.kind == 80) {
			Get();
		} else if (la.kind == 9) {
			Get();
		} else SynErr(182);
	}

	void primary() {
		switch (la.kind) {
		case 3: {
			Get();
			break;
		}
		case 1: case 4: case 59: case 80: case 95: case 101: case 105: case 121: case 122: case 124: case 126: case 130: case 131: case 132: case 133: {
			what();
			break;
		}
		case 118: {
			Get();
			break;
		}
		case 2: case 13: {
			if (la.kind == 13) {
				Get();
			}
			Expect(2);
			break;
		}
		case 14: {
			Get();
			if (StartOf(9)) {
				setmembers();
			}
			Expect(15);
			break;
		}
		case 22: {
			Get();
			expression();
			Expect(23);
			break;
		}
		case 125: {
			Get();
			randomwhat();
			break;
		}
		default: SynErr(183); break;
		}
	}

	void something() {
		if (la.kind == 6) {
			Get();
		}
		ID();
	}

	void firstorlast() {
		if (la.kind == 95) {
			Get();
		} else if (la.kind == 96) {
			Get();
		} else SynErr(184);
	}

	void wordorcharacter() {
		if (la.kind == 97) {
			Get();
		} else if (la.kind == 98) {
			Get();
		} else if (la.kind == 99) {
			Get();
		} else if (la.kind == 100) {
			Get();
		} else SynErr(185);
	}

	void into() {
		Expect(101);
		expression();
	}

	void ifstatement() {
		Expect(102);
		expression();
		Expect(61);
		statements();
		if (la.kind == 103) {
			elsiflist();
		}
		if (la.kind == 30) {
			elsepart();
		}
		Expect(35);
		Expect(102);
		Expect(10);
	}

	void dependingstatement() {
		Expect(104);
		Expect(105);
		primary();
		dependcases();
		Expect(35);
		genSym12();
		Expect(10);
	}

	void elsiflist() {
		Expect(103);
		expression();
		Expect(61);
		statements();
		while (la.kind == 103) {
			Get();
			expression();
			Expect(61);
			statements();
		}
	}

	void elsepart() {
		Expect(30);
		statements();
	}

	void dependcases() {
		dependcase();
		while (StartOf(22)) {
			dependcase();
		}
	}

	void genSym12() {
		if (la.kind == 106) {
			Get();
		} else if (la.kind == 104) {
			Get();
		} else SynErr(186);
	}

	void dependcase() {
		if (la.kind == 30) {
			Get();
			statements();
		} else if (StartOf(23)) {
			righthandside();
			Expect(61);
			statements();
		} else SynErr(187);
	}

	void righthandside() {
		if (StartOf(24)) {
			filter();
		} else if (StartOf(25)) {
			if (la.kind == 6) {
				Get();
			}
			realrighthandside();
		} else SynErr(188);
	}

	void foreach() {
		if (la.kind == 109) {
			Get();
			if (la.kind == 110) {
				Get();
			}
		} else if (la.kind == 110) {
			Get();
		} else SynErr(189);
	}

	void loopfilters() {
		if (StartOf(24)) {
			filters();
		} else if (la.kind == 108) {
			Get();
			factor();
			Expect(28);
			factor();
		} else SynErr(190);
	}

	void filters() {
		filter();
		while (la.kind == 16) {
			Get();
			filter();
		}
	}

	void factor() {
		factorleft();
		if (StartOf(26)) {
			if (la.kind == 6) {
				Get();
			}
			factorright();
		}
	}

	void foractor() {
		Expect(109);
		what();
	}

	void onoroff() {
		if (la.kind == 105) {
			Get();
		} else if (la.kind == 122) {
			Get();
		} else SynErr(191);
	}

	void term() {
		factor();
		while (la.kind == 28) {
			Get();
			term();
		}
	}

	void factorleft() {
		if (StartOf(27)) {
			primary();
		} else if (StartOf(28)) {
			aggregate();
			filters();
		} else SynErr(192);
	}

	void factorright() {
		switch (la.kind) {
		case 29: {
			Get();
			ID();
			break;
		}
		case 46: case 47: case 48: case 49: {
			is();
			something();
			break;
		}
		case 13: case 24: case 141: case 142: {
			binop();
			factor();
			break;
		}
		case 84: case 134: case 135: case 136: case 137: case 138: case 139: case 140: {
			where();
			break;
		}
		case 18: case 143: case 144: case 145: case 146: case 147: case 148: {
			relop();
			factor();
			break;
		}
		case 124: {
			Get();
			factor();
			break;
		}
		case 108: {
			Get();
			factor();
			Expect(28);
			factor();
			break;
		}
		default: SynErr(193); break;
		}
	}

	void aggregate() {
		if (la.kind == 62) {
			Get();
		} else if (la.kind == 127 || la.kind == 128 || la.kind == 129) {
			aggregator();
			Expect(126);
			ID();
		} else SynErr(194);
	}

	void binop() {
		if (la.kind == 141) {
			Get();
		} else if (la.kind == 13) {
			Get();
		} else if (la.kind == 24) {
			Get();
		} else if (la.kind == 142) {
			Get();
		} else SynErr(195);
	}

	void relop() {
		switch (la.kind) {
		case 143: {
			Get();
			break;
		}
		case 18: {
			Get();
			break;
		}
		case 144: {
			Get();
			break;
		}
		case 145: {
			Get();
			break;
		}
		case 146: {
			Get();
			break;
		}
		case 147: {
			Get();
			break;
		}
		case 148: {
			Get();
			break;
		}
		default: SynErr(196); break;
		}
	}

	void filter() {
		if (StartOf(29)) {
			if (la.kind == 6) {
				Get();
			}
			if (StartOf(19)) {
				where();
			} else if (la.kind == 29) {
				Get();
				ID();
			} else SynErr(197);
		} else if (StartOf(30)) {
			is();
			something();
		} else SynErr(198);
	}

	void realrighthandside() {
		if (StartOf(31)) {
			relop();
			primary();
		} else if (la.kind == 124) {
			Get();
			factor();
		} else if (la.kind == 108) {
			Get();
			factor();
			Expect(28);
			factor();
		} else SynErr(199);
	}

	void randomwhat() {
		if (StartOf(32)) {
			if (la.kind == 138 || la.kind == 139 || la.kind == 140) {
				transitivity();
			}
			Expect(84);
			primary();
		} else if (StartOf(27)) {
			primary();
			Expect(40);
			primary();
		} else SynErr(200);
	}

	void transitivity() {
		if (la.kind == 138) {
			Get();
		} else if (la.kind == 139) {
			Get();
		} else if (la.kind == 140) {
			Get();
		} else SynErr(201);
	}

	void aggregator() {
		if (la.kind == 127) {
			Get();
		} else if (la.kind == 128) {
			Get();
		} else if (la.kind == 129) {
			Get();
		} else SynErr(202);
	}

	void simplewhat() {
		if (StartOf(5)) {
			ID();
		} else if (la.kind == 130) {
			Get();
		} else if (la.kind == 131) {
			Get();
			if (la.kind == 132) {
				Get();
			} else if (la.kind == 133) {
				Get();
			} else SynErr(203);
		} else SynErr(204);
	}

	void properwhere() {
		if (la.kind == 134) {
			Get();
		} else if (la.kind == 135) {
			Get();
		} else if (la.kind == 136) {
			Get();
			what();
		} else if (la.kind == 84) {
			Get();
			primary();
		} else if (la.kind == 137) {
			Get();
			what();
		} else SynErr(205);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		adventure();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,T,x,T, x,T,x,x, x,x,x,x, x,x,x,x, T,T,T,x, x,x,T,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,T,x, x,T,T,T, x,T,x,T, T,T,T,T, x,T,T,x, x,x,x,x, x,x,T,x, T,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,T,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,T,T, T,x,x,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,T,T, T,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,T,T, T,x,x,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,T,T,x, T,T,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,x,T, T,T,x,x, x,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,T,x,T, x,x,x,x, x,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x,T, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,x,T, T,T,x,x, x,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, T,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, T,x,T,x, x,x,x,x, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,T, T,T,T,T, T,x,x,x, x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,T, T,T,T,T, T,x,x,x, x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, T,x,x,x, x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,T,x,x, x,x,T,x, x,x,x,x, T,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x,x, x},
		{x,T,T,T, T,x,x,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,T,T,x, T,T,T,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, T,x,x,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x}

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
			case 9: s = "\"no\" expected"; break;
			case 10: s = "\".\" expected"; break;
			case 11: s = "\"prompt\" expected"; break;
			case 12: s = "\"import\" expected"; break;
			case 13: s = "\"-\" expected"; break;
			case 14: s = "\"{\" expected"; break;
			case 15: s = "\"}\" expected"; break;
			case 16: s = "\",\" expected"; break;
			case 17: s = "\"synonyms\" expected"; break;
			case 18: s = "\"=\" expected"; break;
			case 19: s = "\"message\" expected"; break;
			case 20: s = "\":\" expected"; break;
			case 21: s = "\"syntax\" expected"; break;
			case 22: s = "\"(\" expected"; break;
			case 23: s = "\")\" expected"; break;
			case 24: s = "\"*\" expected"; break;
			case 25: s = "\"!\" expected"; break;
			case 26: s = "\"*!\" expected"; break;
			case 27: s = "\"!*\" expected"; break;
			case 28: s = "\"and\" expected"; break;
			case 29: s = "\"isa\" expected"; break;
			case 30: s = "\"else\" expected"; break;
			case 31: s = "\"where\" expected"; break;
			case 32: s = "\"meta\" expected"; break;
			case 33: s = "\"verb\" expected"; break;
			case 34: s = "\"when\" expected"; break;
			case 35: s = "\"end\" expected"; break;
			case 36: s = "\"check\" expected"; break;
			case 37: s = "\"does\" expected"; break;
			case 38: s = "\"every\" expected"; break;
			case 39: s = "\"add\" expected"; break;
			case 40: s = "\"to\" expected"; break;
			case 41: s = "\"the\" expected"; break;
			case 42: s = "\"definite\" expected"; break;
			case 43: s = "\"indefinite\" expected"; break;
			case 44: s = "\"negative\" expected"; break;
			case 45: s = "\"exit\" expected"; break;
			case 46: s = "\"is\" expected"; break;
			case 47: s = "\"are\" expected"; break;
			case 48: s = "\"has\" expected"; break;
			case 49: s = "\"can\" expected"; break;
			case 50: s = "\"description\" expected"; break;
			case 51: s = "\"article\" expected"; break;
			case 52: s = "\"form\" expected"; break;
			case 53: s = "\"entered\" expected"; break;
			case 54: s = "\"initialize\" expected"; break;
			case 55: s = "\"mentioned\" expected"; break;
			case 56: s = "\"name\" expected"; break;
			case 57: s = "\"pronoun\" expected"; break;
			case 58: s = "\"with\" expected"; break;
			case 59: s = "\"taking\" expected"; break;
			case 60: s = "\"limits\" expected"; break;
			case 61: s = "\"then\" expected"; break;
			case 62: s = "\"count\" expected"; break;
			case 63: s = "\"header\" expected"; break;
			case 64: s = "\"extract\" expected"; break;
			case 65: s = "\"event\" expected"; break;
			case 66: s = "\"script\" expected"; break;
			case 67: s = "\"step\" expected"; break;
			case 68: s = "\"after\" expected"; break;
			case 69: s = "\"wait\" expected"; break;
			case 70: s = "\"until\" expected"; break;
			case 71: s = "\"=>\" expected"; break;
			case 72: s = "\"start\" expected"; break;
			case 73: s = "\"describe\" expected"; break;
			case 74: s = "\"say\" expected"; break;
			case 75: s = "\"list\" expected"; break;
			case 76: s = "\"show\" expected"; break;
			case 77: s = "\"play\" expected"; break;
			case 78: s = "\"style\" expected"; break;
			case 79: s = "\"an\" expected"; break;
			case 80: s = "\"it\" expected"; break;
			case 81: s = "\"empty\" expected"; break;
			case 82: s = "\"locate\" expected"; break;
			case 83: s = "\"include\" expected"; break;
			case 84: s = "\"in\" expected"; break;
			case 85: s = "\"exclude\" expected"; break;
			case 86: s = "\"from\" expected"; break;
			case 87: s = "\"cancel\" expected"; break;
			case 88: s = "\"schedule\" expected"; break;
			case 89: s = "\"make\" expected"; break;
			case 90: s = "\"strip\" expected"; break;
			case 91: s = "\"increase\" expected"; break;
			case 92: s = "\"by\" expected"; break;
			case 93: s = "\"decrease\" expected"; break;
			case 94: s = "\"set\" expected"; break;
			case 95: s = "\"first\" expected"; break;
			case 96: s = "\"last\" expected"; break;
			case 97: s = "\"word\" expected"; break;
			case 98: s = "\"words\" expected"; break;
			case 99: s = "\"character\" expected"; break;
			case 100: s = "\"characters\" expected"; break;
			case 101: s = "\"into\" expected"; break;
			case 102: s = "\"if\" expected"; break;
			case 103: s = "\"elsif\" expected"; break;
			case 104: s = "\"depending\" expected"; break;
			case 105: s = "\"on\" expected"; break;
			case 106: s = "\"depend\" expected"; break;
			case 107: s = "\"do\" expected"; break;
			case 108: s = "\"between\" expected"; break;
			case 109: s = "\"for\" expected"; break;
			case 110: s = "\"each\" expected"; break;
			case 111: s = "\"stop\" expected"; break;
			case 112: s = "\"use\" expected"; break;
			case 113: s = "\"quit\" expected"; break;
			case 114: s = "\"look\" expected"; break;
			case 115: s = "\"save\" expected"; break;
			case 116: s = "\"restore\" expected"; break;
			case 117: s = "\"restart\" expected"; break;
			case 118: s = "\"score\" expected"; break;
			case 119: s = "\"transcript\" expected"; break;
			case 120: s = "\"system\" expected"; break;
			case 121: s = "\"visits\" expected"; break;
			case 122: s = "\"off\" expected"; break;
			case 123: s = "\"or\" expected"; break;
			case 124: s = "\"contains\" expected"; break;
			case 125: s = "\"random\" expected"; break;
			case 126: s = "\"of\" expected"; break;
			case 127: s = "\"max\" expected"; break;
			case 128: s = "\"min\" expected"; break;
			case 129: s = "\"sum\" expected"; break;
			case 130: s = "\"this\" expected"; break;
			case 131: s = "\"current\" expected"; break;
			case 132: s = "\"actor\" expected"; break;
			case 133: s = "\"location\" expected"; break;
			case 134: s = "\"here\" expected"; break;
			case 135: s = "\"nearby\" expected"; break;
			case 136: s = "\"at\" expected"; break;
			case 137: s = "\"near\" expected"; break;
			case 138: s = "\"transitively\" expected"; break;
			case 139: s = "\"directly\" expected"; break;
			case 140: s = "\"indirectly\" expected"; break;
			case 141: s = "\"+\" expected"; break;
			case 142: s = "\"/\" expected"; break;
			case 143: s = "\"<>\" expected"; break;
			case 144: s = "\"==\" expected"; break;
			case 145: s = "\">=\" expected"; break;
			case 146: s = "\"<=\" expected"; break;
			case 147: s = "\">\" expected"; break;
			case 148: s = "\"<\" expected"; break;
			case 149: s = "\"before\" expected"; break;
			case 150: s = "\"only\" expected"; break;
			case 151: s = "??? expected"; break;
			case 152: s = "invalid options"; break;
			case 153: s = "invalid declaration"; break;
			case 154: s = "invalid ID"; break;
			case 155: s = "invalid optionvalue"; break;
			case 156: s = "invalid attributevalue"; break;
			case 157: s = "invalid setmember"; break;
			case 158: s = "invalid optionalsyntaxrestrictions"; break;
			case 159: s = "invalid syntaxelement"; break;
			case 160: s = "invalid restrictionclass"; break;
			case 161: s = "invalid verbbody"; break;
			case 162: s = "invalid checks"; break;
			case 163: s = "invalid optionalqual"; break;
			case 164: s = "invalid property"; break;
			case 165: s = "invalid is"; break;
			case 166: s = "invalid description"; break;
			case 167: s = "invalid articleorform"; break;
			case 168: s = "invalid containerbody"; break;
			case 169: s = "invalid extract"; break;
			case 170: s = "invalid limitattribute"; break;
			case 171: s = "invalid elseorthen"; break;
			case 172: s = "invalid stepcondition"; break;
			case 173: s = "invalid then"; break;
			case 174: s = "invalid statement"; break;
			case 175: s = "invalid outputstatement"; break;
			case 176: s = "invalid specialstatement"; break;
			case 177: s = "invalid manipulationstatement"; break;
			case 178: s = "invalid actorstatement"; break;
			case 179: s = "invalid eventstatement"; break;
			case 180: s = "invalid assignmentstatement"; break;
			case 181: s = "invalid conditionalstatement"; break;
			case 182: s = "invalid sayform"; break;
			case 183: s = "invalid primary"; break;
			case 184: s = "invalid firstorlast"; break;
			case 185: s = "invalid wordorcharacter"; break;
			case 186: s = "invalid genSym12"; break;
			case 187: s = "invalid dependcase"; break;
			case 188: s = "invalid righthandside"; break;
			case 189: s = "invalid foreach"; break;
			case 190: s = "invalid loopfilters"; break;
			case 191: s = "invalid onoroff"; break;
			case 192: s = "invalid factorleft"; break;
			case 193: s = "invalid factorright"; break;
			case 194: s = "invalid aggregate"; break;
			case 195: s = "invalid binop"; break;
			case 196: s = "invalid relop"; break;
			case 197: s = "invalid filter"; break;
			case 198: s = "invalid filter"; break;
			case 199: s = "invalid realrighthandside"; break;
			case 200: s = "invalid randomwhat"; break;
			case 201: s = "invalid transitivity"; break;
			case 202: s = "invalid aggregator"; break;
			case 203: s = "invalid simplewhat"; break;
			case 204: s = "invalid simplewhat"; break;
			case 205: s = "invalid properwhere"; break;
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
