	import java.util.Vector;
	
	class functions {
	
		/**
		 * Verifica se uma dada expressão está na Forma Normal Conjuntiva. Para isso acontecer,
		 * a expressão tem que ser compostas apenas por conjunções de disjunções.
		 * @param exp
		 * @return
		 */
		static boolean verifyFNC(String exp){
			if (exp.contains(">")){ //FNC não contém o operador implica >
				return false;
			}
			//Identificação cláusula por cláusula, a partir da expressão exp
			int left = 0;
			int right = 0;
			for (int i=0; i<exp.length(); i++){
				if (exp.charAt(i)=='('){
					left = i;
					if (left>0 && right==0){
						return false;
					}
				} else if (exp.charAt(i)==')'){
					right = i;
					for (int j=left+1; j<right-1; j++){
						//Não é permitido que apareça qualquer um dos seguintes operadores dentro de uma cláusula
						if (exp.charAt(j)=='(' || exp.charAt(j)==')' || exp.charAt(j)=='.' || exp.charAt(j)=='>'){
							return false;
						}
					}
				}
			}
			return true;
		}
	
		/**
		 * Verifica se uma dada expressão contém apenas cláusulas de Horn, sabendo que a expressão
		 * já está na FNC. Para isso, cada cláusula deve conter apenas no máximo um literal positivo.
		 * @param exp
		 * @return
		 */
		static boolean verifyHorn(String exp){
			if (verifyFNC(exp)){ //É necessário estar na FNC 
				int left=0;
				int right=0;
				int num_var=0;
				int num_neg=0;
				//Identificação cláusula a cláusula
				for (int i=0; i<exp.length(); i++){
					if (exp.charAt(i)=='('){
						left = i;
					} else if (exp.charAt(i)==')'){
						right = i;
						for (int j=left+1; j<right; j++){
							if (exp.charAt(j)=='a' || exp.charAt(j)=='b' || exp.charAt(j)=='c' || exp.charAt(j)=='d'){
								num_var++;
							} else if (exp.charAt(j)=='-'){
								num_neg++;
							}
						}
						//Se é cláusula de Horn, então cada cláusula deve conter no máximo um literal positivo. Por
						//isso, Se houver n literais, n-1 deles devem ser negativos. Se houver apenas
						//um literal é c. de Horn e se tiver apenas literais negativos também é.
						if (num_neg != num_var-1 && num_var!=1 && num_neg!=num_var){
							return false;
						}
						num_var=0; num_neg =0;
					}
				}
				return true;
			} else {
				return false;
			}
		}
	
		/**
		 * Função que retorna TRUE caso a cláusula seja um literal isolado (positivo ou não)
		 * @param exp
		 * @return
		 */
		static boolean isLiteral(String exp){
			if (exp.length()==3 || exp.length()==4){
				return true;
			}
			return false;
		}
	
		/**
		 * Método que a partir da expressão, insere num vetor todas as suas cláusulas
		 * separadamente, uma em cada posição.
		 * @param exp
		 * @param clausulas
		 */
		static void gerarClausulas(String exp, Vector<String> clausulas){
			int left=0;
			int right=0;
			//identifica o parênteses à esquerda, o parênteses à direita e adiciona
			//a um vector o que tiver do "(" até o ")"
			for (int i=0; i<exp.length(); i++){
				if (exp.charAt(i)=='('){
					left = i;
				} else if (exp.charAt(i)==')'){
					right = i;
					clausulas.add(exp.substring(left, right+1));
				}
			}
		}
	
		/**
		 * Método que identifica se alguma cláusula de uma expressão é do formato (x) ou (-x),
		 * onde a cláusula é apenas um "literal isolado".
		 * @param clausulas
		 * @return
		 */
		static boolean contemLiteralIso(Vector<String> clausulas){
			for (int i=0; i<clausulas.size(); i++){
				if (clausulas.get(i).length()==3 || clausulas.get(i).length()==4){
					return true;
				}
			}
			return false;
		}
	
		/**
		 * Método que separa todos os literais de uma cláusula em um vetor "cl" com os literais isoladamente.
		 * @param clausulas
		 * @param cl
		 * @param j
		 */
		static void separarLiterais (Vector<String> clausulas, Vector<String> cl, int j){
			/*
			 * for que varre uma cláusula inteira isolando os literais nela contidos. Por
			 * exemplo, uma cláusula (-x.y.z) preencherá o vetor cl com -x, y, z.
			 */
			for (int k=0; k<clausulas.get(j).length(); k++){ 
				//Primeiro caso: o literal está logo no início da cláusula
				if (clausulas.get(j).charAt(k)=='('){
					if (clausulas.get(j).charAt(k+1)== '-'){ //literal negativo
						cl.add("-"+clausulas.get(j).charAt(k+2));
					} else { //literal positivo
						cl.add(""+clausulas.get(j).charAt(k+1));
					}
					//Segundo caso: o literal está precedido de um operador de disjunção
				} else if (clausulas.get(j).charAt(k)=='+'){
					if (clausulas.get(j).charAt(k+1)== '-'){ //literal negativo
						cl.add("-"+clausulas.get(j).charAt(k+2));
					} else { //literal positivo
						cl.add(""+clausulas.get(j).charAt(k+1));
					}
				}
			}
		}
	
		/**
		 * Método que dado um literal, elimina o seu oposto da cláusula. Ou seja, se estamos
		 * analisando a partir de um literal (-x) e encontrarmos uma cláusula do tipo
		 * (... + x), a cláusula passará a ser (...), com o x removido. O oposto também é válido,
		 * eliminando -x de (... +-x) a partir de (x).
		 * @param clausulas
		 * @param cl
		 * @param i
		 * @param j
		 */
		static int eliminar (Vector<String> clausulas, Vector<String> cl, int i, int j){
			for (int k=0; k<cl.size(); k++){
				//Primeiro caso: a clausula[i] é do tipo(-x) e o literal cl[k] é do tipo (x)
				if (clausulas.get(i).equals("(-" + cl.get(k) + ")")){
					cl.remove(k);
					String aux = "(";
					for (int l=0 ; l< cl.size() ; ++l){
						aux += cl.get(l) + "+";
					}
					aux = aux.substring(0, aux.length()-1)+")";
					clausulas.set(j, aux);
					if (isLiteral(clausulas.get(j))){
						return 0;
						/*
						 *Toda vez que é encontrada uma nova cláusula que é um literal
						 *isolado, ele deve ser comparado novamente à todas as outras cláusulas.
						 *Por isso, é retornado 0 pois quando esse método é chamado, ele
						 *faz o for voltar para o i=0, reiniciando a comparação. 
						 */
					}
				//Segundo caso: a clausula[i] é do tipo(x) e o literal cl[k] é do tipo (-x)
				} else if (("-"+clausulas.get(i).substring(1, clausulas.get(i).length()-1)).equals(cl.get(k))) {
					cl.remove(k);
					String aux = "(";
					for (int l=0 ; l< cl.size() ; ++l){
						aux += cl.get(l) + "+";
					}
					aux = aux.substring(0, aux.length()-1)+")";
					clausulas.set(j, aux);
					if (isLiteral(clausulas.get(j))){
						return 0;
					}
				}
			}
			/*
			 * Se ele não deu return 0 em algum ponto, não foi gerada nenhuma
			 * cláusula que é um literal isolado, logo não há necessidade de 
			 * reiterar tudo novamente. Por isso, é retornado o próprio i e é
			 * seguida a sequência do for normalmente.
			 */
			return i;
		}
	
		/**
		 * Método que verifica se há uma cláusula vazia no conjunto de cláusulas. Se há uma cláusula vazia,
		 * é porque todos os seus literais foram eliminados. Para isso ocorrer, é necessário que um literal do
		 * tipo (x) elimine um do tipo (-x) e vice-versa, gerando uma cláusula vazia e assim, identificando
		 * uma contradição na expressão inicial.
		 * @param clausulas
		 * @return
		 */
		static boolean houveContradicao(Vector<String> clausulas){
			for (int i=0; i<clausulas.size(); ++i){
				if (clausulas.get(i).equals(")")){
					return true;
				}
			}
			return false;
		}
	
		/**
		 * Função que realiza o Método da Resolução, conferindo se uma expressão é
		 * satisfatível ou insatisfatível.
		 * @param exp
		 * @param clausulas
		 * @return
		 */
		static boolean SAT(String exp, Vector<String> clausulas){
			gerarClausulas(exp, clausulas);
			if (!contemLiteralIso(clausulas)){
				return true;
			} else {
				for (int i=0; i<clausulas.size(); i++){//percorre o vector à procura de um literal isolado 
					if (isLiteral(clausulas.get(i))) { //(if) se a cláusula na posição i do vector for um literal isolado
						Vector<String> cl = new Vector<String>();
						for (int j=0; j<clausulas.size(); j++){ //varre o vector de cláusula em cláusula
							separarLiterais(clausulas, cl, j);
							i = eliminar(clausulas, cl, i, j);
							if (houveContradicao(clausulas)){
								cl.clear();
								return false;
							}
							cl.clear();
						} 
					}
				}
				return true;
			}
		}
	
	}
	
	public class Resolucao {
		public static void main(String[] args) {
			Arquivo arq = new Arquivo ("Expressoes.in", "Expressoes.out");
			int cases = arq.readInt();
			int cont=1;
			String exp;
			Vector<String> clausulas = new Vector<String>();
			while (cases>0){
				exp = arq.readString();
				arq.print("caso #" + cont + ": ");
				if (functions.verifyFNC(exp)){
					if (functions.verifyHorn(exp)){
						if (functions.SAT(exp, clausulas)){
							arq.print("satisfativel");
						} else {
							arq.print("insatisfativel");
						}
					} else {
						arq.print("nem todas as clausulas sao de horn");
					}
				} else {
					arq.print("nao esta na FNC");
				}
				arq.println();
				clausulas.clear();
				cont++;
				cases--;
			}
		}
	}