package it.polimi.tiw.beans;

public class User {

	private String email;
	private String username;
	private String nome;
	private String cognome;
	private String password; // Forse inutile;
	private String indirizzo;
	
	
	
	public User() {
	}



	public User(String email, String username, String nome, String cognome, String password, String indirizzo)
	{
		this.email = email;
		this.username = username;
		this.nome = nome;
		this.cognome = cognome;
		this.password = password;
		this.indirizzo = indirizzo;
		
	}
	
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	
	
}
