import broker.Count;
import broker.algorithm.StockTradingBrokerAlgorithm;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        final var listaDeArquivos = List.of(
                "trinta_enunciado.txt",
                "trinta.txt",
                "cem.txt",
                "trezentos.txt",
                "mil.txt",
                "tres_mil.txt",
                "cinco_mil.txt",
                "dez_mil.txt",
                "cinquenta_mil.txt",
                "cem_mil.txt",
                "milhao.txt",
                "dez_milhoes.txt"
        );

        listaDeArquivos.forEach(i -> {
                    final var stockTradingBroker = new StockTradingBrokerAlgorithm();
                    try {
                        Count.count = 0;
                        stockTradingBroker.runFile(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("'''---...---'''---...---");
                });
    }
}
