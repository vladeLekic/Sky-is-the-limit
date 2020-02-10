package com.gmail.freshideassoftware.skyislimit;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

public class ModelSelectedAnimation extends NodeAnimation {
    protected static final long ROTATION_TIMER = 10;
    protected static final long MOVEMENT_TIMER = 50;
    protected static final double SCOPE = 9;
    protected static final double STEP = 0.01;
    protected int orientation = 1;
    protected int countOfSteps = 0;

    protected int timePassed = 0;

    public ModelSelectedAnimation(Node _node){
        super(_node);
        node.setLocalScale(new Vector3(2,2,2));
    }

    @Override
    public void animation() throws InterruptedException{
        while (true){
            if(!isAlive) break;

            if(timePassed % MOVEMENT_TIMER == 0) {
                currentPosition.y += STEP * orientation;
                countOfSteps += orientation;
                if (Math.abs(countOfSteps) == SCOPE) orientation *= -1;
                timePassed = 0;
                node.setLocalPosition(currentPosition);
            }

            rotate();

            Thread.sleep(ROTATION_TIMER);
            timePassed += ROTATION_TIMER;
        }
    }

    protected void rotate(){
        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 1)));
    }

}
