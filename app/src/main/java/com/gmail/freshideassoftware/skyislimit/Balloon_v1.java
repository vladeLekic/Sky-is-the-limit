package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

public class Balloon_v1 extends BalloonModel {
    public static final int id = 1;


    public Balloon_v1(Context _context, Vector3 _position, Scene _scene) {
        super(_context, _position, "hot_air_balloon_v1.sfb", _scene, 2, 7,6);

        position.y -= 0.1;
    }
}
