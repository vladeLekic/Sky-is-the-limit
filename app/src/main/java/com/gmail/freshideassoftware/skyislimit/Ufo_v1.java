package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

public class Ufo_v1 extends  UfoModel {
    public static final int id = 5;


    public Ufo_v1(Context _context, Vector3 _position, Scene _scene) {
        super(_context, _position, "ufo_v1.sfb", _scene, 3, 8,6);
        position.y -= 0.05;
    }
}
