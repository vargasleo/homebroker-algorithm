package algorithm;

import heap.MaxPQ;
import heap.MinPQ;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.io.File.separatorChar;
import static java.lang.Integer.parseInt;
import static java.nio.file.Files.newBufferedReader;

public class StockTradingBrokerAlgorithm {
    private static long profit = 0L;
    private final String RESOURCES_PATH = System.getProperty("user.dir") + separatorChar + "src" + separatorChar + "main" + separatorChar + "resources" + separatorChar;
    private MinPQ<Stock> sellOrders;
    private MaxPQ<Stock> buyOrders;

    static class Stock implements Comparable<Stock> {
        public long price;
        public long quantity;

        public Stock(int price, int quantity) {
            this.price = price;
            this.quantity = quantity;
        }

        @Override
        public int compareTo(Stock o) {
            if (this.quantity != o.quantity)
                return Long.compare(this.quantity, o.quantity);
            return 0;
        }
    }

    public void run() throws IOException {
        final var ordersFilename = this.askOrdersFilename();
        final var ordersPath = Paths.get(RESOURCES_PATH + ordersFilename);
        final var ordersReader = newBufferedReader(ordersPath, StandardCharsets.UTF_8);
        this.doRun(ordersReader);
        this.printResults(ordersFilename);
    }

    private void printResults(String ordersFilename) {
        System.out.println(ordersFilename);
        System.out.println("Lucro total: " + profit);
        System.out.println("Compras restantes: " + buyOrders.size());
        System.out.println("Vendas restantes: " + sellOrders.size());
        System.out.println("Tempo: [EM CONSTRUÇÃO]");
    }

    private String askOrdersFilename() {
        final var sc = new Scanner(System.in);
        final var ordersFilename = sc.next();
        sc.close();
        return ordersFilename;
    }

    private void doRun(BufferedReader ordersReader) throws IOException {
        var line = ordersReader.readLine();

        final var heapCapacity = parseInt(line);

        sellOrders = new MinPQ<>(heapCapacity);
        buyOrders = new MaxPQ<>(heapCapacity);

        while ((line = ordersReader.readLine()) != null) {

            var data = line.split(" ");
            final var stock = new Stock(parseInt(data[1]), parseInt(data[2]));

            if (data[0].equals("C"))
                buyOrders.insert(stock);
            else
                sellOrders.insert(stock);

            executePossibleOrders();
        }
        ordersReader.close();
    }

    private void executePossibleOrders() {
        var isLucrativeOperation = true;
        do {
            if (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
                var buyOrder = buyOrders.max();
                var sellOrder = sellOrders.min();
                if (buyOrder.quantity >= sellOrder.quantity) {
                    if (sellOrder.price > buyOrder.price) {
                        profit += (buyOrders.delMax().quantity - sellOrder.quantity) * buyOrder.price;
                        sellOrder.price -= buyOrder.price;
                    } else if (sellOrder.price == buyOrder.price)
                        profit += (buyOrders.delMax().quantity - sellOrders.delMin().quantity) * buyOrder.price;
                    else {
                        profit += sellOrders.delMin().price * (buyOrder.quantity - sellOrder.quantity);
                        buyOrder.price -= sellOrder.price;
                    }
                } else
                    isLucrativeOperation = false;

            }
        } while (!sellOrders.isEmpty() && !buyOrders.isEmpty() && isLucrativeOperation);
    }
}
