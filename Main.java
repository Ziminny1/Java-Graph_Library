import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        Grafo g = new Grafo(false, false ,false);

        g.AdicionarVertice("A");
        g.AdicionarVertice("B");
        g.AdicionarVertice("C");
        g.AdicionarVertice("D");
        g.AdicionarVertice("E");
        g.AdicionarVertice("F");

        g.AdicionarAresta("A", "C");
        g.AdicionarAresta("A", "D");
        g.AdicionarAresta("A", "F");
        g.AdicionarAresta("B", "E");
        g.AdicionarAresta("B", "F");
        g.AdicionarAresta("C", "D");
        g.AdicionarAresta("C", "E");
        g.AdicionarAresta("E", "F");

        for(Grafo gn : g.RetornaGirvanNewman(2)){
            System.out.println(gn);
            System.out.println("AUTORES MAIS INFLUENTES PERANTE A CENTRALIDADE DE GRAU");
            ArrayList<String> autoresInfluentesGrauGirvan = gn.RetornaArestasCentralidadeGrau();
            for (int i = 0; i < autoresInfluentesGrauGirvan.size(); i++){
                System.out.println(autoresInfluentesGrauGirvan.get(i));
            }
        }

        System.out.println();
        System.out.println("Arestas Relevantes Intermediacao: ");
        ArrayList<String> ateste = g.RetornaArestasIntermediacao();
        for (int i = 0; i < ateste.size(); i++){
            System.out.println(ateste.get(i));
        }

        for(String vertice : g.ExtrairCentralidadeProximidade()){
            System.out.println(vertice);
        }

        for(String vertice : g.ExtrairExcentricidade()){
            System.out.println(vertice);
        }

        System.out.println(g.MediaDistanciasGeodesicas());


        Grafo grafo = CSVReader.lerCSV("tabela_artigos_limpa.csv");
        System.out.println(grafo);

        // 1 - Quais pares de autores são os mais produtivos dentro da rede? Elenque os 10 pares de autores mais produtivos da rede
        System.out.println();
        System.out.println("PARES DE AUTORES MAIS PRODUTIVOS");
        ArrayList<String> paresAutores = grafo.RetornaArestas();
        for (int i = 0; i < 10; i++){
            System.out.println(paresAutores.get(i));
        }

        // 2 - Quantas componentes o grafo possui? O que isso representa?
        System.out.println();
        System.out.println("COMPONENTES: "+grafo.ExtrairComponentes().size());

        for (ArrayList componente : grafo.ExtrairComponentes()){
            System.out.println("COMPONENTES TAMANHO: "+componente.size());
        }

        // 3 - Qual é a distribuição dos graus dos nós da rede? Essa distribuição demonstra comportamento de uma rede complexa?
        System.out.println();
        HistogramaGrau.gerarHistograma(grafo, "HistogramaArtigos");

        // 4 - Quais são os 10 autores mais influentes perante a métrica de centralidade de grau? O que essa métrica representa nesse contexto?
        System.out.println();
        System.out.println("AUTORES MAIS INFLUENTES PERANTE A CENTRALIDADE DE GRAU");
        ArrayList<String> autoresInfluentesGrau = grafo.RetornaArestasCentralidadeGrau();
        for (int i = 0; i < 10; i++){
            System.out.println(autoresInfluentesGrau.get(i));
        }

        // 5 - Quais são os 10 autores mais influentes perante a métrica de centralidade de intermediação? O que essa métrica representa nesse contexto?
        System.out.println();
        System.out.println("Autores Influentes Intermediacao: ");
        ArrayList<String> autoresInfluentesIntermediacao = grafo.RetornaVerticesCentralidadeIntermediacao();
        for (int i = 0; i < 10; i++){
            System.out.println(autoresInfluentesIntermediacao.get(i));
        }

        System.out.println("TESTE RECUPERACAO DE PESO APOS INTERMEDIACAO"+grafo.RecuperarPeso("RODRIGO DA ROSA RIGHI", "CRISTIANO ANDRE DA COSTA"));

        // 6 - Quais são os 10 autores mais influentes perante a métrica de centralidade de proximidade? O que essa métrica representa nesse contexto?
        System.out.println();
        System.out.println("Autores Influentes Proximidade: ");
        ArrayList<String> autoresInfluentesProximidade = grafo.RetornaVerticesCentralidadeProximidade();
        for (int i = 0; i < 16; i++){
            System.out.println(autoresInfluentesProximidade.get(i));
        }

        // 7 - Quais são os 10 autores mais influentes perante a métrica de centralidade de excentricidade? O que essa métrica representa nesse contexto?
        // Removendo as vertices que fazem parte das menores componentes do grafo

        grafo.RemoverVertice("PEDRO HENRIQUE BUGATTI");
        grafo.RemoverVertice("PRISCILA TIEMI MAEDA SAITO");
        grafo.RemoverVertice("EDUARDO FONTOURA COSTA");
        grafo.RemoverVertice("ELIAS SALOMAO HELOU NETO");
        grafo.RemoverVertice("HANSENCLEVER DE FRANCA BASSANI");
        grafo.RemoverVertice("EDUARDO SANY LABER");
        grafo.RemoverVertice("MARCO SERPA MOLINARO");

        System.out.println("COMPONENTES: "+grafo.ExtrairComponentes().size());

        System.out.println();
        System.out.println("Autores Influentes Excentricidade");
        ArrayList<String> autoresInfluentesExcentricidade = grafo.RetornaVerticesCentralidadeExcentricidade();
        for (int i = 0; i < 10; i++){
            System.out.println(autoresInfluentesExcentricidade.get(i));
        }

        // 8 - Calcule o diâmetro e o raio do grafo. O que essas métricas representam nesse contexto?
        System.out.println("DIAMETRO: "+grafo.ExtrairDiametro());
        System.out.println("RAIO: "+grafo.ExtrairRaio());

        // 9 - Quais são as 10 arestas mais relevantes no grafo perante a métrica de centralidade de intermediação? Dentre essas arestas, há algum comportamento que se destaca?
        System.out.println();
        System.out.println("Arestas Relevantes Intermediacao: ");
        ArrayList<String> arestasRelevantesIntermediacao = grafo.RetornaArestasIntermediacao();
        for (int i = 0; i < 10; i++){
            System.out.println(arestasRelevantesIntermediacao.get(i));
        }

        // 10 - Qual é a média das distâncias geodésicas da maior componente do grafo? O que essa métrica representa nesse contexto?
        System.out.println("MEDIA DAS DISTANCIAS GEODESICAS: "+grafo.MediaDistanciasGeodesicas());

        // 11 Dentro do grafo, encontre a componente com a maior quantidade de vértices. Dentro desta
        //componente, execute o algoritmo de Girvan-Newman e encontre as quatro principais
        //comunidades. Para cada comunidade, identifique e discuta os autores mais significativos de acordo com as métricas que julgar mais adequado.
        for(Grafo gn : grafo.RetornaGirvanNewman(4)){
            System.out.println(gn);
            System.out.println("AUTORES MAIS INFLUENTES PERANTE A CENTRALIDADE DE GRAU");
            ArrayList<String> autoresInfluentesGrauGirvan = gn.RetornaArestasCentralidadeGrau();
            for (int i = 0; i < autoresInfluentesGrauGirvan.size(); i++){
                System.out.println(autoresInfluentesGrauGirvan.get(i));
            }
        }

        //grafo.CalculaCaminhos();
    }
}
