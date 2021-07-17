package sample;

public class Disease {
    private int x;
    private int y;
    private int diseaseType;

    public Disease(int tempX, int tempY,int type){//1 is lion diesease, 2 is zebra diesease, and 3 is hunter disease
        diseaseType=type;
        x = tempX;
        y = tempY;
    }
    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getDiseaseType(){
        return diseaseType;
    }
}
