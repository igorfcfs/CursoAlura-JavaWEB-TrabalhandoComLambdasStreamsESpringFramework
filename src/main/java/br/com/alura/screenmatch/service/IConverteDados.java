package br.com.alura.screenmatch.service;

public interface IConverteDados {
	public <T> T obterDados(String json, Class<T> clas);
}
