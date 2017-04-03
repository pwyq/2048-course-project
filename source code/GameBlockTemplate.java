package lab4_204_43.uwaterloo.ca.lab4_204_43;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by yanqing on 22/03/17.
 */

public abstract class GameBlockTemplate extends ImageView {
    public GameBlockTemplate(Context gbCTX){
        super(gbCTX);
    }

    public abstract void setDestination(GameLoopTask.gameDirection myDir);

    public abstract void move();
}
