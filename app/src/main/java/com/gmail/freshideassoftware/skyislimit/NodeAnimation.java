package com.gmail.freshideassoftware.skyislimit;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.UiThread;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

public abstract class NodeAnimation extends Thread {
    protected Node node;

    protected boolean isAlive = true;
    protected Vector3 startPosition, currentPosition;
    protected Quaternion startRotation, currentRotation;

    public NodeAnimation(Node _node){
        node = _node;

        startPosition = new Vector3(node.getLocalPosition());
        startRotation = new Quaternion(node.getLocalRotation());

        currentPosition = node.getLocalPosition();
        currentRotation = node.getLocalRotation();
    }

    public void run(){
        try {
            animation();
        } catch (InterruptedException e) { }

        node.setLocalScale(new Vector3(1,1,1));
        node.setLocalRotation(startRotation);
        node.setLocalPosition(startPosition);
    }

    public abstract void animation() throws InterruptedException;

    public void finish(){
        isAlive = false;
        this.interrupt();
    }
}
