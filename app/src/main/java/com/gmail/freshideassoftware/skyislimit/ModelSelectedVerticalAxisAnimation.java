package com.gmail.freshideassoftware.skyislimit;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

public class ModelSelectedVerticalAxisAnimation extends ModelSelectedAnimation {

    public ModelSelectedVerticalAxisAnimation(Node _node){
        super(_node);
        node.setLocalScale(new Vector3(2,2,2));
    }

    @Override
    protected void rotate(){
        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 0f, 1f), 1)));
    }
}
