package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;
import android.net.Uri;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

public abstract class FlyingModel {
    protected Node node;
    protected Scene scene;
    protected Vector3 position;
    protected Context context;
    protected Quaternion startRotation = null;

    protected int damage;
    protected int life;
    protected int speed;

    protected HeartSystem heartSystem;

    public FlyingModel(Context _context, Vector3 _position, String UriName, Scene _scene, int _damage, int _life, int _speed){
        position = _position;
        context = _context;
        scene = _scene;

        damage = _damage;
        life = _life;
        speed = _speed;

        heartSystem = new HeartSystem(life, context, new Vector3(position), scene);

        ModelRenderable.builder()
                .setSource(context, Uri.parse(UriName))
                .build()
                .thenAccept(modelRenderable -> addModelToScene(modelRenderable));
    }

    public abstract void moveLeft();
    public abstract void moveRight();
    public abstract void moveForward();
    public abstract void moveBack();
    public abstract void moveUp();
    public abstract void moveDown();


    protected void addModelToScene(ModelRenderable modelRenderable){
        node = new Node();
        node.setRenderable(modelRenderable);
        startRotation = new Quaternion(node.getLocalRotation());
        node.setLocalPosition(position);
        scene.addChild(node);
    }

    public void lifeRemove(){
        heartSystem.removeLife();
    }

    public void removeFromScene(){
        scene.removeChild(node);
    }

    public int getLife(){
        return heartSystem.getCurrentLifes();
    }

    public static int getLife(int id){
        switch (id){
            case 1: return 7;
            case 2: return 6;
            case 3: return 7;
            case 4: return 5;
            case 5: return 8;
            case 6: return 9;
            case 7: return 10;
            case 8: return 8;
        }
        return -1;
    }

    public static int getSpeed(int id){
        switch (id){
            case 1: return 6;
            case 2: return 2;
            case 3: return 2;
            case 4: return 4;
            case 5: return 6;
            case 6: return 6;
            case 7: return 10;
            case 8: return 8;
        }
        return -1;
    }

    public static int getDamage(int id){
        switch (id){
            case 1: return 2;
            case 2: return 2;
            case 3: return 1;
            case 4: return 1;
            case 5: return 3;
            case 6: return 3;
            case 7: return 5;
            case 8: return 4;
        }
        return -1;
    }
}
