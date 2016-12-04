#include <stdio.h>
#include "diamond.h"

int main(int argc, char** argv){

  board_t* b = createBoard();
  printf("board initialisée\n");
  tree_t* t = createTree();
    printf("tree initialisé\n");
  int idCellBlue = 1;
  int idCellRed = 0;  
  setFirstBlueChoice(t,b,idCellBlue);
    printf("BlueChoice initialisé\n");
  setFirstRedChoice(t,b,idCellRed);
    printf("RedChoice initialisé\n");
  buildTree(t,b);
    printf("tree construit\n");
  int nbBlueVictories = computeBlueVictories(t->root);
  int nbRedVictories = computeRedVictories(t->root);
  int nbDraws = computeDraws(t->root);
  printf("nb configuration: %d, nb blue victories: %d, nb red victories: %d, nb draws: %d\n",nbConfigurations,nbBlueVictories, nbRedVictories, nbDraws);
    freeBoard(b);
    printf("board libérée, délivrée\n");
    freeTree(t);
    printf("tree libéré\n");
  return 0;
}
