package controllers.MyController;

//package competition;

//import core.GameStateInterface;

//Interface to create a ghost controller. Implement this and fill in the AI logic to determine the
//next directions to take
public interface SimGhostsController {
    int[] getActions(SimGameState gs);
}