package broker.algorithm;

import broker.Count;
import broker.heap.MaxPQ;
import broker.heap.MinPQ;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Scanner;

import static java.io.File.separatorChar;
import static java.lang.Integer.parseInt;
import static java.nio.file.Files.newBufferedReader;
import static java.time.temporal.ChronoUnit.*;

public class StockTradingBrokerAlgorithm {
    private long profit = 0;
    private int totalOrders;
    private final String RESOURCES_PATH = System.getProperty("user.dir") + separatorChar + "src" + separatorChar + "main" + separatorChar + "resources" + separatorChar;
    private MinPQ<Stock> sellOrders;
    private MaxPQ<Stock> buyOrders;
    private LocalTime tempoInicial;

    public void runFile(String i) throws IOException {
        final var ordersPath = Paths.get(RESOURCES_PATH + i);
        final var ordersReader = newBufferedReader(ordersPath, StandardCharsets.UTF_8);
        this.doRun(ordersReader);
        this.printResults(i);
    }

    static class Stock implements Comparable<Stock> {
        public long quantity;
        public long price;

        public Stock(int quantity, int price) {
            this.quantity = quantity;
            this.price = price;
        }

        @Override
        public int compareTo(Stock o) {
            if (this.price != o.price)
                return Long.compare(this.price, o.price);
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
        final var tempoExecucao = (double) MICROS.between(tempoInicial, LocalTime.now()) / 1000000;
        System.out.println(ordersFilename);
        System.out.println("Número total de ordens: " + totalOrders);
        System.out.println("Número aproximado de operações: " + Count.count);
        System.out.println("Tempo aproximado de execução: " + tempoExecucao);
        System.out.println("Lucro: " + profit);
        System.out.println("Ordens de compra restantes: " + buyOrders.size());
        System.out.println("Ordens de venda restantes: " + sellOrders.size());
    }

    private String askOrdersFilename() {
        final var sc = new Scanner(System.in);
        final var ordersFilename = sc.next();
        sc.close();
        return ordersFilename;
    }

    protected void doRun(BufferedReader ordersReader) throws IOException {
        tempoInicial = LocalTime.now();
        var line = ordersReader.readLine();
        totalOrders = parseInt(line);
        sellOrders = new MinPQ<>(totalOrders);
        buyOrders = new MaxPQ<>(totalOrders);
        Count.count++;
        while ((line = ordersReader.readLine()) != null) {
            var data = line.split(" ");
            final var stock = new Stock(parseInt(data[1]), parseInt(data[2]));
            Count.count++;
            if (data[0].equals("C"))
                buyOrders.insert(stock);
            else if (data[0].equals("V"))
                sellOrders.insert(stock);
            else
                throw new IllegalArgumentException("Formato de arquivo inválido.");
            executePossibleOrders();
        }
        ordersReader.close();
    }


    private void executePossibleOrders() {
        var isLucrativeOperation = true;
        Count.count++;
        do {
            Count.count++;
            if (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
                var buyOrder = buyOrders.max();
                var sellOrder = sellOrders.min();
                if (buyOrder.price >= sellOrder.price) {
                    if (sellOrder.quantity > buyOrder.quantity) {
                        profit += (buyOrders.delMax().price - sellOrder.price) * buyOrder.quantity;
                        sellOrder.quantity -= buyOrder.quantity;
                    } else if (sellOrder.quantity == buyOrder.quantity)
                        profit += (buyOrders.delMax().price - sellOrders.delMin().price) * buyOrder.quantity;
                    else {
                        profit += sellOrders.delMin().quantity * (buyOrder.price - sellOrder.price);
                        buyOrder.quantity -= sellOrder.quantity;
                    }
                } else
                    isLucrativeOperation = false;
            }
        } while (!sellOrders.isEmpty() && !buyOrders.isEmpty() && isLucrativeOperation);
    }
}
