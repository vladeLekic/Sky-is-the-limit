package com.gmail.freshideassoftware.skyislimit;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

import java.util.ArrayList;

public class RaceActivity extends AppCompatActivity {

    private boolean gameTerminate = false;

    private ArFragment arFragment;
    private int x = 0;

    private boolean flyingModelPlaced = false;
    private FlyingModel flyingModel = null;

    private Node startButtonNode = null;
    private Node endGameModelNode = null;
    private Vector3 endGameModelPosition = null;

    private ArrayList<Cloud> listOfClouds;
    private Vector3 positionCloudStart = new Vector3();
    private SunAndMoonThread sunAndMoonThread = null;
    private Cloud cloud;
    private Thread creatingCloudsThread = null;

    private int numOfCloudsCreated = 0;
    private int level = 1;

    private Experience experience = null;

    private Button leftButton, rightButton, forwardButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        listOfClouds = new ArrayList<>();

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                if(flyingModelPlaced) return;
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);

                experience = new Experience(RaceActivity.this, anchorNode.getLocalPosition(), arFragment.getArSceneView().getScene());
                sunAndMoonThread = new SunAndMoonThread(RaceActivity.this, new Vector3(anchorNode.getLocalPosition()), arFragment.getArSceneView().getScene());
                sunAndMoonThread.start();

                positionCloudStart.x = anchorNode.getLocalPosition().x;
                positionCloudStart.y = anchorNode.getLocalPosition().y;
                positionCloudStart.z = anchorNode.getLocalPosition().z;

                endGameModelPosition = new Vector3(anchorNode.getLocalPosition());

                Cloud.setStartPosition(positionCloudStart);

                createFlyingModel(anchorNode.getLocalPosition());
                flyingModelPlaced = true;

                ViewRenderable.builder()
                        .setView(RaceActivity.this, R.layout.start_model_view)
                        .build()
                        .thenAccept(viewRenderable -> {
                           Node node = new Node();

                           node.setRenderable(viewRenderable);
                           Vector3 pos = new Vector3(positionCloudStart);
                           pos.z -= 1;
                           node.setLocalPosition(pos);

                           View view = viewRenderable.getView();
                           ImageView imageView = (ImageView) view.findViewById(R.id.imageViewStart);

                           startButtonNode = node;

                           arFragment.getArSceneView().getScene().addChild(node);

                           imageView.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   startNewGame();
                                   arFragment.getArSceneView().getScene().removeChild(startButtonNode);
                                   startButtonNode = null;
                               }
                           });

                        });
            }
        });

        Button button;

        button = (Button) findViewById(R.id.buttonLeft);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flyingModel.moveLeft();
            }
        });
        leftButton = button;

        button = (Button) findViewById(R.id.buttonRight);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flyingModel.moveRight();
            }
        });
        rightButton = button;

        button = (Button) findViewById(R.id.buttonForward);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flyingModel.moveForward();
            }
        });
        forwardButton = button;

        button = (Button) findViewById(R.id.buttonBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flyingModel.moveBack();
            }
        });
        backButton = button;

        disableAllButtons();
    }

    private void disableAllButtons() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftButton.setVisibility(View.INVISIBLE);
                rightButton.setVisibility(View.INVISIBLE);
                forwardButton.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.INVISIBLE);

                leftButton.setEnabled(false);
                rightButton.setEnabled(false);
                forwardButton.setEnabled(false);
                backButton.setEnabled(false);
            }
        });
    }

    private void enableAllButtons(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                forwardButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);

                leftButton.setEnabled(true);
                rightButton.setEnabled(true);
                forwardButton.setEnabled(true);
                backButton.setEnabled(true);
            }
        });
    }

    private void startNewGame() {

        enableAllButtons();

        flyingModel.moveUp();
        gameTerminate = false;
        creatingCloudsThread = new Thread (
                () -> {
                    while(true) {
                        runOnUiThread(()->{
                            experience.increaseExperience(2*level);

                            ModelRenderable
                                    .builder()
                                    .setSource(RaceActivity.this, Uri.parse("cloud_v1.sfb"))
                                    .build()
                                    .thenAccept(modelRenderable -> {
                                        Node node = new Node();
                                        node.setRenderable(modelRenderable);
                                        node.setLocalPosition(positionCloudStart);
                                        arFragment.getArSceneView().getScene().addChild(node);
                                        numOfCloudsCreated++;
                                        cloud = new Cloud(node, arFragment.getArSceneView().getScene());
                                        listOfClouds.add(cloud);
                                        if(numOfCloudsCreated>=30){
                                            level++;
                                            numOfCloudsCreated-=30;
                                        }
                                    });
                        });
                        try {
                            if(1500/level < 300) Thread.sleep(300);
                            else Thread.sleep(1500/level);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(RaceActivity.this.gameTerminate) break;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for(Cloud e: listOfClouds)
                                e.removeFromScene();

                        }
                    });

                }
        );
        creatingCloudsThread.start();


        new Thread (
                () -> {
                    while(true){
                        runOnUiThread(()->{
                            for(Cloud e:listOfClouds){
                                e.moveForward();
                                Vector3 positionOfCloud = e.getPosition();

                                Node n = null;


                                if(e.getPosition().z-0.03<=flyingModel.position.z && e.getPosition().z+0.03>=flyingModel.position.z)
                                    n = arFragment.getArSceneView().getScene().overlapTest(flyingModel.node);

                                if(n!=null){
                                    flyingModel.lifeRemove();
                                    if(flyingModel.getLife()>0) {
                                        arFragment.getArSceneView().getScene().removeChild(e.getNode());
                                    }
                                    else{
                                        experience.removeFromScreen();
                                        RaceActivity.this.gameTerminate = true;
                                    }
                                }

                                if((positionOfCloud.z-flyingModel.node.getLocalPosition().z)>1) {
                                    e.removeFromScene();
                                }
                            }

                        });
                        try {
                            if(100-level*level<20) Thread.sleep(20);
                            else Thread.sleep(100 - level * level);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(RaceActivity.this.gameTerminate) break;
                    }

                    terminateGame();

                }
        ).start();

    }

    private void terminateGame() {
        disableAllButtons();

        numOfCloudsCreated = 0;
        level = 1;

        creatingCloudsThread.interrupt();
        sunAndMoonThread.interrupt();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arFragment.getArSceneView().getScene().removeChild(experience.node);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewRenderable
                        .builder()
                        .setView(RaceActivity.this, R.layout.end_race_model_view)
                        .build()
                        .thenAccept(viewRenderable -> {
                            Node node = new Node();
                            View view = viewRenderable.getView();

                            TextView textView = view.findViewById(R.id.textViewExperienceDisplay);
                            textView.setText(experience.getExp() + "");



                            ImageView imageView = view.findViewById(R.id.imageViewRestart);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    arFragment.getArSceneView().getScene().removeChild(node);


                                    experience = new Experience(RaceActivity.this, new Vector3(endGameModelPosition), arFragment.getArSceneView().getScene());
                                    sunAndMoonThread = new SunAndMoonThread(RaceActivity.this, new Vector3(endGameModelPosition), arFragment.getArSceneView().getScene());
                                    sunAndMoonThread.start();

                                    Cloud.setStartPosition(positionCloudStart);
                                    createFlyingModel(new Vector3(endGameModelPosition));


                                    ViewRenderable.builder()
                                            .setView(RaceActivity.this, R.layout.start_model_view)
                                            .build()
                                            .thenAccept(viewRenderable -> {
                                                Node node = new Node();

                                                node.setRenderable(viewRenderable);
                                                Vector3 pos = new Vector3(positionCloudStart);
                                                pos.z -= 1;
                                                node.setLocalPosition(pos);


                                                View view1 = viewRenderable.getView();
                                                ImageView imageView = (ImageView) view1.findViewById(R.id.imageViewStart);

                                                startButtonNode = node;

                                                arFragment.getArSceneView().getScene().addChild(node);

                                                imageView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        startNewGame();
                                                        arFragment.getArSceneView().getScene().removeChild(startButtonNode);
                                                        startButtonNode = null;
                                                    }
                                                });

                                            });
                                }
                            });

                            node.setRenderable(viewRenderable);
                            Vector3 pos = new Vector3(positionCloudStart);
                            pos.z -= 1;
                            node.setLocalPosition(pos);
                            arFragment.getArSceneView().getScene().addChild(node);
                            endGameModelNode = node;
                        });

                int expToAdd = MainActivity.getExperience();
                expToAdd += experience.getExp();
                MainActivity.setExperience(expToAdd);

                flyingModel.removeFromScene();
                flyingModel = null;
            }
        });
  }


    private void createFlyingModel(Vector3 position) {
        int id = Profile.getProfile().getId();

        if(id==1) flyingModel = new Balloon_v1(RaceActivity.this, position, arFragment.getArSceneView().getScene());
        else if(id==2) flyingModel = new Balloon_v2(RaceActivity.this, position, arFragment.getArSceneView().getScene());
            else if(id==3) flyingModel = new Balloon_v3(RaceActivity.this, position, arFragment.getArSceneView().getScene());
                else if(id==4) flyingModel = new Balloon_v4(RaceActivity.this, position, arFragment.getArSceneView().getScene());
                    else if(id==5) flyingModel = new Ufo_v1(RaceActivity.this, position, arFragment.getArSceneView().getScene());
                        else if(id==6) flyingModel = new Ufo_v2(RaceActivity.this, position, arFragment.getArSceneView().getScene());
                            else if(id==7) flyingModel = new Ufo_v3(RaceActivity.this, position, arFragment.getArSceneView().getScene());
                                else if(id==8) flyingModel = new Ufo_v4(RaceActivity.this, position, arFragment.getArSceneView().getScene());
    }
}
