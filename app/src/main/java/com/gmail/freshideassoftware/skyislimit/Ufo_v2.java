package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

public class Ufo_v2 extends UfoModel {
    public static final int id = 6;

    public Ufo_v2(Context _context, Vector3 _position, Scene _scene) {
        super(_context, _position, "ufo_v2.sfb", _scene, 3, 9,6);
        position.y -= 0.05;
    }
}
