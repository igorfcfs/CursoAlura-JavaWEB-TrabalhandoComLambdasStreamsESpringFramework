package br.com.alura.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {

	private Scanner leitura = new Scanner(System.in);

	private ConsumoApi consumo = new ConsumoApi();

	private final String ENDERECO = "https://www.omdbapi.com/?t=";

	private final String API_KEY = "&apikey=6585022c";

	private ConverteDados conversor = new ConverteDados();

	private List<DadosTemporada> temporadas = new ArrayList<>();

	public void exibeMenu() {
		System.out.print("Digite o nome da série para busca: ");
		var nomeSerie = leitura.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);

		for (int i = 1; i <= dados.totalTemporadas(); i++) {
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);
		// temporadas.forEach(t -> System.out.println(t));

		/* Temporadas e Episódios */

//	    for(int i = 0; i < dados.totalTemporadas(); i++) {
//	    	List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//	    	for(int j = 0; j < episodiosTemporada.size(); j++) {
//	    		System.out.println(episodiosTemporada.get(j).titulo());
//	    	}
//	    }

//	    for(var t : temporadas) {
//	    	for (var e : t.episodios()) {
//	    		System.out.println(e.titulo());
//	    	}
//	    }

//	    temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

		/* Temporadas e episódios com mensagens */

//	    System.out.println("\nSeasons and Episodes:");
//	    for(int i = 0; i < dados.totalTemporadas(); i++) {
//	    	System.out.printf("\nSeason %d:\n", i);
//	    	List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//	    	for(int j = 0; j < episodiosTemporada.size(); j++) {
//	    		System.out.println((j+1) + ". " + episodiosTemporada.get(j).titulo());
//	    	}
//	    }

//	    System.out.println("\nSeasons and Episodes:");
//	    for(var t : temporadas) {
//	    	System.out.printf("\nSeason %d:\n", t.numero());
//	    	for (var e : t.episodios()) {
//	    		System.out.println(e.numero() + ". " + e.titulo());
//	    	}
//	    }

		System.out.println("\nSeasons and Episodes:");
		temporadas.forEach(t -> {
			System.out.printf("\nSeason %d:\n", t.numero());
			t.episodios().forEach(e -> {
				System.out.println(e.numero() + ". " + e.titulo());
			});
		});

		// Operacoes em stream: intermediarias -> geram novos fluxos de dados para fazer
		// operacoes agregadas; finais -> finalizam (coletar para uma outra lista,
		// imprimir, percorrer [forEach()], etc)
//	    List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico");
//	    nomes.stream().sorted().forEach(System.out::println);
//	    nomes.stream().sorted().limit(3).forEach(System.out::println);
//	    nomes.stream().sorted().limit(3).filter(n -> n.startsWith("N")).forEach(System.out::println);
//	    nomes.stream().sorted().limit(3).filter(n -> n.startsWith("N")).map(n -> n.toUpperCase()).forEach(System.out::println);

		// map -> recurso que transforma um dado
		// flatMap -> recurso que aglutina lista
		// .collect(Collectors.toList()) -> retorna uma lista mutavel
		// .toList() -> retorna uma lista imutavel
		System.out.println("\nTop 10 Episodes:");
		List<DadosEpisodio> dadosEpisodios = temporadas.stream().flatMap(t -> t.episodios().stream())
				.collect(Collectors.toList()); // lista mutavel

		dadosEpisodios.stream().filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
				.peek(e -> System.out.println("Primeiro Filtro (N/A) " + e))
				.sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
				.peek(e -> System.out.println("Ordenação: " + e)).limit(10).peek(e -> System.out.println("Limit: " + e))
				.map(e -> e.titulo().toUpperCase()).peek(e -> System.out.println("Mapeamento: " + e))
				.forEach(System.out::println);

		System.out.println();

		List<Episodio> episodios = temporadas.stream()
				.flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d)))
				.collect(Collectors.toList());
		episodios.forEach(System.out::println);

		System.out.print("\nDigite o episódio que quer buscar: ");
		var trechoTitulo = leitura.nextLine();

		Optional<Episodio> episodioBuscado = episodios.stream()
				.filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase())).findFirst();
		System.out.println(episodioBuscado);
		if (episodioBuscado.isPresent()) {
			System.out.println("Episódio Encontrado!");
			System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
		} else {
			System.out.println("Episódio não encontrado!");
		}

		System.out.print("\nA partir de que ano você deseja ver os episódios: ");
		var ano = leitura.nextInt();
		leitura.nextLine();

		LocalDate dataBusca = LocalDate.of(ano, 1, 1);

		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		episodios.stream().filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
				.forEach(e -> System.out.println("Temporada: " + e.getTemporada() + " | Episódio: " + e.getTitulo()
						+ " | Data Lançamento: " + e.getDataLancamento().format(formatador)));

		Map<Integer, Double> avaliacoesPorTemporada = episodios.stream().filter(e -> e.getAvaliacao() > 0.0).collect(
				Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));
		System.out.println(avaliacoesPorTemporada);

		DoubleSummaryStatistics est = episodios.stream().filter(e -> e.getAvaliacao() > 0.0)
				.collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
		System.out.println("Média: " + est.getAverage());
		System.out.println("Melhor episódio: " + est.getMax());
		System.out.println("Pior episódio: " + est.getMin());
		System.out.println("Episódios avaliados: " + est.getCount());
	}
}
