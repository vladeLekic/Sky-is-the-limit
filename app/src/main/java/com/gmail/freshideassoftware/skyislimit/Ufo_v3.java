package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

public class Ufo_v3 extends UfoModel {
    public static final int id = 7;

    public Ufo_v3(Context _context, Vector3 _position, Scene _scene) {
        super(_context, _position, "ufo_v3.sfb", _scene, 5, 10,10);
        position.y -= 0.02;
    }
}
