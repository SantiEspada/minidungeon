import java.util.Random;

public class Room {
    int fromX, fromY, toX, toY;
    boolean hasDoor = false;
    Random rnd;

    Room(int fromX, int fromY, int toX, int toY, Random rnd) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX-1;
        this.toY = toY-1;
        this.rnd = rnd;
    }

    public int getFromX() {
        return fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    public boolean hasDoor() {
        return hasDoor;
    }

    public int[] getSidePos(int side){
        int x = fromX, y = fromY;

        switch (side){
            case 1:{
                x = rnd.nextInt(toX - fromX + 1) + fromX;
                y = fromY - 1;
            } break;

            case 2:{
                x = toX + 1;
                y = rnd.nextInt(toY-fromY + 1)+fromY;
            } break;

            case 3:{
                x = rnd.nextInt(toX - fromX + 1) + fromX;
                y = toY + 1;
            } break;

            case 4:{
                x = toX - 1;
                y = rnd.nextInt(toY-fromY + 1)+fromY;
            } break;
        }

        int pos[] = {x, y};
        return pos;
    }

    public void setDoor() {
        this.hasDoor = true;
    }

    public boolean equals(Room room){
        return (this.fromX == room.getFromX() && this.fromY == room.getFromY() && this.toX == room.getToX() && this.toY == room.getToY());
    }

    public boolean belongs(int x, int y){
        return (x >= fromX && x <= toX && y >= fromY && y <= toY);
    }

    public boolean doorOK(int x, int y){
        return (x >= fromX-1 && x <= toX+1 && y >= fromY-1 && y <= toY+1);
    }
}
