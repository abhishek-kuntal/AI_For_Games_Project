package controllers.MyController;

//package competition;

//import core.GameStateInterface;

//Interface to create a ghost controller. Implement this and fill in the AI logic to determine the
//next direction to take
public interface SimMsPacManController {
    public int getAction(SimGameState gs);
}