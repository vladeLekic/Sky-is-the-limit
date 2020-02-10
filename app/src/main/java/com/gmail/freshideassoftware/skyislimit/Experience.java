package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class Experience {
    Node node;
    private Context context;
    private Vector3 position;
    private Scene scene;

    private TextView textViewExperience;

    private int exp = 0;

    public Experience(Context _context, Vector3 _position, Scene _scene){
        context = _context;
        position = new Vector3(_position);
        scene = _scene;
        //position.x += 1.2;
        position.z += 0.3;

        ViewRenderable.builder()
                .setView(context, R.layout.experience_model_view)
                .build()
                .thenAccept(viewRenderable -> addXPModelToScene(viewRenderable));
    }

    private void addXPModelToScene(ViewRenderable viewRenderable) {
        View view = viewRenderable.getView();
        node = new Node();

        textViewExperience = (TextView) view.findViewById(R.id.textViewExperience);
        textViewExperience.setText("0");

        node.setRenderable(viewRenderable);

        node.setLocalPosition(position);
        scene.addChild(node);
    }

    public void increaseExperience(int num){
        exp+=num;
        textViewExperience.setText(exp+"");
    }

    public int getExp(){
        return exp;
    }

    public void removeFromScreen(){
        scene.removeChild(node);
    }
}
