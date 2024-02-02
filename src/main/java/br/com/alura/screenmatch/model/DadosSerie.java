package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Annotations
//JsonAlias("name") nome -> le um atributo name, mas na hora de escrever escreve nome
//JsonProperty("name") nome -> le um atributo name e na hora de escrever tambem escreve name

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie (@JsonAlias("Title") String titulo,
                                                    @JsonAlias("totalSeasons") Integer totalTemporadas,
                                                    @JsonAlias("imdbRating") String avaliacao) {
}

