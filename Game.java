import minidungeon.MiniDungeonGUI;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import java.awt.Color;
import java.awt.Component;
import java.util.Random;

public class Game {
    private MiniDungeonGUI gui;
    private Random rnd;
    private Box[][][] board;
    private int size, level = 0, pointerX = 0, pointerY = 0;
    private Player player;
    private boolean partyMode = false, explore = false, eyeAppeared = false, showPointer = false, firstCommand = true, gameOver = false;
    private boolean[] revealedLevels = new boolean[5];
    private long seed;
    private Color defColor;
    private Enemy[][] enemies = new Enemy[5][20];
    private Room[][] rooms = new Room[5][15];

    Game(int size, MiniDungeonGUI gui, Random rnd, long seed) throws InterruptedException {
        newGame(size, gui, rnd, seed);
    }

    void newGame(int size, MiniDungeonGUI gui, Random rnd, long seed) throws InterruptedException {
        //Hide the GUI until we have all ready
        gui.setVisible(false);

        //Get the default background color
        defColor = gui.getContentPane().getBackground();

        //Set default and given parameters
        this.size = size;
        this.board = new Box[5][size][size];
        this.gui = gui;
        this.rnd = rnd;
        this.seed = seed;
        this.level = 0;
        this.pointerX = 0;
        this.pointerY = 0;
        this.partyMode = false;
        this.explore = false;
        this.eyeAppeared = false;
        this.showPointer = false;
        this.firstCommand = true;
        this.gameOver = false;
        this.enemies = new Enemy[5][20];
        this.rooms = new Room[5][15];
        for (int i = 0; i < 5; i++) {
            this.revealedLevels[i] = false;
        }

        //Hide player labels as we don't have a player yet
        hideLabels();

        //Set the board gray
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                gui.md_setSquareColor(x, y, 64, 64, 64);
                gui.md_setSquareImage(x, y, "nothing.png");
            }
        }

        //Wait until labels are hidden and the board is gray to set the gui visible
        Thread.sleep(500);
        gui.setVisible(true);

        //We'll ask for the name and profile pic of the player
        String name, pic;
        gui.md_println("");
        gui.md_println("Enter your name or press enter for");
        gui.md_println("the default one");
        String lastAction = gui.md_getLastAction();

        while (!(lastAction.split(" ")[0].equals("command"))) {
            lastAction = gui.md_getLastAction();
            Thread.sleep(5);
        }
        lastAction += " ";
        if (lastAction.split("ommand")[1].trim().length() > 0) {
            name = lastAction.split("ommand ")[1].trim();
        } else {
            name = "Mr. X";
        }
        gui.md_clearCommandBar();
        gui.md_clearConsole();

        gui.md_println("Enter the name of your desired profile");
        gui.md_println("picture (it should be in the 'images'");
        gui.md_println("folder) or press enter for the default");
        gui.md_println("one");
        lastAction = gui.md_getLastAction();

        while (!(lastAction.split(" ")[0].equals("command"))) {
            lastAction = gui.md_getLastAction();
            Thread.sleep(5);
        }
        lastAction += " ";
        if (lastAction.split("ommand")[1].trim().length() > 0) {
            pic = lastAction.split("ommand ")[1].trim();
        } else {
            pic = "portrait.jpg";
        }
        gui.md_clearCommandBar();

        //Create the player with the given data and show labels
        this.player = new Player(gui, name, pic, rnd);

        //this.player = new Player(gui, rnd);
        showLabels();

        //Generate map for level 0
        gui.md_clearConsole();
        gui.md_println("Generating level...");
        generateMap(0);

        board[1] = null;
        board[2] = null;
        board[3] = null;
        board[4] = null;

        //Find a suitable box for the player to appear
        int x = rnd.nextInt(size), y = rnd.nextInt(size);
        while (!board[0][x][y].isType("corridor")) {
            x = rnd.nextInt(size);
            y = rnd.nextInt(size);
        }
        player.setPos(x, y);

        //Add player's and pointer sprite and move them to their places
        gui.md_addSprite(0, "player.png", true);
        gui.md_addSprite(1, "pointer.png", true);
        gui.md_moveSprite(0, x, y);
        gui.md_moveSprite(1, 0, 0);

        //Mark the boxes around the player as visible
        setVisible();

        //Clear the console and show the welcome text
        gui.md_clearConsole();
        gui.md_println("Hello, " + this.player.getName() + "!");
        gui.md_println("");
        gui.md_println("• If you need help, type 'help' and");
        gui.md_println(" press enter");
        gui.md_println("");
        gui.md_println("• If you have a seed and you'd prefer to");
        gui.md_println(" start again with it, type 'setSeed='");
        gui.md_println(" followed by your seed and press enter");
        gui.md_println("");
        gui.md_println("Have fun!");

        //Update the board and make the player sprite visible
        updateBoard();
        gui.md_setSpriteVisible(0, true);
    }

    private void hideLabels() {
        //Set all to 0 and player name to Welcome
        gui.md_clearConsole();
        gui.md_clearCommandBar();
        gui.md_clearSprites();
        gui.md_setPortraitPlayer("nothing.png");
        gui.md_setTextPlayerName("Welcome!");
        gui.md_setTextGold(0);
        gui.md_setTextFood(0);
        gui.md_setTextHealthCurrent(0);
        gui.md_setTextHealthMax(0);
        gui.md_setTextFloor(0);
        gui.md_setTextStrength(0);
        gui.md_setTextPerception(0);

        //Find all labels and hide them setting their color to the background color
        //We don't actually hide them -using setVisible(false)- because that would
        //change a bit the layout
        JLayeredPane layeredPane = (JLayeredPane) gui.getContentPane().getComponent(0);
        for (Component c : layeredPane.getComponents()) {
            if (c instanceof JLayeredPane) {
                for (Component sc : ((JLayeredPane) c).getComponents()) {
                    if (sc instanceof JLabel) {
                        switch (((JLabel) sc).getText()) {
                            case "Portrait":
                            case "Gold":
                            case "Perception":
                            case "Strength":
                            case "Health":
                            case "Food":
                            case "Floor":
                            case "0":
                            case "0 / 0":
                            case "1":
                                sc.setForeground(defColor);
                                sc.setBackground(defColor);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void showLabels() {
        //Find all labels and make them visible by setting their color to black
        JLayeredPane layeredPane = (JLayeredPane) gui.getContentPane().getComponent(0);
        for (Component c : layeredPane.getComponents()) {
            if (c instanceof JLayeredPane) {
                for (Component sc : ((JLayeredPane) c).getComponents()) {
                    if (sc instanceof JLabel) {
                        switch (((JLabel) sc).getText()) {
                            case "Portrait":
                            case "Gold":
                            case "Perception":
                            case "Strength":
                            case "Health":
                            case "Food":
                            case "Floor":
                            case "500":
                            case "20 / 20":
                            case "0":
                            case "1":
                                sc.setForeground(Color.black);
                                break;
                        }
                    }
                }
            }
        }
    }

    void updateBoard() {
        //This updates the board in the GUI getting the parameters of every box
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Box box = board[level][x][y];
                int r = box.getR();
                int g = box.getG();
                int b = box.getB();
                if (partyMode && box.getType().equals("empty")) {
                    r = rnd.nextInt(200) + 50;
                    g = rnd.nextInt(200) + 50;
                    b = rnd.nextInt(200) + 50;
                }
                if (box.isVisible() || explore) {
                    gui.md_setSquareColor(x, y, r, g, b);
                    switch (box.getObject()) {
                        case "none":
                            gui.md_setSquareImage(x, y, "nothing.png");
                            break;
                        case "formerObject":
                            //For some reason, when the player gets into a box with an object and the background is removed,
                            //the sprite of the player disappears; that is why we put the player picture as background of the box
                            if (player.getX() == x && player.getY() == y) {
                                gui.md_setSquareImage(x, y, "player.png");
                            } else {
                                gui.md_setSquareImage(x, y, "nothing.png");
                            }
                            break;
                        default:
                            if (box.getObject().equals("enemy")) {
                                gui.md_setSquareImage(x, y, box.getEnemy() + ".png");
                            } else {
                                gui.md_setSquareImage(x, y, box.getObject() + ".png");
                            }
                            break;
                    }
                } else {
                    gui.md_setSquareColor(x, y, 64, 64, 64);
                    gui.md_setSquareImage(x, y, "nothing.png");
                }
                if (partyMode) {
                    r = rnd.nextInt(200) + 50;
                    g = rnd.nextInt(200) + 50;
                    b = rnd.nextInt(200) + 50;
                    gui.getContentPane().setBackground(new Color(r, g, b));
                } else {
                    gui.getContentPane().setBackground(defColor);
                }
            }
        }
    }

    void command(String command) throws InterruptedException {
        //This handles every command we type in the gui

        //If it's the first command, we clear the welcome message
        if (firstCommand) {
            gui.md_clearConsole();
            firstCommand = false;
        }
        if(command.length() > 0) {
            switch (command) {
                case "getSeed": {
                    gui.md_println("Current seed is " + seed);
                    break;
                }

                case "partyMode": {
                    this.partyMode = !partyMode;
                    gui.md_println((partyMode) ? "PARTY MODE IS ON!!!" : "Party mode off :c");
                    break;
                }

                case "explore": {
                    this.explore = !explore;
                    gui.md_println((explore) ? "Map revealed" : "Map hidden");
                    break;
                }

                case "showPointer": {
                    if (showPointer) {
                        gui.md_println("Exiting pointer mode...");
                        explore = false;
                        showPointer = false;
                        gui.md_setSpriteVisible(1, false);
                    } else {
                        gui.md_println("Entering pointer mode...");
                        explore = true;
                        showPointer = true;
                        gui.md_setSpriteVisible(1, true);
                    }

                    break;
                }

                case "clear": {
                    gui.md_clearConsole();
                    break;
                }

                case "new": {
                    seed = System.currentTimeMillis();
                    rnd.setSeed(seed);
                    newGame(size, gui, rnd, seed);
                    break;
                }

                default: {
                    if (command.contains("=")) {
                        String argument = command.split("=")[1];
                        command = command.split("=")[0];

                        switch (command) {
                            case "setSeed": {
                                seed = Long.parseLong(argument);
                                if (seed > 0) {
                                    rnd.setSeed(seed);
                                    newGame(size, gui, rnd, seed);
                                }
                                break;
                            }

                            case "level": {
                                int level = Integer.parseInt(argument);
                                setLevel(level);
                                break;
                            }

                            case "boxInfo": {
                                String[] cords = argument.split("x");
                                int x = Integer.parseInt(cords[0]), y = Integer.parseInt(cords[1]);
                                gui.md_println("Box @ (" + x + ", " + y + "):\n" + board[level][x][y]);
                            }
                            default:
                                gui.md_println("Command not found");
                                break;
                        }
                    } else {
                        gui.md_println("Command not found");
                    }
                    break;
                }
            }
        }
        gui.md_clearCommandBar();
    }

    private void generateMap(int level) throws InterruptedException {
        //This function is CRUCIAL as is it's the one which generates the map
        Box[][] map = board[level];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                map[x][y] = new Box();
            }
        }

        //Rooms generation
        //First of all, rooms are generated. Corridors will be generated later to link those rooms
        generateRooms(level);

        //Corridors generation
        //Now we link all rooms and create some dummy corridors as well
        generateCorridors(level);

        //Now we place objects and enemies
        placeObjects(level);
        placeEnemies(level);
    }
    private void debug(){
        echo("Debugging...");
    }
    private void generateRooms(int level) throws InterruptedException {
        Box[][] map = board[level];

        int total = rnd.nextInt(6)+10; //total numbers of room that will be generated
        int gap = 4; //minimum gap between rooms
        boolean stairsPlaced = false;

        for(int n=0; n<total; n++) {
            boolean roomGenerated = false;

            while(!roomGenerated){
                //Dimensions of the room
                int width = rnd.nextInt(4) + 3;
                int height = rnd.nextInt(4) + 3;

                //Position of the room
                int x = rnd.nextInt(size-width);
                int y = rnd.nextInt(size-height);

                if(level > 0){
                    //If the level is other than 1, it will be placed according to the stair that leads to that level
                    while(!board[level-1][x][y].isType("stairsDown")) {
                        x = rnd.nextInt(size);
                        y = rnd.nextInt(size);
                    }

                    //Position the stairs to the upper level for later
                    map[x][y].setType("stairsUp");

                    //We position the room randomly but with the stairs inside
                    x -= rnd.nextInt(width-1)+1;
                    y -= rnd.nextInt(height-1)+1;
                }

                //To check the boxes next to the room, we take into account the gap between rooms
                int checkX = x - gap;
                int checkY = y - gap;
                int checkWidth = width + gap;
                int checkHeight = height + gap;

                int collisions = 0; //this will be counting the amount of collisions (aka non-empty boxes) found while checking

                for(int xx = checkX; xx < (x+checkWidth); xx++){
                    for(int yy = checkY; yy < (y+checkHeight); yy++){
                        if(inBoard(xx, yy)) {
                            if (!map[xx][yy].isType("empty")) {
                                collisions++;
                            }
                        } else {
                            collisions++;
                        }
                    }
                }

                if(collisions == 0){ //if there's no collisions we can actually create the room
                    for(int xx = x; xx < (x+width); xx++){
                        for(int yy = y; yy < (y+height); yy++){
                            if(inBoard(xx, yy)) {
                                if(xx > x && yy > y && (rnd.nextInt(5)==1 || n == total-1) && !stairsPlaced && n != 0){
                                    map[xx][yy].setType("stairsDown");
                                    stairsPlaced = true;
                                } else {
                                    map[xx][yy].setType("room");
                                }
                                updateBoard();
                            }
                        }
                    }
                    roomGenerated = true;
                    rooms[level][n] = new Room(x, y, x+width, y+height, rnd);
                }
            }
        }

        echo("\n" + total + " rooms generated!!\n");
    }

    private void generateCorridors(int level) throws InterruptedException {
        Box[][] map = board[level];

        boolean done = false;
        int n = 0;

        while(!done) {
            //The basic idea is to generate a corridor starting from other corridor that ends in a room, so everything
            //finally gets connected

            Room endRoom = null;
            for(int i = 0; i < actualRoomsNumber(level); i++){
                if(!rooms[level][i].hasDoor())
                    endRoom = rooms[level][i];
            }

            //Random position and direction
            int x;
            x = rnd.nextInt(size);
            int y = rnd.nextInt(size);
            int direction = newDir();

            if(n == 0) { //If it's the first corridor, we have to start from and empty spot
                while (!map[x][y].isType("empty")) {
                    x = rnd.nextInt(size);
                    y = rnd.nextInt(size);
                }
            } else { //Else we should start from an existing corridor
                while (!map[x][y].isType("corridor")) {
                    x = rnd.nextInt(size);
                    y = rnd.nextInt(size);
                }
            }

            boolean reached = false;
            boolean corridorOK = true;

            int firstTries = 4;
            int secondTries = 0;
            int length = 0;

            //Check if starting position surroundings and its corners are not rooms
            boolean posOK = true;
            int[] pos = nextPos(x, y, direction);
            int provX = pos[0];
            int provY = pos[1];

            if(checkSurroundings(provX, y, 0, "room", level)) posOK = false;
            if(inBoard(provX+1, provY+1)){
                if(map[provX+1][provY+1].isType("room")){
                    posOK = false;
                }
            }
            if(inBoard(provX+1, provY-1)){
                if(map[provX+1][provY-1].isType("room")){
                    posOK = false;
                }
            }
            if(inBoard(provX-1, provY+1)){
                if(map[provX-1][provY+1].isType("room")){
                    posOK = false;
                }
            }
            if(inBoard(provX-1, provY-1)){
                if(map[provX-1][provY-1].isType("room")){
                    posOK = false;
                }
            }

            if(posOK) {
                int lastLength = 0;
                while (!reached && (firstTries > 0) && corridorOK) { //While the corridor hasn't reached its room OR there are enough tries left
                    if(lastLength != length) secondTries++;
                    boolean changeDirection = false;
                    while (!changeDirection && !reached) { //While the direction is not changed, we'll keep on building the corridor that way
                        boolean placeCorridor = false; //This will determine if the box finally becomes a corridor or not
                        pos = nextPos(x, y, direction); //Get the next position
                        provX = pos[0];
                        provY = pos[1]; //Provisional position to do some checks
                        if (inBoard(provX, provY)) { //First of all we have to check if the candidate position belongs to the board
                            if (checkSurroundings(provX, provY, direction, "provCorridor", level) ||checkSurroundings(provX, provY, direction, "corridor", level) || checkSurroundings(provX, provY, direction, "room", level)) {
                                /*If the position is surrounded by corridors/rooms
                                  BUT maybe it's an intersection, which is not bad
                                  What we want to avoid is creating pseudo-rooms (aka two corridors joint together)

                                  Example:
                                       X                X
                                  OK: XXX       Not OK: XX
                                       X                XX

                                  So, to avoid this, we also check if the next position will be also surrounded by corridors
                                */
                                pos = nextPos(x, y, direction);
                                int provXX = pos[0], provYY = pos[1];
                                if (inBoard(provXX, provYY)) {
                                    if (!(checkSurroundings(provX, provY, direction, "provCorridor", level) ||checkSurroundings(provXX, provYY, direction, "corridor", level) || checkSurroundings(provXX, provYY, direction, "room", level))) {
                                        //The next position is not surrounded by corridors/rooms, we can place the corridor there
                                        placeCorridor = true;
                                    } else {
                                        //Pseudo-room will be created, change the direction
                                        changeDirection = true;
                                    }
                                }
                            } else {
                                //All fine, we can place the corridor there
                                placeCorridor = true;
                            }
                            if (placeCorridor) { //If we can place the corridor, we do so
                                x = provX;
                                y = provY;
                                if(!map[x][y].isType("corridor")) {
                                    map[x][y].setType("provCorridor");
                                }
                                lastLength = length;
                                length++;

                                //First we check if the following position will be the door for our desired room
                                pos = nextPos(x, y, direction);
                                pos = nextPos(pos[0], pos[1], direction);

                                provX = pos[0];
                                provY = pos[1];

                                if(endRoom.belongs(provX, provY)){
                                    pos = nextPos(x, y, direction);
                                    map[pos[0]][pos[1]].setType("provDoor");
                                    changeDirection = true;
                                    reached = true;
                                }

                                updateBoard();
                            }
                        } else {
                            changeDirection = true;
                        }

                        if (length > 1 && rnd.nextInt(10) == 1) { //Add some randomness to the generation
                            changeDirection = true;
                        }

                        if(changeDirection) direction = (direction == 4) ? 1 : direction + 1; //Change direction

                        if (changeDirection && length == 0) {
                            firstTries--;
                        }

                        if(secondTries > 20){
                            corridorOK = false;
                        }
                    }
                }

                //Check if the corridor actually reached its objective
                if(corridorOK && reached){ //If so, turn the provisional corridors into actual ones
                    endRoom.setDoor();
                    for(int xx = 0; xx < size; xx++){
                        for(int yy = 0; yy < size; yy++){
                            if(map[xx][yy].isType("provCorridor")){
                                map[xx][yy].setType("corridor");
                            } else if(map[xx][yy].isType("provDoor")){
                                map[xx][yy].setType("closedDoor");
                            }
                        }
                    }
                    System.out.println("Corridor placed!!");
                } else { //If no, revert all
                    for(int xx = 0; xx < size; xx++){
                        for(int yy = 0; yy < size; yy++){
                            if(map[xx][yy].isType("provCorridor") || map[xx][yy].isType("provDoor")){
                                map[xx][yy].setType("empty");
                            }
                        }
                    }
                }

                done = true;
                for (Room roomProv : rooms[level]) {
                    if (roomProv != null) {
                        if(!roomProv.hasDoor()){
                            done = false;
                        }
                    }
                }

                if(firstTries > 0 && corridorOK){
                    n++;
                }
            }
        }

        //At this points all rooms are joint together. Now we'll place some other dummy corridors going nowhere
        int nCorridors = rnd.nextInt(10)+30;
        int i = 0;
        for(i = 0; i < nCorridors; i++){
            int x = 0, y = 0;
            int direction = newDir();

            while(!map[x][y].isType("corridor")){
                x = rnd.nextInt(size);
                y = rnd.nextInt(size);
            }

            boolean endCorridor = false;
            int[] pos;
            int tries = 0;
            int length = 0;
            while(!endCorridor){
                if(inBoard(x, y)) {
                    map[x][y].setType("corridor");
                    length++;
                    updateBoard();
                } else {
                    endCorridor = true;
                }

                pos = nextPos(x, y, direction);
                int provX = pos[0], provY = pos[1];
                if(checkSurroundings(provX, provY, direction, "corridor", level) ||
                        checkSurroundings(provX, provY, direction, "closedDoor", level) ||
                        checkSurroundings(provX, provY, direction, "room", level)){
                    pos = nextPos(x, y, direction);
                    int provXX = pos[0], provYY = pos[1];
                    if(checkSurroundings(provXX, provYY, direction, "corridor", level) ||
                            checkSurroundings(provXX, provYY, direction, "closedDoor", level) ||
                            checkSurroundings(provXX, provYY, direction, "room", level)){
                        direction = (direction == 4) ? 1 : direction + 1;
                        tries++;
                        if(tries > 4){
                            endCorridor = true;
                        }
                    } else {
                        tries = 0;
                        x = provX;
                        y = provY;
                    }
                } else {
                    tries = 0;
                    x = provX;
                    y = provY;
                }

                if(rnd.nextInt(10) == 1){
                    direction = newDir(direction);
                }
            }

            if(length < 10){
                i--;
            }
        }

        echo((i+n) + " total corridors generated!!");

    }

    private int actualRoomsNumber(int level){
        int n = 0;
        for(Room room : rooms[level]){
            if(room != null) n++;
        }

        return n;
    }

    private void echo(String out){
        System.out.println(out);
    }

    private int oppositeDir(int direction){
        int oppositeDir;

        switch (direction){
            case 1:
                oppositeDir = 3;
                break;
            case 2:
                oppositeDir = 4;
                break;
            case 3:
                oppositeDir = 1;
                break;
            case 4:
                oppositeDir = 2;
                break;
            default:
                oppositeDir = 0;
        }

        return oppositeDir;
    }

    private int newDir(){
        return rnd.nextInt(4) + 1;
    }

    private int newDir(int direction){
        int newDir = newDir();
        int oppositeDir = oppositeDir(direction);

        while(newDir == direction || newDir == oppositeDir){
            newDir = newDir();
        }

        return newDir;
    }

    private int[] nextPos(int x, int y, int direction){
        switch (direction){
            case 1:
                y--;
                break;
            case 2:
                x++;
                break;
            case 3:
                y++;
                break;
            case 4:
                x--;
                break;
        }

        int[] pos = {x, y};
        return  pos;
    }

    private boolean checkSurroundings(int x, int y, int direction, String type, int level){
        Box[][] map = board[level];

        int noCheckDir = oppositeDir(direction);

        boolean reached = false;
        for(int i = 1; i < 5; i++){
            if(i != noCheckDir){
                Box checkBox = null;

                switch (i){
                    case 1:{
                        checkBox = inBoard(x, y-1) ? map[x][y-1] : null;
                    } break;

                    case 2:{
                        checkBox = inBoard(x+1, y) ? map[x+1][y] : null;
                    } break;

                    case 3:{
                        checkBox = inBoard(x, y+1) ? map[x][y+1] : null;
                    } break;

                    case 4:{
                        checkBox = inBoard(x-1, y) ? map[x-1][y] : null;
                    } break;
                }

                if(checkBox != null){
                    if(checkBox.isType(type)){
                        reached = true;
                    }
                }
            }
        }

        return reached;
    }

    private void placeObjects(int level){
        //Sword
        addObject("sword", 1, level);

        //Heart
        addObject("heart", 1, level);

        //Eye
        if (((rnd.nextInt(3) == 1) && !eyeAppeared) || (level == 5 && !eyeAppeared)) {
            addObject("eye", 1, level);
        }

        //Potions
        int n = rnd.nextInt(3) + 2;
        addObject("potion", n, level);

        //Gold
        n = rnd.nextInt(16) + 15;
        addObject("gold", n, level);

        //Apples
        n = rnd.nextInt(6) + 5;
        addObject("apple", n, level);
    }

    private void placeEnemies(int level){
        int enemyN = 0;
        int n;

        //Yaya (aka Level Boss)
        addEnemy("yaya", enemyN, 1, level);
        enemyN++;

        //Poo
        n = 2;
        addEnemy("poo", enemyN, n, level);
        enemyN+=n;

        //Red demon
        n = 5;
        addEnemy("redDemon", enemyN, n, level);
        enemyN+=n;

        //Purple demon
        n = 5;
        addEnemy("purpleDemon", enemyN, n, level);
        enemyN+=n;

        //Alien
        n = 6;
        addEnemy("alien", enemyN, n, level);
    }

    private int[] findSuitableObjectPlace(int level) {
        Box[][] map = board[level];

        //Finds a suitable place for an object
        int x = rnd.nextInt(size), y = rnd.nextInt(size);
        Box box = map[x][y];
        Box boxLeft = (x - 1 > -1) ? map[x - 1][y] : box;
        Box boxRight = (x + 1 < size) ? map[x + 1][y] : box;
        Box boxTop = (y - 1 > -1) ? map[y - 1][x] : box;
        Box boxBottom = (y + 1 < size) ? map[y + 1][x] : box;

        //Checks if the box is a corridor or a room and if the surroundings have or not other objects
        while (!((box.getType().equals("corridor") || box.getType().equals("room")) && (box.getObject().equals("none")) && (boxLeft.getObject().equals("none")) && (boxRight.getObject().equals("none")) && (boxTop.getObject().equals("none")) && (boxBottom.getObject().equals("none")))) {
            x = rnd.nextInt(size);
            y = rnd.nextInt(size);
            box = map[x][y];
            boxLeft = (x - 1 > -1) ? map[x - 1][y] : box;
            boxRight = (x + 1 < size) ? map[x + 1][y] : box;
            boxTop = (y - 1 > -1) ? map[y - 1][x] : box;
            boxBottom = (y + 1 < size) ? map[y + 1][x] : box;
        }

        int[] result = {x, y};
        return result;
    }


    private void addObject(String object, int n, int level) {
        Box[][] map = board[level];

        switch (object) {
            case "sword":
            case "heart":
            case "eye":
            case "potion":
            case "gold":
            case "apple":
                for (int i = 0; i < n; i++) {
                    int[] place = findSuitableObjectPlace(level);
                    map[place[0]][place[1]].setObject(object);
                } break;
        }
    }

    private void addEnemy(String enemy, int enemyN, int n, int level) {
        Box[][] map = board[level];

        switch (enemy) {
            case "yaya": { //Yaya must appear in a room, that is why this is a little different
                int x = rnd.nextInt(size), y = rnd.nextInt(size);
                while (!(map[x][y].getType().equals("room") && map[x][y].getObject().equals("none"))) {
                    x = rnd.nextInt(size);
                    y = rnd.nextInt(size);
                }
                map[x][y].setEnemy(enemy);
                enemies[level][0] = new Enemy(enemy, x, y);
            }
            break;

            case "poo":
            case "redDemon":
            case "purpleDemon":
            case "alien": {
                for (int i = enemyN; i < (enemyN + n); i++) {
                    int[] place = findSuitableObjectPlace(level);
                    map[place[0]][place[1]].setEnemy(enemy);
                    enemies[level][i] = new Enemy(enemy, place[0], place[1]);
                }
            }
            break;
        }
    }

    private void setVisible() {
        int x = player.getX();
        int y = player.getY();
        board[level][x][y].setVisible(true);
        if (inBoard(x + 1, y)) board[level][x + 1][y].setVisible(true);
        if (inBoard(x - 1, y)) board[level][x - 1][y].setVisible(true);
        if (inBoard(x, y + 1)) board[level][x][y + 1].setVisible(true);
        if (inBoard(x, y - 1)) board[level][x][y - 1].setVisible(true);
        if (player.getPerception() == 2) {
            if (inBoard(x + 2, y)) board[level][x + 2][y].setVisible(true);
            if (inBoard(x - 2, y)) board[level][x - 2][y].setVisible(true);
            if (inBoard(x, y + 2)) board[level][x][y + 2].setVisible(true);
            if (inBoard(x, y - 2)) board[level][x][y - 2].setVisible(true);
            if (inBoard(x + 1, y + 1)) board[level][x + 1][y + 1].setVisible(true);
            if (inBoard(x + 1, y - 1)) board[level][x + 1][y - 1].setVisible(true);
            if (inBoard(x - 1, y + 1)) board[level][x - 1][y + 1].setVisible(true);
            if (inBoard(x - 1, y - 1)) board[level][x - 1][y - 1].setVisible(true);
        }
    }

    private boolean inBoard(int x, int y) {
        return (x < size && y < size && x > -1 && y > -1);
    }


    void movePlayer(String direction) throws InterruptedException {
        int x = (showPointer) ? pointerX : player.getX();
        int y = (showPointer) ? pointerY : player.getY();

        switch (direction) {
            case "up": {
                y--;
            }
            break;
            case "right": {
                x++;
            }
            break;
            case "down": {
                y++;
            }
            break;
            case "left": {
                x--;
            }
            break;
        }

        if (x > -1 && x < size && y > -1 && y < size) {
            if (!showPointer) {
                if(board[level][x][y].isType("closedDoor")){
                    board[level][x][y].setType("openDoor");
                    gui.md_println("Door opened");
                } else if(board[level][x][y].isType("stairsDown")){
                    setLevel(level+1);
                } else if(board[level][x][y].isType("stairsUp")){
                    setLevel(level+1);
                } else if ((board[level][x][y].isWalkable())) {
                    if (checkEnemies(x, y, true)) {
                        player.setPos(x, y);
                        player.decrFood();
                        checkObjects();
                        setVisible();
                        gui.md_moveSprite(0, x, y);
                    }
                }
            } else {
                pointerX = x;
                pointerY = y;
                gui.md_moveSprite(1, x, y);
                gui.md_clearConsole();
                gui.md_println("(" + x + ", " + y + ")");
                gui.md_println(board[level][pointerX][pointerY].toString());
            }
        }
    }

    public void checkObjects() {
        //Check if the box contains an object and therefore if any further action is needed
        Box box = board[level][player.getX()][player.getY()];

        switch (box.getObject()) {
            case "sword": {
                player.incrStrength();
                gui.md_println(player.getName() + " has more strength!!!");
                break;
            }

            case "heart": {
                player.incrMaxHealth();
                gui.md_println(player.getName() + " can have more health!!!");
                break;
            }

            case "eye": {
                player.incrPerception();
                gui.md_println(player.getName() + " has more perception!!!");
                break;
            }

            case "potion": {
                int init = player.getHealth();
                player.incrHealth();
                gui.md_println((init == player.getHealth()) ? player.getName() + " has enough health!!!" : (player.getHealth() - init) + " points healed");
                break;
            }

            case "gold": {
                int init = player.getGold();
                player.incrGold();
                gui.md_println((player.getGold() - init) + " gold found");
                break;
            }

            case "apple": {
                int init = player.getFood();
                player.incrFood();
                gui.md_println((player.getFood() - init) + " food points recovered");
                break;
            }
        }

        if (box.getObject().equals("formerObject")) {
            box.setObject("none");
        } else {
            box.setObject("formerObject");
        }
    }

    public boolean checkEnemies(int x, int y, boolean playerAttacking) {
        Box box = board[level][x][y];

        boolean check = false;
        if(box.getObject().equals("enemy")){
            Enemy enemy = null;
            for(Enemy enemyProv : enemies[level]){
                if(enemyProv != null) {
                    if (enemyProv.getX() == x && enemyProv.getY() == y) {
                        enemy = enemyProv;
                    }
                }
            }
            if (playerAttacking) {
                enemy.decrHealth(player.getStrength());
                if (enemy.getHealth() < 0) {
                    gui.md_println(enemy.getName() + " died!");
                    box.setObject("formerEnemy");
                    check = true;
                } else {
                    gui.md_println(enemy.getName() + " lost " + player.getStrength() + " health! " + enemy.getHealth() + "left");
                }
            } else {
                player.decrHealth(enemy.getStrength());
                if (player.getHealth() < 0) {
                    gui.md_println(player.getName() + " died! Game over");
                    gameOver = true;
                    check = true;
                } else {
                    gui.md_println(player.getName() + " lost " + player.getStrength() + " health! ");
                }
            }
        } else {
            check = true;
        }
        return check;
    }

    public void setLevel(int level) throws InterruptedException {
        if(board[level] == null){
            gui.md_println("Generating level...");
            generateMap(level);
        }

        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                if(board[level][x][y].isType("stairsUp")){
                    gui.md_moveSprite(0, x, y);
                }
            }
        }
        this.level = level;
    }
}