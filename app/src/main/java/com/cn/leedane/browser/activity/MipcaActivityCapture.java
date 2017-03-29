package com.cn.leedane.browser.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cn.leedane.browser.R;
import com.cn.leedane.browser.application.BaseApplication;
import com.cn.leedane.browser.handler.CommonHandler;
import com.cn.leedane.browser.util.ConstantsUtil;
import com.cn.leedane.browser.util.DesUtils;
import com.cn.leedane.browser.util.MediaUtil;
import com.cn.leedane.browser.util.StringUtil;
import com.cn.leedane.browser.util.ToastUtil;
import com.cn.leedane.browser.zxing.camera.CameraManager;
import com.cn.leedane.browser.zxing.decoding.CaptureActivityHandler;
import com.cn.leedane.browser.zxing.decoding.InactivityTimer;
import com.cn.leedane.browser.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
/**
 * 我的扫码activity
 * Created by LeeDane on 2016/4/14.
 */
public class MipcaActivityCapture extends Activity implements Callback {

	private static final int GET_SYSTEM_IMAGE_CODE = 1034;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private boolean isLogin;

	private Button mButtonScanImage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		mButtonScanImage = (Button)findViewById(R.id.scan_image);
		mButtonScanImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//调用系统图库
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				intent.putExtra("crop", true);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, GET_SYSTEM_IMAGE_CODE);
			}
		});
		/*Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MipcaActivityCapture.this.finish();
				
			}
		});*/

		isLogin = BaseApplication.isLogin();
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * ����ɨ����
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
			dealScanResult(resultString);
		}
	}

	/**
	 * 处理扫描二维码后的结果
	 * @param resultString
	 */
	private void dealScanResult(String resultString){
		if(StringUtil.isLink(resultString)){
			//内部链接
			if(resultString.startsWith(ConstantsUtil.DEFAULT_SERVER_URL)){
				String link = ConstantsUtil.DEFAULT_SERVER_URL + ConstantsUtil.WEB_APP_DOWNLOAD_PATH;
				if(resultString.startsWith(link)){
					Map<String, String> params = StringUtil.getUrlParams(resultString);
					if(params.containsKey("leedaneapp")){
						String value = params.get("leedaneapp");
						if(StringUtil.isNotNull(value)){
							try{
								DesUtils desUtils = new DesUtils();
								JSONObject jsonObject = new JSONObject(new String(desUtils.decrypt(value)));
								if(jsonObject.has("tableName") && jsonObject.has("tableId")){//打开详情
									//启动详情的activity
									/** todo 打开详情页面**/
									//关闭当前的扫码页面
									finish();
								}else if(jsonObject.has("account") && jsonObject.has("id")){ //打开用户个人中心
									/** todo 打开个人中心页面**/
								}else{
									ToastUtil.failure(MipcaActivityCapture.this, "暂时不支持的类型，请更新最新版本后尝试");
									return;
								}
							}catch (Exception e){
								e.printStackTrace();
								ToastUtil.failure(MipcaActivityCapture.this, "数据解析失败！");
								return;
							}
						}
					}else if(params.containsKey("scan_login")){
						//判断是否登录
						if(!BaseApplication.isLogin()){
							/** todo 去登录验证**/
							finish();
							return;
						}else{
							/** todo 跳到扫码成功页面**/
							finish();
						}
					}else{
						//无法处理的内部链接，直接外部打开
						CommonHandler.openLink(MipcaActivityCapture.this, link);
					}
				}else{
					//无法处理的内部链接，直接外部打开
					CommonHandler.openLink(MipcaActivityCapture.this, link);
				}
			}else{
				//外部链接直接打开
				CommonHandler.openLink(MipcaActivityCapture.this, resultString);
			}
		}else{
			//非链接直接弹出数据
			ToastUtil.success(MipcaActivityCapture.this, resultString);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(MipcaActivityCapture.this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	/**
	 * 弹出加载ProgressDiaLog
	 */
	private ProgressDialog mProgressDialog;
	/**
	 * 显示加载Dialog
	 * @param title  标题
	 * @param main  内容
	 * @param cancelable 是否可以取消
	 */
	protected void showLoadingDialog(String title, String main, boolean cancelable){
		dismissLoadingDialog();
		mProgressDialog = ProgressDialog.show(MipcaActivityCapture.this, title, main, true, cancelable);
	}
	/**
	 * 隐藏加载Dialog
	 */
	protected void dismissLoadingDialog(){
		if(mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == GET_SYSTEM_IMAGE_CODE) {
				final Uri uri = data.getData();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						String imgPath = MediaUtil.getImageAbsolutePath(MipcaActivityCapture.this, uri);
						Result result = scanningImage(imgPath);
						if (result != null) {
							String str = result.getText();
							if (!str.equals("")) {
								dealScanResult(str);
								return;
							}
						}
						Toast.makeText(MipcaActivityCapture.this, "扫描图片失败", Toast.LENGTH_SHORT).show();

						//Toast.makeText(MipcaActivityCapture.this, "Scan failed--->"+decode(uri.getPath()), Toast.LENGTH_SHORT).show();
					}
				}, 500);

			}
		}
	}
	private Bitmap scanBitmap;

	/**
	 * 解析二维码图片
	 * @param path
	 * @return
	 */
	protected Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		// DecodeHintType 和EncodeHintType
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);

		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}
}