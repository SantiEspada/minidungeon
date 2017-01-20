public class Box {
    int r = 40, g = r, b = r, group = 0;
    String type = "empty", object = "none", enemy = "none";
    boolean walkable = false, visible = false;

    Box(){

    }

    public String getObject() {
        return object;
    }

    public String getEnemy() {
        return enemy;
    }

    public void setObject(String object) {
        switch (object){
            case "sword":
            case "heart":
            case "eye":
            case "potion":
            case "gold":
            case "apple":
            case "formerObject":
            case "formerEnemy":
                this.object = object;
                break;
            default:
                this.object = "none";
                break;
        }
    }

    public void setEnemy(String enemy){
        switch (enemy) {
            case "yaya":
            case "poo":
            case "redDemon":
            case "purpleDemon":
            case "alien":
                this.object = "enemy";
                this.enemy = enemy;
                break;
            default:
                this.object = "none";
                this.enemy = "none";
                break;
        }
    }

    public void setColor(int r, int g, int b){
        setR(r);
        setG(g);
        setB(b);
    }

    public void setR(int r) {
        if(r < 256 && r > -1)
            this.r = r;
    }

    public void setG(int g) {
        if(g < 256 && g > -1)
            this.g = g;
    }

    public void setB(int b) {
        if(b < 256 && b > -1)
            this.b = b;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setType(String type) {
        this.type = type;

        switch (type){
            case "empty":
                setColor(40, 40, 40);
                setWalkable(false);
                break;
            case "provCorridor":
            case "corridor":
                setColor(255,255,255);
                setWalkable(true);
                break;
            case "room":
                setColor(240, 240, 240);
                setWalkable(true);
                break;
            case "provDoor":
            case "closedDoor":
                setColor(255, 0, 0);
                setWalkable(false);
                break;
            case "openDoor":
                setColor(200, 100, 0);
                setWalkable(true);
                break;
            case "stairsUp":
                setColor(0, 255, 0);
                setWalkable(true);
                break;
            case "stairsDown":
                setColor(0, 255, 0);
                setWalkable(true);
                break;
            default:
                setType("empty");
                break;
        }
    }

    public String getType() {
        return type;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isType(String type){
        return this.type.equals(type);
    }

    public boolean isObject(String object){
        return this.object.equals(object);
    }

    public boolean isEnemy(String Enemy){
        return this.enemy.equals(Enemy);
    }

    public String toString(){
        String string = "Type: " + type;
        string+="\nObject: " + object;
        string+="\nVisible: " + visible;
        string+="\nWalkable: " + walkable;
        string+="\nColor: " + r + " " + g + " " + b;
        return string;
    }
}
