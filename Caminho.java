import java.util.ArrayList;

public class Caminho {
    private ArrayList<String> caminho;
    private Float peso;

    public Caminho(ArrayList<String> caminho, Float peso) {
        this.caminho = caminho;
        this.peso = peso;
    }

    public ArrayList<String> getCaminho() {
        return caminho;
    }

    public void setCaminho(ArrayList<String> caminho) {
        this.caminho = caminho;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }
}
