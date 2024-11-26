import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVReader {
    public CSVReader() {
    }

    public static Grafo lerCSV(String file) {
        Grafo grafo = new Grafo(false, false, true, true);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            try {
                boolean primeiraLinha = true;

                String line;
                while((line = reader.readLine()) != null) {
                    if (primeiraLinha) {
                        primeiraLinha = false;
                    } else {
                        Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]");
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            String autoresString = matcher.group(1).replaceAll("'", "");
                            String[] autores = autoresString.split(",");

                            for (int i = 0; i < autores.length; i++){
                                autores[i] = autores[i].trim();
                            }

                            String[] var9 = autores;
                            int j = autores.length;

                            for(int var11 = 0; var11 < j; ++var11) {
                                String autor = var9[var11];
                                if (!grafo.ExisteVertice(autor)) {
                                    grafo.AdicionarVertice(autor);
                                }
                            }

                            for(int i = 0; i < autores.length - 1; ++i) {
                                for(j = i + 1; j < autores.length; ++j) {
                                    if (grafo.VerificarAresta(autores[i], autores[j])) {
                                        grafo.AdicionarAresta(autores[i], autores[j], grafo.RecuperarPeso(autores[i], autores[j])+1);
                                    } else {
                                        grafo.AdicionarAresta(autores[i], autores[j], 1.0F);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable var14) {
                try {
                    reader.close();
                } catch (Throwable var13) {
                    var14.addSuppressed(var13);
                }

                throw var14;
            }

            reader.close();
        } catch (IOException var15) {
            IOException e = var15;
            e.printStackTrace();
        }

        return grafo;
    }
}