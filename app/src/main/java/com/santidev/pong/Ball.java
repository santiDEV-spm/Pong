package com.santidev.pong;

import android.graphics.RectF;

import java.util.Random;

public class Ball {

    private RectF mRect;
    private float mVelX, mVelY;
    private float mBallWidth, mBallHeight;

    public Ball(int screenX, int screenY){

        //hacemos el tamanio de la bola dinamico en relacion a la resolucion de la pantalla.
        this.mBallWidth = screenX / 100;
        this.mBallHeight = this.mBallWidth;

        //al inicio, hacemos viajar la pelota de modo que por segundo recorra
        // 1/4 de la altura de la pantalla
        mVelX = screenX / 4;
        mVelY = mVelX;

        //inicializamos el rectangulo que representa la zona de la bola
        mRect = new RectF();

    }

    /**Obtenemos una referencia al RectF que representa la localizacion de la bola para luego gestionar colisiones**/
    public RectF getRect(){
        return mRect;
    }

    public void update(long fps){
        //Sn = So + V x t
        float t = 1/fps;
        //aplicamos las formulas de la fisica en eje X y el eje Y por separado
        mRect.left = mRect.left + mVelX * t; //eje x
        mRect.top = mRect.top + mVelY * t; // eje y
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;
    }

    //invertir la velocidad cuando chocamos el borde superior e inferior
    public void  reverseYVelocity(){
        mVelY = - mVelY;
    }

    //invertir la velocidad horizontal cuando chocamos con alguna de las dos palas
    public void reverseXVelocity(){
        mVelX = -mVelX;
    }

    //Acelerar aleatoriamente la bola
    public void setRandomXVelociy(){
        Random random = new Random();
        int addVelocity = random.nextInt(2);
        if(addVelocity == 0){
            reverseXVelocity();
        }
    }

    //vamos a subir la velocidad un 10% cada vez que choque con la pala/ cada vez q se marque un gol
    public void increaseVelocity(){
        mVelX *= 1.1f;
        mVelY *= 1.1f;
    }

    public void clearObstacleY(float y){
        mRect.bottom = y;
        mRect.top = y - mBallHeight;
    }

    public void clearObstacleX(float x){
        mRect.left = x;
        mRect.right = x + mBallWidth;
    }

    public void reset(int x, int y){
        clearObstacleX(x/2);
        clearObstacleY(y-20);
    }
}
