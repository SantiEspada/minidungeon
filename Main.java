import java.util.Random;
import minidungeon.MiniDungeonGUI;

public class Main {
    public static void main(String[] args) throws InterruptedException{
        final int size = 50;
        final Random rnd = new Random();
        long seed = -1;

        for(String arg:args){
            if(arg.indexOf("seed") >= 0){
                seed = Long.valueOf(arg.split("eed=")[1]).longValue();
                rnd.setSeed(seed);
            }
        }
        //For testing
        seed = 1482097216324L;
        rnd.setSeed(seed);

        if(seed == -1){
            seed = System.currentTimeMillis();
            rnd.setSeed(seed);
        }

        final MiniDungeonGUI gui = new MiniDungeonGUI(size, size);
        gui.setTitle("MiniDungeon Chachi :D");

        final Game game = new Game(size, gui, rnd, seed);

        while(true){
            game.updateBoard();
            String lastAction = gui.md_getLastAction()+" ";
            if(lastAction.contains("command")){
                String command = lastAction.split("ommand")[1].trim();
                game.command(command);
			} else if(lastAction.equals("new game")){
                game.newGame(size, gui, rnd, System.currentTimeMillis());
            } else {
                lastAction = lastAction.trim();
                if(lastAction.length() > 1) {
                    game.movePlayer(lastAction);
                }
            }
            Thread.sleep(10);
        }
    }
}
