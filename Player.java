import minidungeon.MiniDungeonGUI;

import java.util.Random;

public class Player {
    private int gold = 0;
	private int health = 20;
	private int maximumHealth = 20;
	private int strength = 1;
	private int perception = 1;
	private int food = 500;
	private int x;
	private int y;
	private MiniDungeonGUI gui;
	private Random rnd;
	private String name;

	Player(MiniDungeonGUI gui, String name, String pic, Random rnd){
        this.gui = gui;
        this.rnd = rnd;
        this.name = name;
        gui.md_setPortraitPlayer(pic);
        gui.md_setTextPlayerName(name);
        gui.md_setTextGold(gold);
        gui.md_setTextHealthCurrent(health);
        gui.md_setTextHealthMax(maximumHealth);
        gui.md_setTextFood(food);
        gui.md_setTextStrength(strength);
        gui.md_setTextPerception(perception);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    public void setPos(int x, int y){
        setX(x);
        setY(y);
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
        gui.md_setTextGold(this.gold);
    }

    public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		if(health > 0){
		    if(health <= maximumHealth){
                this.health = health;
            } else {
                this.health = maximumHealth;
            }
        } else {
		    this.health = 0;
        }

        gui.md_setTextHealthCurrent(this.health);
	}

	public int getMaximumHealth() {
		return maximumHealth;
	}

	public void setMaximumHealth(int maximumHealth) {
		this.maximumHealth = maximumHealth;
		gui.md_setTextHealthMax(this.maximumHealth);
	}

    public int getStrength() {
        return strength;
    }

	public void setStrength(int strength) {
		if(strength<=0) this.strength = 1;
		else this.strength = strength;
        gui.md_setTextStrength(this.strength);
	}

	public int getPerception() {
		return perception;
	}

	public void setPerception(int perception) {
		if (perception<=0) this.perception = 1;
		else this.perception = perception;
        gui.md_setTextPerception(this.perception);
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		if (food <= 0){
		    setStrength(strength/2);
		    setPerception(perception/2);
		    this.food = 0;
		} else {
		    this.food = food;
        }
        gui.md_setTextFood(this.food);
	}

    public void incrMaxHealth(){
        setMaximumHealth(health+5);
    }

    public void incrHealth(){
        setHealth(health+10);
    }

    public void decrHealth(){
        setHealth(health-1);
    }

    public void decrHealth(int n){
        setHealth(health - n);
    }

    public void incrStrength(){
        setStrength(strength+1);
    }

    public void incrGold(){
        setGold(gold+(rnd.nextInt(1000)+1));
    }

    public void decrGold(){
        setGold(gold-1);
    }

    public void incrPerception(){
        setPerception(perception+1);
    }

    public void incrFood(){
        setFood(food+(rnd.nextInt(200)+1));
    }

	public void decrFood(){
        setFood(food-1);
    }
}
