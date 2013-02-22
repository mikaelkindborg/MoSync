/* Copyright (C) 2011 MoSync AB

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License,
version 2, as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
MA 02110-1301, USA.
*/

package com.mosync.nativeui.ui.widgets;

import static com.mosync.internal.android.MoSyncHelpers.SYSLOG;

import java.util.List;

import android.app.Activity;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @brief An implementation of SurfaceView that is strictly used for camera purposes.
 *
 * @author Ali Sarrafi
 */
public class MoSyncCameraPreview
	extends SurfaceView
	implements SurfaceHolder.Callback
{

	/**
	 * The surface that displays the live camera preview.
	 */
	SurfaceHolder mHolder;

	/**
	 * An instance of camera Hardware.
	 */
	public Camera mCamera;

	public Camera.Size mPreviewSize;

	/**
	 * Currently selected camera.
	 */
	public int mCameraIndex = 0;

	public Activity mActivity;

	/**
	 * Constructor
	 *
	 * @param context the context of the application we are running on.
	 */
	public MoSyncCameraPreview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mActivity = (Activity)context;
	}


	/**
	 * Activates everything when the surface is created.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		mHolder = holder;
		// if (mCamera != null)
		// initiateCamera();
	}

	/**
	 * Initiates the camera hardware.
	 */
	public void initiateCamera()
	{
		SYSLOG("initiateCamera");
		if(mCamera == null)
			return;
		try
		{
			setCameraDisplayOrientation();

			if(mHolder == null)
			{
				return;
			}

			mCamera.setPreviewDisplay(mHolder);

			// If we use the default preview size things seams to be more stable
			Camera.Parameters param = mCamera.getParameters();
			mPreviewSize = param.getPreviewSize();

			// TODO: This should not be needed, we have not updated parameters.
			mCamera.setParameters(param);
		}
		catch (Exception e)
		{
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * Sets the Orientation of the camera Preview o be the same as MoSyncApp.
	 */
	private void setCameraDisplayOrientation()
	{
		// Set the orientation of the picture on old Android phones
		Camera.Parameters parameters = mCamera.getParameters();

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			parameters.set("orientation", "portrait");
			// default camera orientation on android is landscape
			// So we need to rotate the preview
			parameters.setRotation(90);
			mCamera.setDisplayOrientation(90);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			parameters.set("orientation", "landscape");
			parameters.setRotation(0);
			mCamera.setDisplayOrientation(0);
		}
		mCamera.setParameters(parameters);
	}

	/*
	private void OLDsetCameraDisplayOrientation() {
		// Set the orientation of the picture on old Android phones
		Camera.Parameters parameters = mCamera.getParameters();

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			parameters.set("orientation", "portrait");
			// default camera orientation on android is landscape
			// So we need to rotate the preview
			parameters.setRotation(90);
			mCamera.setDisplayOrientation(90);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			parameters.set("orientation", "landscape");
			parameters.setRotation(0);
			mCamera.setDisplayOrientation(0);
		}
		mCamera.setParameters(parameters);
	}
	*/

	/**
	 * the function that is called by the system when the preview is destroyed
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		try {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
					mCamera = null;
			}
		} catch (RuntimeException e) {
			SYSLOG("Failed to stopPreivew after surface destory");
		}
	}

	/**
	* This function is called by the system when the size of the surface is known
	*/
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
	{
		if(mCamera == null)
			return;

		try
		{
			if(mHolder == null)
				return;
			mCamera.setPreviewDisplay(mHolder);
		}
		catch(Exception e)
		{
			SYSLOG(e.getMessage());
		}

		// If we use the default preview size things seams to be more stable
		Camera.Parameters param = mCamera.getParameters();
		mPreviewSize = param.getPreviewSize();

		mCamera.setParameters(param);
	}

	/**
	 * A wrapper function to calculate the nearest supported size
	 * to the size set by the user. Android does not allow using
	 * Any custom size for camera.
	 *
	 * @param sizes a List that includes the supported sizes
	 * @param width custom width requested by user
	 * @param height custom height requested by the user.
	 * @return a Size object consisting the best matching size
	 */
	public Camera.Size getOptimalSize(List<Camera.Size> sizes, int width, int height)
	{
		final double ASPECT_TOLERANCE = 0.2;
		double targetRatio = (double) width / height;
		if (sizes == null)
			return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetWidth = width;

		// Try to find an size match aspect ratio and size
		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.width - targetWidth) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.width - targetWidth);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the
		// aspect ratio set by the user
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.width - targetWidth) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.width - targetWidth);
				}
			}
		}
		return optimalSize;
	}
} // class MoSyncCameraPreview
