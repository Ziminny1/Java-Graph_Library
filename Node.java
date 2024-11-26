
import java.util.LinkedList;
import java.util.ArrayList;

public class Node {

    String nome;
    LinkedList<Vizinho> vizinhos = new LinkedList<>();

    public Node(String nome) {

        this.nome = nome;
    }

    public String getNome() {

        return nome;
    }

    public Integer getSize() {

        return vizinhos.size();
    }

    public void AdicionarAresta(String vizinhoNome, float peso) {

        boolean existe = false;

        for (Vizinho vizinho : vizinhos) {

            if (vizinho.getNome().equals(vizinhoNome)) {
                existe = true;
                vizinho.setPeso(peso);

                break;
            }
        }
        if (!existe) {

            vizinhos.add(new Vizinho(vizinhoNome, peso));
        }
    }

    public void removerAresta(String vizinhoNome) {

        for (int i = 0; i < vizinhos.size(); i++) {

            if (vizinhos.get(i).getNome().equals(vizinhoNome)) {
                vizinhos.remove(i);

                break;
            }
        }
    }

    public boolean verificaAresta(String vizinhoNome) {

        for (Vizinho vizinho : vizinhos) {

            if (vizinho.getNome().equals(vizinhoNome)) {
                return true;
            }
        }

        return false;
    }

    public Float recuperarPeso(String vizinhoNome) {

        for (Vizinho vizinho : vizinhos) {

            if (vizinho.getNome().equals(vizinhoNome)) {
                return vizinho.getPeso();
            }
        }

        return Float.POSITIVE_INFINITY;
    }

    public ArrayList<Vizinho> recuperarArestas() {

        return new ArrayList<>(vizinhos);
    }
}
