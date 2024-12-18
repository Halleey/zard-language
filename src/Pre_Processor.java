public class Pre_Processor{
  //ALERT: mal construido, erros,..., porem é util para uso posterior

  
  //syntax load "file.zdl"
  /*os preprocessadores sao diretivas que faram processos antes de relamente ser interpletado, como
  load/include "file.zdl", que de certa forma escreve no pwd.zd "file.zdl" ou "file.zd" caso seja uma fonte.

  pwd.zd:
  //
  main(){
   int my_other_var;
  }
  isto esta sendo carregado
  ^
  |
  load "file.zdl";
  main(){
   print(my_other_var);
  }*/
  public enum Pre_Processor_Kind {
   INCLUDE_FILE//...
  }

  private ArrayList<Notation> current_token;
  private String instruction_name;
  private Pre_Processor_Kind instruction;

  public Pre_Processor(String inst_namd, Pre_Processor_Kind _ppk){
   this.instruction_name = inst_namd;
   this.instruction_kind = _ppk;
  }

  public ArrayList<Pre_Processor_opc> Pre_Procsssor_Lex(String file_content){
    //isso é mais notation_lexer
    int current_line = 0;
    char current_char;

    if(!file_content) return null;

    while(current_char != '\0'){
      if(current_char ){}


    }//cria key_token

   while(current_Notation != Token.TokenType.EOF){

   switch(current_token);
    case "@Error":
    case "@include":
    case "@author":
    case "@define":
      return new Notation(current_Notation);
    default:
     Span_Error("invalid notation {"+Notation._name+"}");
   }//usar tokenize para uso de @jump_to(line), algo relacionado com goto. isso pode ser otimo para debbug
  }

  public void execute(){
   //implementa a parte de pegar o nome do arquivo, no diretorio

   String filename = "lib.zdl";
   //adapta o código para o souce para o Parser, Lexer, Token,...
   ArrayList<Pre_Processor_opc> ops = Pre_Processor_Lex();
  }
}


//syntax test
/*
//lib.zdl
class any<any_type>(
 any_type x;
 any_type y;
);

define int lib_var: 10
define  function O(any N){
 return N;
}
define lambda n(x) {x+1}//[return exp[indentifier[x,value[expected_value]]+number[1]]]

EOF//

//test.zd
@load "lib.zdl"
main(){
 print(lib_var);
}
EOF//

ou apenas vincilar os codigos.
o uso de notations comk java seria bom.

*/
