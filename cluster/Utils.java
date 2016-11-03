package cluster;

	/**
	 * Classe utilitária com funções que visam reduzir código duplicado e prover funções que serão
	 * utilizadas por várias classes do sistema.
	 *
	 * @author Maycon Junior, Wagner Narde
	 * @since 03/11/2016
	 */
public final class Utils{

	public static final char SEPARADOR = '#'; //Separador de campos utilizado para login/senha.

	/** Copia um array para outro.
	* A cópia é realizada percorrendo o array de origem a partir da posição inicial especificada em orix,
	* então, acessa o array destino na posição inicial especificada em deix, e copia o elemento da origem
	* para o destino, em um loop até que um dos arrays chegue ao final.
	*
	* @param or Array origem.
	* @param de Array destino.
	* @param orix posição inicial da origem.
	* @param deix posição inicial do destino.
	*/
	public static <T> void copy(T[] or, T[] de, int orix, int deix){
		for(int x=orix, y=deix ; x < or.length && y < de.length ; x++, y++){
			de[y] = or[x];
		}
	}
}
