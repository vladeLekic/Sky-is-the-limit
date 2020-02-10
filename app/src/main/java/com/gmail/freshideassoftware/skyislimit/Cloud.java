package com.gmail.freshideassoftware.skyislimit;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

public class Cloud {
    private static final double DISTANCE_TO_MOVE = 0.05;

    private static final int NUM_OF_FILEDS = 7;

    private static Vector3 startPosition = null;
    private Vector3 position;
    private Node node;
    private Scene scene;

    public Cloud(Node _node, Scene _scene){
        node = _node;
        scene = _scene;
        if(startPosition!=null) {
            position = new Vector3(startPosition);
            double x = (5 - Math.random()*9)  * 0.15;
            position.x += x;
            position.y += 0.2;
            position.z -= 1.5;
        }

        node.setLocalPosition(position);
    }

    public Node getNode(){
        return node;
    }

    public Vector3 getPosition(){
        return position;
    }

    public static void setStartPosition(Vector3 _position){
        startPosition = _position;
    }

    public void moveRight(){
        position.x += DISTANCE_TO_MOVE;
        node.setLocalPosition(position);
    }

    public void moveLeft(){
        position.x -= DISTANCE_TO_MOVE;
        node.setLocalPosition(position);
    }

    public void moveForward(){
        position.z += DISTANCE_TO_MOVE;
        node.setLocalPosition(position);
    }

    public void removeFromScene(){
        scene.removeChild(node);
    }

}
