package com.gmail.freshideassoftware.skyislimit;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import java.util.ArrayList;

public class ModelGalleryActivity extends AppCompatActivity {
    private static final boolean enableExpSystem = true;

    private ArFragment arFragment;
    private ProgressBar progressBarLife, progressBarSpeed, progressBarDamage;

    private NodeAnimation nodeAnimationThread;
    private Node nodeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_gallery);

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.arFragmentModelGallery);

        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);

        //list sfb files of all models
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("hot_air_balloon_v1.sfb");
        arrayList.add("hot_air_balloon_v2.sfb");
        arrayList.add("hot_air_balloon_v3.sfb");
        arrayList.add("hot_air_balloon_v4.sfb");
        arrayList.add("ufo_v1.sfb");
        arrayList.add("ufo_v2.sfb");
        arrayList.add("ufo_v3.sfb");
        arrayList.add("ufo_v4.sfb");

        ArrayList<Long> arrayListExp = new ArrayList<>();
        arrayListExp.add((long) 100000);
        arrayListExp.add((long) 0);
        arrayListExp.add((long) 10000);
        arrayListExp.add((long) 50000);
        arrayListExp.add((long) 200000);
        arrayListExp.add((long) 500000);
        arrayListExp.add((long) 1000000);
        arrayListExp.add((long) 800000);

        ArrayList<String> arrayListExpTitle = new ArrayList<>();
        arrayListExpTitle.add("100 000");
        arrayListExpTitle.add("0");
        arrayListExpTitle.add("10 000");
        arrayListExpTitle.add("50 000");
        arrayListExpTitle.add("200 000");
        arrayListExpTitle.add("500 000");
        arrayListExpTitle.add("1 000 000");
        arrayListExpTitle.add("800 000");

        //Radius of circle
        float Radius = (float) 1;


        addViews(Radius);

        if(!enableExpSystem) addCircleShapeModelGallery(Radius, arrayList);
        else addCircleShapeModelGallery(Radius, arrayList, arrayListExp, arrayListExpTitle);
    }

    private void addCircleShapeModelGallery(float Radius, ArrayList<String> arrayList) {

        int count_of_model = 0;
        double current_s = 0, s = Radius * Math.PI;
        double step = Radius * Math.PI / (arrayList.size() - 1);

        for(int i=0; i<arrayList.size(); i++){
            String name = (i + 1) + "";
            double finalX, finalZ;

            double teta = Math.PI * i / (arrayList.size() - 1);
            double x = Math.cos(teta) * Radius;
            finalZ = Math.sin(teta) * Radius * -1;

            if(current_s<s/2){
                x *= -1;
            }

            finalX = x;

            ModelRenderable.builder()
                    .setSource(this, Uri.parse(arrayList.get(i)))
                    .build()
                    .thenAccept(modelRenderable -> addBalloonModelToScene(modelRenderable, new Vector3((float)finalX, (float)-0.1, (float)(-0.5 + finalZ)), name));
            s += step;
        }
    }

    private void addCircleShapeModelGallery(float Radius, ArrayList<String> arrayList, ArrayList<Long> arrayListExp, ArrayList<String> arrayListExpTitle) {

        int count_of_model = 0;
        double current_s = 0, s = Radius * Math.PI;
        double step = Radius * Math.PI / (arrayList.size() - 1);

        for(int i=0; i<arrayList.size(); i++){
            String name = (i + 1) + "";
            String title = arrayListExpTitle.get(i);
            long exp = arrayListExp.get(i);
            double finalX, finalZ;

            double teta = Math.PI * i / (arrayList.size() - 1);
            double x = Math.cos(teta) * Radius;
            finalZ = Math.sin(teta) * Radius * -1;

            if(current_s<s/2){
                x *= -1;
            }

            finalX = x;

            ModelRenderable.builder()
                    .setSource(this, Uri.parse(arrayList.get(i)))
                    .build()
                    .thenAccept(modelRenderable -> addBalloonModelToSceneWithExp(modelRenderable, new Vector3((float)finalX, (float)-0.1, (float)(-0.5 + finalZ)), name, arrayListExp));

            ViewRenderable.builder()
                    .setView(this, R.layout.experience_model_view)
                    .build()
                    .thenAccept(viewRenderable -> addViewExpToScene(viewRenderable, new Vector3((float)finalX, (float)-0.1, (float)(-0.5 + finalZ)), name, exp, (90 - teta * 180 / Math.PI), title));

            s += step;
        }
    }

    private void addBalloonModelToSceneWithExp(ModelRenderable modelRenderable, Vector3 position, String name, ArrayList<Long> arrayListExp) {
        Node node = new Node();

        if(name.equals("4")){
            node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(1f, 0f, 0f), -90)));
            position.y += 0.1;
        }

        node.setRenderable(modelRenderable);
        node.setWorldPosition(position);
        node.setName(name);

        if(name.equals(Profile.getProfile().getId()+"")){
            nodeSelected = node;
            nodeAnimationThread = new ModelSelectedAnimation(node);
            nodeAnimationThread.start();
        }


        node.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if(nodeSelected.equals(node)) return;

                int id = Integer.parseInt(node.getName());
                if(arrayListExp.get(id-1)>MainActivity.getExperience()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ModelGalleryActivity.this, "Make more experience", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }


                nodeAnimationThread.finish();

                if(name.equals("4")){
                    nodeAnimationThread = new ModelSelectedVerticalAxisAnimation(node);
                }
                else nodeAnimationThread = new ModelSelectedAnimation(node);

                nodeAnimationThread.start();
                nodeSelected = node;

                Profile.getProfile().setId(id);

                progressBarLife.setProgress(FlyingModel.getLife(id));
                progressBarSpeed.setProgress(FlyingModel.getSpeed(id));
                progressBarDamage.setProgress(FlyingModel.getDamage(id));
            }
        });

        arFragment.getArSceneView().getScene().addChild(node);
    }

    private void addViewExpToScene(ViewRenderable viewRenderable, Vector3 pos, String name, long exp, double teta, String title) {
        Node node = new Node();

        pos.y -= 0.2;

        node.setWorldPosition(pos);
        node.setRenderable(viewRenderable);
        node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(0f, 1f, 0f), (float) teta)));


        View view = viewRenderable.getView();
        TextView textView = (TextView) view.findViewById(R.id.textViewExperience);
        textView.setText(title);

        arFragment.getArSceneView().getScene().addChild(node);
    }


    private void addViews(float Radius) {

        ViewRenderable.builder()
                .setView(this, R.layout.gallery_model_view)
                .build()
                .thenAccept(viewRenderable -> addViewToScene(viewRenderable));


        ViewRenderable.builder()
                .setView(this, R.layout.experience_gallery_model_view)
                .build()
                .thenAccept(viewRenderable -> addExperienceViewToScene(viewRenderable, Radius));

    }

    private void addExperienceViewToScene(ViewRenderable viewRenderable, float Radius) {
        View view = viewRenderable.getView();

        TextView textView = (TextView) view.findViewById(R.id.textViewExperienceGallery);
        textView.setText(MainActivity.getExperience() + "");

        Vector3 position = new Vector3();
        position.z -= Radius;

        Node node =  new Node();
        node.setRenderable(viewRenderable);
        node.setWorldPosition(new Vector3(0, (float)(-0.4), -1));

        arFragment.getArSceneView().getScene().addChild(node);
    }

    private void addViewToScene(ViewRenderable viewRenderable) {
        View view = viewRenderable.getView();
        progressBarLife = (ProgressBar) view.findViewById(R.id.progressBarLife);
        progressBarLife.setMax(10);
        progressBarLife.setProgress(FlyingModel.getLife(Profile.getProfile().getId()));

        progressBarSpeed = (ProgressBar) view.findViewById(R.id.progressBarSpeed);
        progressBarSpeed.setMax(10);
        progressBarSpeed.setProgress(FlyingModel.getSpeed(Profile.getProfile().getId()));

        progressBarDamage = (ProgressBar) view.findViewById(R.id.progressBarDamage);
        progressBarDamage.setMax(5);
        progressBarDamage.setProgress(FlyingModel.getDamage(Profile.getProfile().getId()));

        Node node = new Node();

        node.setRenderable(viewRenderable);
        node.setWorldPosition(new Vector3(0, -1, -1));

        arFragment.getArSceneView().getScene().addChild(node);

    }

    private void addBalloonModelToScene(ModelRenderable modelRenderable, Vector3 position, String name) {
        Node node = new Node();

        if(name.equals("4")){
            node.setLocalRotation(Quaternion.multiply(node.getLocalRotation(), Quaternion.axisAngle(new Vector3(1f, 0f, 0f), -90)));
            position.y += 0.1;
        }

        node.setRenderable(modelRenderable);
        node.setWorldPosition(position);
        node.setName(name);

        if(name.equals(Profile.getProfile().getId()+"")){
            nodeSelected = node;
            nodeAnimationThread = new ModelSelectedAnimation(node);
            nodeAnimationThread.start();
        }


        node.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if(nodeSelected.equals(node)) return;
                nodeAnimationThread.finish();

                if(name.equals("4")){
                    nodeAnimationThread = new ModelSelectedVerticalAxisAnimation(node);
                }
                else nodeAnimationThread = new ModelSelectedAnimation(node);

                nodeAnimationThread.start();
                nodeSelected = node;

                int id = Integer.parseInt(node.getName());
                Profile.getProfile().setId(id);

                progressBarLife.setProgress(FlyingModel.getLife(id));
                progressBarSpeed.setProgress(FlyingModel.getSpeed(id));
                progressBarDamage.setProgress(FlyingModel.getDamage(id));
            }
        });

        arFragment.getArSceneView().getScene().addChild(node);
    }
}
