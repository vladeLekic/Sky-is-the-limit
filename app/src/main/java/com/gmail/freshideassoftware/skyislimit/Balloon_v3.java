package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

public class Balloon_v3 extends BalloonModel {
    public static final int id = 3;

    public Balloon_v3(Context _context, Vector3 _position, Scene _scene) {
        super(_context, _position, "hot_air_balloon_v3.sfb", _scene, 1, 7, 2);

        position.y -= 0.1;
    }
}