package lab4_204_43.uwaterloo.ca.lab4_204_43;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by yanqing on 16/03/17.
 */

public class GameBlock extends GameBlockTemplate {
    private final float ACC = 4.0f;// the accelerometer of the block
    private final float IMAGE_SCALE = 0.6f;

    public RelativeLayout gbRL;
    private GameLoopTask myGLT;

    private int myCoordX;
    private int myCoordY;
    private int targetX;
    private int targetY;
    private int Velocity;
    public int blockNumber;

    public TextView gbNumTV;
    private int numViewOffsetX = 150;
    private int numViewOffsetY = 80;

    private GameLoopTask.gameDirection myDir =GameLoopTask.gameDirection.NO_MOVEMENT;

    private int [] blockNum = new int [4];
    public boolean toBeRemoved = false;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameBlock(Context gbContext, RelativeLayout RL, int coord_X, int coord_Y, GameLoopTask task ){
        super(gbContext);
        this.setImageResource(R.drawable.gameblock);
        this.gbRL = RL;
        this.myGLT = task;

        myCoordX = coord_X;         // the (x,y) value
        myCoordY = coord_Y;
        targetX = myCoordX;         // take in Coordinate and assign
        targetY = myCoordY;
        Velocity = 0;               // initialize the velocity of block to 0
        myDir = GameLoopTask.gameDirection.NO_MOVEMENT;
        setX(myCoordX);
        setY(myCoordY);
        setScaleX(IMAGE_SCALE);     // initialize image scale
        setScaleY(IMAGE_SCALE);

        Random myRandom = new Random();
        blockNumber = (myRandom.nextInt(2) + 1) * 2;
        gbNumTV = new TextView(gbContext);
        gbRL.addView(gbNumTV);
        gbNumTV.setX(myCoordX+numViewOffsetX);
        gbNumTV.setY(myCoordY+numViewOffsetY);
        gbNumTV.setText(String.format("%d",blockNumber));
        gbNumTV.setTextSize(50.0f);
        gbNumTV.setTextColor(Color.BLACK);
        blockNum = new int[4];
        toBeRemoved = false;
    }
    public  int[] getTarget(){
        int[] targetcoord = new int[2];
        targetcoord[0] = targetX;
        targetcoord[1] = targetY;
        return targetcoord;
    }
    public int[] getCoord(){
        int[] curr = new int[2];
        curr[0] = myCoordX;
        curr[1] = myCoordY;
        return curr;
    }

    // Delete everything of THE block
    public  GameBlock remove(){
        gbRL.removeView(gbNumTV);
        myGLT = null;
        myDir = null;
        return this;
    }
    public  int getNumber(){
        return  blockNumber;
    }

    public int[] removeZero (){//remove the 0 from the array of a 4 number int array
        int count = 0;
        for (int x : blockNum) {
            if (x != 0)
                count++;
        }

        int[] tempArr = new int[count];
        int j = 0;
        for (int a = 0; a < blockNum.length; a++) {
            if (blockNum[a] != 0) {
                tempArr[a - j] = blockNum[a];
            } else j++;
        }
        return tempArr;
    }

