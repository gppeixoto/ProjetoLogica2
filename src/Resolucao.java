	import java.util.Vector;
	
	class functions {
	
		/**
		 * Verifica se uma dada express�o est� na Forma Normal Conjuntiva. Para isso acontecer,
		 * a express�o tem que ser compostas apenas por conjun��es de disjun��es.
		 * @param exp
		 * @return
		 */
		static boolean verifyFNC(String exp){
			if (exp.contains(">")){ //FNC n�o cont�m o operador implica >
				return false;
			}
			//Identifica��o cl�usula por cl�usula, a partir da express�o exp
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
						//N�o � permitido que apare�a qualquer um dos seguintes operadores dentro de uma cl�usula
						if (exp.charAt(j)=='(' || exp.charAt(j)==')' || exp.charAt(j)=='.' || exp.charAt(j)=='>'){
							return false;
						}
					}
				}
			}
			return true;
		}
	
		/**
		 * Verifica se uma dada express�o cont�m apenas cl�usulas de Horn, sabendo que a express�o
		 * j� est� na FNC. Para isso, cada cl�usula deve conter apenas no m�ximo um literal positivo.
		 * @param exp
		 * @return
		 */
		static boolean verifyHorn(String exp){
			if (verifyFNC(exp)){ //� necess�rio estar na FNC 
				int left=0;
				int right=0;
				int num_var=0;
				int num_neg=0;
				//Identifica��o cl�usula a cl�usula
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
						//Se � cl�usula de Horn, ent�o cada cl�usula deve conter no m�ximo um literal positivo. Por
						//isso, Se houver n literais, n-1 deles devem ser negativos. Se houver apenas
						//um literal � c. de Horn e se tiver apenas literais negativos tamb�m �.
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
		 * Fun��o que retorna TRUE caso a cl�usula seja um literal isolado (positivo ou n�o)
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
		 * M�todo que a partir da express�o, insere num vetor todas as suas cl�usulas
		 * separadamente, uma em cada posi��o.
		 * @param exp
		 * @param clausulas
		 */
		static void gerarClausulas(String exp, Vector<String> clausulas){
			int left=0;
			int right=0;
			//identifica o par�nteses � esquerda, o par�nteses � direita e adiciona
			//a um vector o que tiver do "(" at� o ")"
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
		 * M�todo que identifica se alguma cl�usula de uma express�o � do formato (x) ou (-x),
		 * onde a cl�usula � apenas um "literal isolado".
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
		 * M�todo que separa todos os literais de uma cl�usula em um vetor "cl" com os literais isoladamente.
		 * @param clausulas
		 * @param cl
		 * @param j
		 */
		static void separarLiterais (Vector<String> clausulas, Vector<String> cl, int j){
			/*
			 * for que varre uma cl�usula inteira isolando os literais nela contidos. Por
			 * exemplo, uma cl�usula (-x.y.z) preencher� o vetor cl com -x, y, z.
			 */
			for (int k=0; k<clausulas.get(j).length(); k++){ 
				//Primeiro caso: o literal est� logo no in�cio da cl�usula
				if (clausulas.get(j).charAt(k)=='('){
					if (clausulas.get(j).charAt(k+1)== '-'){ //literal negativo
						cl.add("-"+clausulas.get(j).charAt(k+2));
					} else { //literal positivo
						cl.add(""+clausulas.get(j).charAt(k+1));
					}
					//Segundo caso: o literal est� precedido de um operador de disjun��o
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
		 * M�todo que dado um literal, elimina o seu oposto da cl�usula. Ou seja, se estamos
		 * analisando a partir de um literal (-x) e encontrarmos uma cl�usula do tipo
		 * (... + x), a cl�usula passar� a ser (...), com o x removido. O oposto tamb�m � v�lido,
		 * eliminando -x de (... +-x) a partir de (x).
		 * @param clausulas
		 * @param cl
		 * @param i
		 * @param j
		 */
		static int eliminar (Vector<String> clausulas, Vector<String> cl, int i, int j){
			for (int k=0; k<cl.size(); k++){
				//Primeiro caso: a clausula[i] � do tipo(-x) e o literal cl[k] � do tipo (x)
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
						 *Toda vez que � encontrada uma nova cl�usula que � um literal
						 *isolado, ele deve ser comparado novamente � todas as outras cl�usulas.
						 *Por isso, � retornado 0 pois quando esse m�todo � chamado, ele
						 *faz o for voltar para o i=0, reiniciando a compara��o. 
						 */
					}
				//Segundo caso: a clausula[i] � do tipo(x) e o literal cl[k] � do tipo (-x)
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
			 * Se ele n�o deu return 0 em algum ponto, n�o foi gerada nenhuma
			 * cl�usula que � um literal isolado, logo n�o h� necessidade de 
			 * reiterar tudo novamente. Por isso, � retornado o pr�prio i e �
			 * seguida a sequ�ncia do for normalmente.
			 */
			return i;
		}
	
		/**
		 * M�todo que verifica se h� uma cl�usula vazia no conjunto de cl�usulas. Se h� uma cl�usula vazia,
		 * � porque todos os seus literais foram eliminados. Para isso ocorrer, � necess�rio que um literal do
		 * tipo (x) elimine um do tipo (-x) e vice-versa, gerando uma cl�usula vazia e assim, identificando
		 * uma contradi��o na express�o inicial.
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
		 * Fun��o que realiza o M�todo da Resolu��o, conferindo se uma express�o �
		 * satisfat�vel ou insatisfat�vel.
		 * @param exp
		 * @param clausulas
		 * @return
		 */
		static boolean SAT(String exp, Vector<String> clausulas){
			gerarClausulas(exp, clausulas);
			if (!contemLiteralIso(clausulas)){
				return true;
			} else {
				for (int i=0; i<clausulas.size(); i++){//percorre o vector � procura de um literal isolado 
					if (isLiteral(clausulas.get(i))) { //(if) se a cl�usula na posi��o i do vector for um literal isolado
						Vector<String> cl = new Vector<String>();
						for (int j=0; j<clausulas.size(); j++){ //varre o vector de cl�usula em cl�usula
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