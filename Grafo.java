import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Grafo {

    private Estrutura estrutura;
    private boolean representacaoMatriz;
    private LinkedHashMap<String, Integer> vertices = new LinkedHashMap<>();
    private boolean direcionado;
    private boolean ponderado;

    // Caminhos guardados pelo dijkstra para evitar calcular novamente
    private LinkedHashMap<String, Caminho> caminhosDijkstra = new LinkedHashMap<>();

    private boolean conectado = false;

    private boolean invertivel = false;
    private boolean pesosEstaoInvertidos = false;


    // Guardando as arestas sem os pesos invertidos
    private ArrayList<String> arestasNaoInvertidas = new ArrayList<>();
    private ArrayList<Float> pesosNaoInvertidos = new ArrayList<>();

    public Grafo(Estrutura estrutura, LinkedHashMap<String, Integer> vertices, boolean representacaoMatriz, boolean direcionado, boolean ponderado) {

        this.direcionado = direcionado;
        this.ponderado = ponderado;
        this.representacaoMatriz = representacaoMatriz;
        this.vertices = vertices;
        this.estrutura = estrutura;
    }

    public Grafo(boolean representacaoMatriz, boolean direcionado, boolean ponderado) {

        this.direcionado = direcionado;
        this.ponderado = ponderado;
        this.representacaoMatriz = representacaoMatriz;

        if (representacaoMatriz) {
            estrutura = new Matriz();

        } else {
            estrutura = new Lista();
        }
    }

    public Grafo(boolean representacaoMatriz, boolean direcionado, boolean ponderado, boolean invertivel) {

        this.direcionado = direcionado;
        this.ponderado = ponderado;
        this.representacaoMatriz = representacaoMatriz;
        this.invertivel = invertivel;

        if (representacaoMatriz) {
            estrutura = new Matriz();

        } else {
            estrutura = new Lista();
        }
    }

    public Grafo(String file) {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            HashMap<Integer, String> indices = new HashMap<>();

            while ((line = reader.readLine()) != null) {

                if (line.contains("% directed")) {
                    direcionado = Boolean.parseBoolean(line.split("=")[1]);
                }
                if (line.contains("% weighted")) {
                    ponderado = Boolean.parseBoolean(line.split("=")[1]);
                }
                if (line.contains("% representation")) {
                    String representation = line.split("=")[1];

                    if (representation.equals("adjacency_matrix")) {
                        representacaoMatriz = true;
                        estrutura = new Matriz();

                    } else {
                        representacaoMatriz = false;
                        estrutura = new Lista();
                    }
                }
                if (line.startsWith("*Vertices")) {

                    while ((line = reader.readLine()) != null && !line.startsWith("*")) {

                        if (line.isEmpty()) {

                            continue;
                        }

                        int indice = Integer.parseInt(line.split("  ")[0]);

                        String vertice = line.split("  ")[1];

                        AdicionarVertice(vertice);

                        indices.put(indice, vertice);
                    }
                }
                if (line.startsWith("*arcs")) {

                    while ((line = reader.readLine()) != null) {

                        int indiceA = Integer.parseInt(line.split("  ")[0]);
                        int indiceB = Integer.parseInt(line.split("  ")[1]);
                        float peso = Float.parseFloat(line.split("  ")[2]);

                        String verticeA = indices.get(indiceA);
                        String verticeB = indices.get(indiceB);

                        AdicionarAresta(verticeA, verticeB, peso);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void AdicionarVertice(String vertice) {
        // removendo a certeza que o grafo continua conectado
        conectado = false;

        if (!vertices.containsKey(vertice)) {
            vertices.put(vertice, vertices.size());

            if (representacaoMatriz) {
                Matriz matriz = (Matriz) estrutura;
                matriz.AdicionarVertice(vertices.get(vertice));

            } else {
                Lista lista = (Lista) estrutura;
                lista.AdicionarVertice(vertice);
            }
        }
    }

    public void AdicionarAresta(String verticeA, String verticeB, float peso) {
        // removendo a certeza que o grafo continua conectado
        conectado = false;

        if (ponderado) {

            if (vertices.containsKey(verticeA) && vertices.containsKey(verticeB)) {

                if (representacaoMatriz) {
                    Matriz matriz = (Matriz) estrutura;
                    matriz.AdicionarAresta(vertices.get(verticeA), vertices.get(verticeB), peso);

                    if (!direcionado) {
                        matriz.AdicionarAresta(vertices.get(verticeB), vertices.get(verticeA), peso);
                    }

                } else {
                    Lista lista = (Lista) estrutura;
                    lista.AdicionarAresta(verticeA, verticeB, peso);

                    if (!direcionado) {
                        lista.AdicionarAresta(verticeB, verticeA, peso);
                    }
                }
            }

        } else {
            AdicionarAresta(verticeA, verticeB);
        }
    }

    public void AdicionarAresta(String verticeA, String verticeB) {
        // removendo a certeza que o grafo continua conectado
        conectado = false;

        if (vertices.containsKey(verticeA) && vertices.containsKey(verticeB)) {

            if (representacaoMatriz) {
                Matriz matriz = (Matriz) estrutura;
                matriz.AdicionarAresta(vertices.get(verticeA), vertices.get(verticeB), 1);

                if (!direcionado) {
                    matriz.AdicionarAresta(vertices.get(verticeB), vertices.get(verticeA), 1);
                }

            } else {
                Lista lista = (Lista) estrutura;
                lista.AdicionarAresta(verticeA, verticeB, 1);

                if (!direcionado) {
                    lista.AdicionarAresta(verticeB, verticeA, 1);
                }
            }
        }
    }

    public void RemoverVertice(String vertice) {

        // removendo a certeza que o grafo continua conectado
        conectado = false;

        if (vertices.containsKey(vertice)) {
            int verticeARemover = vertices.get(vertice);
            Set<String> chavesACorrigir = vertices.keySet();

            for (String chave : chavesACorrigir) {

                if (vertices.get(chave) > verticeARemover) {
                    vertices.replace(chave, vertices.get(chave) - 1);
                }
            }
            if (representacaoMatriz) {
                Matriz matriz = (Matriz) estrutura;
                matriz.RemoverVertice(verticeARemover);

            } else {
                Lista lista = (Lista) estrutura;
                lista.RemoverVertice(vertice);
            }
            vertices.remove(vertice);
        }
    }

    public void RemoverAresta(String verticeA, String verticeB) {

        // removendo a certeza que o grafo continua conectado
        conectado = false;

        if (vertices.containsKey(verticeA) && vertices.containsKey(verticeB)) {

            if (representacaoMatriz) {
                Matriz matriz = (Matriz) estrutura;
                matriz.RemoverAresta(vertices.get(verticeA), vertices.get(verticeB));

                if (!direcionado) {
                    matriz.RemoverAresta(vertices.get(verticeB), vertices.get(verticeA));
                }

            } else {
                Lista lista = (Lista) estrutura;
                lista.RemoverAresta(verticeA, verticeB);

                if (!direcionado) {
                    lista.RemoverAresta(verticeB, verticeA);
                }
            }
        }
    }

    public boolean VerificarAresta(String verticeA, String verticeB) {

        if (vertices.containsKey(verticeA) && vertices.containsKey(verticeB)) {

            if (representacaoMatriz) {
                Matriz matriz = (Matriz) estrutura;
                boolean arestaAB = matriz.VerificarAresta(vertices.get(verticeA), vertices.get(verticeB));

                if (!direcionado) {
                    boolean arestaBA = matriz.VerificarAresta(vertices.get(verticeB), vertices.get(verticeA));
                    return (arestaAB && arestaBA);
                }

                return arestaAB;

            } else {
                Lista lista = (Lista) estrutura;
                boolean arestaAB = lista.VerificarAresta(verticeA, verticeB);

                if (!direcionado) {
                    boolean arestaBA = lista.VerificarAresta(verticeB, verticeA);
                    return (arestaAB && arestaBA);
                }

                return arestaAB;
            }
        }

        return false;
    }

    public int CalcularDegree(String vertice) {

        if (vertices.containsKey(vertice)) {

            if (representacaoMatriz) {
                Matriz matriz = (Matriz) estrutura;
                return matriz.CalcularDegree(vertices.get(vertice), direcionado);

            } else {
                Lista lista = (Lista) estrutura;
                return lista.CalcularDegree(vertice, direcionado);
            }
        }

        return 0;
    }

    public int CalcularOutDegree(String vertice) {

        if (vertices.containsKey(vertice)) {

            if (representacaoMatriz) {
                Matriz matriz = (Matriz) estrutura;
                return matriz.CalcularOutDegree(vertices.get(vertice));
            }

            Lista lista = (Lista) estrutura;
            return lista.CalcularOutDegree(vertice);
        }

        return 0;
    }

    public int CalcularInDegree(String vertice) {

        if (vertices.containsKey(vertice)) {

            if (representacaoMatriz) {

                Matriz matriz = (Matriz) estrutura;
                return matriz.CalcularInDegree(vertices.get(vertice));
            }

            Lista lista = (Lista) estrutura;
            return lista.CalcularInDegree(vertice);
        }

        return 0;
    }

    public float RecuperarPeso(String verticeA, String verticeB) {

        if (vertices.containsKey(verticeA) && vertices.containsKey(verticeB)) {

            if (representacaoMatriz) {
                Matriz matriz = (Matriz) estrutura;
                return matriz.RecuperarPeso(vertices.get(verticeA), vertices.get(verticeB));

            } else {
                Lista lista = (Lista) estrutura;
                return lista.RecuperarPeso(verticeA, verticeB);
            }
        }

        return Float.POSITIVE_INFINITY;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder();

        Set<String> chaves = vertices.keySet();

        string.append("Nos: ");

        for (String chave : chaves) {
            string.append(chave).append(" (").append(vertices.get(chave)).append(")").append(", ");
        }

        string.deleteCharAt(string.length() - 1).deleteCharAt(string.length() - 1);

        string.append("\n");

        int num_espacos = 2;

        for (String chave : chaves) {

            if (chave.length() > num_espacos) {
                num_espacos = chave.length() + 2;
            }
        }

        if (representacaoMatriz) {
            string.append("Arestas (matriz): ");

            for (String chave : chaves) {

                string.append("\n");

                string.append(String.format("%-" + num_espacos + "s", chave + ":"));

                Matriz matriz = (Matriz) estrutura;

                for (float aresta : matriz.RecuperarArestas(vertices.get(chave))) {

                    if (aresta == Float.POSITIVE_INFINITY) {
                        string.append(" ").append(Float.NaN).append(" ");

                    } else {
                        string.append(" ").append(aresta).append(" ");
                    }
                }
            }

        } else {
            string.append("Arestas (lista de adjacencias): ");

            for (String chave : chaves) {

                string.append("\n");

                string.append(String.format("%-" + num_espacos + "s", chave + "(" + vertices.get(chave) + ")" + ":"));

                Lista lista = (Lista) estrutura;

                ArrayList<Vizinho> arestas = lista.RecuperarArestas(chave);

                for (Vizinho aresta : arestas) {

                    string.append(" ").append(aresta.getNome()).append(": ").append(aresta.getPeso()).append(",");
                }

                if (!arestas.isEmpty()) {
                    string.deleteCharAt(string.length() - 1);
                }
            }
        }

        return string.toString();
    }

    public Grafo Warshall() {

        if (representacaoMatriz) {
            return new Grafo(estrutura.Warshall(ponderado), vertices, representacaoMatriz, direcionado, ponderado);

        } else {
            Lista lista = (Lista) estrutura;
            return new Grafo(lista.Warshall(ponderado), vertices, true, direcionado, ponderado);
        }
    }

    public ArrayList<Object> BuscaProfundidade(String verticeA, String verticeB) {

        long tempoComeco = System.nanoTime();

        ArrayList<String> stack = new ArrayList<>();
        ArrayList<String> visitados = new ArrayList<>();

        stack.add(verticeA);

        while (!stack.isEmpty()) {

            String verticeAtual = stack.remove(stack.size() - 1);

            if (!visitados.contains(verticeAtual)) {
                visitados.add(verticeAtual);
            }

            for (String vizinho : PegaVizinhos(verticeAtual)) {

                if (!visitados.contains(vizinho)) {
                    stack.add(vizinho);
                }
            }

            if (verticeAtual.equals(verticeB)) {

                break;
            }
        }

        long tempoFim = System.nanoTime();
        double tempoTotal = (tempoFim - tempoComeco) / 1e9;

        ArrayList<Object> caminhoTempo = new ArrayList<>();

        caminhoTempo.add(visitados);
        caminhoTempo.add(tempoTotal);

        return caminhoTempo;
    }

    public ArrayList<Object> BuscaLargura(String verticeA, String verticeB) {

        long tempoComeco = System.nanoTime();

        ArrayList<String> queue = new ArrayList<>();
        ArrayList<String> visitados = new ArrayList<>();

        queue.add(verticeA);

        while (!queue.isEmpty()) {

            String verticeAtual = queue.remove(0);

            if (!visitados.contains(verticeAtual)) {
                visitados.add(verticeAtual);
            }

            for (String vizinho : PegaVizinhos(verticeAtual)) {

                if (!visitados.contains(vizinho)) {
                    queue.add(vizinho);
                }
            }

            if (verticeAtual.equals(verticeB)) {

                break;
            }
        }

        long tempoFim = System.nanoTime();
        double duration = (tempoFim - tempoComeco) / 1e9;

        ArrayList<Object> caminhoTempo = new ArrayList<>();

        caminhoTempo.add(visitados);
        caminhoTempo.add(duration);

        return caminhoTempo;
    }

    public ArrayList<Object> Dijkstra(String verticeA, String verticeB) {

        long tempoComeco = System.nanoTime();

        Hashtable<String, String> predecessores = new Hashtable<>();
        Hashtable<String, Float> custos_acumulados = new Hashtable<>();

        for (String vertice : vertices.keySet()) {

            custos_acumulados.put(vertice, Float.POSITIVE_INFINITY);
            predecessores.put(vertice, "null");
        }

        custos_acumulados.replace(verticeA, 0f);

        ArrayList<String> pendentes = new ArrayList<>();

        pendentes.addAll(vertices.keySet());

        while (!pendentes.isEmpty()) {

            String verticeAtual = ExtraiMin(pendentes, custos_acumulados);

            if (verticeAtual.equals("null")) {

                break;
            }

            pendentes.remove(verticeAtual);

            for (String vizinho : PegaVizinhos(verticeAtual)) {

                float novoCusto = custos_acumulados.get(verticeAtual) + RecuperarPeso(verticeAtual, vizinho);

                if (novoCusto < custos_acumulados.get(vizinho)) {
                    custos_acumulados.replace(vizinho, novoCusto);
                    predecessores.replace(vizinho, verticeAtual);
                }
            }
        }

        ArrayList<String> caminho = new ArrayList<>();

        float custoTotal = custos_acumulados.get(verticeB);

        String verticeAtual = verticeB;

        while (!verticeAtual.equals("null")) {

            caminho.add(0, verticeAtual);
            verticeAtual = predecessores.get(verticeAtual);
        }

        long tempoFim = System.nanoTime();
        double tempoTotal = (tempoFim - tempoComeco) / 1e9;

        ArrayList<Object> caminhoCustoTempo = new ArrayList<>();

        caminhoCustoTempo.add(caminho);
        caminhoCustoTempo.add(custoTotal);
        caminhoCustoTempo.add(tempoTotal);

        return caminhoCustoTempo;
    }

    public ArrayList<String> PegaVizinhos(String vertice) {

        ArrayList<String> vizinhos_string = new ArrayList<>();

        if (representacaoMatriz) {

            Matriz matriz = (Matriz) estrutura;

            ArrayList<Integer> vizinhos = matriz.PegaVizinhos(vertices.get(vertice));

            Set<String> chaves = vertices.keySet();

            for (String chave : chaves) {

                if (vizinhos.contains(vertices.get(chave))) {
                    vizinhos_string.add(chave);
                }
            }

        } else {
            Lista lista = (Lista) estrutura;
            vizinhos_string = lista.PegaVizinhos(vertice);
        }

        Collections.sort(vizinhos_string);

        return vizinhos_string;
    }

    public String ExtraiMin(ArrayList<String> vertices, Hashtable<String, Float> pesos_acumulados) {

        String verticeMenorCusto = null;

        float minPeso = Float.POSITIVE_INFINITY;

        for (String vertice : vertices) {

            if (pesos_acumulados.get(vertice) <= minPeso) {
                minPeso = pesos_acumulados.get(vertice);
                verticeMenorCusto = vertice;
            }
        }

        return verticeMenorCusto;
    }

    public boolean VerificaConectado() {

        Matriz matriz_warshall = estrutura.Warshall(ponderado);

        conectado = matriz_warshall.VerificaConectado();
        return conectado;
    }

    public Grafo Prim() {

        if (VerificaConectado()) {
            Hashtable<String, String> predecessores = new Hashtable<>();
            Hashtable<String, Float> pesos = new Hashtable<>();

            ArrayList<String> pendentes = new ArrayList<>();

            for (String vertice : vertices.keySet()) {

                predecessores.put(vertice, "null");
                pesos.put(vertice, Float.POSITIVE_INFINITY);
                pendentes.add(vertice);
            }

            while (!pendentes.isEmpty()) {

                String vertice = ExtraiMin(pendentes, pesos);
                pendentes.remove(vertice);

                for (String vizinho : PegaVizinhos(vertice)) {

                    float peso = RecuperarPeso(vertice, vizinho);

                    if (pendentes.contains(vizinho) && peso < pesos.get(vizinho)) {
                        predecessores.replace(vizinho, vertice);
                        pesos.replace(vizinho, peso);
                    }
                }
            }

            Estrutura estrutura_retorno;

            if (representacaoMatriz) {
                estrutura_retorno = new Matriz();

            } else {
                estrutura_retorno = new Lista();
            }

            Grafo grafo_retorno = new Grafo(representacaoMatriz, direcionado, ponderado);

            for (String vertice : vertices.keySet()) {

                grafo_retorno.AdicionarVertice(vertice);
            }

            for (String verticeInicio : predecessores.keySet()) {

                String verticeFinal = predecessores.get(verticeInicio);

                if (!verticeFinal.equals("null")) {
                    grafo_retorno.AdicionarAresta(verticeFinal, verticeInicio, pesos.get(verticeInicio));
                }
            }

            return grafo_retorno;
        }

        return this;
    }

    public boolean verificaEuleriano() {

        if (direcionado) {

            if (!VerificaConectado()) {
                System.out.println("\nO grafo nao eh euleriano porque nao eh fortemente conectado ou o direcionamento impede o ciclo.");
                return false;
            }

            for (String vertice : vertices.keySet()) {

                int grauEntrada = CalcularInDegree(vertice);
                int grauSaida = CalcularOutDegree(vertice);

                if (grauEntrada != grauSaida) {
                    System.out.println("\nO grafo nao eh Euleriano porque o vertice " + vertice + " tem graus de entrada e saida diferentes.");

                    return false;
                }
            }

            System.out.println("\nO grafo eh Euleriano.");

            return true;

        } else {

            if (!VerificaConectado()) {
                System.out.println("\nO grafo nao eh Euleriano porque nao eh conectado.");

                return false;
            }

            for (String vertice : vertices.keySet()) {

                int grau = CalcularDegree(vertice);

                if (grau % 2 != 0) {
                    System.out.println("\nO grafo nao eh Euleriano porque o vertice " + vertice + " tem grau impar.");

                    return false;
                }
            }

            System.out.println("\nO grafo eh Euleriano.");

            return true;
        }
    }

    public Map<String, Integer> getGrausVertices() {

        Map<String, Integer> graus = new HashMap<>();

        for (String vertice : vertices.keySet()) {

            graus.put(vertice, CalcularDegree(vertice));
        }

        return graus;
    }

    public void salvaPajek(String file) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            String representacao;

            if (representacaoMatriz) {
                representacao = "adjacency_matrix";

            } else {
                representacao = "adjacency_list";
            }

            writer.println("% directed=" + direcionado);
            writer.println("% weighted=" + ponderado);
            writer.println("% representation=" + representacao);

            writer.println("*Vertices " + vertices.size());

            for (String vertice : vertices.keySet()) {

                writer.println(vertices.get(vertice) + "  " + vertice + "  ");
            }
            writer.println();

            writer.println("*arcs");

            for (String verticeA : vertices.keySet()) {

                for (String verticeB : vertices.keySet()) {

                    if (VerificarAresta(verticeA, verticeB)) {
                        float peso = RecuperarPeso(verticeA, verticeB);
                        writer.println(vertices.get(verticeA) + "  " + vertices.get(verticeB) + "  " + peso);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Parte 1 - Extração de componentes (não direcionado)
    public ArrayList<ArrayList> ExtrairComponentes() {

        ArrayList<ArrayList> componentes = new ArrayList<>();

        if (!direcionado) {
            // adicionando todos os vertices na lista de vertices pendentes
            ArrayList<String> pendentes = new ArrayList<>();
            pendentes.addAll(vertices.keySet());

            while (!pendentes.isEmpty()) {

                ArrayList<String> componente = new ArrayList<>();

                // escolhendo o primeiro vertice da lista de pendentes e
                // percorrendo seus vizinhos com uma busca de profundidade
                String ultimoAdicionado = pendentes.remove(0);
                componente.add(ultimoAdicionado);

                for (String vertice : EncontrarAlcancaveis(ultimoAdicionado)) {
                    if (!componente.contains(vertice)) {
                        componente.add(vertice);
                        pendentes.remove(vertice);
                    }
                }
                componentes.add(componente);
            }
        }

        // se for direcionado chama uma função específica que roda o algoritmo de kosaraju
        if (direcionado) {
            componentes = ExtrairComponentesFortemente();
        }

        return componentes;
    }

    public ArrayList<String> EncontrarAlcancaveis(String verticeA) {

        ArrayList<String> stack = new ArrayList<>();
        ArrayList<String> visitados = new ArrayList<>();

        stack.add(verticeA);

        while (!stack.isEmpty()) {

            String verticeAtual = stack.remove(stack.size() - 1);

            if (!visitados.contains(verticeAtual)) {
                visitados.add(verticeAtual);
            }

            for (String vizinho : PegaVizinhos(verticeAtual)) {

                if (!visitados.contains(vizinho)) {
                    stack.add(vizinho);
                }
            }
        }
        return visitados;
    }

    // Parte 1 - Extração de Componentes fortemente conectados
    public ArrayList<ArrayList> ExtrairComponentesFortemente() {

        // Rodando o algoritmo de kosaraju para encontrar os componentes fortemente conectados ( apenas para grafos direcionados )
        if (direcionado) {

            // adicionando todos os vertices na lista de vertices pendentes
            ArrayList<String> pendentes = new ArrayList<>();
            pendentes.addAll(vertices.keySet());

            ArrayList<String> visitados = new ArrayList<>();
            ArrayList<String> finalizados = new ArrayList<>();

            while (!pendentes.isEmpty()) {

                // Rodando DFS com o primeiro vertice da lista de pendentes e guardando os visitados/finalizados de cada vertice
                String verticeInicial = pendentes.remove(0);
                if (!visitados.contains(verticeInicial)) {
                    visitados.add(verticeInicial);
                }

                ArrayList<String> stack = new ArrayList<>();
                stack.add(verticeInicial);

                while (!stack.isEmpty()) {

                    String verticeAtual = stack.remove(stack.size() - 1);
                    pendentes.remove(verticeAtual);

                    if (!visitados.contains(verticeAtual)) {
                        visitados.add(verticeAtual);
                    }

                    ArrayList<String> vizinhos = PegaVizinhos(verticeAtual);
                    Collections.sort(vizinhos, Collections.reverseOrder());

                    // verificando se o vertice está finalizado
                    boolean finalizavel = true;
                    for (String vizinho : vizinhos) {
                        if (!visitados.contains(vizinho)) {
                            finalizavel = false;
                            break;
                        }
                    }

                    if (!finalizados.contains(verticeAtual) && finalizavel) {
                        finalizados.add(verticeAtual);

                        // verificando na lista de visitados os nos que podem ser finalizados
                        for (int i = visitados.size() - 1; i > -1; i--) {
                            boolean finalizado = true;
                            for (String vizinho : PegaVizinhos(visitados.get(i))) {
                                if (!finalizados.contains(vizinho)) {
                                    if (!visitados.contains(vizinho)) {
                                        finalizado = false;
                                        break;
                                    }
                                }
                            }

                            if (finalizado && !finalizados.contains(visitados.get(i))) {
                                finalizados.add(visitados.get(i));
                            }

                        }
                    }

                    for (String vizinho : vizinhos) {

                        if (!visitados.contains(vizinho)) {
                            stack.add(vizinho);
                        }
                    }
                }

            }

            Collections.reverse(finalizados);

            Grafo gT = TransporGrafo();

            return gT.ExtrairComponentesTransposto(finalizados);
        }
        return null;
    }

    // função feita para um grafo transposto receber a ordem decrescente obtida no grafo original
    public ArrayList<ArrayList> ExtrairComponentesTransposto(ArrayList<String> finalizadosDecrescente) {

        ArrayList<ArrayList> componentes = new ArrayList<>();

        // adicionando todos os vertices recebidos em finalizadosDecrescente

        ArrayList<String> visitados = new ArrayList<>();
        ArrayList<String> finalizados = new ArrayList<>();

        while (!finalizadosDecrescente.isEmpty()) {

            ArrayList<String> componente = new ArrayList<>();

            // Rodando DFS com o primeiro vertice da lista de finalizadosDecrescente e guardando os visitados/finalizados de cada vertice
            String verticeInicial = finalizadosDecrescente.remove(0);
            if (!visitados.contains(verticeInicial)) {
                visitados.add(verticeInicial);
                componente.add(verticeInicial);
            }

            ArrayList<String> stack = new ArrayList<>();
            stack.add(verticeInicial);

            while (!stack.isEmpty()) {

                String verticeAtual = stack.remove(stack.size() - 1);
                finalizadosDecrescente.remove(verticeAtual);

                if (!visitados.contains(verticeAtual)) {
                    visitados.add(verticeAtual);
                    componente.add(verticeAtual);
                }

                ArrayList<String> vizinhos = PegaVizinhos(verticeAtual);
                Collections.sort(vizinhos, Collections.reverseOrder());

                // verificando se o vertice está finalizado
                boolean finalizavel = true;
                for (String vizinho : vizinhos) {
                    if (!visitados.contains(vizinho)) {
                        finalizavel = false;
                        break;
                    }
                }

                if (!finalizados.contains(verticeAtual) && finalizavel) {
                    finalizados.add(verticeAtual);

                    // verificando na lista de visitados os nos que podem ser finalizados
                    for (int i = visitados.size() - 1; i > -1; i--) {
                        boolean finalizado = true;
                        for (String vizinho : PegaVizinhos(visitados.get(i))) {
                            if (!finalizados.contains(vizinho)) {
                                if (!visitados.contains(vizinho)) {
                                    finalizado = false;
                                    break;
                                }
                            }
                        }

                        if (finalizado && !finalizados.contains(visitados.get(i))) {
                            finalizados.add(visitados.get(i));
                        }

                    }
                }

                for (String vizinho : vizinhos) {

                    if (!visitados.contains(vizinho)) {
                        stack.add(vizinho);
                    }
                }
            }

            componentes.add(componente);
        }
        return componentes;
    }

    public Grafo TransporGrafo() {
        ArrayList<String> arestas = new ArrayList<>();

        // criando o grafo transposto com as mesmas caracteristicas
        Grafo gTransposto = new Grafo(representacaoMatriz, direcionado, ponderado);

        // adicionando as vertices e recuperando as arestas de cada vertice
        for (String vertice : vertices.keySet()) {
            gTransposto.AdicionarVertice(vertice);

            for (String vizinho : PegaVizinhos(vertice)) {
                if (ponderado) {
                    arestas.add(vertice + " " + vizinho + " " + RecuperarPeso(vertice, vizinho));
                } else {
                    arestas.add(vertice + " " + vizinho);
                }
            }
        }

        // adicionando as arestas transpostas ao novo grafo
        for (String aresta : arestas) {
            String[] tokens = aresta.split(" ");
            if (ponderado) {
                gTransposto.AdicionarAresta(tokens[1], tokens[0], Float.parseFloat(tokens[2]));
            } else {
                gTransposto.AdicionarAresta(tokens[1], tokens[0]);
            }
        }

        return gTransposto;
    }

    // 3 - Função que retorna uma lista de strings representando a centralidade de grau de cada vertice do grafo
    public ArrayList<String> ExtrairCentralidadeGrau() {
        ArrayList<String> centralidades = new ArrayList<>();

        for (String vertice : vertices.keySet()) {
            centralidades.add(vertice + "," + ExtrairCentralidadeGrau(vertice));
        }

        return centralidades;
    }

    // 3 - função que retorna a centralidade de grau de um vertice específico
    public Float ExtrairCentralidadeGrau(String vertice) {
        float centralidade = 0;

        centralidade = CalcularOutDegree(vertice) / (vertices.size() - 1f);

        return centralidade;
    }

    // 5 - Função que retorna uma lista de strings representando a centralidade de proximidade de cada vertice do grafo
    public ArrayList<String> ExtrairCentralidadeProximidade() {
        ArrayList<String> proximidades = new ArrayList<>();

        for (String vertice : vertices.keySet()) {
            proximidades.add(vertice + "," + ExtrairCentralidadeProximidade(vertice));
        }

        return proximidades;
    }

    // 5 - Função que retorna a centralidade de proximidade de um vertice específico
    public Float ExtrairCentralidadeProximidade(String vertice) {

        boolean pesosAlteradosNoEscopo = false;
        if (invertivel && !pesosEstaoInvertidos) {
            InverterPesos();
            pesosAlteradosNoEscopo = true;
        }

        float proximidade = 0;

        int somatoriaDosCaminhos = 0;

        for (String node : vertices.keySet()) {
            if (!node.equals(vertice)) {
                if (caminhosDijkstra.isEmpty()) {
                    CalculaCaminhos();
                }
                if (caminhosDijkstra.containsKey(vertice + "," + node)) {
                    ArrayList<String> caminho = caminhosDijkstra.get(vertice + "," + node).getCaminho();
                    somatoriaDosCaminhos += caminho.size() - 1;
                } else if (caminhosDijkstra.containsKey(node + "," + vertice)) {
                    ArrayList<String> caminho = caminhosDijkstra.get(node + "," + vertice).getCaminho();
                    somatoriaDosCaminhos += caminho.size() - 1;
                }
            }
        }

        if (somatoriaDosCaminhos == 0) {
            proximidade = 0;
        } else {
            if (direcionado) {
                proximidade = (vertices.size() - 1f) / somatoriaDosCaminhos;
            } else {
                proximidade = (EncontrarAlcancaveis(vertice).size() - 1f) / somatoriaDosCaminhos;
            }
        }

        if (invertivel && pesosEstaoInvertidos && pesosAlteradosNoEscopo) {
            ReverterPesos();
        }

        return proximidade;
    }

    // 6 - Função que retorna a excentricidade de cada vertice do grafo
    public ArrayList<String> ExtrairExcentricidade() {
        ArrayList<String> excentricidades = new ArrayList<>();

        for (String vertice : vertices.keySet()) {
            excentricidades.add(vertice + "," + ExtrairExcentricidade(vertice));
        }

        return excentricidades;
    }

    // 6 - Função que retorna a excentricidade de um vertice específico
    public Integer ExtrairExcentricidade(String vertice) {
        if (!conectado) {
            VerificaConectado();
        }
        if (conectado) {

            boolean pesosAlteradosNoEscopo = false;
            if (invertivel && !pesosEstaoInvertidos) {
                InverterPesos();
                pesosAlteradosNoEscopo = true;
            }

            int excentricidade = Integer.MIN_VALUE;

            for (String node : vertices.keySet()) {
                if (!node.equals(vertice)) {

                    if (caminhosDijkstra.isEmpty()) {
                        CalculaCaminhos();
                    }
                    if (caminhosDijkstra.containsKey(vertice + "," + node)) {
                        ArrayList<String> caminho = caminhosDijkstra.get(vertice + "," + node).getCaminho();
                        if (caminho.size() - 1 > excentricidade) {
                            excentricidade = caminho.size() - 1;
                        }
                    } else if (caminhosDijkstra.containsKey(node + "," + vertice)) {
                        ArrayList<String> caminho = caminhosDijkstra.get(node + "," + vertice).getCaminho();
                        if (caminho.size() - 1 > excentricidade) {
                            excentricidade = caminho.size() - 1;
                        }
                    }

                    //ArrayList<String> caminho = (ArrayList<String>) Dijkstra(vertice, node).get(0);
                }
            }


            if (invertivel && pesosEstaoInvertidos && pesosAlteradosNoEscopo) {
                ReverterPesos();
            }
            return excentricidade;
        }
        return null;
    }

    // 7 - Função que retorna o diametro do grafo
    public Integer ExtrairDiametro() {
        if (VerificaConectado()) {

            boolean pesosAlteradosNoEscopo = false;
            if (invertivel && !pesosEstaoInvertidos) {
                InverterPesos();
                pesosAlteradosNoEscopo = true;
            }

            int diametro = Integer.MIN_VALUE;
            for (String vertice : vertices.keySet()) {
                int excentricidadeVertice = ExtrairExcentricidade(vertice);
                if (excentricidadeVertice > diametro) {
                    diametro = excentricidadeVertice;
                }
            }

            if (invertivel && pesosEstaoInvertidos && pesosAlteradosNoEscopo) {
                ReverterPesos();
            }
            return diametro;
        }
        return null;
    }

    // 8 - Função que retorna o raio do grafo
    public Integer ExtrairRaio() {
        if (VerificaConectado()) {

            boolean pesosAlteradosNoEscopo = false;
            if (invertivel && !pesosEstaoInvertidos) {
                InverterPesos();
                pesosAlteradosNoEscopo = true;
            }

            int raio = Integer.MAX_VALUE;
            for (String vertice : vertices.keySet()) {
                int excentricidadeVertice = ExtrairExcentricidade(vertice);
                if (excentricidadeVertice < raio) {
                    raio = excentricidadeVertice;
                }
            }

            if (invertivel && pesosEstaoInvertidos && pesosAlteradosNoEscopo) {
                ReverterPesos();
            }
            return raio;
        }
        return null;
    }

    // Dijkstra que encontra multiplos caminhos
    public ArrayList<ArrayList<String>> DijkstraMultiplo(String verticeA, String verticeB) {

        ArrayList<ArrayList<String>> caminhosPossiveis = new ArrayList<>();

        Hashtable<String, String> predecessores = new Hashtable<>();
        Hashtable<String, Float> custos_acumulados = new Hashtable<>();

        for (String vertice : vertices.keySet()) {

            custos_acumulados.put(vertice, Float.POSITIVE_INFINITY);
            predecessores.put(vertice, "null");
        }

        custos_acumulados.replace(verticeA, 0f);

        ArrayList<String> pendentes = new ArrayList<>();

        pendentes.addAll(vertices.keySet());

        while (!pendentes.isEmpty()) {

            String verticeAtual = ExtraiMin(pendentes, custos_acumulados);

            if (verticeAtual.equals("null")) {

                break;
            }

            pendentes.remove(verticeAtual);

            for (String vizinho : PegaVizinhos(verticeAtual)) {

                float novoCusto = custos_acumulados.get(verticeAtual) + RecuperarPeso(verticeAtual, vizinho);

                if (novoCusto < custos_acumulados.get(vizinho)) {
                    custos_acumulados.replace(vizinho, novoCusto);
                    predecessores.replace(vizinho, verticeAtual);
                } else if (novoCusto == custos_acumulados.get(vizinho)) {
                    // CRIAR NOVO DIJKSTRA QUE ENCONTRA UM CAMINHO ALTERNATIVO

                    Hashtable<String, String> predecessores_alternativo = new Hashtable<>();
                    for (String predecessor : predecessores.keySet()) {
                        predecessores_alternativo.put(predecessor, predecessores.get(predecessor));
                    }
                    predecessores_alternativo.replace(vizinho, verticeAtual);

                    Hashtable<String, Float> custos_acumulados_alternativo = new Hashtable<>();
                    for (String custo : custos_acumulados.keySet()) {
                        custos_acumulados_alternativo.put(custo, custos_acumulados.get(custo));
                    }
                    custos_acumulados_alternativo.replace(vizinho, novoCusto);

                    ArrayList<String> pendentes_alternativo = new ArrayList<>();
                    for (String pendente : pendentes) {
                        pendentes_alternativo.add(pendente);
                    }

                    for (ArrayList<String> caminhoAlternativo : DijkstraMultiplo(verticeA, verticeB, predecessores_alternativo, custos_acumulados_alternativo, pendentes_alternativo)) {
                        if (!caminhosPossiveis.contains(caminhoAlternativo)) {
                            caminhosPossiveis.add(caminhoAlternativo);
                        }
                    }
                }
            }
        }

        boolean caminhoInvalido = false;

        ArrayList<String> caminho = new ArrayList<>();

        String verticeAtual = verticeB;

        while (!verticeAtual.equals("null")) {
            if (caminho.contains(verticeAtual)) {
                caminhoInvalido = true;
                break;
            }

            caminho.add(0, verticeAtual);
            verticeAtual = predecessores.get(verticeAtual);
        }

        if (!caminhoInvalido) {
            if (!caminhosPossiveis.contains(caminho)) {
                caminhosPossiveis.add(caminho);
            }
        }

        return caminhosPossiveis;
    }

    // Dijkstra que é chamado quando um vizinho tem o mesmo valor de custo que os custos acumulados
    public ArrayList<ArrayList<String>> DijkstraMultiplo(String verticeA, String verticeB, Hashtable<String, String> predecessores,
                                                         Hashtable<String, Float> custos_acumulados, ArrayList<String> pendentes) {

        ArrayList<ArrayList<String>> caminhosPossiveis = new ArrayList<>();

        while (!pendentes.isEmpty()) {

            String verticeAtual = ExtraiMin(pendentes, custos_acumulados);

            if (verticeAtual.equals("null")) {

                break;
            }

            pendentes.remove(verticeAtual);

            for (String vizinho : PegaVizinhos(verticeAtual)) {

                float novoCusto = custos_acumulados.get(verticeAtual) + RecuperarPeso(verticeAtual, vizinho);

                if (novoCusto < custos_acumulados.get(vizinho)) {
                    custos_acumulados.replace(vizinho, novoCusto);
                    predecessores.replace(vizinho, verticeAtual);
                } else if (novoCusto == custos_acumulados.get(vizinho)) {

                    Hashtable<String, String> predecessores_alternativo = new Hashtable<>();
                    for (String predecessor : predecessores.keySet()) {
                        predecessores_alternativo.put(predecessor, predecessores.get(predecessor));
                    }
                    predecessores_alternativo.replace(vizinho, verticeAtual);

                    Hashtable<String, Float> custos_acumulados_alternativo = new Hashtable<>();
                    for (String custo : custos_acumulados.keySet()) {
                        custos_acumulados_alternativo.put(custo, custos_acumulados.get(custo));
                    }
                    custos_acumulados_alternativo.replace(vizinho, novoCusto);

                    for (ArrayList<String> caminhoAlternativo : DijkstraMultiplo(verticeA, verticeB, predecessores_alternativo, custos_acumulados_alternativo, pendentes)) {
                        if (!caminhosPossiveis.contains(caminhoAlternativo)) {
                            caminhosPossiveis.add(caminhoAlternativo);
                        }
                    }
                }
            }
        }

        boolean caminhoInvalido = false;

        ArrayList<String> caminho = new ArrayList<>();

        String verticeAtual = verticeB;

        while (!verticeAtual.equals("null")) {
            if (caminho.contains(verticeAtual)) {
                caminhoInvalido = true;
                break;
            }
            caminho.add(0, verticeAtual);
            verticeAtual = predecessores.get(verticeAtual);
        }

        if (!caminho.contains(verticeA) || !caminho.contains(verticeB)) {
            caminhoInvalido = true;
        }

        if (!caminhoInvalido) {
            if (!caminhosPossiveis.contains(caminho)) {
                caminhosPossiveis.add(caminho);
            }
        }
        return caminhosPossiveis;
    }

    // 4 - Função que retorna a centralidade de intermediação de um vertice específico
    public Float ExtrairCentralidadeIntermediacao(String vertice) {

        // boolean que verifica se essa função precisou inverter os pesos, se for verdadeiro, significa que nao há uma função superior dentro do grafo
        // que esta tratando dos pesos alterados
        boolean pesosAlteradosNoEscopo = false;
        if (invertivel && !pesosEstaoInvertidos) {
            InverterPesos();
            pesosAlteradosNoEscopo = true;
        }

        float intermediacao = 0;

        ArrayList<String> nodes = new ArrayList<>();
        for (String node : vertices.keySet()) {
            nodes.add(node);
        }

        float somaCaminhos = 0;

        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i; j < vertices.size(); j++) {
                if (!nodes.get(i).equals(nodes.get(j))) {
                    float somaCaminhosAlternativos = 0;
                    float somaCaminhosComVertice = 0;

                    for (ArrayList<String> caminho : DijkstraMultiplo(nodes.get(i), nodes.get(j))) {
                        somaCaminhosAlternativos += 1;
                        if (caminho.contains(vertice) && !caminho.get(0).equals(vertice) && !caminho.get(caminho.size() - 1).equals(vertice)) {
                            somaCaminhosComVertice += 1;
                        }
                    }

                    somaCaminhos += somaCaminhosComVertice / somaCaminhosAlternativos;
                }
            }
        }

        float normalizador = 2f / ((vertices.size() - 1) * (vertices.size() - 2));
        intermediacao = normalizador * somaCaminhos;

        if (invertivel && pesosEstaoInvertidos && pesosAlteradosNoEscopo) {
            ReverterPesos();
        }
        return intermediacao;
    }

    // 4 - Função que retorna a centralidade de intermediação de todos os vertices
    public ArrayList<String> ExtrairCentralidadeIntermediacao() {
        ArrayList<String> intermediacoes = new ArrayList<>();

        for (String vertice : vertices.keySet()) {
            intermediacoes.add(vertice + "," + ExtrairCentralidadeIntermediacao(vertice));
        }

        return intermediacoes;
    }

    public Float ExtrairCentralidadedeIntermediacao(String vertice) {

        // boolean que verifica se essa função precisou inverter os pesos, se for verdadeiro, significa que nao há uma função superior dentro do grafo
        // que esta tratando dos pesos alterados
        boolean pesosAlteradosNoEscopo = false;
        if (invertivel && !pesosEstaoInvertidos) {
            InverterPesos();
            pesosAlteradosNoEscopo = true;
        }

        float intermediacao = 0;

        float somaCaminhos = 0;

        for (String par : caminhosDijkstra.keySet()) {
            ArrayList<String> caminho = new ArrayList<>();
            caminho = caminhosDijkstra.get(par).getCaminho();
            if (!caminho.isEmpty()) {
                if (caminho.contains(vertice) && !caminho.get(0).equals(vertice) && !caminho.get(caminho.size() - 1).equals(vertice)) {
                    somaCaminhos += 1;
                }
            }
        }

        float normalizador = 2f / ((vertices.size() - 1) * (vertices.size() - 2));
        intermediacao = normalizador * somaCaminhos;

        if (invertivel && pesosEstaoInvertidos && pesosAlteradosNoEscopo) {
            ReverterPesos();
        }
        return intermediacao;
    }

    // 9 - Função que retorna a centralidade de intermediação de uma aresta específica
    public Float ExtrairIntermediacaoAresta(String verticeA, String verticeB) {
        if (VerificarAresta(verticeA, verticeB)) {

            boolean pesosAlteradosNoEscopo = false;
            if (invertivel && !pesosEstaoInvertidos) {
                InverterPesos();
                pesosAlteradosNoEscopo = true;
            }

            float intermediacao = 0;

            ArrayList<String> nodes = new ArrayList<>();
            for (String node : vertices.keySet()) {
                nodes.add(node);
            }

            float somaCaminhos = 0;

            for (int i = 0; i < vertices.size(); i++) {
                for (int j = i; j < vertices.size(); j++) {
                    if (!nodes.get(i).equals(nodes.get(j))) {
                        float somaCaminhosAlternativos = 0;
                        float somaCaminhosComAresta = 0;

                        for (ArrayList<String> caminho : DijkstraMultiplo(nodes.get(i), nodes.get(j))) {
                            somaCaminhosAlternativos += 1;
                            for (int k = 0; k < caminho.size(); k++) {
                                if (caminho.get(k).equals(verticeA) && k + 1 < caminho.size()) {
                                    if (caminho.get(k + 1).equals(verticeB)) {
                                        somaCaminhosComAresta += 1;
                                        break;
                                    }
                                } else if (caminho.get(k).equals(verticeB) && k + 1 < caminho.size()) {
                                    if (caminho.get(k + 1).equals(verticeA)) {
                                        somaCaminhosComAresta += 1;
                                        break;
                                    }
                                }
                            }
                        }

                        somaCaminhos += somaCaminhosComAresta / somaCaminhosAlternativos;
                    }
                }
            }

            float normalizador = 1f / ((vertices.size() - 1) * (vertices.size()));
            intermediacao = normalizador * somaCaminhos;

            if (invertivel && pesosEstaoInvertidos && pesosAlteradosNoEscopo) {
                ReverterPesos();
            }
            return intermediacao;
        }
        return null;
    }

    public Float ExtrairIntermediacaoArestas(String verticeA, String verticeB) {
        if (VerificarAresta(verticeA, verticeB)) {

            boolean pesosAlteradosNoEscopo = false;
            if (invertivel && !pesosEstaoInvertidos) {
                InverterPesos();
                pesosAlteradosNoEscopo = true;
            }

            if (caminhosDijkstra.isEmpty()) {
                CalculaCaminhos();
            }

            float intermediacao = 0;

            float somaCaminhos = 0;

            for (String par : caminhosDijkstra.keySet()) {
                ArrayList<String> caminho = new ArrayList<>();
                caminho = caminhosDijkstra.get(par).getCaminho();
                if (!caminho.isEmpty()) {
                    if (caminho.contains(verticeA) && caminho.contains(verticeB)) {
                        if (caminho.indexOf(verticeA) == caminho.indexOf(verticeB) - 1 || caminho.indexOf(verticeA) == caminho.indexOf(verticeB) + 1)
                            somaCaminhos += 1;
                    }
                }
            }

            float normalizador = 1f / ((vertices.size() - 1) * (vertices.size()));
            intermediacao = normalizador * somaCaminhos;

            if (invertivel && pesosEstaoInvertidos && pesosAlteradosNoEscopo) {
                ReverterPesos();
            }
            return intermediacao;
        }
        return null;
    }


    // 9 - Função que retorna a centralidade de intermediação de cada aresta do grafo
    public ArrayList<String> ExtrairIntermediacaoAresta() {
        ArrayList<String> intermediacoes = new ArrayList<>();

        ArrayList<String> nodes = new ArrayList<>();
        nodes.addAll(vertices.keySet());

        ArrayList<String> arestas = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i; j < nodes.size(); j++) {
                if (VerificarAresta(nodes.get(i), nodes.get(j)) && !nodes.get(i).equals(nodes.get(j))) {

                    arestas.add(nodes.get(i) + "-" + nodes.get(j));
                }
            }
        }

        for (String aresta : arestas) {
            String[] tokens = aresta.split("-");
            intermediacoes.add(aresta + "," + ExtrairIntermediacaoAresta(tokens[0], tokens[1]));
        }

        return intermediacoes;
    }

    public ArrayList<Grafo> GirvanNewman(int subgrafos) {
        if (subgrafos > vertices.size()) {
            System.out.println("numero de subgrafos desejados maior do que numero de vertices");
            return null;
        }

        ArrayList<Grafo> comunidades = new ArrayList<>();
        ArrayList<String> arestasRemovidas = new ArrayList<>();

        while (ExtrairComponentes().size() < subgrafos) {

            ArrayList<String> arestas = ExtrairIntermediacaoAresta();

            float maiorBetweenness = 0;
            String maiorAresta = null;

            for (String aresta : arestas) {
                String[] tokens = aresta.split(",");
                if (Float.parseFloat(tokens[1]) >= maiorBetweenness) {
                    maiorBetweenness = Float.parseFloat(tokens[1]);
                    maiorAresta = tokens[0];
                }
            }
            String[] maiorArestaVertices = maiorAresta.split("-");
            arestasRemovidas.add(maiorAresta + "," + RecuperarPeso(maiorArestaVertices[0], maiorArestaVertices[1]));
            RemoverAresta(maiorArestaVertices[0], maiorArestaVertices[1].replaceAll(",", ""));
        }

        //criando os grafos correspondentes a cada comunidade/componente encontrada.
        for (ArrayList<String> componente : ExtrairComponentes()) {
            //para cada componente extraido, cria-se um grafo novo com os vertices do componente e adiciona ao retorno
            Grafo gn = new Grafo(representacaoMatriz, direcionado, ponderado);
            // inserindo cada vertice e suas arestas no grafo
            for (String vertice : componente) {
                gn.AdicionarVertice(vertice);
            }
            for (String vertice : componente) {
                for (String vizinho : PegaVizinhos(vertice)) {
                    gn.AdicionarAresta(vertice, vizinho, RecuperarPeso(vertice, vizinho));
                }
            }
            comunidades.add(gn);
        }

        //repondo as arestas que foram removidas
        for (String aresta : arestasRemovidas) {

            String[] verticesPesoArestaRemovida = aresta.split(",");
            String[] verticesArestaRemovida = verticesPesoArestaRemovida[0].split("-");
            AdicionarAresta(verticesArestaRemovida[0], verticesArestaRemovida[1], Float.parseFloat(verticesPesoArestaRemovida[1]));
        }
        return comunidades;
    }

    public boolean ExisteVertice(String vertice) {
        return vertices.containsKey(vertice);
    }

    public ArrayList<String> RetornaArestas() {
        ArrayList<String> arestas = new ArrayList<>();
        ArrayList<Float> pesos = new ArrayList<>();

        for (String vertice : vertices.keySet()) {
            for (String vizinho : PegaVizinhos(vertice)) {
                if (!arestas.contains(vizinho + " - " + vertice)) {
                    arestas.add(vertice + " - " + vizinho);
                    pesos.add(RecuperarPeso(vertice, vizinho));
                }
            }
        }

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < arestas.size(); i++) {
            indices.add(i);
        }

        indices.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return pesos.get(i1).compareTo(pesos.get(i2));
            }
        });

        Collections.reverse(indices);

        ArrayList<String> arestasOrdenadas = new ArrayList<>();
        ArrayList<Float> pesosOrdenados = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            arestasOrdenadas.add(arestas.get(indices.get(i)));
            pesosOrdenados.add(pesos.get(indices.get(i)));
        }

        ArrayList<String> arestasPesosOrdenadas = new ArrayList<>();

        for (int i = 0; i < arestasOrdenadas.size(); i++) {
            arestasPesosOrdenadas.add(arestasOrdenadas.get(i) + ": " + pesosOrdenados.get(i));
        }

        return arestasPesosOrdenadas;
    }

    public ArrayList<String> RetornaArestasCentralidadeGrau() {
        ArrayList<String> listaVertices = new ArrayList<>();
        ArrayList<Float> graus = new ArrayList<>();

        for (String vertice : vertices.keySet()) {
            listaVertices.add(vertice);
            graus.add(ExtrairCentralidadeGrau(vertice));

        }

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < listaVertices.size(); i++) {
            indices.add(i);
        }

        indices.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return graus.get(i1).compareTo(graus.get(i2));
            }
        });

        Collections.reverse(indices);

        ArrayList<String> verticesOrdenados = new ArrayList<>();
        ArrayList<Float> grausOrdenados = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            verticesOrdenados.add(listaVertices.get(indices.get(i)));
            grausOrdenados.add(graus.get(indices.get(i)));
        }

        ArrayList<String> verticesGrausOrdenados = new ArrayList<>();

        for (int i = 0; i < verticesOrdenados.size(); i++) {
            verticesGrausOrdenados.add(verticesOrdenados.get(i) + ": " + grausOrdenados.get(i));
        }

        return verticesGrausOrdenados;
    }

    public ArrayList<String> RetornaVerticesCentralidadeIntermediacao() {

        if (invertivel && !pesosEstaoInvertidos) {
            InverterPesos();
        }

        ArrayList<String> listaVertices = new ArrayList<>();
        ArrayList<Float> intermediacoes = new ArrayList<>();

        for (String vertice : vertices.keySet()) {
            listaVertices.add(vertice);
            intermediacoes.add(ExtrairCentralidadedeIntermediacao(vertice));

        }

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < listaVertices.size(); i++) {
            indices.add(i);
        }

        indices.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return intermediacoes.get(i1).compareTo(intermediacoes.get(i2));
            }
        });

        Collections.reverse(indices);

        ArrayList<String> verticesOrdenados = new ArrayList<>();
        ArrayList<Float> intermediacoesOrdenadas = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            verticesOrdenados.add(listaVertices.get(indices.get(i)));
            intermediacoesOrdenadas.add(intermediacoes.get(indices.get(i)));
        }

        ArrayList<String> verticesIntermediacoesOrdenados = new ArrayList<>();

        for (int i = 0; i < verticesOrdenados.size(); i++) {
            verticesIntermediacoesOrdenados.add(verticesOrdenados.get(i) + ": " + intermediacoesOrdenadas.get(i));
        }

        if (invertivel && pesosEstaoInvertidos) {
            ReverterPesos();
        }

        return verticesIntermediacoesOrdenados;
    }

    public ArrayList<String> RetornaVerticesCentralidadeProximidade() {

        if (invertivel && !pesosEstaoInvertidos) {
            InverterPesos();
        }

        ArrayList<String> listaVertices = new ArrayList<>();
        ArrayList<Float> proximidades = new ArrayList<>();

        for (String vertice : vertices.keySet()) {
            listaVertices.add(vertice);
            proximidades.add(ExtrairCentralidadeProximidade(vertice));
        }

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < listaVertices.size(); i++) {
            indices.add(i);
        }

        indices.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return proximidades.get(i1).compareTo(proximidades.get(i2));
            }
        });

        Collections.reverse(indices);

        ArrayList<String> verticesOrdenados = new ArrayList<>();
        ArrayList<Float> proximidadesOrdenadas = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            verticesOrdenados.add(listaVertices.get(indices.get(i)));
            proximidadesOrdenadas.add(proximidades.get(indices.get(i)));
        }

        ArrayList<String> verticesProximidadeOrdenados = new ArrayList<>();

        for (int i = 0; i < verticesOrdenados.size(); i++) {
            verticesProximidadeOrdenados.add(verticesOrdenados.get(i) + ": " + proximidadesOrdenadas.get(i));
        }

        if (invertivel && pesosEstaoInvertidos) {
            ReverterPesos();
        }

        return verticesProximidadeOrdenados;
    }

    public ArrayList<String> RetornaVerticesCentralidadeExcentricidade() {

        ArrayList<String> listaVertices = new ArrayList<>();
        ArrayList<Integer> excentricidades = new ArrayList<>();

        if (!conectado){
            VerificaConectado();
        }

        if (!conectado){
            for (String vertice : vertices.keySet()) {
                listaVertices.add(vertice);
                excentricidades.add(null);
            }
            ArrayList<String> verticesExcentricidadesNull = new ArrayList<>();

            for (int i = 0; i < listaVertices.size(); i++) {
                verticesExcentricidadesNull.add(listaVertices.get(i) + ": " + excentricidades.get(i));
            }

            return verticesExcentricidadesNull;
        }

        if (invertivel && !pesosEstaoInvertidos) {
            InverterPesos();
        }

        for (String vertice : vertices.keySet()) {
            listaVertices.add(vertice);
            excentricidades.add(ExtrairExcentricidade(vertice));
        }

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < listaVertices.size(); i++) {
            indices.add(i);
        }

        indices.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return excentricidades.get(i1).compareTo(excentricidades.get(i2));
            }
        });

        ArrayList<String> verticesOrdenados = new ArrayList<>();
        ArrayList<Integer> excentricidadesOrdenadas = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            verticesOrdenados.add(listaVertices.get(indices.get(i)));
            excentricidadesOrdenadas.add(excentricidades.get(indices.get(i)));
        }

        ArrayList<String> verticesExcentricidadesOrdenados = new ArrayList<>();

        for (int i = 0; i < verticesOrdenados.size(); i++) {
            verticesExcentricidadesOrdenados.add(verticesOrdenados.get(i) + ": " + excentricidadesOrdenadas.get(i));
        }

        if (invertivel && pesosEstaoInvertidos) {
            ReverterPesos();
        }

        return verticesExcentricidadesOrdenados;
    }

    public ArrayList<String> RetornaArestasIntermediacao() {
        if (invertivel && !pesosEstaoInvertidos) {
            InverterPesos();
        }

        ArrayList<String> listaArestas = new ArrayList<>();
        ArrayList<Float> intermediacoes = new ArrayList<>();

        ArrayList<String> nodes = new ArrayList<>();
        nodes.addAll(vertices.keySet());

        for (String vertice : vertices.keySet()) {
            for (String vizinho : PegaVizinhos(vertice)) {
                if (!listaArestas.contains(vizinho + "," + (vertice))) {
                    listaArestas.add(vertice + "," + (vizinho));
                }
            }
        }

        for (String aresta : listaArestas) {
            String[] tokens = aresta.split(",");
            intermediacoes.add(ExtrairIntermediacaoArestas(tokens[0], tokens[1]));
        }

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < listaArestas.size(); i++) {
            indices.add(i);
        }

        indices.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return intermediacoes.get(i1).compareTo(intermediacoes.get(i2));
            }
        });

        Collections.reverse(indices);

        ArrayList<String> arestasOrdenadas = new ArrayList<>();
        ArrayList<Float> intermediacoesOrdenadas = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            arestasOrdenadas.add(listaArestas.get(indices.get(i)));
            intermediacoesOrdenadas.add(intermediacoes.get(indices.get(i)));
        }

        ArrayList<String> arestasIntermediacoesOrdenadas = new ArrayList<>();

        for (int i = 0; i < arestasOrdenadas.size(); i++) {
            arestasIntermediacoesOrdenadas.add(arestasOrdenadas.get(i) + ": " + intermediacoesOrdenadas.get(i));
        }

        if (invertivel && pesosEstaoInvertidos) {
            ReverterPesos();
        }
        return arestasIntermediacoesOrdenadas;
    }

    // Calcula e guarda os menores caminhos conhecidos com dijkstra
    public void CalculaCaminhos() {

        caminhosDijkstra.clear();

        ArrayList<Object> caminhos = new ArrayList<>();
        ArrayList<String> nodes = new ArrayList<>(vertices.keySet());

        for (String vertice : vertices.keySet()) {
            Dijkstra(vertice);
        }
    }

    // Dijkstra que retorna o menor caminho de um vertice para cada outro vertice no grafo [(par, caminho, custo), (par, caminho, custo), ...]
    public ArrayList<Object> Dijkstra(String verticeA) {

        Hashtable<String, String> predecessores = new Hashtable<>();
        Hashtable<String, Float> custos_acumulados = new Hashtable<>();

        for (String vertice : vertices.keySet()) {

            custos_acumulados.put(vertice, Float.POSITIVE_INFINITY);
            predecessores.put(vertice, "null");
        }

        custos_acumulados.replace(verticeA, 0f);

        ArrayList<String> pendentes = new ArrayList<>();

        pendentes.addAll(vertices.keySet());

        while (!pendentes.isEmpty()) {

            String verticeAtual = ExtraiMin(pendentes, custos_acumulados);

            if (verticeAtual.equals("null")) {

                break;
            }

            pendentes.remove(verticeAtual);

            for (String vizinho : PegaVizinhos(verticeAtual)) {

                float novoCusto = custos_acumulados.get(verticeAtual) + RecuperarPeso(verticeAtual, vizinho);

                if (novoCusto < custos_acumulados.get(vizinho)) {
                    custos_acumulados.replace(vizinho, novoCusto);
                    predecessores.replace(vizinho, verticeAtual);
                }
            }
        }

        ArrayList<Object> listaCaminhos = new ArrayList<>();

        for (String verticeB : vertices.keySet()) {

            if (!verticeB.equals(verticeA)) {

                ArrayList<String> caminho = new ArrayList<>();

                float custoTotal = custos_acumulados.get(verticeB);

                String verticeAtual = verticeB;

                while (!verticeAtual.equals("null")) {

                    caminho.add(0, verticeAtual);
                    verticeAtual = predecessores.get(verticeAtual);
                }

                ArrayList<Object> parCaminhoCusto = new ArrayList<>();

                parCaminhoCusto.add(verticeA + "," + verticeB);
                parCaminhoCusto.add(caminho);
                parCaminhoCusto.add(custoTotal);

                //System.out.println(parCaminhoCusto);

                if (caminho.get(0).equals(verticeA) && caminho.get(caminho.size() - 1).equals(verticeB)) {
                    if (!caminhosDijkstra.containsKey(verticeA + "," + verticeB) && !caminhosDijkstra.containsKey(verticeB + "," + verticeA)) {
                        caminhosDijkstra.put(verticeA + "," + verticeB, new Caminho(caminho, custoTotal));
                    }
                }

                listaCaminhos.add(parCaminhoCusto);
            }
        }

        return listaCaminhos;
    }

    public float MediaDistanciasGeodesicas() {

        if (invertivel && !pesosEstaoInvertidos) {
            InverterPesos();
        }

        if (caminhosDijkstra.isEmpty()) {
            CalculaCaminhos();
        }

        float somaExcentricidades = 0;

        ArrayList<String> nodes = new ArrayList<>(vertices.keySet());

        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = i; j < vertices.size(); j++) {
                if (caminhosDijkstra.containsKey(nodes.get(i) + "," + nodes.get(j))) {
                    somaExcentricidades += caminhosDijkstra.get(nodes.get(i) + "," + nodes.get(j)).getCaminho().size() - 1;
                } else if (caminhosDijkstra.containsKey(nodes.get(j) + "," + nodes.get(i))) {
                    somaExcentricidades += caminhosDijkstra.get(nodes.get(j) + "," + nodes.get(i)).getCaminho().size() - 1;
                }
            }
        }
        int n = vertices.size();

        if (invertivel && pesosEstaoInvertidos) {
            ReverterPesos();
        }
        return somaExcentricidades / ((n * (n - 1)) / 2f);
    }

    public ArrayList<Grafo> RetornaGirvanNewman(int subgrafos) {
        if (subgrafos > vertices.size()) {
            System.out.println("numero de subgrafos desejados maior do que numero de vertices");
            return null;
        }

        ArrayList<Grafo> comunidades = new ArrayList<>();
        ArrayList<String> arestasRemovidas = new ArrayList<>();

        while (ExtrairComponentes().size() < subgrafos) {

            if (caminhosDijkstra.isEmpty()) {
                CalculaCaminhos();
            }

            ArrayList<String> arestas = RetornaArestasIntermediacao();

            float maiorBetweenness = 0;
            String maiorAresta = null;

            for (String aresta : arestas) {
                String[] tokens = aresta.split(":");
                if (Float.parseFloat(tokens[1].trim()) >= maiorBetweenness && Float.parseFloat(tokens[1].trim()) != Float.POSITIVE_INFINITY) {
                    maiorBetweenness = Float.parseFloat(tokens[1]);
                    maiorAresta = tokens[0];
                }
            }
            String[] maiorArestaVertices = maiorAresta.split(",");
            arestasRemovidas.add(maiorAresta + "," + RecuperarPeso(maiorArestaVertices[0], maiorArestaVertices[1]));

            // ADICIONAR UMA FUNCAO QUE APENAS REMOVE CAMINHOS QUE UTILIZAVAM A ARESTA E RECALCULA A PARTIR DESSE PONTO.
            RemoverAresta(maiorArestaVertices[0], maiorArestaVertices[1]);

            ArrayList<String> paresAfetados = new ArrayList<>();
            for (String par : caminhosDijkstra.keySet()) {
                ArrayList<String> caminho = new ArrayList<>();
                caminho = caminhosDijkstra.get(par).getCaminho();
                if (!caminho.isEmpty()) {
                    if (caminho.contains(maiorArestaVertices[0]) && caminho.contains(maiorArestaVertices[1])) {
                        if (caminho.indexOf(maiorArestaVertices[0]) == caminho.indexOf(maiorArestaVertices[1]) - 1 || caminho.indexOf(maiorArestaVertices[0]) == caminho.indexOf(maiorArestaVertices[1]) + 1) {
                            paresAfetados.add(par);
                        }
                    }
                }
            }

            for (String par : paresAfetados){
                caminhosDijkstra.remove(par);
            }

            recalculaCaminhos(paresAfetados);
        }

        //criando os grafos correspondentes a cada comunidade/componente encontrada.
        for (ArrayList<String> componente : ExtrairComponentes()) {
            //para cada componente extraido, cria-se um grafo novo com os vertices do componente e adiciona ao retorno
            Grafo gn = new Grafo(representacaoMatriz, direcionado, ponderado);
            // inserindo cada vertice e suas arestas no grafo
            for (String vertice : componente) {
                gn.AdicionarVertice(vertice);
            }
            for (String vertice : componente) {
                for (String vizinho : PegaVizinhos(vertice)) {
                    gn.AdicionarAresta(vertice, vizinho, RecuperarPeso(vertice, vizinho));
                }
            }
            comunidades.add(gn);
        }

        //repondo as arestas que foram removidas
        for (String aresta : arestasRemovidas) {

            String[] verticesPesoArestaRemovida = aresta.split(",");
            AdicionarAresta(verticesPesoArestaRemovida[0], verticesPesoArestaRemovida[1], Float.parseFloat(verticesPesoArestaRemovida[2]));
        }
        return comunidades;
    }

    // recalculando caminhos que foram removidos pelo girvanNewman
    public void recalculaCaminhos(ArrayList<String> paresAfetados) {

        ArrayList<String> verticesAfetados = new ArrayList<>();

        // Separando os vertices distintos
        for (String par : paresAfetados){

            String[] componentesPar = par.split(",");
            if (!verticesAfetados.contains(componentesPar[0])){
                verticesAfetados.add(componentesPar[0]);
            }

            if (!verticesAfetados.contains(componentesPar[1])){
                verticesAfetados.add(componentesPar[1]);
            }
        }

        for (String vertice : verticesAfetados) {
            Dijkstra(vertice);
        }
    }

    // Funcao que muda temporariamente os pesos das arestas, fazendo com que arestas com um maior peso possuam um peso temporario pequeno enquanto uma
    public void InverterPesos() {

        if (invertivel && !pesosEstaoInvertidos) {

            pesosEstaoInvertidos = true;

            ArrayList<String> arestas = new ArrayList<>();
            ArrayList<Float> pesos = new ArrayList<>();

            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;

            for (String vertice : vertices.keySet()) {
                for (String vizinho : PegaVizinhos(vertice)) {
                    if (!arestas.contains(vertice + "," + vizinho) && !arestas.contains(vizinho + "," + vertice)) {
                        arestas.add(vertice + "," + vizinho);
                        Float peso = RecuperarPeso(vertice, vizinho);
                        pesos.add(peso);
                        if (max < peso) {
                            max = peso;
                        }
                        if (min > peso) {
                            min = peso;
                        }
                    }
                }
            }

            float maxEscala = Float.MIN_VALUE;
            float minEscala = Float.MAX_VALUE;

            maxEscala = max / max;
            minEscala = min / max;

            ArrayList<Integer> indices = new ArrayList<>();
            for (int i = 0; i < arestas.size(); i++) {
                indices.add(i);
            }

            indices.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer i1, Integer i2) {
                    return pesos.get(i1).compareTo(pesos.get(i2));
                }
            });

            ArrayList<String> arestasOrdenadas = new ArrayList<>();
            ArrayList<Float> pesosOrdenados = new ArrayList<>();

            for (int i = 0; i < indices.size(); i++) {
                arestasOrdenadas.add(arestas.get(indices.get(i)));
                pesosOrdenados.add(pesos.get(indices.get(i)));

                arestasNaoInvertidas.add(arestas.get(indices.get(i)));
                pesosNaoInvertidos.add(pesos.get(indices.get(i)));
            }

            float alcance = maxEscala - minEscala;
            float novoPeso = ((max - 192f) / alcance) * (maxEscala - minEscala) + min;

            for (int i = 0; i < indices.size(); i++) {
                novoPeso = ((max - pesosOrdenados.get(i)) / alcance) * (maxEscala - minEscala) + min;

                String[] par = arestasOrdenadas.get(i).split(",");

                if (VerificarAresta(par[0], par[1])) {
                    AdicionarAresta(par[0], par[1], novoPeso);
                } else if (VerificarAresta(par[1], par[0])) {
                    AdicionarAresta(par[1], par[0], novoPeso);
                }
            }

            caminhosDijkstra.clear();
            CalculaCaminhos();
        }
    }

    // reverte os pesos do grafo para os pesos guardados
    public void ReverterPesos() {
        if (invertivel && pesosEstaoInvertidos) {
            pesosEstaoInvertidos = false;

            for (int i = 0; i < arestasNaoInvertidas.size(); i++) {

                String[] par = arestasNaoInvertidas.get(i).split(",");

                if (VerificarAresta(par[0], par[1])) {
                    AdicionarAresta(par[0], par[1], pesosNaoInvertidos.get(i));
                } else if (VerificarAresta(par[1], par[0])) {
                    AdicionarAresta(par[1], par[0], pesosNaoInvertidos.get(i));
                }
            }

        }
    }
}
