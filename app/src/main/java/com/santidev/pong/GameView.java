package com.santidev.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

public class GameView extends SurfaceView implements Runnable {

    //hilo de ejecucuion del juego
    Thread mGameThread = null;
    //Surface que nos permita dibujar en ella
    SurfaceHolder mSurfaceHolder;

    //Booleana para saber si el juego esta en ejecucion o no
    volatile boolean mIsPlaying;

    // esta variable nos sirve para saber si el juego esta pausado actualmente
    boolean mPaused = true;
    //canvas y pintura para el dibujo
    Canvas mCanvas;
    Paint mPaint;

    //Guardar los FPS del videojuego
    long mFPS;

    //tamanio de la pantalla pixeles
    int mScreenX, mScreenY;
    //Variablrs de los jugadores
    Paddle mPaddle;
    Ball mBall;

    //Efectos de sonidos
    SoundPool sp;
    int beed1ID = -1;
    int beed2ID = -1;
    int beed3ID = -1;
    int loseLiveID = -1;

    //variables de juego
    int mScore = 0;
    int mLives = 3;

    public GameView(Context context, int x, int y) {
        super(context);
        //inicializar los tamanio de la pantalla / juego
        mScreenX = x;
        mScreenY = y;

        //inicializar objetos de dibujo
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        //variables del jugador
        mPaddle = new Paddle(mScreenX, mScreenY);
        mBall = new Ball(mScreenX, mScreenY);

        //carga de sonidos
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //si es posteior a lollipop
            AudioAttributes attrs = new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(attrs)
                    .build();
        }else {
            //si es anterior a lollipop
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }

        //tenemos el sound pool inicializado -> podemos cargar lo 4 audios
        try {
            //manager q nos ayuda a acceder a la carpeta assets
            AssetManager manager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = manager.openFd("beep1.ogg");
            beed1ID = sp.load(descriptor, 0);

            descriptor = manager.openFd("beep2.ogg");
            beed2ID = sp.load(descriptor, 0);

            descriptor = manager.openFd("beep3.ogg");
            beed3ID = sp.load(descriptor, 0);

            descriptor = manager.openFd("loseLife.ogg");
            loseLiveID = sp.load(descriptor, 0);

        }catch (IOException e){
            e.printStackTrace();
        }

        setUpGameElements();
    }

    public void setUpGameElements(){
        mBall.reset(mScreenX, mScreenY);

        if(mLives == 0){ //Solo reseteamos el contador si no tenemos vidas...
            mScore = 0;
            mLives = 3;
        }
    }

    @Override
    public void run() {
        while (mIsPlaying){
            //capturamos el tiempo actual en milisegundos
            long startFrameTime = System.currentTimeMillis();

            if (!mPaused){
                update();
            }
            draw();

            //calculamos los FPS para luego utilizar el tiempo entre uno y otro
            //para animar coherentemente en el update
            long endFrameTime = System.currentTimeMillis();
            long loopDuration = endFrameTime - startFrameTime;
            if(loopDuration > 0 ){
                mFPS = 1000/loopDuration;
            }
        }
    }


    //actualiza las variables del videojuego
    public void update(){
        //recalculamos la posicion de bola y pala
        mPaddle.update(mFPS);
        mBall.update(mFPS);

        //comprobamos si la pala y la bola ambas colisionan
        if(RectF.intersects(mPaddle.getRect(),mBall.getRect())){
            mBall.setRandomXVelociy();
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mPaddle.getRect().top-2);

            mScore++;
            mBall.increaseVelocity();
            sp.play(beed1ID, 1,1,0,0,1);
        }

        //cuando la bola se sale por abajo(el jugador no le ha dado)
        if (mBall.getRect().bottom >  mScreenY){
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mScreenY-2);
            mLives--;
            sp.play(loseLiveID, 1,1,0,0,1);
            //si me quedo sin vidas, game over y reset..
            if(mLives == 0 ){
                mPaused = true;
                setUpGameElements();
            }
        }

        //la bola se sale por izq o derecha
        if (mBall.getRect().left < 0){
            mBall.reverseXVelocity();
            mBall.clearObstacleX(2);
            sp.play(beed3ID, 1,1,0,0,1);

        }

        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mBall.clearObstacleX(mScreenX - 22);
            sp.play(beed3ID, 1,1,0,0,1);
        }

        //cuando la bola se sale por arriba
        if(mBall.getRect().top < 0){
            mBall.reverseYVelocity();
            mBall.clearObstacleY(11);
            sp.play(beed3ID, 1,1,0,0,1);

        }

    }

    //dibuja en pantalla en base a los resultados del calculo del update
    //moviemiento, colisiones...
    public void draw(){
        //comprobamos que la superficie es valida para pintar
        if (mSurfaceHolder.getSurface().isValid()){
            //podemos pintar
            mCanvas = mSurfaceHolder.lockCanvas();

            //Pintamos primero lo que esta mas lejos: el fondo
            mCanvas.drawColor(Color.argb(0, 0, 0, 0));
            //elegimos el color de la pala y la bola
            mPaint.setColor(Color.argb(255,255,255,255));
            //dibujamos en pantalla la pala
            mCanvas.drawRect(mPaddle.getRect(), mPaint);
            //dibujamos en pantalla la bola
            mCanvas.drawRect(mBall.getRect(), mPaint);
            //dibujamos la puntuacion y las vidas
            mPaint.setTextSize(40);
            mCanvas.drawText("Puntos: " + mScore + " Vidas: " + mLives, 10, 50, mPaint);

            //desbloqueo la superficie indicando el canvas que la va a reemplazar
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    //reanuda la partida
    public void resume(){
        mIsPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    //pausara la partida
    public void pause(){
        mIsPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mPaused = false;
                if(event.getX() < mScreenX/2){
                    mPaddle.setMovementState(mPaddle.LEFT);
                }else {
                    mPaddle.setMovementState(mPaddle.RIGHT);
                }
                break;
            case MotionEvent.ACTION_UP:
                mPaddle.setMovementState(mPaddle.STOPPED);
                break;
        }
        return true;
    }
}
