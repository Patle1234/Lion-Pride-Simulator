package sample;

public class Water {//The Water Class is for the water spots on the Map
    private int x;
    private int y;

    public Water(int tempX,int tempY){//Controller
        x = tempX;
        y = tempY;
    }
    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
