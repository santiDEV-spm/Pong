package com.santidev.pong;

import android.graphics.RectF;

public class Paddle {

    //RectF con las cuatro coordenadas de la pala
    private RectF mRect;
    //tamanio de la pala
    private float mLength, mHeight;
    //extremo izquierdo y superior de la pala
    private float mXCoord, mYcoord;
    //cuantos pixeles por segundo puede moverse la pala
    private float mPaddleSpeed;

    //Estaddos posibles de la pala
    public final  int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    public int mPaddleMoving = STOPPED;

    //Los bordes de la pantalla para no salirnos de ella
    private int mScreenX, mScreenY;

    public Paddle(int x, int y){
        //definimos los bordes de la pantalla segun los parametros del constructor
        mScreenX = x;
        mScreenY = y;

        //definimos los tamanios de la pala en un porcentaje de los globales de la pantalla
        mLength = mScreenX/8;
        mHeight = mScreenY / 25;

        //ponemos la pala al iniciar en el centro de la pantalla
        mXCoord = mScreenX / 2;
        mYcoord = mScreenY - 20;
        mRect = new RectF(mXCoord, mYcoord, mXCoord + mLength, mYcoord + mHeight);
        //definimos la velociddad inicial de la pala
        mPaddleSpeed = mScreenX;
    }

    //metodo para recuperar el rectangulo global de la pala...
    public RectF getRect(){
        return this.mRect;
    }

    //metodo para cambiar el estado de la pala
    public void setMovementState(int state){
        mPaddleMoving = state;
    }

    //lamaremos al update para mover la pala al pixel que toque segun el tipo de movimiento
    public void update(long fps){
        //Sf = So +/- V x t;
        float t = 1/fps;
        if(mPaddleMoving  == LEFT){
            mXCoord = mXCoord - mPaddleSpeed * t;
        }
        if(mPaddleMoving == RIGHT){
            mXCoord = mXCoord + mPaddleSpeed * t;
        }
        //despues del movimiento del usuario debemos asegurarnos que no nos salimos de la pantalla
        if(mRect.left < 0){
            mXCoord = 0;
        }
        if(mRect.right > mScreenX){
            mXCoord = mScreenX - mRect.right-mRect.left;
        }

        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }


}
