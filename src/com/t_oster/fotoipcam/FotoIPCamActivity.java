This file is part of VisiCut.

    VisiCut is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    VisiCut is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with VisiCut.  If not, see <http://www.gnu.org/licenses/>.
package com.t_oster.fotoipcam;

import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class FotoIPCamActivity extends Activity implements OnClickListener,
		Callback, PictureCallback {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

		WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);

		mSurfaceHolder = mSurfaceView.getHolder();

		mSurfaceHolder.addCallback(this);

		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		bt = (Button) this.findViewById(R.id.button1);
		tt = (TextView) this.findViewById(R.id.text);
		bt.setText("start");
		bt.setOnClickListener(this);
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Button bt;
	private TextView tt;
	private Webserver server;

	@Override
	public void onClick(View v) {
		if (server == null) {
			try {
				server = new Webserver(this);
				tt.setText("Server started");
				bt.setText("Stop");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				tt.setText(e.getMessage());
			}
		} else if (server != null) {
			server.stop();
			server = null;
			tt.setText("Server Stopped");
			bt.setText("Start");
		}
	}

	public byte[] takePicture()
	{
		//tt.setText("Triggered Picture");
		mCamera.takePicture(null, null, this);
		synchronized(mCamera)
		{
			try {
				mCamera.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//tt.setText("Wait interrupted");
			}
		}
		//tt.setText("Picture ready");
		return this.getLastPicture();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(width, height);
		mCamera.setParameters(p);
		try {

			mCamera.setPreviewDisplay(holder);

		} catch (IOException e) {

			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	private Camera mCamera;
	private boolean mPreviewRunning;

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private byte[] lastPicture;
	
	public byte[] getLastPicture()
	{
		return lastPicture;
	}
	
	@Override
	public void onPictureTaken(byte[] arg0, Camera arg1) {
		lastPicture = arg0;
		synchronized(mCamera)
		{
			mCamera.startPreview();
			mCamera.notify();
		}
	}

}