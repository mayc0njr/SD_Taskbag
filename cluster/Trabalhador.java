package cluster;

import java.io.*;
import java.net.*;

/**
 * Classe que representa uma máquina que irá receber os parâmetros,
 * do taskbag, e realizar as tentativas de quebra de senha.
 * Implementa a interface Runnable para que possa ser usada como Thread.
 * @author Maycon Junior, Wagner Narde
 * @since 03/11/2016
 */
public class Trabalhador implements Runnable{
	int min, max; //Limites inferior e superior para testar a senha.
	String usuario; //Usuario para o qual tentará quebrar a senha.
	Socket server; //Servidor de autenticação
	String msg; //usada para enviar a mensagem com login e senha para o servidor.
	boolean lib; //indica se o método será pela faixa de senhas, ou por uma biblioteca.
	boolean flag = false; //Indica se encontrou ou não a senha.
	boolean stop = false; //Indica se a execução pode parar.
	DataInputStream in; //Stream de entrada do socket.
	DataOutputStream out; //Stream de saida do socket.
	String[] biblioteca; //biblioteca de senhas possíveis

	/**
	 * Construtor básico da classe, chamado apenas por outros construtores para reutilização de código.
	 * @param servidor
	 * 			Servidor alvo.
	 * @param porta
	 * 			Porta de conexao do servidor alvo.
	 * @param usuario
	 * 			Usuario do qual deseja-se descobrir a senha.
	 */
	Trabalhador(String servidor, int porta, String usuario) throws UnknownHostException, IOException{
		server = new Socket(servidor, porta);
		this.usuario = usuario;
	}

	/**
	 * Construtor da classe, usado para construir um trabalhador baseado em uma biblioteca de senhas.
	 * @param servidor
	 * 			Servidor alvo.
	 * @param porta
	 * 			Porta de conexao do servidor alvo.
	 * @param usuario
	 * 			Usuario do qual deseja-se descobrir a senha.
	 * @param biblioteca
	 * 			Biblioteca contendo as possíveis senhas utilizadas pelo usuario.
	 */
	public Trabalhador(String servidor, int porta, String usuario, String[] biblioteca) throws UnknownHostException, IOException{
		this(servidor, porta, usuario);
		Utils.copy(biblioteca, this.biblioteca, 0, 0);
		lib = true;
	}

	/**
	 * Construtor da classe, usado para construir um trabalhador baseado em uma faixa de possíveis senhas.
	 * @param servidor
	 * 			Servidor alvo.
	 * @param porta
	 * 			Porta de conexao do servidor alvo.
	 * @param usuario
	 * 			Usuario do qual deseja-se descobrir a senha.
	 * @param min
	 * 			Limite mínimo, ou seja, a partir de qual valor inicia a força bruta.
	 * @param max
	 * 			Limite máximo, ou seja, até onde a força bruta irá tentar obter sucesso.
	 */
	public Trabalhador(String servidor, int porta, String usuario, int min, int max) throws UnknownHostException, IOException{
		this(servidor, porta, usuario);
		this.min = min;
		this.max = max;
		in = new DataInputStream(server.getInputStream());
		out = new DataOutputStream(server.getOutputStream());
		lib = false;
	}

	/** Realiza o processamento da quebra senhas.
	 * A partir do valor minimo, faz uma tentativa em força bruta de todas as senhas
	 * até que a correta seja encontrada, ou o valor limite tenha sido alcançado.
	 * <p>
	 * Observação: O método utilizado é dependente do construtor utilizado.
	 * Objetos construídos com limites min/max, tentarão todos os valores dentro da faixa,
	 * Objetos construídos com bibliotecas, utilizarão todas as senhas contidas na mesma para a quebra.
	 */
	public void run(){
		if(lib){
			for(String pwd : biblioteca){//For each, percorrendo o array de senhas da biblioteca.
				if(stop)
					break;
				if(testaSenha(pwd)){
					flag = true;
					break;
				}
			}
		}
		else{
			for(int x = min ; x < max; x++){ //For, percorrendo todas as senhas possiveis dentro de um limite específico.
				if(stop)
					break;
				if(testaSenha(Integer.toString(x))){
					flag = true;
					break;
				}
			}
		}
		status();
	}

	/**
	 * Testa uma única senha, enviando a requisição ao servidor e retornando a resposta.
	 *
	 * @param pwd Senha que será testada.
	 * @return boolean dizendo se a senha foi aceita ou não.
	 */
	public boolean testaSenha(String pwd){
		msg = usuario + Utils.SEPARADOR + pwd;
		try{
			out.writeUTF(msg);
			String data = in.readUTF();
			if(data.equals("accept")){
				return true;
			}
		}catch(IOException e){
			System.err.println("Exception: \n\t" + e);
		}
		return false;

	}
	public void status(){
		//envia uma mensagem para o taskBag com a flag. Se for true, envia que conseguiu, se for false, envia falha.
		//Desta forma o taskbag pode pedir que as outras máquinas encarregadas parem de de buscar a senha.
	}

	/**
	 * Chamado por objetos externos à classe.
	 * Avisa para a Thread que ela pode finalizar a execução.
	 */
	public void encerrar(){
		this.stop = true;
	}


}
