package sample;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;


public class Controller {
    int x = 25;//20
    int y = 36;//36
    Button[][] btn = new Button[x][y];
    int[][] gameGrid = new int[x][y];//0 is open, 1 is lion, 2 is zebra, 3 is hunter, 4 is water,5 is disease, 6 is spawn spots spots, 7 is sick lion, 8 is sick zebra, 9 is sick hunter

    //arrays
    ArrayList<Disease> lionDiseaseSpots= new ArrayList<>();
    ArrayList<Disease> zebraDiseaseSpots= new ArrayList<>();
    ArrayList<Disease> hunterDiseaseSpots= new ArrayList<>();

    ArrayList<Lion> lions = new ArrayList<>();
    ArrayList<Lion> dyingLions = new ArrayList<>();

    ArrayList<Zebra> zebras = new ArrayList<>();
    ArrayList<Zebra> dyingZebras = new ArrayList<>();

    ArrayList<Hunter> hunters = new ArrayList<>();
    ArrayList<Hunter> dyingHunters = new ArrayList<>();

    private ArrayList<String> lionPossibleLoc = new ArrayList<>();
    private ArrayList<String> zebraPossibleLoc = new ArrayList<>();
    private ArrayList<String> hunterPossibleLoc = new ArrayList<>();

    ArrayList<Integer> lionPopulation = new ArrayList<>();
    ArrayList<Integer> zebraPopulation = new ArrayList<>();
    ArrayList<Integer> hunterPopulation = new ArrayList<>();

    ArrayList<Water> water = new ArrayList<>();

    //instance fields
    int oldestLionAge=0;
    int oldestZebraAge=0;
    int oldestHunterAge=0;
    int droughtCounter=0;
    int weatherCounter=0;
    int lionProb=86;
    int hunterProb=89;
    int zebraProb=84;
    boolean ifHarshWeather=false;
    boolean ifDrought=false;
    String interact;
    long graphTime=System.nanoTime();

    @FXML
    private AnchorPane aPane;

    @FXML
    private GridPane gPane;

    @FXML
    private LineChart lChart, zChart, hChart;

    @FXML
    private ListView lionNews, zebraNews, hunterNews ;

    @FXML
    Button startButton;

    @FXML
    TextField lionField, zebraField,hunterField;

    @FXML
    ChoiceBox diseaseChoice;

