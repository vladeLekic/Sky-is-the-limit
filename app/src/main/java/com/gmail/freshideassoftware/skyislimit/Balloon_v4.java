package com.gmail.freshideassoftware.skyislimit;

import android.app.Activity;
import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
public class Balloon_v4 extends BalloonModel {
    public static final int id = 4;

    public Balloon_v4(Context _context, Vector3 _position, Scene _scene) {
        super(_context, _position, "hot_air_balloon_v4.sfb", _scene,1,5, 4);



        new Thread(()->{
            try{
                while(node==null) Thread.sleep(1);
            }catch (InterruptedException e) { }

            node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(1f, 0f, 0f), -90)));
            this.startRotation = node.getLocalRotation();

        }).start();
    }


    @Override
    public void moveLeft(){
        if(currentField==1) return;
        currentField--;

        new Thread(()->{
            synchronized (Balloon_v4.this){
                numOfThread++;
            }

            try {
                for (int i = 0; i < 15; i++) {
                    position.x -= 0.01;

                    ((Activity) context).runOnUiThread(() -> {
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 1f, 0f), -1*stepOfRotation)));
                        leftMovementRotation++;
                        node.setLocalPosition(position);
                    });
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                }

            }catch (InterruptedException e) { }



            while(leftMovementRotation>0){
                ((Activity) context).runOnUiThread(() -> {
                    synchronized (Balloon_v4.this) {
                        if (leftMovementRotation <= 0) return;
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 1f, 0f), stepOfRotation)));
                        leftMovementRotation--;
                    }
                });
                try {
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                } catch (InterruptedException e) { }
            }

            synchronized (Balloon_v4.this){
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
            synchronized (Balloon_v4.this){
                numOfThread++;
            }

            try {
                for (int i = 0; i < 15; i++) {
                    position.x += 0.01;

                    ((Activity) context).runOnUiThread(() -> {
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 1f, 0f), stepOfRotation)));
                        rightMovementRotation++;
                        node.setLocalPosition(position);
                    });
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                }

            }catch (InterruptedException e) { }


            while(rightMovementRotation>0){
                ((Activity) context).runOnUiThread(() -> {
                    synchronized (Balloon_v4.this) {
                        if(rightMovementRotation<=0) return;
                        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 1f, 0f), -stepOfRotation)));
                        rightMovementRotation--;
                    }
                });
                try {
                    Thread.sleep(TIME_SIMULATION_OF_SPEED);
                } catch (InterruptedException e) { }
            }

            synchronized (Balloon_v4.this){
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