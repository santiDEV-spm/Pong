package com.santidev.pong;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ManinActivity";

    //Creamos una variable de tipo game view que se encarge de gestionanr la logica
    // del videojuego y responder a eventos de touch en la pantalla

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creamos una referencia a la pantalla a traves del manager
        Display display = getWindowManager().getDefaultDisplay();
        //nos quedamos con el tamanio de la pantalla en una variable de tipo punto
        Point size = new Point();
        display.getSize(size);

        Log.d(TAG, "Tamanio de la pantalla: " + size.x+" , " + size.y);
        gameView = new GameView(this, size.x, size.y);
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}