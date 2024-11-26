import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.Random;

public class ConstrutorRedes {
    public ConstrutorRedes() {
    }

    public Grafo GerarRedeAleatoria(Integer numVertices, Float probabilidade) {

        Grafo g = new Grafo(false, false, false);


        // gerando vertices
        for (int i = 0; i < numVertices; i++) {
            g.AdicionarVertice(String.valueOf(i));
        }


        //gerando arestas aleatorias com base na probabilidade
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (i != j) {
                    Random r = new Random();
                    float sorteio = r.nextFloat();

                    if (sorteio < probabilidade) {
                        g.AdicionarAresta(String.valueOf(i), String.valueOf(j));
                    }
                }
            }
        }
        return g;
    }

    public Grafo GerarMundoPequeno(int numVertices, int k, float probabilidade) {
        int grau_inicial = (int) Math.ceil(k / 2f);
        Grafo g = new Grafo(false, false, false);

        // gerando vertices
        for (int i = 0; i < numVertices; i++) {
            g.AdicionarVertice(String.valueOf(i));
        }

        // lista de arestas
        ArrayList<String> arestas = new ArrayList<>();

        // criando arestas com base no grau inicial
        for (int i = 0; i < numVertices; i++) {

            int j = -grau_inicial;

            while (g.CalcularDegree(String.valueOf(i)) < k) {
                if (j >= numVertices) {
                    break;
                }

                if (j + i >= numVertices) {

                    if (g.CalcularDegree(String.valueOf(j + i - numVertices)) < k) {

                        g.AdicionarAresta(String.valueOf(i), String.valueOf(j + i - numVertices));
                        arestas.add(i + " " + (j + i - numVertices));

                    }

                } else if (j + i < 0) {
                    if (g.CalcularDegree(String.valueOf(numVertices + j)) < k) {

                        g.AdicionarAresta(String.valueOf(i), String.valueOf(numVertices + i + j));
                        arestas.add(i + " " + (numVertices + i + j));
                    }

                } else if (g.CalcularDegree(String.valueOf(j + i)) < k && j != 0) {

                    g.AdicionarAresta(String.valueOf(i), String.valueOf(j + i));
                    arestas.add(i + " " + (j + i));

                }

                j++;
            }
        }

        // mudando as arestas aleatoriamente com probabilidade

        for (String aresta : arestas) {
            String[] vertices = aresta.split(" ");

            String verticeA = vertices[0];
            String verticeB = vertices[1];

            if (g.VerificarAresta(verticeA, verticeB)) {
                Random r = new Random();
                float sorteio = r.nextFloat();
                if (sorteio < probabilidade) {
                    g.RemoverAresta(verticeA, verticeB);

                    int sorteioVertice = r.nextInt(numVertices);
                    int i = 0;
                    while (sorteioVertice != Integer.parseInt(verticeA) || sorteioVertice != Integer.parseInt(verticeB)) {
                        if (i > numVertices) {
                            break;
                        }
                        sorteioVertice = r.nextInt(numVertices);
                        i++;
                    }

                    g.AdicionarAresta(verticeA, String.valueOf(sorteioVertice));
                }
            }
        }

        return g;
    }


    public Grafo GerarScaleFree(int numVertices, int k) {
        Grafo g = new Grafo(false, false, false);

        if (k <= numVertices) {

            Random r = new Random();

            // escolhendo o numero inicial de vertices (5% do valor total mas não menor do que 2)
            int numInicial = (int) (numVertices * 0.05);
            if (numInicial < k) {
                numInicial = k;
            }
            if (numInicial == 1 && numInicial + 1 < numVertices) {
                numInicial++;
            }

            int i;
            // gerando o numero inicial de vertices
            for (i = 0; i < numInicial; i++) {
                g.AdicionarVertice(String.valueOf(i));
            }

            // criando as conexões iniciais
            for (int j = 0; j < numInicial; j++) {
                for (int n = 0; n < k; n++){
                    int sorteio = r.nextInt(numInicial);

                    while (sorteio == j || g.VerificarAresta(String.valueOf(j), String.valueOf(sorteio))) {
                        sorteio = r.nextInt(numInicial);
                    }
                    g.AdicionarAresta(String.valueOf(j), String.valueOf(sorteio));
                }
            }

            // criando as vertices restantes e adicionando as conexões
            for (; i < numVertices; i++) {

                g.AdicionarVertice(String.valueOf(i));

                int somaDeArestas = 0;
                for (int m = 0; m < i; m++) {
                    somaDeArestas += g.CalcularDegree(String.valueOf(m));
                }

                while (g.CalcularDegree(String.valueOf(i)) < k) {

                    int proximoVertice = r.nextInt(i - 1);

                    // calculando o denominador

                    float sorteio = r.nextFloat();

                    float probabilidade = g.CalcularDegree(String.valueOf(proximoVertice)) / (float) somaDeArestas;

                    if (sorteio < probabilidade) {
                        g.AdicionarAresta(String.valueOf(i), String.valueOf(proximoVertice));
                    }

                }
            }

        }
        return g;
    }
}
