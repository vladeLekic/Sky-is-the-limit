package com.gmail.freshideassoftware.skyislimit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.ArrayList;
import java.util.UUID;


public class TwoPlayerGameActivity extends AppCompatActivity {
    private ArFragment arFragment;

    private String enemyBulletsName = "enemyBullets", myBulletsName = "myBullets";

    private FlyingModel myFlyingModel = null;
    private FlyingModel enemyFlyingModel = null;
    private int enemyFlyingModelId = -1;
    private Vector3 startPosition = null;

    Node firstNode, secondNode;
    private boolean gameEnableToStart = false;

    private Button listen, listDevices;
    private ListView listView;

    private Button buttonMoveLeft, buttonMoveRight, buttonMoveForward, buttonMoveBack, buttonFire, buttonMoveUp, buttonMoveDown;

    private SendReceive sendReceive;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice[] btArray;


    static final byte COMMAND_MOVE_LEFT = 1;
    static final byte COMMAND_MOVE_RIGHT = 2;
    static final byte COMMAND_MOVE_FORWARD = 3;
    static final byte COMMAND_MOVE_BACK = 4;
    static final byte COMMAND_MOVE_UP = 5;
    static final byte COMMAND_MOVE_DOWN = 6;
    static final byte COMMAND_FIRE = 7;
    // for creating new enemy flying model use negativ numbers
    static final byte COMMAND_ENEMY_CREATED = 7;


    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "BTGame";
    private static final UUID MY_UUID = UUID.fromString("a39c6d43-aeeb-43e9-b4d3-18fb0f5de908");


