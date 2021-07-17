package sample;

import java.util.ArrayList;

public class Hunter {
    private int x;
    private int y;
    private int age;
    private int position;
    private boolean gender;//true is female and false is male
    private long startTime;
    private int waterHave=24;//35
    private int waterNeed=10;
    int reproduceTimer=10;
    private boolean ifSick=false;
    private int sickTimer;

    //controller
    public Hunter(int x,int y,int position){
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

    //movement code for hunters
    public void changeLoc(int[][] gameGrid,ArrayList<Hunter> hunterArray, ArrayList<Disease> hunterDisease){
        int counter=0;
        boolean check = false;
        int vx;
        int vy;
        int radius=3;
        //String foundPrey="-1";
        String huntZebra=ifNear(gameGrid,2,7);
        String huntLion=ifNear(gameGrid,1,7);
        String findMate=ifNear(gameGrid,3,5);
        String waterSource=ifNear(gameGrid,4,6);
        int tempx = x;
        int tempy = y;
        while(!check){
            if(counter<10) {
                //movement based on mate
                if(!(findMate.equals("-1"))&& checkGender(hunterArray,5) && reproduceTimer>8&& age>4){
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
                    //movement based on hunting a zebra
                }else if(!(huntZebra.equals("-1"))){
                    vx= Integer.parseInt(huntZebra.substring(0, huntZebra.length() / 2));
                    vy=Integer.parseInt(huntZebra.substring(huntZebra.length() / 2));
                   //huntPrey(gameGrid,2);
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
                    //movement bases on hunting lion
                }else if(!(huntLion.equals("-1"))){//if there is a lion in a x block radius, it will track it down
                    vx= Integer.parseInt(huntLion.substring(0, huntLion.length() / 2));
                    vy=Integer.parseInt(huntLion.substring(huntLion.length() / 2));
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
                    //movement based on water
                }else if(waterHave<=waterNeed&&!(waterSource.equals("-1"))) {
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
                    //random movement
                } else {
                    if (Math.random() > .5) {
                        tempx++;
                    } else {
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
                        //if already sick
                        if (ifSick && sickTimer < 4) {
                            gameGrid[x][y] = 5;
                            gameGrid[tempx][tempy] = 9;
                            hunterDisease.add(new Disease(x,y,3));
                            sickTimer++;
                            check = true;
                            x = tempx;
                            y = tempy;
                            reproduceTimer++;
                        }

                        //hunter gets sick
                        if (gameGrid[tempx][tempy] == 5) {
                            if (sickTimer == 0) {
                                ifSick = true;
                                gameGrid[x][y] = 0;
                                gameGrid[tempx][tempy] = 9;
                                check = true;
                                x = tempx;
                                y = tempy;
                                reproduceTimer++;
                            }
                        }

                        if (gameGrid[tempx][tempy] == 0) {
                            gameGrid[x][y] = 0;
                            gameGrid[tempx][tempy] = 3;
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

    public boolean ifIsSick(){
        if(sickTimer>3&& ifSick){
            return true;
        }
        return false;
    }

    public String ifNear(int tempGrid[][],int type,int rad){
        int radius=rad;

        for (int r = x-radius;r<=x+radius && r<tempGrid.length && r>0;r++){
            for(int c = y-radius;c<=y+radius && c<tempGrid[r].length && c>0;c++){
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

    public boolean checkGender(ArrayList<Hunter> hunterArray,int radius){

        for (int i = 0;i<hunterArray.size();i++){
            if(hunterArray.get(i).getX() >=x-radius && hunterArray.get(i).getX()<=x+radius &&
                    hunterArray.get(i).getY() >=y-radius && hunterArray.get(i).getY()<=y+radius &&
                    hunterArray.get(i).getX()!=x && hunterArray.get(i).getY()!=y) {
                if (hunterArray.get(i).getGender()!=gender) {

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

}