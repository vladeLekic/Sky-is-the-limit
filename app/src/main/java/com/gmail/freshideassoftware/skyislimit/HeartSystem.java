package com.gmail.freshideassoftware.skyislimit;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class HeartSystem {

    private int numOfLifes;
    private Vector3 position;
    private Context context;
    private Scene scene;

    private Node[] heartModels;
    private int currentLifes;

    public HeartSystem(int _numOfLifes, Context _context, Vector3 _position, Scene _scene){
        numOfLifes = _numOfLifes;
        position = _position;
        context = _context;
        scene = _scene;
        heartModels = new Node[numOfLifes];

        ModelRenderable.builder()
                .setSource(context, Uri.parse("heart_v1.sfb"))
                .build()
                .thenAccept(modelRenderable -> addHeartModelToScene(modelRenderable));


    }

    private void addHeartModelToScene(ModelRenderable modelRenderable) {

        position.x += 0.7;

        for(int i=0; i<numOfLifes; i++){

            Node node = new Node();
            heartModels[currentLifes++] = node;
            node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 1f, 0f), -90)));
            node.setRenderable(modelRenderable);
            node.setLocalPosition(position);
            scene.addChild(node);
            position.z -= 0.1;
        }

    }

    public int getCurrentLifes(){
        return currentLifes;
    }

    public boolean removeLife(){
        if(currentLifes==0) return false;

        currentLifes--;

        ((Activity)context).runOnUiThread(()->{
            scene.removeChild(heartModels[currentLifes]);
        });
        if(currentLifes==0) return false;
        return true;
    }
}
