import java_cup.runtime.*;

%%

%class Lexer
%unicode
%cup
%line
%column

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
Comment = "//" [^\r\n]*

Identifier = [A-Za-z_][A-Za-z0-9_]*
InvalidIdentifier = [0-9][A-Za-z0-9_]*
Number = [0-9]+
String = \"([^\\\"]|\\.)*\"
TripleQuotedString = \"\"\"([^\"]*|\"[^\"]|\"\"[^\"])*\"\"\"

%%

<YYINITIAL> {
  {WhiteSpace}    { /* ignore */ }
  {Comment}       { /* ignore */ }

  // Keywords
  "config"        { return symbol(sym.CONFIG); }
  "base_url"      { return symbol(sym.BASE_URL); }
  "header"        { return symbol(sym.HEADER); }
  "let"           { return symbol(sym.LET); }
  "test"          { return symbol(sym.TEST); }

  "active"        { return symbol(sym.ACTIVE); }
  "true"          { return symbol(sym.TRUE); }
  "false"         { return symbol(sym.FALSE); }

  "GET"           { return symbol(sym.GET); }
  "POST"          { return symbol(sym.POST); }
  "PUT"           { return symbol(sym.PUT); }
  "DELETE"        { return symbol(sym.DELETE); }
  "expect"        { return symbol(sym.EXPECT); }
  "status"        { return symbol(sym.STATUS); }
  "body"          { return symbol(sym.BODY); }
  "contains"      { return symbol(sym.CONTAINS); }
   "in"           { return symbol(sym.IN); }

  // Symbols
  "{"             { return symbol(sym.LBRACE); }
  "}"             { return symbol(sym.RBRACE); }
  ";"             { return symbol(sym.SEMICOLON); }
  "="             { return symbol(sym.EQUALS); }
  ".."            { return symbol(sym.DOTDOT); }

  ":"           { return symbol(sym.VERDOTDOT); }

  // Literals
   {TripleQuotedString} {                                  // Extra feaauture
      String text = yytext();
      String content = text.substring(3, text.length()-3);
      return symbol(sym.MULTILINE_STRING, content);
    }
  {Identifier}    { return symbol(sym.IDENTIFIER, yytext()); }
  {Number}        { return symbol(sym.NUMBER, Integer.parseInt(yytext())); }
  {String}        { return symbol(sym.STRING, yytext().substring(1, yytext().length()-1)); }
  {InvalidIdentifier} { throw new Error("Identifier cannot start with a digit at line "+(yyline+1)+", column "+(yycolumn+1)); }
}

[^]               { throw new Error("Illegal character <"+yytext()+"> at line "+(yyline+1)+", column "+(yycolumn+1)); }