    //Object for bullet manipulation
    private ArrayList<Node> bullets = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_game);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragmentTwoPlayers);

        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                if(!gameEnableToStart) return;
                if(sendReceive==null) return;
                if(myFlyingModel!=null) return;
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);

                startPosition = new Vector3(anchorNode.getLocalPosition());
                createMyFlyingModel(anchorNode.getLocalPosition());
                createEnemyFlyingModel(startPosition, enemyFlyingModelId);

                startGame();
                enableAllMovementButtons();
            }
        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }


        ViewRenderable.builder()
                .setView(this, R.layout.bluetooth_model_view)
                .build()
                .thenAccept(viewRenderable -> addViewToScene(viewRenderable));

        buttonMoveLeft = (Button) findViewById(R.id.buttonLeft);
        buttonMoveLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReceive.write(new byte[]{COMMAND_MOVE_LEFT});
                myFlyingModel.moveLeft();
            }
        });

        buttonMoveForward = (Button) findViewById(R.id.buttonForward);
        buttonMoveForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReceive.write(new byte[]{ COMMAND_MOVE_FORWARD});
                myFlyingModel.moveForward();
            }
        });

        buttonMoveRight = (Button) findViewById(R.id.buttonRight);
        buttonMoveRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReceive.write(new byte[]{COMMAND_MOVE_RIGHT});
                myFlyingModel.moveRight();
            }
        });

        buttonMoveBack = (Button) findViewById(R.id.buttonBack);
        buttonMoveBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReceive.write(new byte[]{COMMAND_MOVE_BACK});
                myFlyingModel.moveBack();
            }
        });

        buttonMoveUp = (Button) findViewById(R.id.buttonUp);
        buttonMoveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReceive.write(new byte[]{COMMAND_MOVE_UP});
                myFlyingModel.moveUp();
            }
        });

        buttonMoveDown = (Button) findViewById(R.id.buttonDown);
        buttonMoveDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReceive.write(new byte[]{COMMAND_MOVE_DOWN});
                myFlyingModel.moveDown();
            }
        });

        buttonFire = (Button) findViewById(R.id.buttonFire);
        buttonFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialFactory.makeOpaqueWithColor(TwoPlayerGameActivity.this, new Color(ContextCompat.getColor(TwoPlayerGameActivity.this, R.color.colorMain)))
                        .thenAccept(material -> {
                            ModelRenderable modelRenderable = ShapeFactory.makeSphere((float) 0.02, new Vector3(0,0,0), material);
                            placeModel(modelRenderable, myFlyingModel.position);
                        });
                sendReceive.write(new byte[]{COMMAND_FIRE});
            }
        });

        disableAllMovementButtons();
    }

    private void disableAllMovementButtons(){
        buttonMoveLeft.setVisibility(View.INVISIBLE);
        buttonMoveRight.setVisibility(View.INVISIBLE);
        buttonMoveForward.setVisibility(View.INVISIBLE);
        buttonMoveBack.setVisibility(View.INVISIBLE);
        //buttonFire.setVisibility(View.INVISIBLE);
        //buttonMoveUp.setVisibility(View.INVISIBLE);
        //buttonMoveDown.setVisibility(View.INVISIBLE);

        buttonMoveLeft.setEnabled(false);
        buttonMoveRight.setEnabled(false);
        buttonMoveForward.setEnabled(false);
        buttonMoveBack.setEnabled(false);
        buttonFire.setEnabled(false);
        buttonMoveUp.setEnabled(false);
        buttonMoveDown.setEnabled(false);
    }

    private void enableAllMovementButtons(){
        buttonMoveLeft.setVisibility(View.VISIBLE);
        buttonMoveRight.setVisibility(View.VISIBLE);
        buttonMoveForward.setVisibility(View.VISIBLE);
        buttonMoveBack.setVisibility(View.VISIBLE);
        buttonFire.setVisibility(View.VISIBLE);
        buttonMoveUp.setVisibility(View.VISIBLE);
        buttonMoveDown.setVisibility(View.VISIBLE);

        buttonMoveLeft.setEnabled(true);
        buttonMoveRight.setEnabled(true);
        buttonMoveForward.setEnabled(true);
        buttonMoveBack.setEnabled(true);
        buttonFire.setEnabled(true);
        buttonMoveUp.setEnabled(true);
        buttonMoveDown.setEnabled(true);
    }

    private void startGame() {

        new Thread(){

            public void run(){

                try {


                    while (true) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList<Node> toRemove = new ArrayList<>();

                                Scene scene = arFragment.getArSceneView().getScene();
                                synchronized (TwoPlayerGameActivity.this) {
                                    for (Node e : bullets) {
                                        if(e.getName().equals("erased")) continue;
                                        Vector3 pos = e.getLocalPosition();


                                        if(e.getName().equals(myBulletsName)) pos.z -= 0.03;
                                        else pos.z += 0.03;

                                        Node n = scene.overlapTest(e);
                                        if (n != null) {
                                            if(e.getName().equals(myBulletsName)) {
                                                for(int i=0; i<myFlyingModel.damage; i++){
                                                    enemyFlyingModel.lifeRemove();
                                                    if(enemyFlyingModel.getLife()==0){
                                                        endGame(true);
                                                        interrupt();
                                                    }
                                                }
                                            }
                                            else {
                                                for(int i=0; i<enemyFlyingModel.damage; i++){
                                                    myFlyingModel.lifeRemove();
                                                    if(myFlyingModel.getLife()==0) {
                                                        endGame(false);
                                                        interrupt();
                                                    }
                                                }
                                            }
                                            scene.removeChild(e);
                                            e.setName("erased");
                                            toRemove.add(e);
                                            //bullets.remove(e);
                                            continue;
                                        }

                                        if ((e.getName().equals(myBulletsName) && startPosition.z - pos.z > 3) ||
                                                (e.getName().equals(enemyBulletsName) && startPosition.z - pos.z < -3)) {
                                            arFragment.getArSceneView().getScene().removeChild(e);
                                            e.setName("erased");
                                            toRemove.add(e);
                                            //bullets.remove(e);
                                            continue;
                                        }


                                        e.setLocalPosition(pos);
                                    }

                                    for(Node e: toRemove)
                                        bullets.remove(e);

                                }
                            }
                        });

                        Thread.sleep(30);
                    }

                } catch (InterruptedException e) { }

                synchronized (TwoPlayerGameActivity.this) {
                    for (Node e : bullets) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                arFragment.getArSceneView().getScene().removeChild(e);
                            }
                        });
                    }
                }

                while(myFlyingModel.getLife()>0) myFlyingModel.lifeRemove();
                while(enemyFlyingModel.getLife()>0) enemyFlyingModel.lifeRemove();

            }

        }.start();
    }

    private void endGame(boolean b) {

        ViewRenderable.builder()
                .setView(TwoPlayerGameActivity.this, R.layout.end_two_player_game_model_view)
                .build()
                .thenAccept(viewRenderable -> {

                    View view = viewRenderable.getView();
                    TextView textView = (TextView) view.findViewById(R.id.textViewEndGame);

                    if(b) textView.setText("WIN!");
                    else textView.setText("LOSE!");

                    if(b)textView.setTextColor(ContextCompat.getColor(TwoPlayerGameActivity.this, R.color.colorMain));
                    else textView.setTextColor(ContextCompat.getColor(TwoPlayerGameActivity.this, R.color.colorAccent));

                    Node node = new Node();

                    node.setRenderable(viewRenderable);
                    node.setLocalPosition(new Vector3(this.startPosition));

                    arFragment.getArSceneView().getScene().addChild(node);


                    arFragment.getArSceneView().getScene().removeChild(myFlyingModel.node);
                    arFragment.getArSceneView().getScene().removeChild(enemyFlyingModel.node);
                });

    }

    private void placeModel(ModelRenderable modelRenderable, Vector3 position) {
        Node node = new Node();
        Vector3 pos = new Vector3(position);

        pos.z -= 0.1;
        pos.y += 0.1;

        node.setRenderable(modelRenderable);
        node.setLocalPosition(pos);

        arFragment.getArSceneView().getScene().addChild(node);
        synchronized (TwoPlayerGameActivity.this) {
            node.setName(myBulletsName);
            bullets.add(node);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        bluetoothAdapter.disable();
    }

//
//    private byte[] createBytesFromShort(short message) {
//        byte[] ret = new byte[2];
//
//        ret[0] = (byte) (message & 0xff);
//        ret[1] = (byte) ((message >> 8) & 0xff);
//        return  ret;
//    }

    private void addViewToScene(ViewRenderable viewRenderable) {
        View view = viewRenderable.getView();

        listView = (ListView) view.findViewById(R.id.listViewServers);
        listen = (Button) view.findViewById(R.id.buttonServer);
        listDevices = (Button) view.findViewById(R.id.buttonListServer);

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();
                arFragment.getArSceneView().getScene().removeChild(firstNode);
                addIdButton();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass = new ClientClass(btArray[i]);
                clientClass.start();
                arFragment.getArSceneView().getScene().removeChild(firstNode);
                addIdButton();
            }
        });


        Node node = new Node();

        node.setRenderable(viewRenderable);
        node.setWorldPosition(new Vector3(0, 0, -2));


        firstNode = node;

        arFragment.getArSceneView().getScene().addChild(node);
    }

    private void addIdButton() {

        ViewRenderable.builder()
                .setView(TwoPlayerGameActivity.this, R.layout.start_model_view)
                .build()
                .thenAccept(viewRenderable -> {
                   View view = viewRenderable.getView();


                    ImageView imageView = (ImageView) view.findViewById(R.id.imageViewStart);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            arFragment.getArSceneView().getScene().removeChild(secondNode);
                            gameEnableToStart = true;
                            sendReceive.write(new byte[]{(byte) (-1*Profile.getProfile().getId())});
                        }
                    });

                    Node node = new Node();

                    node.setRenderable(viewRenderable);
                    node.setWorldPosition(new Vector3(0,0,-1));

                    arFragment.getArSceneView().getScene().addChild(node);
                    secondNode = node;
                });

    }

    private class ServerClass extends Thread{
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket = null;

            while(socket==null){
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(socket!=null){
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1){
            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try {
                socket.connect();
                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private int bytes;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            bytes = 0;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run(){
            byte[] buffer = new byte[1];

            while(true){
                try {
                    bytes = inputStream.read(buffer);
                    short command = (short)(buffer[0]);
                    bytes = command;

                    if(bytes<0){
                        enemyFlyingModelId = -command;
                    }


                    if(enemyFlyingModel==null) continue;


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (bytes){
                                case COMMAND_MOVE_LEFT: enemyFlyingModel.moveRight(); break;
                                case COMMAND_MOVE_RIGHT: enemyFlyingModel.moveLeft(); break;
                                case COMMAND_MOVE_FORWARD: enemyFlyingModel.moveBack(); break;
                                case COMMAND_MOVE_BACK: enemyFlyingModel.moveForward(); break;
                                case COMMAND_MOVE_UP: enemyFlyingModel.moveUp(); break;
                                case COMMAND_MOVE_DOWN: enemyFlyingModel.moveDown(); break;
                                case COMMAND_FIRE: enemyFire(); break;
                            }
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enemyFire() {
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              MaterialFactory.makeOpaqueWithColor(TwoPlayerGameActivity.this, new Color(ContextCompat.getColor(TwoPlayerGameActivity.this, R.color.colorAccent)))
                      .thenAccept(material -> {
                          ModelRenderable modelRenderable = ShapeFactory.makeSphere((float) 0.02, new Vector3(0,0,0), material);
                          placeEnemyModel(modelRenderable, enemyFlyingModel.position);
                      });
          }
      });
    }

    private void placeEnemyModel(ModelRenderable modelRenderable, Vector3 position) {
        Node node = new Node();
        Vector3 pos = new Vector3(position);

        pos.z += 0.1;
        pos.y += 0.1;

        node.setRenderable(modelRenderable);
        node.setLocalPosition(pos);

        arFragment.getArSceneView().getScene().addChild(node);
        synchronized (TwoPlayerGameActivity.this) {
            node.setName(enemyBulletsName);
            bullets.add(node);
        }
    }

    private void createMyFlyingModel(Vector3 position) {
        int id = Profile.getProfile().getId();

        if(id==1) myFlyingModel = new Balloon_v1(this, position, arFragment.getArSceneView().getScene());
        else if(id==2) myFlyingModel = new Balloon_v2(this, position, arFragment.getArSceneView().getScene());
        else if(id==3) myFlyingModel = new Balloon_v3(this, position, arFragment.getArSceneView().getScene());
        else if(id==4) myFlyingModel = new Balloon_v4(this, position, arFragment.getArSceneView().getScene());
        else if(id==5) myFlyingModel = new Ufo_v1(this, position, arFragment.getArSceneView().getScene());
        else if(id==6) myFlyingModel = new Ufo_v2(this, position, arFragment.getArSceneView().getScene());
        else if(id==7) myFlyingModel = new Ufo_v3(this, position, arFragment.getArSceneView().getScene());
        else if(id==8) myFlyingModel = new Ufo_v4(this, position, arFragment.getArSceneView().getScene());
    }

    private void createEnemyFlyingModel(Vector3 position, int id){
        Vector3 myPosition = new Vector3(position);
        myPosition.z -= 1.75;

        if(id==1) enemyFlyingModel = new Balloon_v1(this, myPosition, arFragment.getArSceneView().getScene());
        else if(id==2) enemyFlyingModel = new Balloon_v2(this,  myPosition, arFragment.getArSceneView().getScene());
        else if(id==3) enemyFlyingModel = new Balloon_v3(this,  myPosition, arFragment.getArSceneView().getScene());
        else if(id==4) enemyFlyingModel = new Balloon_v4(this,  myPosition, arFragment.getArSceneView().getScene());
        else if(id==5) enemyFlyingModel = new Ufo_v1(this,  myPosition, arFragment.getArSceneView().getScene());
        else if(id==6) enemyFlyingModel = new Ufo_v2(this,  myPosition, arFragment.getArSceneView().getScene());
        else if(id==7) enemyFlyingModel = new Ufo_v3(this,  myPosition, arFragment.getArSceneView().getScene());
        else if(id==8) enemyFlyingModel = new Ufo_v4(this,  myPosition, arFragment.getArSceneView().getScene());

    }
}