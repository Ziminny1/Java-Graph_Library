

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HistogramaGrau {

    public static void gerarHistograma(Grafo g, String nomeArquivo) throws IOException {

        Map<String, Integer> grausVertices = g.getGrausVertices();

        Map<Integer, Integer> contagemGraus = new HashMap<>();

        for (Integer grau : grausVertices.values()) {

            if (contagemGraus.containsKey(grau)) {
                Integer contagemAtual = contagemGraus.get(grau);
                contagemGraus.put(grau, contagemAtual + 1);

            } else {
                contagemGraus.put(grau, 1);
            }
        }


        int[] graus = new int[contagemGraus.size()];
        int[] quantidades = new int[contagemGraus.size()];

        int index = 0;

        for (Map.Entry<Integer, Integer> entry : contagemGraus.entrySet()) {

            graus[index] = entry.getKey();
            quantidades[index] = entry.getValue();
            index++;
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(800).height(600)
                .title("Distribuição do Grau dos Nós")
                .xAxisTitle("Grau")
                .yAxisTitle("Quantidade de Vértices")
                .build();


        chart.getStyler().setHasAnnotations(true);

        chart.addSeries("Graus", graus, quantidades);

        String arquivo = String.format("./%s.png", nomeArquivo);

        BitmapEncoder.saveBitmap(chart, arquivo, BitmapFormat.PNG);

        String str = String.format("Histograma gerado com sucesso e salvo como '%s.png'", nomeArquivo);
        System.out.println(str);
    }

    public static void gerarHistograma(Grafo g) throws IOException {

        Map<String, Integer> grausVertices = g.getGrausVertices();

        Map<Integer, Integer> contagemGraus = new HashMap<>();

        for (Integer grau : grausVertices.values()) {

            if (contagemGraus.containsKey(grau)) {
                Integer contagemAtual = contagemGraus.get(grau);
                contagemGraus.put(grau, contagemAtual + 1);

            } else {
                contagemGraus.put(grau, 1);
            }
        }


        int[] graus = new int[contagemGraus.size()];
        int[] quantidades = new int[contagemGraus.size()];

        int index = 0;

        for (Map.Entry<Integer, Integer> entry : contagemGraus.entrySet()) {

            graus[index] = entry.getKey();
            quantidades[index] = entry.getValue();
            index++;
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(800).height(600)
                .title("Distribuição do Grau dos Nós")
                .xAxisTitle("Grau")
                .yAxisTitle("Quantidade de Vértices")
                .build();


        chart.getStyler().setHasAnnotations(true);

        chart.addSeries("Graus", graus, quantidades);

        BitmapEncoder.saveBitmap(chart, "./HistogramaGrau.png", BitmapFormat.PNG);

        System.out.println("Histograma gerado com sucesso e salvo como 'HistogramaGrau.png'");
    }
}
