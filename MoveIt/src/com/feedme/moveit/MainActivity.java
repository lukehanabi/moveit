package com.feedme.moveit;



import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	Game game = null;
	
	PointF mImagePos = new PointF();
	PointF mImageSource = new PointF();
	PointF mImageTarget = new PointF();
	long mInterpolateTime;
	Matrix matrix = null;
	Canvas tempCanvas = null;
	float rotation = 0;
	
	public Handler updateHandler = new Handler(){
        /** Gets called on every message that is received */
        // @Override
        public void handleMessage(Message msg) {
            game.update();
            game.invalidate();
            super.handleMessage(msg);
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		game = new Game(this);
        setContentView(game);

        Thread myThread = new Thread(new UpdateThread());
        myThread.start();
	}
	
	
	public class UpdateThread implements Runnable {

        @Override
        public void run() {

             while(true){
                 try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                 MainActivity.this.updateHandler.sendEmptyMessage(0);
            }
        }

    }
	
	public class Game extends View {

	    private Bitmap image;
	    private Paint paint;
	    private int x=0;

	    public Game(Context context) {
	        super(context);
	        matrix = new Matrix();
	        
	        Bitmap source = BitmapFactory.decodeResource(this.getResources(), R.drawable.pelu);
	        image = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	        paint = new Paint();
	        Bitmap mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true);
	        tempCanvas = new Canvas(mutableBitmap);
	       
	    }

	    // called every Frame
	    protected void onDraw(Canvas canvas) {

	        canvas.drawBitmap(image, matrix, paint);
	        //tempCanvas.drawBitmap(image, mImagePos.x, mImagePos.y, paint);
	        matrix.reset();
	    }

	    // called by thread
	    public void update() {
	    	final long INTERPOLATION_LENGTH = 2000;
	        long time = SystemClock.uptimeMillis();
	       
	    

	        
	        if (time - mInterpolateTime > INTERPOLATION_LENGTH) {
	            mImageSource.set(mImageTarget);
	            mImageTarget.x = (float)(Math.random() * 400);
	            mImageTarget.y = (float)(Math.random() * 400);
	            mInterpolateTime = time;
	        }

	        float t = (float)(time - mInterpolateTime) / INTERPOLATION_LENGTH;
	        // For some smoothness uncomment this line;
	         //t = t * t * (3 - 2 * t);

	        mImagePos.x = mImageSource.x + (mImageTarget.x - mImageSource.x) * t;
	        mImagePos.y = mImageSource.y + (mImageTarget.y - mImageSource.y) * t;
	        
	        if (rotation == 360) {
	        	rotation = 0;
	        } else {
	        	rotation = rotation + 15;
	        }

	        
	        matrix.postTranslate( mImagePos.x, mImagePos.y );
	        
	        matrix.postRotate(rotation, (image.getWidth())/2, (image.getHeight())/2 );
	        matrix.postTranslate(mImagePos.x,mImagePos.y);
	        
	        
	        

	    }
	}




}
