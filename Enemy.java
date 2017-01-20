import java.util.Random;

public class Enemy {
    private int x, y;
    private String name;
    private String[] attackPhrases;
    private int health;
    private int strength;
    private Random rnd = new Random();

    Enemy(String name, int x, int y){
        switch (name){
            case "yaya":{
                this.name = "Yaya";
                this.strength = 5;
                break;
            }

            case "poo":{
                this.name = "Poo";
                this.strength = 4;
                break;
            }

            case "redDemon":{
                this.name = "Red demon";
                this.strength = 3;
                break;
            }

            case "purpleDemon":{
                this.name = "Purple demon";
                this.strength = 2;
                break;
            }

            case "alien":{
                this.name = "Alien";
                this.strength = 1;
                break;
            }

        }

        setHealth();
        setPos(x, y);
    }

    public void setPos(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public String getAttackPhrase(){
        return attackPhrases[rnd.nextInt(attackPhrases.length)];
    }

    public int getHealth() {
        return health;
    }

    public void setHealth() {
        int max = 2*+strength+1;
        int min = strength+2;

        this.health = rnd.nextInt(max-min+1)+min;
    }

    public void decrHealth(int n){
        this.health -= n;
    }

    public int getStrength() {
        return strength;
    }
}
