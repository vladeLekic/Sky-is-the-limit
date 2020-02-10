package com.gmail.freshideassoftware.skyislimit;

import android.app.Activity;
import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import java.util.*;

public abstract class BalloonModel extends FlyingModel {
    //num of threads
    protected int numOfThread = 0;

    protected float stepOfRotation = (float) 0.75;

    //layer count start
    protected static final int numOfLayers = 5;
    protected int currentLayer = 0;

    //field count start
    protected static final int numOfFileds = 7;
    protected int currentField = (numOfFileds+1)/2;

    //forward and back limits
    protected static final int scopeOfForwardMovement = 5;
    protected int currentScopeOfForwardMovement = 1;

    //number of times balloon rotate for returning in normal state
    protected int leftMovementRotation = 0;
    protected int rightMovementRotation = 0;
    protected int forwardMovementRotation = 0;
    protected int backMovementRotation = 0;

    protected long TIME_SIMULATION_OF_SPEED = (14 - speed) * 5;

    public BalloonModel(Context _context, Vector3 _position, String UriName, Scene _scene, int _damage, int _life, int _speed) {
        super(_context, _position, UriName, _scene, _damage, _life, _speed);
    }

    @Override
    public void moveLeft(){
        if(currentField==1) return;
        currentField--;

        new Thread(()->{
            synchronized (BalloonModel.this){
                numOfThread++;
            }

            try {
                for (int i = 0; i < 15; i++) {
                    position.x -= 0.01;

                    ((Activity) context).runOnUiThread(() -> {
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 0f, 1f), stepOfRotation)));
                        leftMovementRotation++;
                        node.setLocalPosition(position);
                    });
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                }

            }catch (InterruptedException e) { }



            while(leftMovementRotation>0){
                ((Activity) context).runOnUiThread(() -> {
                    synchronized (BalloonModel.this) {
                        if (leftMovementRotation <= 0) return;
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 0f, 1f), -stepOfRotation)));
                        leftMovementRotation--;
                    }
                });
                try {
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                } catch (InterruptedException e) { }
            }

            synchronized (BalloonModel.this){
                numOfThread--;
                if(numOfThread==0){
                    ((Activity) context).runOnUiThread(()->{
                        node.setLocalRotation(startRotation);
                    });
                };
            }

        }).start();
    }

    @Override
    public void moveRight(){
        if(currentField>=numOfFileds) return;
        currentField++;

        new Thread(()->{
            synchronized (BalloonModel.this){
                numOfThread++;
            }

            try {
                for (int i = 0; i < 15; i++) {
                    position.x += 0.01;

                    ((Activity) context).runOnUiThread(() -> {
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 0f, 1f), -stepOfRotation)));
                        rightMovementRotation++;
                        node.setLocalPosition(position);
                    });
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                }

            }catch (InterruptedException e) { }


            while(rightMovementRotation>0){
                ((Activity) context).runOnUiThread(() -> {
                    synchronized (BalloonModel.this) {
                        if(rightMovementRotation<=0) return;
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 0f, 1f), +stepOfRotation)));
                        rightMovementRotation--;
                    }
                });
                try {
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                } catch (InterruptedException e) { }
            }

            synchronized (BalloonModel.this){
                numOfThread--;
                if(numOfThread==0){
                    ((Activity) context).runOnUiThread(()->{
                        node.setLocalRotation(startRotation);
                    });
                };
            }

        }).start();
    }

    @Override
    public void moveForward(){
        if(currentScopeOfForwardMovement>=scopeOfForwardMovement) return;
        currentScopeOfForwardMovement++;

        new Thread(()->{
            synchronized (BalloonModel.this){
                numOfThread++;
            }

            try {

                for (int i = 0; i < 15; i++) {
                    position.z -= 0.01;

                    ((Activity) context).runOnUiThread(() -> {
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(1f, 0f, 0f), -stepOfRotation)));
                        forwardMovementRotation++;
                        node.setLocalPosition(position);
                    });
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                }

            }catch (InterruptedException e) { }


            while(forwardMovementRotation>0){
                ((Activity) context).runOnUiThread(() -> {
                    synchronized (BalloonModel.this) {
                        if(forwardMovementRotation<=0) return;
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(1f, 0f, 0f), +stepOfRotation)));
                        forwardMovementRotation--;
                    }
                });
                try {
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                } catch (InterruptedException e) { }
            }

            synchronized (BalloonModel.this){
                numOfThread--;
                if(numOfThread==0){
                    ((Activity) context).runOnUiThread(()->{
                        node.setLocalRotation(startRotation);
                    });
                };
            }

        }).start();
    }

    @Override
    public void moveBack(){
        if(currentScopeOfForwardMovement<=1) return;
        currentScopeOfForwardMovement--;

        new Thread(()->{
            synchronized (BalloonModel.this){
                numOfThread++;
            }

            try {

                for (int i = 0; i < 15; i++) {
                    position.z += 0.01;

                    ((Activity) context).runOnUiThread(() -> {
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(1f, 0f, 0f), +stepOfRotation)));
                        backMovementRotation++;
                        node.setLocalPosition(position);
                    });
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                }

            }catch (InterruptedException e) { }


            while(backMovementRotation>0){
                ((Activity) context).runOnUiThread(() -> {
                    synchronized (BalloonModel.this) {
                        if(backMovementRotation<=0) return;
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(1f, 0f, 0f), -stepOfRotation)));
                        backMovementRotation--;
                    }
                });
                try {
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                } catch (InterruptedException e) { }
            }

            synchronized (BalloonModel.this){
                numOfThread--;
                if(numOfThread==0){
                    ((Activity) context).runOnUiThread(()->{
                        node.setLocalRotation(startRotation);
                    });
                };

            }

        }).start();
    }

    @Override
    public void moveUp(){
        if(currentLayer==5 || node==null) return;
        currentLayer++;
        new Thread(()->{

            try {
                for(int i=0; i<5; i++) {

                    position.y += 0.04;

                    ((Activity) context).runOnUiThread(() -> {
                        node.setLocalPosition(position);
                    });

                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                }
            } catch (InterruptedException e) {e.printStackTrace();}


            synchronized (BalloonModel.this){
                numOfThread--;
                if(numOfThread==0){
                    ((Activity) context).runOnUiThread(()->{
                        node.setLocalRotation(startRotation);
                    });
                };
            }

        }).start();
    }

    @Override
    public void moveDown(){
        if(currentLayer==0 || currentLayer==1 || node==null) return;
        currentLayer--;

        new Thread(()->{

            try {
                for(int i=0; i<5; i++) {

                    position.y -= 0.04;

                    ((Activity) context).runOnUiThread(() -> {
                        node.setLocalPosition(position);
                    });

                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                }
            } catch (InterruptedException e) {e.printStackTrace();}

            synchronized (BalloonModel.this){
                numOfThread--;
                if(numOfThread==0){
                    ((Activity) context).runOnUiThread(()->{
                        node.setLocalRotation(startRotation);
                    });
                };
            }

        }).start();
    }
}
