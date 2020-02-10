package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

public class Balloon_v2 extends BalloonModel {
    public static final int id = 2;

    public Balloon_v2(Context _context, Vector3 _position, Scene _scene) {
        super(_context, _position, "hot_air_balloon_v2.sfb", _scene, 2, 6, 2);

        position.y -= 0.1;
    }
}
