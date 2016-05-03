package com.example.facialrecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MainActivity extends Activity implements CvCameraViewListener2{

	private CameraBridgeViewBase mOpenCvCameraView ;
	private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
	private static final String    TAG                 = "OCVSample::Activity";
	private File mCascadeFile ;
	private CascadeClassifier mJavaDetector ;
	private Mat mGray  ;
	private Mat mRgba ;
	private int mAbsoluteFaceSize = 0 ;
	private float mRelativeFaceSize   = 0.2f;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		 @Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                {
	                    Log.i(TAG, "OpenCV loaded successfully");
	                    try{
	                    	InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default) ;
	                    	File cascadeDir = getDir("cascade",Context.MODE_PRIVATE) ;
	                    	mCascadeFile = new File(cascadeDir,"haarcascade_frontalface_default.xml") ;
	                    	FileOutputStream os = new FileOutputStream(mCascadeFile) ;
	                    	byte[] buffer = new byte[4096] ;
	                    	int bytesRead ;
	                    	while((bytesRead = is.read(buffer)) != -1){
	                    		os.write(buffer,0,bytesRead);
	                    	}
	                    	is.close();
	                    	os.close();
	                    	mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath()) ;
	                    	if(mJavaDetector.empty())
	                    	{
	                    		Log.e(TAG,"failed to load cascade") ;
	                    		mJavaDetector = null ;
	                    	}
	                    	else
	                    		Log.i(TAG,"Loaded cascade from " + mCascadeFile.getAbsolutePath()) ;
	                    	cascadeDir.delete() ;
	                    }catch(IOException e){
	                    	e.printStackTrace();
	                    	Log.e(TAG,"failed to load cascade.Exception " + e) ;
	                    }
	                    mOpenCvCameraView.enableView() ;              
	                }break;
	                default:
	                {
	                	super.onManagerConnected(status) ;
	                }break;
	            } 
		 }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view) ;
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this) ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	  @Override
	    public void onPause()
	    {
	        super.onPause();
	        if (mOpenCvCameraView != null)
	            mOpenCvCameraView.disableView();
	    }
	
	@Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		mRgba = inputFrame.rgba() ;
		mGray = inputFrame.gray() ;
		Mat mrgbat = mRgba.t() ;
		Mat mgrayt = mGray.t() ;
		Core.flip(mRgba.t(),mrgbat,1) ;
		Core.flip(mGray.t(),mgrayt,1) ;
		Imgproc.resize(mrgbat,mrgbat,mRgba.size()) ;
		Imgproc.resize(mgrayt,mgrayt,mGray.size()) ;
		MatOfRect faces = new MatOfRect() ;
		if (mAbsoluteFaceSize == 0) {
            int height = mgrayt.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
		}
		if(mJavaDetector != null)
			mJavaDetector.detectMultiScale(mgrayt, faces, 1.1, 2, 2,new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
		
		org.opencv.core.Rect[] facesArray = faces.toArray() ;
		for(int i=0;i<facesArray.length;i++)
			Core.rectangle(mrgbat, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3) ;		
		return mrgbat ;
		
		
	}

}