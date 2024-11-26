
import java.util.ArrayList;

public class Matriz extends Estrutura {

    float[][] matriz;

    public Matriz(float[][] matriz) {

        this.matriz = matriz;
    }

    public Matriz() {

        this.matriz = new float[0][0];
    }

    public void AdicionarVertice(int vertice) {

        float[][] nova_matriz = new float[matriz.length + 1][matriz.length + 1];
        int indice_linha = 0;

        for (int i = 0; i < nova_matriz.length; i++) {

            int indice_coluna = 0;

            for (int j = 0; j < nova_matriz[i].length; j++) {

                if (indice_linha < matriz.length && indice_coluna < matriz[i].length) {
                    nova_matriz[i][j] = matriz[i][j];

                } else {
                    nova_matriz[i][j] = Float.POSITIVE_INFINITY;
                }
                indice_coluna++;
            }
            indice_linha++;
        }
        matriz = nova_matriz;
    }

    public void AdicionarAresta(int verticeA, int verticeB, float peso) {

        matriz[verticeA][verticeB] = peso;
    }

    public void RemoverVertice(int vertice) {

        if (matriz.length > 0) {
            float[][] nova_matriz = new float[matriz.length - 1][matriz.length - 1];

            int linha_indice = 0;

            for (int i = 0; i < matriz.length; i++) {

                if (i == vertice) {
                    continue;
                }

                int coluna_indice = 0;

                for (int j = 0; j < matriz[i].length; j++) {

                    if (j == vertice) {
                        continue;
                    }
                    nova_matriz[linha_indice][coluna_indice] = matriz[i][j];
                    coluna_indice++;
                }
                linha_indice++;
            }
            matriz = nova_matriz;
        }
    }

    public void RemoverAresta(int verticeA, int verticeB) {

        matriz[verticeA][verticeB] = Float.POSITIVE_INFINITY;
    }

    public boolean VerificarAresta(int verticeA, int verticeB) {

        if (matriz[verticeA][verticeB] != Float.POSITIVE_INFINITY) {
            return true;
        }
        return false;
    }

    public int CalcularDegree(int vertice, boolean direcionado) {

        if (direcionado) {
            return CalcularOutDegree(vertice) + CalcularInDegree(vertice);

        } else {
            return CalcularOutDegree(vertice);
        }
    }

    public int CalcularOutDegree(int vertice) {

        int num_arestas = 0;

        for (int i = 0; i < matriz[vertice].length; i++) {

            if (matriz[vertice][i] != Float.POSITIVE_INFINITY) {
                num_arestas++;
            }
        }

        return num_arestas;
    }

    public int CalcularInDegree(int vertice) {

        int num_arestas = 0;

        for (int i = 0; i < matriz.length; i++) {

            for (int j = 0; j < matriz[i].length; j++) {

                if (j == vertice) {

                    if (matriz[i][j] != Float.POSITIVE_INFINITY) {
                        num_arestas++;
                    }
                }
            }
        }

        return num_arestas;
    }

    public float RecuperarPeso(int verticeA, int verticeB) {

        return matriz[verticeA][verticeB];
    }

    public float[] RecuperarArestas(int vertice) {

        return matriz[vertice];
    }

    @Override
    public Matriz Warshall(boolean isPonderado) {

        float[][] nova_matriz = new float[matriz.length][matriz.length];

        for (int i = 0; i < matriz.length; i++) {

            for (int j = 0; j < matriz[i].length; j++) {

                if (matriz[i][j] != Float.POSITIVE_INFINITY) {
                    nova_matriz[i][j] = 1;

                } else {
                    nova_matriz[i][j] = 0;
                }
            }
        }

        for (int k = 0; k < matriz.length; k++) {

            for (int i = 0; i < matriz.length; i++) {

                for (int j = 0; j < matriz.length; j++) {

                    if (isPonderado) {

                        if (nova_matriz[i][k] == 1 && nova_matriz[k][j] == 1) {
                            nova_matriz[i][j] = 1;
                        }

                    } else {
                        if (nova_matriz[i][k] != 0 && nova_matriz[k][j] != 0) {
                            nova_matriz[i][j] = 1;
                        }
                    }
                }
            }
        }
        return new Matriz(nova_matriz);
    }

    public ArrayList<Integer> PegaVizinhos(int vertice) {

        ArrayList<Integer> vizinhos = new ArrayList<>();

        for (int i = 0; i < matriz[vertice].length; i++) {

            if (matriz[vertice][i] != Float.POSITIVE_INFINITY) {
                vizinhos.add(i);
            }
        }

        return vizinhos;
    }

    public boolean VerificaConectado() {

        for (int i = 0; i < matriz.length; i++) {

            for (int j = 0; j < matriz[i].length; j++) {

                if (matriz[i][j] == 0) {
                    return false;
                }
            }
        }

        return true;
    }
}
