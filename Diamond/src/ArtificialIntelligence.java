/**
 * Created by malonesk on 05/12/2016.
 */
public class ArtificialIntelligence {
    private Tree t;
    private Board b;
    public int[] coins;
    public ArtificialIntelligence(Tree t, Board b) {
        this.t=t;
        this.b=b;
        coins = new int[]{0,2,12};
    }
    /* computeFirstRedPlay(Board b)
        est appelée par l'IA hard en mode IAHard vs IAhard(0) / Player vs IAhard(2)
        choisis aléatoirement un coup dans les coins du plateau qui n'a pas été joué par bleu
     */
    public int computeFirstRedPlay(Board b){
        int indexCoup = (int)Math.floor(3*Math.random());
        if (b.board[coins[indexCoup]]==-1) {
            return coins[indexCoup];
        }
        else return computeFirstRedPlay(b);
    }
    /* computeFirstRandomRedPlay(Board b)
        est appelée par l'IA facile en mode Player vs IAFacile(1)
        choisis aléatoirement un coup qui n'a pas été joué par bleu
     */
    public int computeFirtRandomRedPlay(Board b){
        int coup = (int)Math.floor(13*Math.random());
        if (b.board[coup]==-1) {
            return coup;
        }
        else return computeFirstRandomBluePlay(b);
    }
    /* computeFirstRandomBluePlay(Board b)
        est appelée par l'IA facile en mode IA facile vs IA Hard (3)
        choisis aléatoirement un coup entre 0 et 12
     */
    public int computeFirstRandomBluePlay(Board b) {
        return (int)Math.floor(13*Math.random());
    }
    /* computeFirstBluePlay(Board b)
        est appelée par l'IA facile en mode IA facile vs IA Hard (3)
        choisis aléatoirement un coup dans les coins
     */
    public int computeFirtBluePlay(Board b) {
        return coins[(int)Math.floor(3*Math.random())];
    }
    /* serachLastPlayNdode(Node n, byte turn)
        recherche le dernier coup joué en suivant le "chemin" des
        coups joués dans l'arbre grâce à l'attribut coupsJoues de Board
        renvoie le noeud n correspondant au dernier coup joué
     */
    public Node searchLastPlayedNode(Node n, byte turn) {
        if(n.idCell==b.coupsJoues[turn-1] && n.turn==turn) {return n;}
        for (Node fils : n.children) {
            if(fils.idCell==b.coupsJoues[fils.turn-1]) {
                return searchLastPlayedNode(fils,turn);
            }
        }
        return null;
    }
    /* computeBestRedPlay(Node n)
        On calcule le meilleure coup de rouge d'une manière bête et méchante.
        Suivant le dernier coup de bleu, on calcule le coup à jouer qui amènera le plus de victoires de rouge
        selon l'arbre des possibilités (qui est préalablement généré)

        méthode appelée par l'IA hard rouge à chaque tour sauf son dernier qui
        nécessité un traitement particulier
    */
    public int computeBestRedPlay(Node n) {

        /* pour tout les fils de n (tout les prochains coups de rouge)
            on calcule le nombre de victoire rouge dans le sous arbre de racine fils que l'on
            stocke que l'on compare avec un buffer max
            on sauvegarde le nom fils qui amène le plus de victoire de rouge
            on retourne l'idCell de ce noeud fils, c'est le meilleur coup que le rouge peut jouer.

         */
        int max=0;
        Node nMax=null;
        if (n.nbChildren!=0) {
            for (Node fils : n.children) {
                int nbWin=t.computeRedVictories(fils);
                if (max<nbWin) {
                    max=nbWin;
                    nMax=fils;
                }
            }
        } else nMax=n;
        if (nMax==null) return -1;
        return nMax.idCell;
    }
    /* computeBestLastRedPlay(Node n)
        Méthode appelée au dernier tour de l'IA hard rouge
        prend en paramètre le noeuds correpondant au dernier coup joué par bleu (tour 6)
        ce noeud a donc obligatoirement 2 fils, l'un sera le coup de rouge,
        l'autre la cellule vide a partir de laquelle le score est calculé

        Suivant la valeur du resultat des 2 fils, on choisis le meilleur coup
        que peux jouer rouge
     */
    public int computeBestLastRedPlay(Node n) {
        int r1 = n.children[0].result;
        int r2 = n.children[1].result;
        if (r1==r2) {
            return n.children[0].idCell;
        } else {
            if (r1==Node.RED_WINS) return n.children[0].idCell; else if (r2==Node.RED_WINS) return n.children[1].idCell;
            else if (r1==Node.DRAW_PARTY) return n.children[0].idCell; else if (r2==Node.DRAW_PARTY) return n.children[1].idCell;
            else return n.children[1].idCell;
        }
    }
    /* computeBestBluePlay(Node n)
        prends en paramètre le noeud n correspondant au dernier coup de rouge
        Calcule récursivement le meilleur coup jouable en fct du nombre de victoires
        de bleus sur les feuilles du sous arbre de racine n
     */
    public int computeBestBluePlay(Node n) {
        int max=0;
        Node nMax=null;
        for (Node fils : n.children) {
            int nbWin=t.computeBlueVictories(fils);
            if (max<nbWin) {
                max=nbWin;
                nMax=fils;
            }
        }
        if (nMax==null) return -1;
        return nMax.idCell;
    }
    /* computeBestBluePlay(Node n)
        prends en paramètre le noeud n correspondant au dernier coup de rouge
        Calcule récursivement le pire coup jouable en fct de la différence
        entre le nb de victoires de bleu et celles de rouge
        sur les feuilles du sous arbre de racine n
     */
    public int computeBadBluePlayer(Node n) {
        /*Nous voulons ici jouer le noeud en dessous duquel les victoires rouges et bleues sont les plus proches.*/
        /*On doit donc, pour chaque fils de n
        *   calculer le nb de victoire rouge
        *   Calculer le nb de victoires bleues
        *   evaluer leur difference
        *   choisir le noeud ou la difference est minimum
        */
        int diff=t.computeBlueVictories(n)*2; //initialisation a une grande valeur
        int buff;
        Node nDiffMin=null;
        for (Node fils : n.children) {
            int redV=t.computeRedVictories(fils);
            int redB=t.computeBlueVictories(fils);
            buff=redB-redV;
            if (diff>buff) {
                diff=buff; //on veut le minimum de diff, ce qui equivaut a vouloir le max de redV par rapport a blueV
                nDiffMin=fils;
            }
        }
        if (nDiffMin==null) return -1;
        return nDiffMin.idCell;
    }

    public int computeBadRedPlayer(Node n) {
        int min=t.computeRedVictories(n)*2; //initialisation a une grande valeur
        int buff;
        Node nMin=null;
        if (n.nbChildren!=0) {
            for (Node fils : n.children) {
                buff = t.computeRedVictories(fils);
                if (min > buff) {
                    min = buff;
                    nMin = fils;
                }
            }
        } else nMin=n;
        if (nMin==null) return -2;
        return nMin.idCell;
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
