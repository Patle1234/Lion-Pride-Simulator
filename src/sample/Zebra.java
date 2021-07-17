package sample;

import java.util.ArrayList;

public class Zebra {
    private int x;
    private int y;
    private int age;
    private int position;
    private boolean gender;//true is female and false is male
    private long startTime;
    private int waterHave=24;//35
    private int waterNeed=10;
    private int reproduceTimer=10;
    private boolean ifSick=false;
    private int sickTimer;

    public Zebra(int x,int y,int position){//controller
        this.x = x;
        this.y = y;
        this.position=position;
        startTime = System.nanoTime();
        if(Math.random()>.50){
            gender=true;
        }else{
            gender = false;
        }
    }


    public void changeLoc(int[][] gameGrid,ArrayList<Zebra> zebraArray, ArrayList<Disease> zebraDisease){//The movement for the zebras
        int counter=0;
        int vx;
        int vy;
        int radius=3;
        boolean check = false;
        while(!check){
            if(counter<10) {
                int tempx = x;
                int tempy = y;
                String waterSource=ifNear(gameGrid,4,6);
                String findMate=ifNear(gameGrid,3,8);
                if(waterHave<=waterNeed&&!(waterSource.equals("-1"))) {//zebra move towards the water
                        vx = Integer.parseInt(waterSource.substring(0, waterSource.length() / 2));
                        vy = Integer.parseInt(waterSource.substring(waterSource.length() / 2));
                        drinkWater(gameGrid);
                        if (vx-radius<=x && vx>x) {
                            tempy++;
                        } else {
                            tempy--;
                        }
                        if (vy-radius<=y && vy>y) {
                            tempx++;
                        } else {
                            tempx--;
                        }
                    }else if(!(findMate.equals("-1"))&& checkGender(zebraArray,5)&& age>4 && reproduceTimer>6){//zebra moves towards mate
                    reproduceTimer=4;
                    vx= Integer.parseInt(findMate.substring(0, findMate.length() / 2));
                    vy=Integer.parseInt(findMate.substring(findMate.length() / 2));
                    if (vx-radius<=x && vx>x) {
                        tempy++;
                    } else {
                        tempy--;
                    }
                    if (vy-radius<=y && vy>y) {
                        tempx++;
                    } else {
                        tempx--;
                    }
                }else {//random movement

                    if (Math.random() > .5) {
                        tempx++;
                    }else{
                        tempx--;
                    }
                    if (Math.random() > .5) {
                        tempy++;
                    } else {
                        tempy--;
                    }

                }
                if (tempx >= 0 && tempy >= 0 && tempx <= gameGrid.length - 1 && tempy <= gameGrid[0].length - 1) {
                    if (gameGrid[tempx][tempy] == 0 || gameGrid[tempx][tempy] == 5) {

                        if (ifSick && sickTimer < 4) {//if the zebra is already sick it will infect other spots
                            gameGrid[tempx][tempy] = 8;
                            gameGrid[x][y] = 5;
                            zebraDisease.add(new Disease(x,y,2));
                            sickTimer++;
                            check = true;
                            x = tempx;
                            y = tempy;
                            reproduceTimer++;
                        }


                        if (gameGrid[tempx][tempy] == 5) {//if the zebra stepped on a sick spot for the first time
                            if (sickTimer == 0) {
                                ifSick = true;
                                gameGrid[x][y] = 0;
                                gameGrid[tempx][tempy] = 5;//8;
                                check = true;
                                x = tempx;
                                y = tempy;
                                reproduceTimer++;
                            }
                        }

                        if (gameGrid[tempx][tempy] == 0) {//regular movement
                            gameGrid[x][y] = 0;
                            gameGrid[tempx][tempy] = 2;
                            check = true;
                            x = tempx;
                            y = tempy;
                            reproduceTimer++;
                        }
                    }
                }
                }else{
                break;
            }
            counter++;
        }
    }


    public boolean spreadDisease(){
        return ifSick;
    }

    public boolean ifIsSick(int[][] gameGrid){
        if(sickTimer>3&& ifSick){
            gameGrid[x][y]=5;
            return true;
        }
        return false;
    }


    public String ifNear(int tempGrid[][],int type,int rad){
        int radius=rad;

        for (int r = x-radius;r<=x+radius && r<tempGrid.length && r>0;r++){
            for(int c = y-radius;c<=y+radius && c<tempGrid[r].length && c>0;c++){//if y coordinate is inside of the grid and in radius
                if(tempGrid[r][c]==type && (r!=x && c!=y)){
                    return r+""+c;
                }
            }
        }


        return ""+(-1);
    }
    public void drinkWater(int gameGrid[][]){
        if(!(ifNear(gameGrid,4,1).equals("-1"))){
            waterHave = 20;
        }
    }

    public boolean checkGender(ArrayList<Zebra> zebraArray,int radius){
        for (int i = 0;i<zebraArray.size();i++){
            if(zebraArray.get(i).getX() >=x-radius && zebraArray.get(i).getX()<=x+radius &&
                    zebraArray.get(i).getY() >=y-radius && zebraArray.get(i).getY()<=y+radius &&
                    zebraArray.get(i).getX()!=x && zebraArray.get(i).getY()!=y) {
                if (zebraArray.get(i).getGender() != gender) {
                    return true;
                }
            }
        }

        return false;
    }

    public void useWater(){
        waterHave--;
    }

    public int getX(){
        return this.x;
    }

    public void resetStartTime(){
        startTime = System.nanoTime();
    }

    public long getStartTime(){
        return startTime;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public void addAge(){
        age++;
    }

    public int getPosition(){
        return position;
    }

    public int getAge(){
        return age;
    }

    public boolean noWater(){
        if(waterHave<=0){
            return true;
        }
        return false;
    }

    public boolean getGender(){
        return gender;
    }

    public void setGender(boolean n){
        gender=n;
    }

}
