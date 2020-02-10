package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;
import android.net.Uri;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;


public class SunAndMoonThread extends Thread {

    private final double Radius = 0.85;

    private Context context;
    private Vector3 pointOfRotation;
    private Scene scene;

    private Vector3 positionOfModel;
    private Node modelNode;

    private final String sun_source = "sun.sfb";
    private final String moon_source = "moon.sfb";
    private String current_source = sun_source;

    private double x1, x2, xc, yc, y;
    private boolean ableToContinueRotation = false;

    public SunAndMoonThread(Context _context, Vector3 _pointOfRotation, Scene _scene){
        super();
        context = _context;
        pointOfRotation = new Vector3(_pointOfRotation);
        pointOfRotation.z -= 0.3;
        scene = _scene;
    }

    public void run(){

        positionOfModel = new Vector3(pointOfRotation);
        positionOfModel.x += Radius;


        ((RaceActivity)context).runOnUiThread(()->{

            ModelRenderable.builder()
                    .setSource(context, Uri.parse(current_source))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(modelRenderable));
        });

        try{

            while(true){

                if(!ableToContinueRotation) continue;

                x1 = pointOfRotation.x - Radius;
                x2 = pointOfRotation.x + Radius;

                xc = pointOfRotation.x;
                yc = pointOfRotation.y;

                for(; x2>x1; x2-=0.01) {

                    ((RaceActivity)context).runOnUiThread(()->{

                            positionOfModel.x = (float) x2;
                            y = yc + Math.sqrt(Radius * Radius - (x2 - xc) * (x2 - xc));
                            positionOfModel.y = (float) y;
                            modelNode.setLocalPosition(positionOfModel);
                    });

                    sleep(100);
                }

                ableToContinueRotation = false;

                ((RaceActivity)context).runOnUiThread(()->{
                    scene.removeChild(modelNode);
                    if(current_source==sun_source) current_source = moon_source;
                    else current_source = sun_source;

                    ModelRenderable.builder()
                            .setSource(context, Uri.parse(current_source))
                            .build()
                            .thenAccept(modelRenderable -> addModelToScene(modelRenderable));
                });

            }

        } catch (InterruptedException e) { }

        if(modelNode!=null)
            ((RaceActivity)context).runOnUiThread(()->{
                 scene.removeChild(modelNode);
            });


    }

    private void addModelToScene(ModelRenderable modelRenderable) {
        Node node = new Node();
        modelNode = node;
        node.setRenderable(modelRenderable);
        node.setLocalPosition(positionOfModel);
        scene.addChild(node);
        ableToContinueRotation = true;
    }

}