    @FXML
    //start function
    private void handleStart(ActionEvent event) {
        int xCoord;
        int yCoord;
        //creates and sets up buttons/spaces on grid
        for (int i = 0; i < btn.length; i++) {
            for (int j = 0; j < btn[0].length; j++) {
                btn[i][j] = new Button();
                //must set minimum size of buttons first
                btn[i][j].setMinSize(0, 0);
                //then change the width and height of each button.  The cell will automatically resize to match
                btn[i][j].setPrefWidth(19);
                btn[i][j].setPrefHeight(19);
                gPane.add(btn[i][j], j, i);
                btn[i][j].setStyle("-fx-background-color:#e6ac00");
                gameGrid[i][j] = 0;
            }
        }

        //creates Lake
        for(int j=0;j<5;j++) {
            xCoord = j +15;
            for (int z = 0; z < 5; z++) {
                yCoord = z+6;
                gameGrid[xCoord][yCoord] = 4;
                water.add(new Water(xCoord, yCoord));
                btn[xCoord][yCoord].setStyle("-fx-background-color:#009FFF");
            }
        }

        //creates pond 1
        for(int j=0;j<3;j++) {
            xCoord = j + 3;
            for (int z = 0; z < 3; z++) {
                yCoord = z + 17;
                gameGrid[xCoord][yCoord] = 4;
                water.add(new Water(xCoord, yCoord));
                btn[xCoord][yCoord].setStyle("-fx-background-color:#009FFF");
            }
        }

        //creates pond 2
        for(int j=0;j<3;j++) {
            xCoord = j + 10;
            for (int z = 0; z < 3; z++) {
                yCoord = z + 28;
                gameGrid[xCoord][yCoord] = 4;
                water.add(new Water(xCoord, yCoord));
                btn[xCoord][yCoord].setStyle("-fx-background-color:#009FFF");
            }
        }



        gPane.setGridLinesVisible(true);
        gPane.setVisible(true);

        //when you click on the button
        EventHandler z = new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t){
            }
        };

        for(int i=0; i<btn.length; i++){
            for(int j=0; j<btn[0].length;j++){
                btn[i][j].setOnMouseClicked(z);

            }
        }
        startButton.setVisible(false);

        diseaseChoice.getItems().add("Lion");
        diseaseChoice.getItems().add("Zebra");
        diseaseChoice.getItems().add("Hunter");

        start();

    }

    //Updates what is on each square
    public void updateScreen(){
        for(int i=0; i<btn.length; i++) {
            for (int j = 0; j < btn[0].length; j++) {
                //during drought
                if (gameGrid[i][j] == 4) {
                    btn[i][j].setStyle("-fx-background-color:#009FFF");
                    if (ifDrought) {
                        gameGrid[i][j] = 0;
                    }
                }

                if (gameGrid[i][j] == 0) {
                        btn[i][j].setStyle("-fx-background-color:#e6ac00");//regular ground
                        if (ifHarshWeather) {//during harsh weather
                            btn[i][j].setStyle("-fx-background-color:#916d00");//desert wet
                        }
                } else if (gameGrid[i][j] == 1) {//lions
                    for (Lion a : lions) {
                        if (i == a.getX() && j == a.getY()) {
                            if (a.getAge() < 4 && a.getGender()) {
                                btn[i][j].setStyle("-fx-background-color:#ff0000");
                            } else if (a.getAge() >= 4 && a.getAge() < 12 && a.getGender()) {
                                btn[i][j].setStyle("-fx-background-color:#b30000");
                            } else if (a.getAge() >= 12 && a.getGender()) {
                                btn[i][j].setStyle("-fx-background-color: #4d0000");
                            } else if (a.getAge() < 4 && !(a.getGender())) {
                                btn[i][j].setStyle("-fx-background-color:#0000ff");
                            } else if (a.getAge() >= 4 && a.getAge() < 12 && !(a.getGender())) {
                                btn[i][j].setStyle("-fx-background-color:#0000b3");
                            } else if (a.getAge() >= 12 && !(a.getGender())) {
                                btn[i][j].setStyle("-fx-background-color:#00004d");
                            }
                        }
                    }
                } else if (gameGrid[i][j] == 2) {//zebras
                    for (Zebra z : zebras) {
                        if (i == z.getX() && j == z.getY()) {
                            if (z.getAge() < 4 && z.getGender()) {
                                btn[i][j].setStyle("-fx-background-color:#face6e");
                            } else if (z.getAge() >= 4 && z.getAge() < 12 && z.getGender()) {
                                btn[i][j].setStyle("-fx-background-color:#d16c00");
                            } else if (z.getAge() >= 12 && z.getGender()) {
                                btn[i][j].setStyle("-fx-background-color:  #d15a00");
                            } else if (z.getAge() < 4 && !(z.getGender())) {
                                btn[i][j].setStyle("-fx-background-color: #b4ff85");
                            } else if (z.getAge() >= 4 && z.getAge() < 12 && !(z.getGender())) {
                                btn[i][j].setStyle("-fx-background-color:#9ce66e");
                            } else if (z.getAge() >= 12 && !(z.getGender())) {
                                btn[i][j].setStyle("-fx-background-color: #75ba4a");
                            }
                        }
                    }
                } else if (gameGrid[i][j] == 3) {//hunters
                    for (Hunter h : hunters) {
                        if (i == h.getX() && j == h.getY()) {
                            if (h.getAge() < 4 && h.getGender()) {
                                btn[i][j].setStyle("-fx-background-color: #e823d4");
                            } else if (h.getAge() >= 4 && h.getAge() < 12 && h.getGender()) {
                                btn[i][j].setStyle("-fx-background-color: #ad119e");
                            } else if (h.getAge() >= 12 && h.getGender()) {
                                btn[i][j].setStyle("-fx-background-color:  #5e0856");
                            } else if (h.getAge() < 4 && !(h.getGender())) {
                                btn[i][j].setStyle("-fx-background-color: #874000");
                            } else if (h.getAge() >= 4 && h.getAge() < 12 && !(h.getGender())) {
                                btn[i][j].setStyle("-fx-background-color:#613e1e");
                            } else if (h.getAge() >= 12 && !(h.getGender())) {
                                btn[i][j].setStyle("-fx-background-color: #3b1c00");
                            }
                        }
                    }
                }else if (gameGrid[i][j] == 5) {//if disease spot
                    boolean ifFind=false;
                    //check the type of disease it is. Either Lion, zebra, or hunter disease
                    for(Disease d: lionDiseaseSpots) {
                        if(i==d.getX() && j==d.getY()) {
                            ifFind=true;
                            btn[d.getX()][d.getY()].setStyle("-fx-background-color:#a4f542");
                        }
                     }
                    if(!ifFind){
                        for(Disease d: zebraDiseaseSpots) {
                            if(i==d.getX() && j==d.getY()) {
                                ifFind=true;
                                btn[d.getX()][d.getY()].setStyle("-fx-background-color:  #42f566");
                            }
                        }
                    }
                    if(!ifFind){
                        for(Disease d: hunterDiseaseSpots) {
                            if(i==d.getX() && j==d.getY()) {
                                btn[d.getX()][d.getY()].setStyle("-fx-background-color: #42f5b6");
                            }
                        }
                    }

                }

                else if (gameGrid[i][j] == 7) {//if sick lion
                    for (Lion l : lions) {

                        if (l.spreadDisease()) {
                            btn[l.getX()][l.getY()].setStyle("-fx-background-color:#2d610b");
                        }
                    }
                }else if(gameGrid[i][j]==8){//if sick zebra
                    for (Zebra z : zebras) {
                        if (z.spreadDisease()) {
                            btn[z.getX()][z.getY()].setStyle("-fx-background-color:  #2d610b");
                        }
                    }
                }else if(gameGrid[i][j]==9){//if sick hunter
                    for (Hunter h : hunters) {
                        if (h.spreadDisease()) {
                            btn[h.getX()][h.getY()].setStyle("-fx-background-color: #2d610b");
                        }
                    }
                }
            }
        }
    }



    @FXML
    public void handleAddLion(){//creates a lion
        for(int i=0; i<Integer.parseInt(lionField.getText());i++) {
            birth(17, 21, "lion");
            gameGrid[lions.get(lions.size() - 1).getX()][lions.get(lions.size() - 1).getY()] = 1;
        }
    }

    @FXML
    public void handleAddZebra(){//creates a zebra
        for(int i=0;i<Integer.parseInt(zebraField.getText());i++) {
            birth(9, 13, "zebra");//4 5
            zebras.get(0).setGender(true);
            gameGrid[zebras.get(zebras.size() - 1).getX()][zebras.get(zebras.size() - 1).getY()] = 2;
        }
    }

    @FXML
    public void handleAddHunter(){//creates a hunter
        for(int i=0;i<Integer.parseInt(hunterField.getText());i++) {
            birth(9, 21, "hunter");//10 25
            gameGrid[hunters.get(hunters.size() - 1).getX()][hunters.get(hunters.size() - 1).getY()] = 3;
        }
    }


    @FXML
    private void lionGraph() {//populates the lion chart
        lChart.getData().clear();

        XYChart.Series series = new XYChart.Series();
        series.setName("Lion Population");

        series.getData().clear();
        for (int i = 0; i < lionPopulation.size()-1; i++) {
            series.getData().add(new XYChart.Data(i, lionPopulation.get(i)));
        }
        lChart.getData().add(series);
    }

    @FXML
    private void zebraGraph() {//populates the zebra chart
        zChart.getData().clear();

        XYChart.Series series = new XYChart.Series();
        series.setName("Zebra Population");
        series.getData().clear();
        for (int i = 0; i < zebraPopulation.size()-1; i++) {
            series.getData().add(new XYChart.Data(i, zebraPopulation.get(i)));
        }
        zChart.getData().add(series);
    }
    @FXML
    private void hunterGraph() {//populates the hunter chart
        hChart.getData().clear();

        XYChart.Series series = new XYChart.Series();
        series.setName("Hunter Population");

        series.getData().clear();
        for (int i = 0; i < hunterPopulation.size()-1; i++) {
            series.getData().add(new XYChart.Data(i, hunterPopulation.get(i)));
        }
        hChart.getData().add(series);
    }


    public int lionAgeProb(Lion l){//probibility for lion death
        int prob=(int)(Math.random()*100);
        if(l.getAge()<4){
            if(prob > lionProb+10) {
                return 1;
            }
        }else if(l.getAge()<15&&l.getAge()>=4){
            if(prob > lionProb+11) {
                return 2;
            }
        }else if(l.getAge()>15) {
            if(prob > lionProb+5) {
                return 3;
            }
        }
        return 0;
    }

    public int zebraAgeProb(Zebra z){//probibility for zebra death
        int prob=(int)(Math.random()*100);
        if(z.getAge()<4){
            if(prob > zebraProb+10) {
                return 1;
            }
        }else if(z.getAge()<15&&z.getAge()>=4){
            if(prob > zebraProb+11) {
                return 2;
            }
        }else if(z.getAge()>zebraProb+3) {
            if(prob > 75) {
                return 3;
            }
        }
        return 0;
    }

    public int hunterAgeProb(Hunter h){//probibility for zebra death
        int prob=(int)(Math.random()*100);
        if(h.getAge()<4){
            if(prob > hunterProb+7) {
                return 1;
            }
        }else if(h.getAge()<15&&h.getAge()>=4){
            if(prob > hunterProb+10) {
                return 2;
            }
        }else if(h.getAge()>15) {
            if(prob > hunterProb+1) {
                return 3;
            }
        }
        return 0;
    }

    public void ifOpenSquare(int i,int j, String species) {//finds an open spot around a spot and returns the x and y of the spot
        if(species.equals("lion")) {
            lionPossibleLoc.clear();//clears previous spots
        }else if(species.equals("zebra")){
            zebraPossibleLoc.clear();//clears previous spots
        }else if(species.equals("hunter")){
            hunterPossibleLoc.clear();//clears previous spots
        }
        int radius = 1;
        int[][] spots = {{radius, 0}, {-radius, 0}, {0, radius}, {0, -radius}, {radius, radius}, {-radius, -radius}, {radius, -radius}, {-radius, radius}};

        for (int z = 0; z < spots.length; z++) {
            i = i + spots[z][0];
            j = j + spots[z][1];
            if (i >= 0 && j >= 0 && i <= gameGrid.length - 1 && j <= gameGrid[0].length - 1) {
                if (spotCheck(i, j, 0)) {
                    if (species.equals("zebra")){
                        zebraPossibleLoc.add(i + "" + j);
                    }else if(species.equals("lion")){
                        lionPossibleLoc.add(i + "" + j);
                    }else if(species.equals("hunter")){
                        hunterPossibleLoc.add(i + "" + j);
                    }
                }
            }
        }
    }

    public boolean spotCheck(int x, int y,int spotType){
        return gameGrid[x][y] ==spotType;
    }//checks if the spot is open

    public void lionReproduce(Lion l){//lion reproduction
        ifOpenSquare(l.getX(),l.getY(),"lion");
        int ty=l.getY();
        int tx=l.getX();

        if(lionPossibleLoc.size()>0){
            String newLocation=lionPossibleLoc.get((int)(Math.random()* lionPossibleLoc.size()-1));
            if(l.getAge()>4/*&&l.getAge()<15*/){
                l.setX(Integer.parseInt(newLocation.substring(0,newLocation.length()/2)));
                l.setY(Integer.parseInt(newLocation.substring(newLocation.length()/2)));
                birth(tx,ty,"lion");
            }
        }
    }

    public void zebraReproduce(Zebra z){//zebra reproduction
        ifOpenSquare(z.getX(),z.getY(),"zebra");
        int ty=z.getY();
        int tx=z.getX();

        if(zebraPossibleLoc.size()>0){
            String newLocation=zebraPossibleLoc.get((int)(Math.random()* zebraPossibleLoc.size()-1));
            if(z.getAge()>4/*&&l.getAge()<15*/){
                z.setX(Integer.parseInt(newLocation.substring(0,newLocation.length()/2)));
                z.setY(Integer.parseInt(newLocation.substring(newLocation.length()/2)));
                birth(tx,ty,"zebra");
            }
        }
    }

    public void hunterReproduce(Hunter h){//hunter reproduction
        ifOpenSquare(h.getX(),h.getY(),"hunter");
        int ty=h.getY();
        int tx=h.getX();

        if(hunterPossibleLoc.size()>0){
            String newLocation=hunterPossibleLoc.get((int)(Math.random()* hunterPossibleLoc.size()-1));
            if(h.getAge()>4/*&&l.getAge()<15*/){
                h.setX(Integer.parseInt(newLocation.substring(0,newLocation.length()/2)));
                h.setY(Integer.parseInt(newLocation.substring(newLocation.length()/2)));
                birth(tx,ty,"hunter");
            }
        }
    }

    public void birth(int x,int y,String species){//creates a new lion, zebra, or hunter
        if(species.equals("lion")) {
            lions.add(new Lion(x, y, lions.size()));
            lionNews.getItems().add("A baby lion was born");
        }else if(species.equals("zebra")){

            zebras.add(new Zebra(x, y, zebras.size()));
            zebraNews.getItems().add("A baby zebra was born");
        }else if(species.equals("hunter")){

            hunters.add(new Hunter(x, y, hunters.size()));
            hunterNews.getItems().add("A baby hunter was born");
        }

    }

    @FXML
    public void killAllLion(){//kills all the lions on the screen
        for (int i = 0; i < lions.size(); i++) {
            gameGrid[lions.get(i).getX()][lions.get(i).getY()]=0;
        }
        lions.clear();
    }

    @FXML
    public void killAllZebra(){//kills all of the zebras
        for (int i = 0; i < zebras.size(); i++) {
            gameGrid[zebras.get(i).getX()][zebras.get(i).getY()]=0;
        }
        zebras.clear();
    }

    @FXML
    public void killAllHunter(){//kills all of the hunters
        for (int i = 0; i < hunters.size(); i++) {
            gameGrid[hunters.get(i).getX()][hunters.get(i).getY()] = 0;
        }
        hunters.clear();
    }


    public void clearLionDead(){//Clears the dying lions array. Since cannot kill while the animation timer is running
        for (int i = 0; i < dyingLions.size(); i++) {
            lions.remove(dyingLions.get(i));
            gameGrid[dyingLions.get(i).getX()][dyingLions.get(i).getY()]=0;
        }
        dyingLions.clear();

    }
    public void clearZebraDead(){//clears the dying zebra array. Since cannot kill while the animation timer is running
        if(dyingZebras.size()>0) {
            for (int i = 0; i < dyingZebras.size(); i++) {
                zebras.remove(dyingZebras.get(i));
                gameGrid[dyingZebras.get(i).getX()][dyingZebras.get(i).getY()] = 0;
            }
            dyingZebras.clear();
        }

    }

    public void clearHunterDead(){//clears the dying hunter array. Since cannot kill while the animation timer is running
        if(dyingHunters.size()>0) {

            for (int i = 0; i < dyingHunters.size(); i++) {
                hunters.remove(dyingHunters.get(i));
                gameGrid[dyingHunters.get(i).getX()][dyingHunters.get(i).getY()] = 0;
            }
            dyingHunters.clear();
        }
    }

    public void lionDie(Lion l){//adds the lion to dying lion array(during loop)
            dyingLions.add(l);
        if(l.getAge()>oldestLionAge){
            lionNews.getItems().add("Lion Number "+(l.getPosition()+1)+", holds the record, for most years lived, at " + l.getAge()+" years.");
        }
    }

    public void zebraDie(Zebra z){//adds the zebra to dying lion array(during loop)
            dyingZebras.add(z);
        if(z.getAge()>oldestZebraAge){
            zebraNews.getItems().add("Zebra Number "+(z.getPosition()+1)+", holds the record, for most years lived, at " + z.getAge()+" years.");
        }
    }

    public void hunterDie(Hunter h){//adds the hunter to dying lion array(during loop)
            dyingHunters.add(h);
        if(h.getAge()>oldestHunterAge){
            hunterNews.getItems().add("Hunter Number "+(h.getPosition()+1)+", holds the record, for most years lived, at " + h.getAge()+" years.");
        }       // }
    }

    @FXML
    public void harshWeather(){//creates harsh weather
        //increases death rate
        lionProb=lionProb-10;
        hunterProb=hunterProb-10;
        zebraProb=zebraProb-10;
        ifHarshWeather=true;
    }

    public void betterWeather(){//reverts the changes made by the harsh weather
        //decreases death reate
        lionProb=lionProb+10;
        hunterProb=hunterProb+10;
        zebraProb=zebraProb+10;
        ifHarshWeather=false;
    }

    public void drought(){
        ifDrought=true;
    }//creates drought

    public void waterNormal(){//reverts changes made by drought
        ifDrought=false;
        for(Water w:water){
            gameGrid[w.getX()][w.getY()] = 4;

        }
    }

    @FXML
    public void chooseDisease(){//gets the selected disease and runs the code
        if(diseaseChoice.getValue().equals("Lion")){
            lionDisease();
        }else if(diseaseChoice.getValue().equals("Zebra")){
            zebraDisease();
        }else if(diseaseChoice.getValue().equals("Hunter")){
            hunterDisease();
        }
    }

    public void lionDisease(){//create the lion disease spots
        int dx;
        int dy;
        //create random disease spots
        for(int i=0;i<5;i++) {
            dx=myRand(0,24);
            dy=myRand(0,35);
            if(lionDiseaseSpots.size()>1) {//if there are already disease spots
                for (Disease d : lionDiseaseSpots) {
                    if ((!(dx == d.getX()) && !(dy == d.getY())&& !(gameGrid[dx][dy]==4) )){//make sure that spot is open
                        gameGrid[dx][dy] = 5;
                        lionDiseaseSpots.add(new Disease(dx, dy, 1));
                        break;
                    } else {
                        dx = myRand(0, 24);
                        dy = myRand(0, 35);
                    }
                }
            }else{
                gameGrid[dx][dy] = 5;
                lionDiseaseSpots.add(new Disease(dx, dy, 1));
            }
        }
    }

    public void zebraDisease(){//creates lion disease spots
        int dx;
        int dy;
        //create random disease spots
        for(int i=0;i<5;i++) {
            dx=myRand(0,24);
            dy=myRand(0,35);
            if(zebraDiseaseSpots.size()>1) {//if already have zebra disease spots on grid
                for (Disease d : zebraDiseaseSpots) {
                    if ((!(dx == d.getX()) && !(dy == d.getY()))&& !(gameGrid[dx][dy]==4)){//checks if spot is open
                        gameGrid[dx][dy] = 5;
                        zebraDiseaseSpots.add(new Disease(dx, dy, 2));
                        break;
                    } else {
                        dx = myRand(0, 24);
                        dy = myRand(0, 35);
                    }
                }
            }else{
                gameGrid[dx][dy] = 5;
                zebraDiseaseSpots.add(new Disease(dx, dy, 2));
            }
        }
    }

    public void hunterDisease(){//creates hunter disease spots
        int dx;
        int dy;
        //create random disease spots
        for(int i=0;i<5;i++) {
            dx=myRand(0,24);
            dy=myRand(0,35);
            if(hunterDiseaseSpots.size()>1) {//if already have zebra disease spots on grid
                for (Disease d : hunterDiseaseSpots) {
                    if ((!(dx == d.getX()) && !(dy == d.getY())) && !(gameGrid[dx][dy]==4)) {//checks if spot is open
                        gameGrid[dx][dy] = 5;
                        hunterDiseaseSpots.add(new Disease(dx, dy, 2));
                        break;
                    } else {
                        dx = myRand(0, 24);
                        dy = myRand(0, 35);
                    }
                }
            }else{
                gameGrid[dx][dy] = 5;
                hunterDiseaseSpots.add(new Disease(dx, dy, 2));
            }
        }
    }

    public void getRidOfDisease(){//gets rid of all of the diseases
        for(Disease l:lionDiseaseSpots){
            gameGrid[l.getX()][l.getY()]=0;
        }
        lionDiseaseSpots.clear();
        for(Disease l:zebraDiseaseSpots) {
            gameGrid[l.getX()][l.getY()] = 0;
        }
        zebraDiseaseSpots.clear();
        for(Disease l:hunterDiseaseSpots){
            gameGrid[l.getX()][l.getY()] = 0;
        }
        hunterDiseaseSpots.clear();
    }

    private int myRand(int lowerBound, int upperBound){//returns random number
        double rand=(int)(Math.random() * (upperBound - lowerBound+1))+lowerBound;
        return (int)rand;
    }


    public void start() {//main loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                //lion
                if(lions.size()>0){
                    for (int i = 0;i<lions.size();i++){
                        if(now - lions.get(i).getStartTime() > 1000000000.0){
                            //lion movement
                            lions.get(i).changeLoc(gameGrid,lions,lionDiseaseSpots);
                            //if lion has been sick for a while kill it
                            if(lions.get(i).ifIsSick(gameGrid)){
                                lionDie(lions.get(i));
                            }
                            //adds age
                            lions.get(i).addAge();
                            //checks if lion will die becusae of natural causes
                            if(lionAgeProb(lions.get(i))>0){
                                lionDie(lions.get(i));
                                lionNews.getItems().add("Lion Number "+ lions.get(i).getPosition()+ "died because of natural causes");
                            }
                            //uses lion water, if in drought it uses up double
                            lions.get(i).useWater();
                            if(ifDrought){
                                lions.get(i).useWater();
                            }
                            //if lion does not have any water left kill it
                            if(lions.get(i).noWater()){
                                lionDie(lions.get(i));
                                lionNews.getItems().add("Lion Number "+ lions.get(i).getPosition()+ "died of dehydration");
                            }
                            //if lion is near zebra it will kill it
                            interact=lions.get(i).ifNear(gameGrid,2,1);
                            if(!(interact.equals("-1"))){
                                for(int k=0;k<zebras.size();k++){
                                    if(Integer.parseInt(interact.substring(0, interact.length() / 2))==zebras.get(k).getX() &&Integer.parseInt(interact.substring(interact.length() / 2))==zebras.get(k).getY()){
                                        if (Math.random()>.3) {
                                            zebraDie(zebras.get(k));
                                            zebraNews.getItems().add("Lion Number " + lions.get(i).getPosition()+" ate Zebra Number "+ zebras.get(k).getPosition() +" for breakfast");
                                        }
                                        break;
                                    }
                                }
                            }
                            //if able to reproduce reproduce
                            if(lions.get(i).checkGender(lions,1)){
                                lionReproduce(lions.get(i));
                            }
                            //resets start time
                            lions.get(i).resetStartTime();

                        }
                    }
                    //clears the dead lions
                    clearLionDead();
                }
                //zebras
                if(zebras.size()>0){
                    for (int i = 0;i<zebras.size();i++){
                        if(now - zebras.get(i).getStartTime() > 1000000000.0){
                            //zebra movement
                            zebras.get(i).changeLoc(gameGrid,zebras,zebraDiseaseSpots);
                            //uses water, if in drought use double
                            zebras.get(i).useWater();
                            if(ifDrought){
                                zebras.get(i).useWater();
                            }
                            //adds age to zebra
                            zebras.get(i).addAge();
                            //kills zebra becuase of old age
                            if(zebraAgeProb(zebras.get(i))>0){
                                zebraDie(zebras.get(i));
                                zebraNews.getItems().add("Zebras Number "+ zebras.get(i).getPosition()+ "died because of natural causes");
                            }
                            //if able to reproduce, reproduces
                            if(zebras.get(i).checkGender(zebras,1)){
                                zebraReproduce(zebras.get(i));
                            }
                            //if it has been sick for a while kill it
                            if(zebras.get(i).ifIsSick(gameGrid)){
                                zebraDie(zebras.get(i));
                            }
                            //if does not have any water kill it
                            if(zebras.get(i).noWater()){
                                zebraDie(zebras.get(i));
                                zebraNews.getItems().add("Zebra Number "+ zebras.get(i).getPosition()+ "died of dehydration");
                            }
                            //resets start timer
                            zebras.get(i).resetStartTime();
                        }
                    }
                    clearZebraDead();
                }

                //hunter
                if(hunters.size()>0){
                    for(int i = 0;i<hunters.size();i++){

                        if(now - hunters.get(i).getStartTime() > 1000000000.0){
                            //Zebra

                            //hunterGraph();
                            //hunter movement
                            hunters.get(i).changeLoc(gameGrid,hunters,hunterDiseaseSpots);

                            //resets hunter start time
                            hunters.get(i).resetStartTime();

                            //adds age to hunter
                            hunters.get(i).addAge();

                            //use water, if drought use double
                            hunters.get(i).useWater();
                            if(ifDrought){
                                hunters.get(i).useWater();
                            }
                            //if hunter has no water then it will die
                            if(hunters.get(i).noWater()){
                                hunterDie(hunters.get(i));
                                hunterNews.getItems().add("Hunter Number "+ hunters.get(i).getPosition()+ "died of dehydration");
                            }
                            //checks if huter dies becasue of natural cause
                            if(hunterAgeProb(hunters.get(i))>0){
                                hunterDie(hunters.get(i));
                                hunterNews.getItems().add("Hunter Number "+ hunters.get(i).getPosition()+ "died because of natural causes");
                            }
                            //if able to reproduce, then reproduces
                            if(hunters.get(i).checkGender(hunters,1)){
                                hunterReproduce(hunters.get(i));
                            }
                            //checks if the hunter is near a zebra, if it is, then it will kill it
                            interact=hunters.get(i).ifNear(gameGrid,2,1);
                            if(!(interact.equals("-1"))){
                                for(int k=0;k<zebras.size();k++){
                                        if(Integer.parseInt(interact.substring(0, interact.length() / 2))==zebras.get(k).getX() &&Integer.parseInt(interact.substring(interact.length() / 2))==zebras.get(k).getY()){
                                            if (Math.random()>.3) {
                                                zebraDie(zebras.get(k));
                                                zebraNews.getItems().add("Hunter Number " + hunters.get(i).getPosition()+" poached Zebra Number "+ zebras.get(k).getPosition());
                                            }
                                            break;
                                        }
                                    }
                            }

                            //checks if the hunter is near a lion, if it is then it will kill it
                            interact=hunters.get(i).ifNear(gameGrid,1,1);
                            if(!(interact.equals("-1"))){
                                for(int k=0;k<lions.size();k++){
                                    if(Integer.parseInt(interact.substring(0, interact.length() / 2))==lions.get(k).getX() &&Integer.parseInt(interact.substring(interact.length() / 2))==lions.get(k).getY()){
                                        if (Math.random()>.3) {
                                            lionDie(lions.get(k));
                                            lionNews.getItems().add("Hunter Number " + hunters.get(i).getPosition()+" poached Lion Number "+ lions.get(k).getPosition());
                                        }
                                        break;
                                    }
                                }
                            }

                        }
                    }
                    clearHunterDead();
                }
                //updates the graph
                if(now - graphTime >  2000000000.0){
                   graphTime=System.nanoTime();//resets the graph time
                    lionPopulation.add(lions.size());
                    zebraPopulation.add(zebras.size());
                    hunterPopulation.add(hunters.size());
                    hunterGraph();
                    zebraGraph();
                    lionGraph();
                    //if there is harsh weather, then add to harsh weather timer
                    if(ifHarshWeather){
                        weatherCounter++;
                    }
                    //if there is a drought, adds to drought timer
                    if(ifDrought){
                        droughtCounter++;
                    }
                }
                //if harsh weater for 4 seconds, then revert back to regular weather
                if(weatherCounter==2){
                    betterWeather();
                    weatherCounter=0;
                }
                //if drought for 8 seconds, then revert back to regular water
                if(droughtCounter==4){
                    waterNormal();
                    droughtCounter=0;
                }

                updateScreen();
            }
        }.start();
    }
}

