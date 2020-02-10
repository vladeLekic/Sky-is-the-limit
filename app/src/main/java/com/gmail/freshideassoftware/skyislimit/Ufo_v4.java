package com.gmail.freshideassoftware.skyislimit;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;

public class Ufo_v4 extends UfoModel {
    public static final int id = 8;


    public Ufo_v4(Context _context, Vector3 _position, Scene _scene) {
        super(_context, _position, "ufo_v4.sfb", _scene, 4, 8,8);
    }
}
