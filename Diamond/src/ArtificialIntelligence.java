/**
 * Created by malonesk on 05/12/2016.
 */
public class ArtificialIntelligence {
    private Tree t;
    private Board b;
    public ArtificialIntelligence(Tree t, Board b) {
        this.t=t;
        this.b=b;
    }
    public Node searchLastBluePlayNode(Node n, byte turn) {
        if(n.idCell==b.coupsJoues[turn-1] && n.turn==turn) {return n;}
        for (Node fils : n.children) {
            if(fils.idCell==b.coupsJoues[fils.turn-1]) {
                return searchLastBluePlayNode(fils,turn);
            }
        }
        return null;
    }
    public int computeBestPlay(Node n) {
        /*On calcule le meilleure coup de rouge d'une manière bête et méchante.
        Suivant le dernier coup de bleu, on calcule le coup à jouer qui amènera le plus de victoires de rouge
        selon l'arbre des possibilités (qui est préalablement généré)
         */
        /* pour tout les fils de n (tout les prochains coups de rouge)
            on calcule le nombre de victoire rouge dans le sous arbre de racine fils que l'on
            stocke que l'on compare avec un buffer max
            on sauvegarde le nom fils qui amène le plus de victoire de rouge
            on retourne l'idCell de ce noeud fils, c'est le meilleur coup que le rouge peut jouer.

         */
        int max=0;
        Node nMax=null;
        for (Node fils : n.children) {
            int nbWin=t.computeRedVictories(fils);
            if (max<nbWin) {
                max=nbWin;
                nMax=fils;
            }
        }
        if (nMax==null) return -1;
        return nMax.idCell;
    }





    public Tree getT() {
        return t;
    }

    public void setT(Tree t) {
        this.t = t;
    }

    public Board getB() {
        return b;
    }

    public void setB(Board b) {
        this.b = b;
    }
}
