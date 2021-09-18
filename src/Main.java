import main.algorithm.StockTradingBrokerAlgorithm;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final var stockTradingBroker = new StockTradingBrokerAlgorithm();
        stockTradingBroker.run();
    }
}