    public int eliminateSameValue(int occupant) {
        int[] tempArr = removeZero();
        if (tempArr.length==4) {
            if(tempArr[0]==tempArr[1]){
                occupant--;
                if(tempArr[2]==tempArr[3]) {
                    occupant--;
                    toBeRemoved=true;
                }
            } else if(tempArr[1]==tempArr[2]){
                occupant--;
            } else if(tempArr[2]==tempArr[3]){
                occupant--;
                toBeRemoved=true;
            }
        }
        if (tempArr.length==3) {
            if(tempArr[0]==tempArr[1]){
                occupant--;
            } else if(tempArr[1]==tempArr[2]){
                occupant--;
                toBeRemoved=true;
            }
        }
        if (tempArr.length==2) {
            if(tempArr[0]==tempArr[1]){
                occupant--;
                toBeRemoved=true;
            }
        }
        return occupant;
    }
    public void setDestination(GameLoopTask.gameDirection setDir) {
        myDir = setDir;
        int targetPoint;
        int numOccupied = 0;
        toBeRemoved = false;
        switch(setDir){
            case NO_MOVEMENT:
                break;
            case LEFT:
                targetPoint = GameLoopTask.LEFT_BOUNDARY;
                blockNum = new int [(GameLoopTask.coordToLoc(myCoordX)) + 1];   // Create an array to store look-ahead block number
                blockNum[GameLoopTask.coordToLoc(myCoordX)] = blockNumber;      // Record the number of current block
                while(targetPoint != myCoordX){
                    blockNum[GameLoopTask.coordToLoc(targetPoint)] =
                            myGLT.blockValue[GameLoopTask.coordToLoc(targetPoint)][GameLoopTask.coordToLoc(myCoordY)];
                    if(myGLT.isOccupied(targetPoint, myCoordY)){
                        numOccupied++;
                    }
                    if(targetPoint<GameLoopTask.RIGHT_BOUNDARY){
                        targetPoint += GameLoopTask.SLOT_ISOLATION;//make sure it not out off range
                    }
                }
                numOccupied = eliminateSameValue(numOccupied);//find the same number and merge
                targetX = GameLoopTask.LEFT_BOUNDARY + numOccupied * GameLoopTask.SLOT_ISOLATION;//set new target
                targetY = myCoordY;
                break;

            case RIGHT:
                targetPoint = GameLoopTask.RIGHT_BOUNDARY;
                blockNum = new int[4-(GameLoopTask.coordToLoc(myCoordX))+1 ];
                blockNum [4-(GameLoopTask.coordToLoc(myCoordX))] = blockNumber;
                while(targetPoint != myCoordX){
                    blockNum[4-(GameLoopTask.coordToLoc(targetPoint))] =
                            myGLT.blockValue[GameLoopTask.coordToLoc(targetPoint)][GameLoopTask.coordToLoc(myCoordY)];
                    if(myGLT.isOccupied(targetPoint, myCoordY)){
                        numOccupied++;
                    }
                    if (targetPoint>GameLoopTask.LEFT_BOUNDARY) {
                        targetPoint -= GameLoopTask.SLOT_ISOLATION;
                    }
                }
                numOccupied = eliminateSameValue(numOccupied);

                targetX = GameLoopTask.RIGHT_BOUNDARY - numOccupied * GameLoopTask.SLOT_ISOLATION;
                targetY = myCoordY;
                break;
            case UP:
                targetPoint = GameLoopTask.UP_BOUNDARY;
                blockNum = new int [(GameLoopTask.coordToLoc(myCoordY))+1];
                blockNum[GameLoopTask.coordToLoc(myCoordY)] = blockNumber;
                while(targetPoint != myCoordY){
                    blockNum[GameLoopTask.coordToLoc(targetPoint)] =
                            myGLT.blockValue[GameLoopTask.coordToLoc(myCoordX)][GameLoopTask.coordToLoc(targetPoint)];

                    if(myGLT.isOccupied(myCoordX, targetPoint)){
                        numOccupied++;
                    }
                    if(targetPoint<GameLoopTask.DOWN_BOUNDARY){
                        targetPoint += GameLoopTask.SLOT_ISOLATION;
                    }
                }
                numOccupied = eliminateSameValue(numOccupied);
                targetY = GameLoopTask.UP_BOUNDARY + numOccupied * GameLoopTask.SLOT_ISOLATION;
                targetX = myCoordX;
                break;
            case DOWN:
                targetPoint = GameLoopTask.DOWN_BOUNDARY;
                blockNum = new int[4-(GameLoopTask.coordToLoc(myCoordY)) +1];
                blockNum [4-(GameLoopTask.coordToLoc(myCoordY))] = blockNumber;
                while(targetPoint != myCoordY){
                    blockNum[4-(GameLoopTask.coordToLoc(targetPoint))] =
                            myGLT.blockValue[GameLoopTask.coordToLoc(myCoordX)][GameLoopTask.coordToLoc(targetPoint)];
                    if(myGLT.isOccupied(myCoordX, targetPoint)){
                        numOccupied++;
                    }
                    if (targetPoint>GameLoopTask.UP_BOUNDARY) {
                        targetPoint -= GameLoopTask.SLOT_ISOLATION;
                    }
                }
                numOccupied = eliminateSameValue(numOccupied);
                targetY = GameLoopTask.DOWN_BOUNDARY - numOccupied * GameLoopTask.SLOT_ISOLATION;
                targetX = myCoordX;
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void move(){

        // Ensure the number of block is at the center of block corresponding to different digits
        if(blockNumber > 10){
            numViewOffsetX = 110;
            if(blockNumber > 100){
                numViewOffsetX = 90;
                gbNumTV.setTextSize(40);
            }
            if(blockNumber > 1000){
                numViewOffsetX = 95;
                numViewOffsetY = 120;
                gbNumTV.setTextSize(30);
            }
        }
        gbNumTV.setText(String.format("%d", blockNumber));
        bringToFront();
        gbNumTV.bringToFront();
        switch(myDir){
            case UP:
                if(myCoordY>targetY){
                    if((myCoordY-Velocity)<=targetY){
                        myCoordY = targetY;
                        Velocity = 0;
                        gbNumTV.setText(String.format("%d", blockNumber));
                    }
                    else{
                        myCoordY -= Velocity;
                        Velocity += ACC;
                    }
                }
                break;
            case DOWN:
                if(myCoordY<targetY){
                    if((myCoordY+Velocity)>=targetY){
                        myCoordY = targetY;
                        Velocity = 0;
                        gbNumTV.setText(String.format("%d", blockNumber));
                    }
                    else{
                        myCoordY += Velocity;
                        Velocity += ACC;
                    }
                }
                break;
            case LEFT:
                if(myCoordX>targetX){
                    if((myCoordX-Velocity)<=targetX){
                        myCoordX = targetX;
                        Velocity = 0;
                        gbNumTV.setText(String.format("%d", blockNumber));
                    }
                    else{
                        myCoordX -= Velocity;
                        Velocity += ACC;
                    }
                }
                break;
            case RIGHT:
                if(myCoordX<targetX){
                    if((myCoordX+Velocity)>=targetX){
                        myCoordX = targetX;
                        Velocity = 0;
                        gbNumTV.setText(String.format("%d", blockNumber));
                    }
                    else{
                        myCoordX += Velocity;
                        Velocity += ACC;
                    }
                }
                break;
            default:
                break;
        }
        setX(myCoordX);
        setY(myCoordY);
        gbNumTV.setX(myCoordX+numViewOffsetX);
        gbNumTV.setY(myCoordY+numViewOffsetY);
    }
}

