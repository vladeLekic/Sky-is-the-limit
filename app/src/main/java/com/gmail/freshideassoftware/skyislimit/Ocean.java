package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;
import android.net.Uri;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class Ocean {
    private static final double distance_x = 0.36;
    private static final double distance_z = 0.29;
    private static final int NUM_OF_ROWS = 5;
    private static final int NUM_OF_COLUMNS = 3;

    private Context context;
    private Vector3 position;
    private Scene scene;

    private Vector3[][] positions;

    public Ocean(Context _context, Vector3 _position, Scene _scene){
        scene = _scene;
        context = _context;
        position = _position;
        position.y -= 0.2;

        positions = new Vector3[NUM_OF_ROWS][NUM_OF_COLUMNS];
        for(int i=0; i<NUM_OF_ROWS; i++){
            double z_dist = (i-(NUM_OF_ROWS-1)/2) * distance_z;
            for(int j=0; j<NUM_OF_COLUMNS; j++){
                positions[i][j] = new Vector3(position);
                double x_dist = (j-(NUM_OF_COLUMNS-1)/2) * distance_x;
                positions[i][j].z += z_dist;
                positions[i][j].x += x_dist;
            }
        }

        for(int i=0; i<NUM_OF_ROWS; i++) {
            int finalI = i;
            for (int j = 0; j < NUM_OF_COLUMNS; j++) {
                int finalJ = j;
                ModelRenderable.builder()
                        .setSource(context, Uri.parse("ocean.sfb"))
                        .build()
                        .thenAccept(modelRenderable -> addOceanModelToScene(modelRenderable, finalI, finalJ));
            }
        }
    }

    private void addOceanModelToScene(ModelRenderable modelRenderable, int i, int j) {
        Node node = new Node();
        node.setRenderable(modelRenderable);
        node.setLocalPosition(positions[i][j]);
        scene.addChild(node);
    }

}
