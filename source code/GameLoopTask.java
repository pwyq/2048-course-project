package lab4_204_43.uwaterloo.ca.lab4_204_43;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

/**
 * Created by yanqing on 16/03/17.
 */

public class GameLoopTask extends TimerTask{
    private Activity myActivity;
    private Context myContext;
    private RelativeLayout MyRL;
    private GameBlock newBlock;

    public enum  gameDirection{UP,DOWN,LEFT,RIGHT,NO_MOVEMENT}
    public LinkedList<GameBlock> myGBList;
    private LinkedList<Integer> tempStore = new LinkedList<>();

    private int randCoordX;
    private int randCoordY;

    public static final int SLOT_ISOLATION = 270;   // 1/4 of the solution of board
    public static final int LEFT_BOUNDARY = -60;
    public static final int UP_BOUNDARY = -60;
    public static final int RIGHT_BOUNDARY = 750; // = LEFT_BOUNDARY + 3 * SLOT_ISOLATION;
    public static final int DOWN_BOUNDARY = 750;  // = UP_BOUNDARY + 3*SLOT_ISOLATION;

    private boolean emptySlot = true;
    private boolean gameWin = false;

    private Random myRandomGen = new Random();
    public int [][] blockValue= new int[4][4];
    private boolean [][] isFull= new boolean[4][4]; // default is false; false means empty slot, true means full slot
    private int [] tempAdd = new int [100];

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameLoopTask(Activity myActivity, RelativeLayout MyRL, Context myContext) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                isFull[i][j]=false;
                blockValue[i][j]=0;
            }
        }
        this.myActivity = myActivity;
        this.MyRL = MyRL;
        this.myContext = myContext;
        myGBList = new LinkedList<>();
        createBlock();
        for(int i=0;i<100;i++){
            tempAdd[i]=0;
        }
    }

    // Convert coordinate (pixel coordinate) to location (cartesian coordinate)
    public static int coordToLoc(int coord){
        return (coord - LEFT_BOUNDARY)/SLOT_ISOLATION;
    }

    // Convert location (cartesian coordinate) to  coordinate (pixel coordinate)
    public static int locToCoord(int coord){
        return LEFT_BOUNDARY + coord*SLOT_ISOLATION;
    }

    // Determine if input coordinate slot is occupied
    public boolean isOccupied(int coordX,int coordY) {
        for(GameBlock gb : myGBList) {
            if(gb.getCoord()[0] == coordX && gb.getCoord()[1] == coordY) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void createBlock(){
        boolean occupied = true;
        randCoordX = myRandomGen.nextInt(4)*SLOT_ISOLATION + LEFT_BOUNDARY;
        randCoordY = myRandomGen.nextInt(4)*SLOT_ISOLATION + UP_BOUNDARY;
        for(int a = 0;a <4; a++){
            for(int j = 0; j < 4; j++){
                if(!isFull[a][j]) {
                    occupied = false;
                }
            }
        }
        if(!occupied) {
            // Create a new coordinate that not being occupied
            while (isFull[coordToLoc(randCoordX)][coordToLoc(randCoordY)]) {
                randCoordX = locToCoord(myRandomGen.nextInt(4));
                randCoordY =  locToCoord(myRandomGen.nextInt(4));
            }
            newBlock = new GameBlock(myContext, MyRL, randCoordX, randCoordY, this);
            MyRL.addView(newBlock);
            myGBList.add(newBlock);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void setDirection(gameDirection newDirection){
        tempStore.clear();
        emptySlot = false;

        for(int i = 0; i < 100; i++){
            tempAdd[i]=0;
        }

        // Set direction for each block in the list
        for(GameBlock gb : myGBList) {
            gb.setDestination(newDirection);
        }
        for(int i = 0; i <4; i++){
            for(int j = 0; j < 4; j++){
                isFull[i][j] = false;
                blockValue[i][j] = 0;
            }
        }
        for(GameBlock gb : myGBList) {
            if(gb.getCoord()[0] != gb.getTarget()[0] || gb.getCoord()[1] != gb.getTarget()[1]){
                emptySlot = true;
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (gb.getTarget()[0] == locToCoord(i) && gb.getTarget()[1] == locToCoord(j)) {
                        isFull[i][j] = true;
                        blockValue[i][j] = gb.getNumber(); // Save the block number in an 2D array
                    }
                }
            }
        }
    }

    // Determine the ending game condition
    public boolean endCondition(){
        boolean gameWon = true;
        for(int i = 0; i <4; i++){
            for(int j = 0; j < 3; j++){
                if(blockValue[j][i] ==blockValue[j+1][i]||blockValue[i][j]==blockValue[i][j+1])
                {
                    gameWon = false;
                }
            }
        }
        return gameWon;
    }

    @Override
    public void run(){
        this.myActivity.runOnUiThread(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                    public void run() {
                        boolean targetReached = true;
                        tempStore.clear();
                        for(GameBlock gb : myGBList){
                            if(gb.getCoord()[0] != gb.getTarget()[0] || gb.getCoord()[1] != gb.getTarget()[1]){
                                targetReached = false;// Ensure every block reaches target coordinate
                            }
                        }
                        for(GameBlock gb : myGBList){
                            gb.move();
                            if(gb.getCoord()[0] == gb.getTarget()[0] && gb.getCoord()[1] == gb.getTarget()[1]){

                                for(GameBlock gb1 : myGBList){
                                    if(gb1.getTarget()[0] == gb.getTarget()[0] && gb1.getTarget()[1] == gb.getTarget()[1] && gb != gb1){
                                        if(gb.toBeRemoved){
                                            // indexOf(): Returns the index of the first occurrence of the specified element in this list,
                                            // or -1 if this list does not contain the element.
                                            if(tempAdd[myGBList.indexOf(gb)] < 1){    // Rearrange the list that going to be removed
                                                tempStore.add(myGBList.indexOf(gb));
                                            }
                                        }
                                        if(tempAdd[myGBList.indexOf(gb)] < 1 && targetReached){
                                            gb.blockNumber *= 2;
                                            if(gb.blockNumber >= 128){
                                                gameWin = true;
                                            }
                                            blockValue[coordToLoc(gb.getTarget()[0])][coordToLoc(gb.getTarget()[1])] = gb.blockNumber;
                                            tempAdd[myGBList.indexOf(gb)] += 1;
                                        }
                                    }
                                }
                            }
                        }
                        if(targetReached){
                            if(emptySlot){
                                createBlock();  // Create a new block and save the Number
                                blockValue[coordToLoc(myGBList.getLast().getCoord()[0])][coordToLoc(myGBList.getLast().getCoord()[1])] =myGBList.getLast().getNumber();
                                emptySlot = false;
                            }
                            int tempCount = 0;
                            // (x-tempCount) to ensure always delete the first block in the list
                            for(int x : tempStore){
                                // Delete every block in the list
                                MyRL.removeView(myGBList.get(x-tempCount).remove());    // clear the view of the block
                                myGBList.remove(x-tempCount);   // clear the reference of the block to the list
                                tempCount++;
                            }
                        }

                        // Determine winning or losing
                        if(gameWin || endCondition()){
                            TextView endGameMsg = new TextView(myContext);
                            MyRL.addView(endGameMsg);
                            endGameMsg.bringToFront();
                            endGameMsg.setTextColor(Color.CYAN);
                            endGameMsg.setTextSize(40);
                            endGameMsg.setX(100);
                            endGameMsg.setY(400);
                            if(gameWin){
                                Log.d("###Game WON###","VICTORY");
                                endGameMsg.setText("VICTORY!");
                            }
                            else if(endCondition()){
                                Log.d("###Game LOST###","YOU LOST");
                                endGameMsg.setText("YOU LOST!");
                            }
                        }
                    }
                }
        );
    }
}
