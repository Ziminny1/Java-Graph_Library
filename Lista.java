
import java.util.ArrayList;

public class Lista extends Estrutura {

    ArrayList<Node> vertices;

    public Lista(ArrayList<Node> lista) {

        this.vertices = lista;
    }

    public Lista() {

        this.vertices = new ArrayList<>();
    }

    public void AdicionarVertice(String vertice) {

        vertices.add(new Node(vertice));
    }

    public void AdicionarAresta(String verticeA, String verticeB, float peso) {

        for (Node vertice : vertices) {

            if (vertice.getNome().equals(verticeA)) {
                vertice.AdicionarAresta(verticeB, peso);

                break;
            }
        }
    }

    public void RemoverVertice(String verticeNome) {

        for (int i = 0; i < vertices.size(); i++) {

            if (vertices.get(i).getNome().equals(verticeNome)) {
                vertices.remove(i);

                break;
            }
        }
        for (Node vertice : vertices) {

            vertice.removerAresta(verticeNome);
        }
    }

    public void RemoverAresta(String verticeA, String verticeB) {

        for (Node vertice : vertices) {

            if (vertice.getNome().equals(verticeA)) {
                vertice.removerAresta(verticeB);

                break;
            }
        }
    }

    public boolean VerificarAresta(String verticeA, String verticeB) {

        for (Node vertice : vertices) {

            if (vertice.getNome().equals(verticeA)) {
                return vertice.verificaAresta(verticeB);
            }
        }

        return false;
    }

    public int CalcularDegree(String vertice, boolean direcionado) {

        if (direcionado) {
            return CalcularInDegree(vertice) + CalcularOutDegree(vertice);

        } else {
            return CalcularOutDegree(vertice);
        }
    }

    public int CalcularInDegree(String vertice) {

        int num_arestas = 0;

        for (Node node : vertices) {

            if (node.verificaAresta(vertice)) {
                num_arestas++;
            }
        }

        return num_arestas;
    }

    public int CalcularOutDegree(String vertice) {

        for (Node node : vertices) {

            if (node.getNome().equals(vertice)) {
                return node.getSize();
            }
        }

        return 0;
    }

    public float RecuperarPeso(String verticeA, String verticeB) {

        for (Node vertice : vertices) {

            if (vertice.getNome().equals(verticeA)) {
                return vertice.recuperarPeso(verticeB);
            }
        }

        return Float.POSITIVE_INFINITY;
    }

    public ArrayList<Vizinho> RecuperarArestas(String vertice) {

        for (Node node : vertices) {

            if (node.getNome().equals(vertice)) {
                return node.recuperarArestas();
            }
        }

        return null;
    }

    @Override
    public Matriz Warshall(boolean ponderado) {

        Matriz matriz_warshall = new Matriz();

        for (Node vertice : vertices) {

            matriz_warshall.AdicionarVertice(vertices.indexOf(vertice));
        }

        for (Node vertice : vertices) {

            for (Vizinho aresta : vertice.recuperarArestas()) {

                for (Node vizinho : vertices) {

                    if (vizinho.getNome().equals(aresta.getNome())) {
                        matriz_warshall.AdicionarAresta(vertices.indexOf(vertice), vertices.indexOf(vizinho), aresta.getPeso());
                    }
                }
            }
        }

        return matriz_warshall.Warshall(ponderado);
    }

    public ArrayList<String> PegaVizinhos(String vertice) {

        ArrayList<String> arestas = new ArrayList<>();

        for (Node node : vertices) {

            if (node.getNome().equals(vertice)) {

                for (Vizinho vizinho : node.recuperarArestas()) {

                    arestas.add(vizinho.getNome());
                }

                break;
            }
        }

        return arestas;
    }
}
