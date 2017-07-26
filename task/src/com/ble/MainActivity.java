package com.ble;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.ble.R;
import com.zxing.activity.CaptureActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public class DataClass {
		public BluetoothDevice device = null;
		public String name;
		public Integer rssi = 0;
		public int count = 0;
		public String address;
		public String version = "";
	}

	private DataClass m_myData = new DataClass();

	BluetoothAdapter mBluetoothAdapter;
	BluetoothGattCharacteristic writeCharacteristic;
	BluetoothGattCharacteristic readCharacteristic;
	BluetoothGatt mBluetoothGatt;

	//byte[] key = {58,96,67,42,92,01,33,31,41,30,15,78,12,19,40,37};
	byte[] key = { 32, 87, 47, 82, 54, 75, 63, 71, 48, 80, 65, 88, 17, 99, 45, 43 };
	 byte[] key2 = { 32, 87, 47, 82, 54, 75, 63, 71, 48, 80, 65, 88, 17, 99, 45, 43 };
	// byte[] key2 = {66,66,66,66,88,88,88,99,48,55,55,88,55,99,55,55};
	byte[] mima = { 0x30, 0x30, 0x30, 0x30, 0x30, 0x30 };
	byte[] mima2 = {0x32, 0x32, 0x32, 0x32, 0x32, 0x32};

	public static final ParcelUuid findServerUUID = ParcelUuid
			.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
	public static final UUID OAD_SERVICE_UUID = UUID
			.fromString("f000ffc0-0451-4000-b000-000000000000");

	public static final UUID bltServerUUID = UUID
			.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
	public static final UUID readDataUUID = UUID
			.fromString("000036f6-0000-1000-8000-00805f9b34fb");

	public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");

	public static final UUID writeDataUUID = UUID
			.fromString("000036f5-0000-1000-8000-00805f9b34fb");

	byte[] token = new byte[4];

	byte[] gettoken = { 0x06, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	volatile String m_nowMac = "";

	TextView tvOADtishi;
	TextView tvState;
	TextView tvOpenTime;
	TextView info;
	Button buttonOpen;
	Button buttonReset;
	Button buttonElectricity;
	Button buttonScanCode;
	Button buttonSetHuancheState;
	Button buttonSearchLockModel;
	Button buttonSetLockModel;
	Button buttonSearchLockWorkState;
	Button buttonGetToken;
	Button buttonSearchGIM_ID;
	Button buttonSearchGSMVersion;
	
	RadioGroup rgModel = null;  
	RadioButton rbZhengchang = null;  
	RadioButton rbYunshu = null; 
	RadioButton rbChongqi = null;  

	Button buttonOAD;
	Button buttonUpgrade;

	TextView updateInfo;
	private ProgressBar mProgressBar;
	private String openTime;

	Button buttonModifyPassword;
	Button buttonModifyKey;

	Button buttonSearchState;
	Button buttonNewKey;
	
	Button buttonShouQuan;
	
	Button buttonGetKey;

	List<ScanFilter> bleScanFilters;
	ScanSettings bleScanSettings;
	BluetoothLeScanner mBluetoothLeScanner;
	TextView proInfo;
	Button buttonRestartBle;

	private static final int CAOZUO_OPEN = 1;
	private static final int CAOZUO_CLOSE = 2;
	private static final int CAOZUO_DIANLIANG = 3;
	private static final int CAOZUO_MIMA = 4;
	private static final int CAOZUO_MIYUE = 5;
	private static final int CAOZUO_OAD = 6;
	private static final int CAOZUO_ZHUANGTAI = 7;
	private static final int CAOZUO_HUANCHESTATE = 8;
	private static final int CAOZUO_SEARCHLOCKMODEL = 9;
	private static final int CAOZUO_SETLOCKMODEL = 10;
	private static final int CAOZUO_SEARCHLOCKWORKSTATE = 11;
	private static final int CAOZUO_GETTOKEN = 12;
	private static final int CAOZUO_GIM_ID = 13;
	private static final int CAOZUO_GSM_VER = 14;
	private int caozuo = 0;

	// Activity
	private static final int FILE_ACTIVITY_REQ = 0;

	// Programming parameters
	private static final short OAD_CONN_INTERVAL = 12; // 15 milliseconds
	private static final short OAD_SUPERVISION_TIMEOUT = 50; // 500 milliseconds
	private static final int GATT_WRITE_TIMEOUT = 300; // Milliseconds

	private static final int FILE_BUFFER_SIZE = 0x40000;
	private static final String FW_CUSTOM_DIRECTORY = Environment.DIRECTORY_DOWNLOADS;
	private static final String FW_FILE_A = "SensorTagImgA.bin";
	private static final String FW_FILE_B = "SensorTagImgB.bin";

	private static final int OAD_BLOCK_SIZE = 16;
	private static final int HAL_FLASH_WORD_SIZE = 4;
	private static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;
	private static final int OAD_IMG_HDR_SIZE = 8;
	private static final long TIMER_INTERVAL = 1000;

	private static final int SEND_INTERVAL = 20; //20 Milliseconds (make sure this
													// is longer than the
													// connection interval)
	private static final int BLOCKS_PER_CONNECTION = 4; // May sent up to four
														// blocks per connection

	// Programming
	private final byte[] mFileBuffer = new byte[FILE_BUFFER_SIZE];
	private final byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];
	private ImgHdr mFileImgHdr = new ImgHdr();
	private ProgInfo mProgInfo = new ProgInfo();

	// Housekeeping
	private boolean mServiceOk = false;
	private boolean mProgramming = false;

	private volatile boolean mBusy = false; // Write/read pending response

	private volatile boolean m_xinMiYue = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//获取蓝牙设备
		getBleDevice();
		//初始化视图
		initView ();
		
		setLockModel();
		
	}
	
	
	//获取蓝牙设备
	private void getBleDevice(){
		
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "您的设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
			finish();
		}

		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "获取蓝牙失败", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 188);
		} else {
			mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

			bleScanFilters = new ArrayList<>();
			bleScanFilters.add(new ScanFilter.Builder().setServiceUuid(
					findServerUUID).build());

			bleScanSettings = new ScanSettings.Builder().build();
			mBluetoothLeScanner.startScan(null, bleScanSettings, scanCallback);
		}

	}
	
	//初始化视图
		private void initView (){
			
			tvOADtishi = (TextView) findViewById(R.id.textView2);
			tvState =(TextView) findViewById(R.id.state);
			tvOpenTime =(TextView) findViewById(R.id.openTime);
			info = (TextView) findViewById(R.id.textView);
			buttonOpen = (Button) findViewById(R.id.openOrCloseLock);
			buttonReset = (Button) findViewById(R.id.reset);
			buttonElectricity = (Button) findViewById(R.id.electricity);
			buttonScanCode = (Button) findViewById(R.id.scanCode);
			buttonSetHuancheState = (Button) findViewById(R.id.setHuanche);
			buttonSearchLockModel =(Button) findViewById(R.id.searchLockModel);
			buttonSetLockModel = (Button) findViewById(R.id.setLockModel);
			buttonSearchLockWorkState = (Button) findViewById(R.id.searchLockWorkState);
			buttonGetToken = (Button) findViewById(R.id.getToken);
			buttonSearchGIM_ID = (Button) findViewById(R.id.searchGIM_ID);
			buttonSearchGSMVersion = (Button) findViewById(R.id.searchGSM_version);
			rgModel = (RadioGroup) findViewById(R.id.setModleLayout);
			rbZhengchang = (RadioButton) findViewById(R.id.zhengchangModel);
			rbChongqi = (RadioButton) findViewById(R.id.chongqiSet);
			rbYunshu = (RadioButton) findViewById(R.id.yunshuModel);

			buttonOAD = (Button) findViewById(R.id.OAD);
			buttonUpgrade = (Button) findViewById(R.id.upgrade);
			buttonRestartBle = (Button) findViewById(R.id.restartBle);
			updateInfo = (TextView) findViewById(R.id.textView1);
			mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
			mProgressBar.setProgress(0);
			proInfo = (TextView) findViewById(R.id.textView3);

			buttonModifyPassword = (Button) findViewById(R.id.modifyPassword);
			buttonModifyKey = (Button) findViewById(R.id.modifyKey);

			buttonSearchState = (Button) findViewById(R.id.searchState);
			buttonNewKey = (Button) findViewById(R.id.newKey);

			buttonShouQuan = (Button) findViewById(R.id.weixinShouquan);
			buttonGetKey = (Button) findViewById(R.id.getKey);
			
			buttonOpen.setOnClickListener(this);
			buttonReset.setOnClickListener(this);
			buttonElectricity.setOnClickListener(this);
			buttonScanCode.setOnClickListener(this);
			buttonOAD.setOnClickListener(this);
			buttonUpgrade.setOnClickListener(this);
			buttonRestartBle.setOnClickListener(this);
			buttonModifyPassword.setOnClickListener(this);
			buttonModifyKey.setOnClickListener(this);
			buttonSearchState.setOnClickListener(this);
			buttonNewKey.setOnClickListener(this);
			buttonShouQuan.setOnClickListener(this);
			buttonGetKey.setOnClickListener(this);
			buttonSetHuancheState.setOnClickListener(this);
			buttonSearchLockModel.setOnClickListener(this);
			buttonSetLockModel.setOnClickListener(this);
			buttonSearchLockWorkState.setOnClickListener(this);
			buttonGetToken.setOnClickListener(this);
			buttonSearchGIM_ID.setOnClickListener(this);
			buttonSearchGSMVersion.setOnClickListener(this);
			
			
		}

		
	   @Override
	    public void onClick(View v) {

	        switch (v.getId()) {
	            //开关锁
	            case R.id.openOrCloseLock:
	            	openCloseNock();
	                break;
	            //复位
	            case R.id.reset:
	            	reset();
	                break;
	            //电量 查询   
	            case R.id.electricity:
	            	electricity();
	                break;
	            //扫码上传
	            case R.id.scanCode:
	            	scanCode();
	                break;
	            //OAD
	            case R.id.OAD:
	            	OAD();
	                break;
	           //获取密钥
	            case R.id.getKey:
	            	getKey();
	                break;
	            //重启蓝牙
	            case R.id.restartBle:
	            	restartBle();
	                break;
	            //修改密码
	            case R.id.modifyPassword:
	            	modifyPassword();
	                break;
	            //修改密钥
	            case R.id.modifyKey:
	            	modifyKey();
	                break;
	            //查询开关锁状态
	            case R.id.searchState:
	            	searchState();
	                break;
	            //新开锁密钥
	            case R.id.newKey:
	            	newKey();
	                break;
	            //微信授权
	            case R.id.weixinShouquan:
	            	shouQuan();
	                break;
	              //连接设备
	            case R.id.upgrade:
	            	upgrade();
	                break;
	              //设置还车状态
	            case R.id.setHuanche:
	            	setHuancheState();
	                break;
	              //查询锁的工作模式
	            case R.id.searchLockModel:
	            	searchLockModel();
	                break;
	                //设置锁的工作模式
	            case R.id.setLockModel:
//	            	setLockModel();
	                break;
	                //查询锁的工作状态
	            case R.id.searchLockWorkState:
	            	searchLockWorkState();
	                break;
	                //查询锁的GSM_ID号
	            case R.id.searchGIM_ID:
	            	searchGIM_ID();
	                break;
	                //查询锁的GSM版本号
	            case R.id.searchGSM_version:
	            	searchGSM_Ver();
	                break;
	                
	        }
	    }
	   
	   private void setLockModel(){
			
			//未调用监听时显示默认选择内容  
	        if(rbZhengchang.getId() == rgModel.getCheckedRadioButtonId()){  
					updateInfo.append(openTime+"  撤防模式设置成功！\r\n");
	        }else if(rbYunshu.getId() == rgModel.getCheckedRadioButtonId()){
					updateInfo.append(openTime+"  设防模式设置成功！\r\n");
	        } else if(rbChongqi.getId() == rgModel.getCheckedRadioButtonId()){  
//					updateInfo.append(openTime+"  重启模式设置！\r\n");
	        	
	        }   
	        //为RadioGroup设置监听事件  
	        rgModel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
	              
	            @Override  
	            public void onCheckedChanged(RadioGroup group, int checkedId) {  
	            	
	            	if (m_myData.device == null) {
     					Toast toast = Toast.makeText(MainActivity.this,
     							"没有设备，请先扫描", Toast.LENGTH_SHORT);
     					toast.setGravity(Gravity.CENTER, 0, 0);
     					toast.show();
     					return;
     		
			}
			
			mBluetoothLeScanner.stopScan(scanCallback);
 			caozuo = CAOZUO_SETLOCKMODEL;
 			
 			if (mBluetoothGatt != null && writeCharacteristic != null) {
					if (mBluetoothGatt.getDevice().getAddress()
							.equals(m_myData.address)) {
						 // TODO Auto-generated method stub  
		                if(checkedId == rbZhengchang.getId()){  
		                	
		                	byte[] zhengchangModle = { 0x05, 0x21, 0x01, 0X00, token[0],
									token[1], token[2], token[3], 0x00, 0x00, 0x00,
									0x00, 0x00, 0x00, 0x00, 0x00 };
								getTime();
								updateInfo.append(openTime+"  撤防状态设置成功！\r\n");
							SendData(zhengchangModle);
							
		                }else if(checkedId == rbYunshu.getId()){
		                	
		                	byte[] yunshuModle = { 0x05, 0x21, 0x01, 0X01, token[0],
									token[1], token[2], token[3], 0x00, 0x00, 0x00,
									0x00, 0x00, 0x00, 0x00, 0x00 };
							getTime();
								updateInfo.append(openTime+"  设防状态设置成功！\r\n");
							SendData(yunshuModle);
		                }else if(checkedId == rbChongqi.getId()){  
		                	
		                	byte[] chongqiModle = { 0x05, 0x21, 0x01, 0X02, token[0],
									token[1], token[2], token[3], 0x00, 0x00, 0x00,
									0x00, 0x00, 0x00, 0x00, 0x00 };
								getTime();
								updateInfo.append(openTime+"  重启模式设置！\r\n");
							SendData(chongqiModle);
		                }    
						
					} else {
						mBluetoothGatt.disconnect();
						mBluetoothGatt.close();
						mBluetoothGatt = null;

						mBluetoothGatt = m_myData.device.connectGatt(
								MainActivity.this, false, mGattCallback);
					}
				} else {
					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
 			
	            }  
	            
	        });  
		}
	   

	//开关锁的方法
	   private void openCloseNock(){
		   
		   if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			if (m_xinMiYue) {
				Toast toast = Toast.makeText(MainActivity.this, "只能用新密钥开锁",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);
			caozuo = CAOZUO_OPEN;

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] openLock = { 0x05, 0x01, 0x06, mima[0], mima[1],
							mima[2], mima[3], mima[4], mima[5], token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00 };
					SendData(openLock);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;
					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
					
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
	   }
	   
	   //复位的方法
	   private void reset(){
		   
		   if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);
			caozuo = CAOZUO_CLOSE;

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] closeLock = { 0x05, 0x0c, 0x01, 0x01, token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00 };
					SendData(closeLock);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;

					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
	   }
	   
	   //电量查询的方法
	   private void electricity(){
		   
		   if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);
			caozuo = CAOZUO_DIANLIANG;

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] dianLiang = { 0x02, 0x01, 0x01, 0x01, token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00 };
					SendData(dianLiang);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;

					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
		   
	   }
	   
	
	   
	   //扫码上传的方法
	   private void scanCode(){
		   
		   if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);

			m_nowMac = m_myData.address;
			// 打开扫描界面扫描条形码或二维码
			Intent openCameraIntent = new Intent(MainActivity.this,
					CaptureActivity.class);
			startActivityForResult(openCameraIntent, 288);
	   }
	   //OAD方法
	   private void OAD(){
		   
		   if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);

			if (!checkFile()) {
				updateInfo.append("Download下找不到升级文件upFile.bin\n");
				return;
			}

			caozuo = CAOZUO_OAD;
			tvOADtishi.setVisibility(View.VISIBLE);
			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] oad = { 0x03, 0x01, 0x01, 0x01, token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00 };
					SendData(oad);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;

					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
	   }
	   
	   //升级的方法
	   private void upgrade(){
		   if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (!mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;
					getTime();
					updateInfo.append(openTime+"  正在尝试连接......\n");
					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallbackOAD);
				}
			} else {
				getTime();
				updateInfo.append(openTime+"  正在尝试连接......\n");
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallbackOAD);
			}

			if (mProgramming) {
				stopProgramming();
			}
	   }
	   
	//重启蓝牙
	public void restartBle() {
		mBluetoothLeScanner.stopScan(scanCallback);

		mBluetoothAdapter.disable();
		DataCleanManager.cleanApplicationData(MainActivity.this);

		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(MainActivity.this, "获取蓝牙失败", Toast.LENGTH_SHORT)
					.show();
			finish();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 188);
			} else {
				mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

				bleScanFilters = new ArrayList<>();
				bleScanFilters.add(new ScanFilter.Builder().setServiceUuid(
						findServerUUID).build());

				bleScanSettings = new ScanSettings.Builder().build();

				if (mBluetoothGatt != null) {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;
				}

				m_myData.device = null;
				m_myData.address = "";
				m_myData.count = 0;
				m_myData.name = "";
				m_myData.version = "";
				info.setText("");

				mBluetoothLeScanner.startScan(null, bleScanSettings,
						scanCallback);
			}

		}
	}
	
	
	private byte[] oldPwd; 
	private byte[] newPwd;
	//修改密码
	private void modifyPassword(){
		
		LayoutInflater factory = LayoutInflater.from(this);  
        final View textEntryView = factory.inflate(R.layout.password_edit, null);  
        final EditText password1 = (EditText) textEntryView.findViewById(R.id.editTextPwd1);  
        final EditText password2 = (EditText)textEntryView.findViewById(R.id.editTextPwd2);  
        AlertDialog.Builder ad1 = new AlertDialog.Builder(MainActivity.this);  
        ad1.setTitle("修改密码");  
        ad1.setIcon(android.R.drawable.ic_dialog_info);  
        ad1.setView(textEntryView);  
        ad1.setPositiveButton("修改", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int i) {  

                String pwd1 = password1.getText().toString();
                String pwd2 = password2.getText().toString();
                
                if (pwd1.length()!=6 || pwd2.length()!=6 ||pwd1 ==null||pwd2 == null) {
                	Toast toast = Toast.makeText(
							MainActivity.this,
							"输入密码格式不正确！",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0,
							0);
					toast.show();
				}
                
                try {
					oldPwd = pwd1.getBytes("UTF-8");
					newPwd = pwd2.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (m_myData.device == null) {
					Toast toast = Toast.makeText(
							MainActivity.this,
							"没有设备，请先扫描",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0,
							0);
					toast.show();
					return;
				}

				mBluetoothLeScanner
						.stopScan(scanCallback);
				caozuo = CAOZUO_MIMA;

				if (mBluetoothGatt != null
						&& writeCharacteristic != null) {
					if (mBluetoothGatt.getDevice()
							.getAddress()
							.equals(m_myData.address)) {
						byte[] sendMiMa1 = { 0x05,
								0x03, 0x06, mima[0],
								mima[1], mima[2],
								mima[3], mima[4],
								mima[5], token[0],
								token[1], token[2],
								token[3], 0x00, 0x00,
								0x00 };
						
//						byte[] sendMiMa2 = { 0x05,
//								0x04, 0x06, newPwd[0],
//								newPwd[1], newPwd[2],
//								newPwd[3], newPwd[4],
//								newPwd[5], token[0],
//								token[1], token[2],
//								token[3], 0x00, 0x00,
//								0x00 };
//						
						SendData(sendMiMa1);
//						SendData(sendMiMa2);
					} else {
						mBluetoothGatt.disconnect();
						mBluetoothGatt.close();
						mBluetoothGatt = null;

						mBluetoothGatt = m_myData.device
								.connectGatt(
										MainActivity.this,
										false,
										mGattCallback);
					}
				} else {
					mBluetoothGatt = m_myData.device
							.connectGatt(
									MainActivity.this,
									false,
									mGattCallback);
				}

			
            
  
            }  
        });  
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int i) {  
  
            }  
        });  
        ad1.show();// 显示对话框  
		
//		new AlertDialog.Builder(MainActivity.this)
//		.setTitle("请确认")
//		.setPositiveButton("修改",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,
//							int which) {}
//				})
//		.setNegativeButton("取消",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,
//							int which) {
//						dialog.cancel();
//					}
//				}).show();	
	}
	
	//修改密钥的方法
	 private void modifyKey() {
		 
		 new AlertDialog.Builder(MainActivity.this)
			.setTitle("请确认")
			.setPositiveButton("修改",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {

							if (m_myData.device == null) {
								Toast toast = Toast.makeText(
										MainActivity.this,
										"没有设备，请先扫描",
										Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0,
										0);
								toast.show();
								return;
							}

							mBluetoothLeScanner
									.stopScan(scanCallback);
							caozuo = CAOZUO_MIYUE;

							if (mBluetoothGatt != null
									&& writeCharacteristic != null) {
								if (mBluetoothGatt.getDevice()
										.getAddress()
										.equals(m_myData.address)) {
									byte[] sendMiYue1 = { 0x07,
											0x01, 0x08, key2[0],
											key2[1], key2[2],
											key2[3], key2[4],
											key2[5], key2[6],
											key2[7], token[0],
											token[1], token[2],
											token[3], 0x00 };
									SendData(sendMiYue1);
								} else {
									mBluetoothGatt.disconnect();
									mBluetoothGatt.close();
									mBluetoothGatt = null;

									mBluetoothGatt = m_myData.device
											.connectGatt(
													MainActivity.this,
													false,
													mGattCallback);
								}
							} else {
								mBluetoothGatt = m_myData.device
										.connectGatt(
												MainActivity.this,
												false,
												mGattCallback);
							}

						}
					})
			.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					}).show();
			
		}
	 
	 //查询开关锁状态方法
	 private void searchState() {
		 
		 if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);
			caozuo = CAOZUO_ZHUANGTAI;

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] zhuangTai = { 0x05, 0x0E, 0x01, 0X01, token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00 };
					SendData(zhuangTai);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;

					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
			
		}
	 
	 //设置还车状态指令方法
	 private void setHuancheState() {
		 
		 if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);
			caozuo = CAOZUO_HUANCHESTATE;

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] setHuanche = { 0x05, 0x14, 0x01, 0X01, token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00 };
					SendData(setHuanche);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;

					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
			
		}
	 
	 	//查询锁的工作模式指令的方法
		private void searchLockModel() {
			 if (m_myData.device == null) {
					Toast toast = Toast.makeText(MainActivity.this,
							"没有设备，请先扫描", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}

				mBluetoothLeScanner.stopScan(scanCallback);
				caozuo = CAOZUO_SEARCHLOCKMODEL;

				if (mBluetoothGatt != null && writeCharacteristic != null) {
					if (mBluetoothGatt.getDevice().getAddress()
							.equals(m_myData.address)) {
						byte[] searchLockModle = { 0x05, 0x20, 0x01, 0X00, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(searchLockModle);
					} else {
						mBluetoothGatt.disconnect();
						mBluetoothGatt.close();
						mBluetoothGatt = null;

						mBluetoothGatt = m_myData.device.connectGatt(
								MainActivity.this, false, mGattCallback);
					}
				} else {
					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
		}
		
		
		

		//查询锁的工作状态方法
		private void searchLockWorkState() {
			
			 if (m_myData.device == null) {
					Toast toast = Toast.makeText(MainActivity.this,
							"没有设备，请先扫描", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}

				mBluetoothLeScanner.stopScan(scanCallback);
				caozuo = CAOZUO_SEARCHLOCKWORKSTATE;

				if (mBluetoothGatt != null && writeCharacteristic != null) {
					if (mBluetoothGatt.getDevice().getAddress()
							.equals(m_myData.address)) {
						byte[] searchLockWork = { 0x05, 0x22, 0x01, 0X00, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(searchLockWork);
					} else {
						mBluetoothGatt.disconnect();
						mBluetoothGatt.close();
						mBluetoothGatt = null;

						mBluetoothGatt = m_myData.device.connectGatt(
								MainActivity.this, false, mGattCallback);
					}
				} else {
					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			
			
		}
		
	//查询锁的GIM_ID方法
	private void searchGIM_ID() {
			
		 if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);
			caozuo = CAOZUO_GIM_ID;

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] searchGimId = { 0x05, 0x23, 0x01, 0X00, token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00 };
					SendData(searchGimId);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;

					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
		
		}
	

	private void searchGSM_Ver() {
		 if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);
			caozuo = CAOZUO_GSM_VER;

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] searchGsmVer = { 0x05, 0x24, 0x01, 0X00, token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00,
							0x00, 0x00, 0x00, 0x00, 0x00 };
					SendData(searchGsmVer);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;

					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
	}
		
	 //新开锁密钥的方法
	 private void newKey() {
		 if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			if (!m_xinMiYue) {
				Toast toast = Toast.makeText(MainActivity.this, "只能用新密钥开锁",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);
			caozuo = CAOZUO_OPEN;

			if (mBluetoothGatt != null && writeCharacteristic != null) {
				if (mBluetoothGatt.getDevice().getAddress()
						.equals(m_myData.address)) {
					byte[] openLock = { 0x05, 0x01, 0x06, mima[0], mima[1],
							mima[2], mima[3], mima[4], mima[5], token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00 };
					SendData(openLock);
				} else {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;

					mBluetoothGatt = m_myData.device.connectGatt(
							MainActivity.this, false, mGattCallback);
				}
			} else {
				mBluetoothGatt = m_myData.device.connectGatt(
						MainActivity.this, false, mGattCallback);
			}
		}
	 
	 //微信授权方法
		private void shouQuan() {
			if (m_myData.device == null) {
				Toast toast = Toast.makeText(MainActivity.this,
						"没有设备，请先扫描", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}

			mBluetoothLeScanner.stopScan(scanCallback);

			m_nowMac = m_myData.address;
			// 打开扫描界面扫描条形码或二维码
			Intent openCameraIntent = new Intent(MainActivity.this,
					CaptureActivity.class);
			startActivityForResult(openCameraIntent, 688);
		}
		
		//获取密钥
		private void getKey() {
			mBluetoothLeScanner.stopScan(scanCallback);
			// 打开扫描界面扫描条形码或二维码
			Intent openCameraIntent = new Intent(MainActivity.this,
					CaptureActivity.class);
			startActivityForResult(openCameraIntent, 788);
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings1) {

			m_xinMiYue = false;

			if (mBluetoothGatt != null) {
				mBluetoothGatt.disconnect();
				mBluetoothGatt.close();
				mBluetoothGatt = null;
			}

			m_myData.device = null;
			m_myData.address = "";
			m_myData.count = 0;
			m_myData.name = "";
			m_myData.version = "";
			info.setText("");

			mBluetoothLeScanner.startScan(bleScanFilters, bleScanSettings,
					scanCallback);

		} else if (id == R.id.action_settings2) {
			mBluetoothLeScanner.stopScan(scanCallback);
		}

		return super.onOptionsItemSelected(item);
	}

	public void SendData(byte[] data) {
		if (m_xinMiYue) {
			byte miwen[] = Encrypt(data, key2);
			if (miwen != null) {
				writeCharacteristic.setValue(miwen);
				mBluetoothGatt.writeCharacteristic(writeCharacteristic);
			}
		} else {
			byte miwen[] = Encrypt(data, key);
			if (miwen != null) {
				writeCharacteristic.setValue(miwen);
				mBluetoothGatt.writeCharacteristic(writeCharacteristic);
			}
		}
		getTime();
	}

	// 加密
	public byte[] Encrypt(byte[] sSrc, byte[] sKey) {

		try {
			SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// "算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc);

			return encrypted;// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
		} catch (Exception ex) {
			return null;
		}
	}

	// 解密
	public byte[] Decrypt(byte[] sSrc, byte[] sKey) {

		try {
			SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] dncrypted = cipher.doFinal(sSrc);
			return dncrypted;

		} catch (Exception ex) {
			return null;
		}
	}

	
	private void getTime(){
		
		SimpleDateFormat sDateFormat =new SimpleDateFormat("HH:mm:ss");
		Date data = new Date(System.currentTimeMillis());
		openTime =sDateFormat.format(data);
		
	}
	
	Handler m_myHandler = new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message mes) {
		
			switch (mes.what) {
			case 1: 
				// 开锁成功
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			case 2: 
				// 者提示信息
				Toast toast = Toast.makeText(MainActivity.this,
						(String) mes.obj, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			case 3: 
				info.setText(m_myData.name + "==="
						+ String.valueOf(m_myData.rssi) + "==="
						+ String.valueOf(m_myData.count) + "==="
						+ m_myData.version + "\r\n" + m_myData.address);
				break;
			
			case 4: 
				info.setText((String) mes.obj);
				break;
		
			case 5: 
				String result = (String) mes.obj;
				if (result.equals("0")) {
					updateInfo.append("支持升级\n");
					mBluetoothGatt = null;
					restartBle();
				} else {
					updateInfo.append("不支持升级，请重试\n");
				}

				break;
		
			case 6: 
				if (!mProgramming) {
					mProgramming = true;
					startProgramming();
				}
				break;
			
			case 7: 
				updateInfo.append(openTime+(String) mes.obj + "\n");
				break;
			
			case 8: 
				mProgressBar.setProgress((mProgInfo.iBlocks * 100)
						/ mProgInfo.nBlocks);
				proInfo.setText(String.valueOf(mProgInfo.iBlocks) + "/"
						+ String.valueOf(mProgInfo.nBlocks));
				break;
			
			
			case 9: 
				
				updateInfo.append(openTime+"  检测成功!  "+"电量是：" +(String) mes.obj +"%\r\n");
				break;
			
			
			case 10: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			
			case 11: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			
			case 12: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			case 13: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			
			case 14: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			case 15: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			
			case 16: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			case 17: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			
			case 18: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			case 19: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			
			
			case 20: 
				
				updateInfo.append(openTime+mes.obj+"\r\n");
				break;
			case 21: 
				
				updateInfo.append(openTime+"  当前电量: "+(String)mes.obj+"%\r\n");
				break;
			
			case 22: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 23: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 24: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 25: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			
			case 26: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;

			case 27: 
	
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;

			case 28: 
	
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 29: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 30:
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
				
			case 31:
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			case 41: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 42: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 43: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 44: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			
			case 45: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;

			case 46: 
	
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;

			case 47: 
	
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 48: 
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			
			case 49:
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
				
			case 50:
				
				updateInfo.append(openTime+(String)mes.obj+"\r\n");
				break;
			default:
				break;
			}
			return false;
		}
	});

	private ScanCallback scanCallback = new ScanCallback() {

		public void onScanResult(int callbackType, ScanResult result) {
			BluetoothDevice device = result.getDevice();
			int rssi = result.getRssi();
			byte[] data = result.getScanRecord().getBytes();
			if (data[5] == 0x01 && data[6] == 0x02) {
				String nowAddress = device.getAddress();
				if (nowAddress == m_myData.address) {
					if (m_myData.rssi != rssi) {
						m_myData.rssi = rssi;
						m_myHandler.sendEmptyMessage(3);
					}
				} else if (rssi > m_myData.rssi || m_myData.device == null) {
					m_myData.device = device;
					m_myData.name = device.getName();
					m_myData.address = nowAddress;
					m_myData.rssi = rssi;
					m_myData.count = 0;
					m_myData.version = "";
					m_myHandler.sendEmptyMessage(3);
				}
			}
		}
	};
	
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				gatt.discoverServices();

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				mBluetoothGatt.disconnect();
				mBluetoothGatt.close();
				mBluetoothGatt = null;
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.w("TAG", "onServicesDiscovered");

			if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothGattService service = gatt.getService(bltServerUUID);
				readCharacteristic = service.getCharacteristic(readDataUUID);
				writeCharacteristic = service.getCharacteristic(writeDataUUID);

				gatt.setCharacteristicNotification(readCharacteristic, true);

				BluetoothGattDescriptor descriptor = readCharacteristic
						.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
				descriptor
						.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				gatt.writeDescriptor(descriptor);

			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			SendData(gettoken);
		}

		private Message msg;
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {

			byte[] values = characteristic.getValue();
			byte[] x = new byte[16];
			System.arraycopy(values, 0, x, 0, 16);
			final byte[] mingwen;
			if (m_xinMiYue) {
				mingwen = Decrypt(x, key2);
			} else {
				mingwen = Decrypt(x, key);
			}

			if (mingwen != null && mingwen.length == 16) {
				if (mingwen[0] == 0x06 && mingwen[1] == 0x02) {
					token[0] = mingwen[3];
					token[1] = mingwen[4];
					token[2] = mingwen[5];
					token[3] = mingwen[6];
					buttonGetToken.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							
							updateInfo.append(openTime +"  令牌："+String.valueOf(mingwen[3])+String.valueOf(mingwen[4])+String.valueOf(mingwen[5])+String.valueOf(mingwen[6])
									+"\n芯片平台类型："+String.valueOf(mingwen[7])
									+"\n固件版本号："+String.valueOf(mingwen[8])
									+"\n设备类型："+String.valueOf(mingwen[9])+"\n\r");
							
						}
					});
					m_myData.version = String.valueOf(mingwen[8]) + "."
							+ String.valueOf(mingwen[9]);
					m_myHandler.sendEmptyMessage(3);
					
					if (caozuo == CAOZUO_OPEN) {
						byte[] openLock = { 0x05, 0x01, 0x06, mima[0], mima[1],
								mima[2], mima[3], mima[4], mima[5], token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00 };
						SendData(openLock);
					} else if (caozuo == CAOZUO_CLOSE) {
						byte[] closeLock = { 0x05, 0x0c, 0x01, 0x01, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(closeLock);
					} else if (caozuo == CAOZUO_DIANLIANG) {
						byte[] dianLiang = { 0x02, 0x01, 0x01, 0x01, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(dianLiang);
					} else if (caozuo == CAOZUO_OAD) {
						byte[] oad = { 0x03, 0x01, 0x01, 0x01, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(oad);
					} else if (caozuo == CAOZUO_MIMA) {
						byte[] sendMiMa1 = { 0x05, 0x03, 0x06, mima[0],
								mima[1], mima[2], mima[3], mima[4], mima[5],
								token[0], token[1], token[2], token[3], 0x00,
								0x00, 0x00 };
//						byte[] sendMiMa2 = { 0x05, 0x03, 0x06, mima[0],
//								mima[1], mima[2], mima[3], mima[4], mima[5],
//								token[0], token[1], token[2], token[3], 0x00,
//								0x00, 0x00 };
						SendData(sendMiMa1);
//						SendData(sendMiMa2);
					} else if (caozuo == CAOZUO_MIYUE) {
						byte[] sendMiYue1 = { 0x07, 0x01, 0x08, key2[0],
								key2[1], key2[2], key2[3], key2[4], key2[5],
								key2[6], key2[7], token[0], token[1], token[2],
								token[3], 0x00 };
						SendData(sendMiYue1);
					} else if (caozuo == CAOZUO_ZHUANGTAI) {
						byte[] zhuangTai = { 0x05, 0x0E, 0x01, 0X01, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(zhuangTai);
					}else if (caozuo == CAOZUO_HUANCHESTATE) {
						byte[] setHuanche = { 0x05, 0x14, 0x01, 0X01, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(setHuanche);
					}else if (caozuo == CAOZUO_SEARCHLOCKMODEL) {
						byte[] searchLockModle = { 0x05, 0x20, 0x01, 0X00, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(searchLockModle);
					}else if (caozuo == CAOZUO_GIM_ID) {
						byte[] searchGsmVer = { 0x05, 0x23, 0x01, 0X00, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(searchGsmVer);
					}else if (caozuo == CAOZUO_GSM_VER) {
						byte[] searchGimId = { 0x05, 0x24, 0x01, 0X00, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(searchGimId);
					}else if (caozuo == CAOZUO_SETLOCKMODEL) {
						byte[] zhengchangModle = { 0x05, 0x21, 0x01, 0X00, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						
						byte[] yunshuModle = { 0x05, 0x21, 0x01, 0X01, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						
						byte[] chongqiModle = { 0x05, 0x21, 0x01, 0X02, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(zhengchangModle);
						SendData(yunshuModle);
						SendData(chongqiModle);
					}else if (caozuo == CAOZUO_SEARCHLOCKWORKSTATE) {
						byte[] searchLockWork = { 0x05, 0x22, 0x01, 0X00, token[0],
								token[1], token[2], token[3], 0x00, 0x00, 0x00,
								0x00, 0x00, 0x00, 0x00, 0x00 };
						SendData(searchLockWork);
						}
					
				} else if (mingwen[0] == 0x05 && mingwen[1] == 0x02) {
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler.obtainMessage(1, 1, 1,"  开电成功！");
						m_myHandler.sendMessage(msg);
					} else {
						Message msg = m_myHandler.obtainMessage(1, 1, 1, "  开电失败！");
						m_myHandler.sendMessage(msg);
					}
				} else if (mingwen[0] == 0x02 && mingwen[1] == 0x02) {
					if (mingwen[3] == 0xff) {
						Message msg = m_myHandler.obtainMessage(9, 1, 1,
								"检测电量失败");
						m_myHandler.sendMessage(msg);
					} else {
						Message msg = m_myHandler.obtainMessage(9, 1, 1,
								String.valueOf(mingwen[3]));
						m_myHandler.sendMessage(msg);
					}
				} else if (mingwen[0] == 0x03 && mingwen[1] == 0x02) {
					Message msg = m_myHandler.obtainMessage(5, 1, 1,
							String.valueOf(mingwen[3]));
					m_myHandler.sendMessage(msg);
				} else if (mingwen[0] == 0x05 && mingwen[1] == 0x05) {
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler.obtainMessage(10, 1, 1,
								"  修改密码成功！");
						m_myHandler.sendMessage(msg);
					} else {
						Message msg = m_myHandler.obtainMessage(10, 1, 1,
								"  修改密码失败！");
						m_myHandler.sendMessage(msg);
					}
				} else if (mingwen[0] == 0x05 && mingwen[1] == 0x08) {
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler
								.obtainMessage(2, 1, 1, "  关电成功！");
						m_myHandler.sendMessage(msg);
					} else {
						Message msg = m_myHandler
								.obtainMessage(2, 1, 1, "  关电失败！");
						m_myHandler.sendMessage(msg);
					}
				} else if (mingwen[0] == 0x05 && mingwen[1] == 0x0d) {
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler
								.obtainMessage(11, 1, 1, "  关电成功！");
						m_myHandler.sendMessage(msg);
					} else {
						Message msg = m_myHandler
								.obtainMessage(11, 1, 1, "  关电失败！");
						m_myHandler.sendMessage(msg);
					}
				} else if (mingwen[0] == 0x05 && mingwen[1] == 0x0f) {
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler
								.obtainMessage(12, 1, 1, "  开启状态！");
						m_myHandler.sendMessage(msg);
					} else if(mingwen[3] == 0x01) {
						Message msg = m_myHandler
								.obtainMessage(12, 1, 1, "  关闭状态！");
						m_myHandler.sendMessage(msg);
					}
				}else if (mingwen[0] == 0x05 && mingwen[1] == 0x15) {
					
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler
								.obtainMessage(13, 1, 1, "  还车设置成功！");
						m_myHandler.sendMessage(msg);
					} else if(mingwen[3] == 0x01) {
						Message msg = m_myHandler
								.obtainMessage(13, 1, 1, "  还车设置失败！");
						m_myHandler.sendMessage(msg);
					}
					
				}else if (mingwen[0] == 0x05 && mingwen[1] == 0x20) {
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler
								.obtainMessage(15, 1, 1, "  当前为撤防状态！");
						m_myHandler.sendMessage(msg);
					} else if(mingwen[3] == 0x01) {
						Message msg = m_myHandler
								.obtainMessage(15, 1, 1, "  当前为设防状态！");
						m_myHandler.sendMessage(msg);
					}
				} else if (mingwen[0] == 0x05 && mingwen[1] == 0x21) {
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler
								.obtainMessage(40, 1, 1, "  锁模式设置成功！");
						m_myHandler.sendMessage(msg);
					} else if(mingwen[3] == 0x01) {
						Message msg = m_myHandler
								.obtainMessage(40, 1, 1, "  锁模式设置失败！");
						m_myHandler.sendMessage(msg);
					}
				}else if (mingwen[0] == 0x05 && mingwen[1] == 0x22) {
					if (mingwen[2] == 0x08) {
						
						byte R1 = mingwen[3];
						byte R2 = mingwen[4];
						byte R3 = mingwen[5];
						Log.e("tag===========", R3+"");
						byte R4 = mingwen[6];
						byte R5 = mingwen[7];
						byte R6 = mingwen[8];
						byte R7 = mingwen[9];
						byte R8 = mingwen[10];
						
						byte R1_Byte0 =(byte) (R1 & 1);
						byte R1_Byte1 =(byte) (R1 & 1<<1);
						byte R1_Byte2 =(byte) (R1 & 1<<2);
						byte R1_Byte3 =(byte) (R1 & 1<<3);
						
						if (R1_Byte0==1) {
							Message msg = m_myHandler
									.obtainMessage(17, 1, 1, "  电源状态：关");
							m_myHandler.sendMessage(msg);
						}else if (R1_Byte0 == 0) {
							Message msg = m_myHandler
									.obtainMessage(17, 1, 1, "  电源状态：开");
							m_myHandler.sendMessage(msg);
						}
						
						if (R1_Byte1 == 2) {
							Message msg = m_myHandler
									.obtainMessage(180, 1, 1, "  振动功能：故障");
							m_myHandler.sendMessage(msg);
						}else if (R1_Byte1 == 0) {
							Message msg = m_myHandler
									.obtainMessage(180, 1, 1, "  振动功能：正常");
							m_myHandler.sendMessage(msg);
						}
						
						if (R1_Byte2 == 4) {
							Message msg = m_myHandler
									.obtainMessage(19, 1, 1, "  振动状态：震动");
							m_myHandler.sendMessage(msg);
						}else if (R1_Byte2 == 0) {
							Message msg = m_myHandler
									.obtainMessage(19, 1, 1, "  振动状态：静止");
							m_myHandler.sendMessage(msg);
						}
						
						if (R1_Byte3 == 8) {
							Message msg = m_myHandler
									.obtainMessage(200, 1, 1, "  充放电状态：充电");
							m_myHandler.sendMessage(msg);
						}else if (R1_Byte3 == 0) {
							Message msg = m_myHandler
									.obtainMessage(200, 1, 1, "  充放电状态：放电");
							m_myHandler.sendMessage(msg);
						}
						
							msg = m_myHandler
								.obtainMessage(210, 1, 1, String.valueOf(R2));
						m_myHandler.sendMessage(msg);
						
						if (R3 ==0) {
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：关电状态");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==1){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：查询SIM卡");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==2){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：注册网络");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==3){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：初始化短信功能");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==4){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：查询GPRS网络");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==5){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：链接PPP");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==6){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：链接TCP");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==7){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：与平台通讯成功");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==(byte)0xFE){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：已上电");
							m_myHandler.sendMessage(msg);
						}
						if(R3 ==(byte)0xFF){
							msg = m_myHandler
									.obtainMessage(22, 1, 1, "  GSM状态：故障");
							m_myHandler.sendMessage(msg);
						}
						
						msg = m_myHandler
								.obtainMessage(23, 1, 1, "  上一次GPRS上线时间："+String.valueOf(R4));
						m_myHandler.sendMessage(msg);
						
						msg = m_myHandler
								.obtainMessage(24, 1, 1, "  上一次GSM信号强度值："+String.valueOf(R5));
						m_myHandler.sendMessage(msg);
						
						if (R6 ==0) {
							msg = m_myHandler
									.obtainMessage(25, 1, 1, "  GPS状态：关闭状态");
							m_myHandler.sendMessage(msg);
						}else if(R6 ==1) {
							msg = m_myHandler
									.obtainMessage(25, 1, 1, "  GPS状态：未定位");
							m_myHandler.sendMessage(msg);
							
						}else if(R6 ==2) {
							msg = m_myHandler
									.obtainMessage(25, 1, 1, "  GPS状态：已定位");
							m_myHandler.sendMessage(msg);
							
						}else if(R6 ==(byte)0xFF) {
							msg = m_myHandler
									.obtainMessage(25, 1, 1, "  GPS状态：故障");
							m_myHandler.sendMessage(msg);
						}
						
						msg = m_myHandler
								.obtainMessage(26, 1, 1, "  上一次GPS定位时间："+String.valueOf(R7));
						m_myHandler.sendMessage(msg);
						
						msg = m_myHandler
								.obtainMessage(27, 1, 1, "  上一次GPS收星数量："+String.valueOf(R8));
						m_myHandler.sendMessage(msg);
						
					}
				}else if (mingwen[0] == 0x05 && mingwen[1] == 0x08) {
					if (mingwen[3] == 0x00) {
						msg = m_myHandler.obtainMessage(29, 1, 1,"  关锁成功！");
						m_myHandler.sendMessage(msg);
					}else if (mingwen[3] == 0x01) {
						msg = m_myHandler.obtainMessage(29, 1, 1,"  关锁失败！");
						m_myHandler.sendMessage(msg);
					}else if (mingwen[3] == 0x02) {
						msg = m_myHandler.obtainMessage(29, 1, 1,"  关锁异常！");
						m_myHandler.sendMessage(msg);
					}
				}else if (mingwen[0] == 0x03 && mingwen[1] == 0x02) {
					if (mingwen[3] == 0x00) {
						Message msg = m_myHandler
								.obtainMessage(30, 1, 1, "  固件升级响应：开始升级！");
						m_myHandler.sendMessage(msg);
					} else if(mingwen[3] == 0x01) {
						Message msg = m_myHandler
								.obtainMessage(30, 1, 1, "  固件升级响应：不支持升级！");
						m_myHandler.sendMessage(msg);
					}
				}else if (mingwen[0] == 0x05 && mingwen[1] == 0x23) {
					
						msg = m_myHandler
								.obtainMessage(28, 1, 1, "  GSM ID："
						+String.valueOf(mingwen[2])
						+String.valueOf(mingwen[3])
						+String.valueOf(mingwen[4])
						+String.valueOf(mingwen[5])
						+String.valueOf(mingwen[6])
						+String.valueOf(mingwen[7]));
						m_myHandler.sendMessage(msg);
				}else if (mingwen[0] == 0x05 && mingwen[1] == 0x24) {
					String GSMVer = null;
					byte[] GSMVersion = Arrays.copyOfRange(mingwen,2,mingwen.length);
					try {
						GSMVer = new String(GSMVersion,"ascii");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
						msg = m_myHandler
								.obtainMessage(28, 1, 1, "  GSM版本号是："
						+GSMVer);
						m_myHandler.sendMessage(msg);
						
						
					
					
			}
				
				else if (mingwen[0] == 0x07 && mingwen[1] == 0x03) {
					if (mingwen[3] == 0x00) {
						m_xinMiYue = true;
						Message msg = m_myHandler.obtainMessage(2, 1, 1,
								"  修改密钥成功");
						m_myHandler.sendMessage(msg);
					} else {
						Message msg = m_myHandler.obtainMessage(2, 1, 1,
								"  修改密钥失败");
						m_myHandler.sendMessage(msg);
					}
				} else if (mingwen[0] == (byte) 0xcb && mingwen[1] == 0x05
						&& mingwen[2] == 0x03) {
					byte[] sendMiMa2 = { 0x05, 0x04, 0x06, mima2[0], mima2[1],
							mima2[2], mima2[3], mima2[4], mima2[5], token[0],
							token[1], token[2], token[3], 0x00, 0x00, 0x00 };
					SendData(sendMiMa2);
				} else if (mingwen[0] == (byte) 0xcb && mingwen[1] == 0x07
						&& mingwen[2] == 0x01) {
					byte[] sendMiYue2 = { 0x07, 0x02, 0x08, key2[8], key2[9],
							key2[10], key2[11], key2[12], key2[13], key2[14],
							key2[15], token[0], token[1], token[2], token[3],
							0x00 };
					SendData(sendMiYue2);
				}
			}
		
		}
		
		
	};

	private final BluetoothGattCallback mGattCallbackOAD = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				gatt.discoverServices();
				getTime();
				Message mess = m_myHandler.obtainMessage(7, 1, 1, "  已连接");
				m_myHandler.sendMessage(mess);

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				mBluetoothGatt.disconnect();
				mBluetoothGatt.close();
				mBluetoothGatt = null;
				getTime();
				Message mess = m_myHandler.obtainMessage(7, 1, 1, "  已断开连接");
				m_myHandler.sendMessage(mess);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {

			if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothGattService service = gatt
						.getService(OAD_SERVICE_UUID);
				List<BluetoothGattCharacteristic> mCharListOad = service
						.getCharacteristics();

				if (mCharListOad.size() == 2) {
					readCharacteristic = mCharListOad.get(0);
					writeCharacteristic = mCharListOad.get(1);
					writeCharacteristic
							.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
					m_myHandler.sendEmptyMessage(6);
				}
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			mBusy = false;
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			mBusy = false;
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			mBusy = false;
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			mBusy = false;
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			mBusy = false;
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 288: {
				String result = data.getExtras().getString("result");
				InsertThread hereThread = new InsertThread(m_nowMac, result);
				new Thread(hereThread).start();
				break;
			}
			case 688: {
				String result = data.getExtras().getString("result");
				ShouQuanThread hereThread = new ShouQuanThread(m_nowMac, result);
				new Thread(hereThread).start();
				break;
			}
			case 788: {
				String result = data.getExtras().getString("result");
				QuMiYaoThread hereThread = new QuMiYaoThread(result);
				new Thread(hereThread).start();
				break;
			}
			case 188: {
				mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

				bleScanFilters = new ArrayList<>();
				bleScanFilters.add(new ScanFilter.Builder().setServiceUuid(
						findServerUUID).build());

				bleScanSettings = new ScanSettings.Builder().build();

				if (mBluetoothGatt != null) {
					mBluetoothGatt.disconnect();
					mBluetoothGatt.close();
					mBluetoothGatt = null;
				}

				m_myData.device = null;
				m_myData.address = "";
				m_myData.count = 0;
				m_myData.name = "";
				m_myData.version = "";
				info.setText("");

				mBluetoothLeScanner.startScan(null, bleScanSettings,
						scanCallback);
				break;
			}
			default: {
				break;
			}
			}
		} else if (requestCode == 188) {
			Toast.makeText(this, "需要打开蓝牙", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private boolean checkFile() {
		File mDir = Environment
				.getExternalStoragePublicDirectory(FW_CUSTOM_DIRECTORY);
		String filepath = mDir.getAbsolutePath() + File.separator
				+ "upFile.bin";
		// Load binary file
		try {
			// Read the file raw into a buffer
			InputStream stream;

			File f = new File(filepath);
			stream = new FileInputStream(f);
			stream.close();
			return true;
		} catch (IOException e) {
			// Handle exceptions here
			updateInfo.append("File open failed: " + filepath + "\n");
			return false;
		}
	}

	private boolean loadFile() {
		boolean fSuccess = false;

		File mDir = Environment
				.getExternalStoragePublicDirectory(FW_CUSTOM_DIRECTORY);
		String filepath = mDir.getAbsolutePath() + File.separator
				+ "upFile.bin";
//		+ "upFile.bin";
		// Load binary file
		try {
			// Read the file raw into a buffer
			InputStream stream;

			File f = new File(filepath);
			stream = new FileInputStream(f);

			stream.read(mFileBuffer, 0, mFileBuffer.length);
			stream.close();
			fSuccess = true;
		} catch (IOException e) {
			// Handle exceptions here
			updateInfo.append("File open failed: " + filepath + "\n");
			return false;
		}

		// Show image info
		mFileImgHdr.ver = Conversion
				.buildUint16(mFileBuffer[5], mFileBuffer[4]);
		mFileImgHdr.len = Conversion
				.buildUint16(mFileBuffer[7], mFileBuffer[6]);
		mFileImgHdr.imgType = ((mFileImgHdr.ver & 1) == 1) ? 'B' : 'A';
		System.arraycopy(mFileBuffer, 8, mFileImgHdr.uid, 0, 4);

		return fSuccess;
	}

	private void startProgramming() {

		updateInfo.append("startProgramming\n");
		if (!loadFile()) {
			updateInfo.append("loadFile failed!\n");
			return;
		}

		mProgressBar.setProgress(0);

		updateInfo.append("Programming started\n");
		mProgramming = true;
		buttonUpgrade.setText("取消升级");

		// Prepare image notification
		byte[] buf = new byte[OAD_IMG_HDR_SIZE + 2 + 2];
		buf[0] = Conversion.loUint16(mFileImgHdr.ver);
		buf[1] = Conversion.hiUint16(mFileImgHdr.ver);
		buf[2] = Conversion.loUint16(mFileImgHdr.len);
		buf[3] = Conversion.hiUint16(mFileImgHdr.len);
		System.arraycopy(mFileImgHdr.uid, 0, buf, 4, 4);

		// Send image notification

		readCharacteristic.setValue(buf);
		mBluetoothGatt.writeCharacteristic(readCharacteristic);

		// Initialize stats
		mProgInfo.reset();

		// Start the programming thread
		new Thread(new OadTask()).start();
	}

	private void stopProgramming() {
		mProgramming = false;
		buttonUpgrade.setText("开始升级");

		if (mProgInfo.iBlocks == mProgInfo.nBlocks) {
			updateInfo.setText("Programming complete!\n");
			mBluetoothGatt = null;
			restartBle();
		} else {
			updateInfo.append("Programming cancelled\n");
		}
	}

	private void programBlock() {
		if (!mProgramming)
			return;

		if (mProgInfo.iBlocks < mProgInfo.nBlocks) {
			mProgramming = true;
			String msg = new String();

			// Prepare block
			mOadBuffer[0] = Conversion.loUint16(mProgInfo.iBlocks);
			mOadBuffer[1] = Conversion.hiUint16(mProgInfo.iBlocks);
			System.arraycopy(mFileBuffer, mProgInfo.iBytes, mOadBuffer, 2,
					OAD_BLOCK_SIZE);
			

			// Send block
			mBusy = true;
			writeCharacteristic.setValue(mOadBuffer);
			boolean success = mBluetoothGatt
					.writeCharacteristic(writeCharacteristic);

			if (success) {
				// Update stats
				mProgInfo.iBlocks++;
				mProgInfo.iBytes += OAD_BLOCK_SIZE;

				m_myHandler.sendEmptyMessage(8);

				if (!waitIdle(GATT_WRITE_TIMEOUT)) {
					mProgramming = false;
					success = false;
					msg = "GATT write timeout\n";
				}
			} else {
				mProgramming = false;
				msg = "GATT writeCharacteristic failed\n";
			}
			if (!success) {
				Message mess = m_myHandler.obtainMessage(7, 1, 1, msg);
				m_myHandler.sendMessage(mess);
			}
		} else {
			mProgramming = false;
		}

		if (!mProgramming) {
			runOnUiThread(new Runnable() {
				public void run() {
					stopProgramming();
				}
			});
		}
	}

	private class OadTask implements Runnable {
		@Override
		public void run() {
			while (mProgramming) {
				try {
					Thread.sleep(SEND_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < BLOCKS_PER_CONNECTION & mProgramming; i++) {
					programBlock();
				}
			}
		}
	}

	public boolean waitIdle(int timeout) {
		timeout /= 10;
		while (--timeout > 0) {
			if (mBusy)
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			else
				break;
		}

		return timeout > 0;
	}

	class InsertThread implements Runnable {
		private String mac, ma;

		public InsertThread(String imac, String ima) {
			mac = imac;
			ma = ima;
		}

		@Override
		public void run() {

			try {
				// 使用get方式
				
				mac = "0102" + mac.replaceAll(":", "").toLowerCase();
								String hereUri = "mac="
						+ mac + "&ma=" + ma;

				HttpGet httpRequest = new HttpGet(hereUri);

				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

					Message msg = m_myHandler.obtainMessage(2, 1, 1, "上传成功");
					m_myHandler.sendMessage(msg);

				} else {
					Message msg = m_myHandler.obtainMessage(2, 1, 1, "上传失败");
					m_myHandler.sendMessage(msg);
				}

			} catch (Exception e) {
				Message msg = m_myHandler.obtainMessage(2, 1, 1,
						"上传失败：" + e.getMessage());
				m_myHandler.sendMessage(msg);
			}
		}
	}


	class ShouQuanThread implements Runnable {
		private String mac, ma;

		public ShouQuanThread(String imac, String ima) {
			mac = imac;
			ma = ima;
		}

		@Override
		public void run() {

			try {
				// 使用get方式
				
				mac =  mac.replaceAll(":", "").toUpperCase();
				ma = URLEncoder.encode(ma);
				String hereUri = "="
						+ ma + "&data2=" + mac;

				HttpGet httpRequest = new HttpGet(hereUri);

				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

					Message msg = m_myHandler.obtainMessage(2, 1, 1, "上传成功");
					m_myHandler.sendMessage(msg);

				} else {
					Message msg = m_myHandler.obtainMessage(2, 1, 1, "上传失败");
					m_myHandler.sendMessage(msg);
				}

			} catch (Exception e) {
				Message msg = m_myHandler.obtainMessage(2, 1, 1,
						"上传失败：" + e.getMessage());
				m_myHandler.sendMessage(msg);
			}
		}
	}


	class QuMiYaoThread implements Runnable {
		private String ma;

		public QuMiYaoThread(String ima) {
			ma = ima;
		}

		@Override
		public void run() {

			try {
				// 使用get方式
				
				String[] temp = ma.split("[?]");

				String hereUri = "?"
						+ temp[1];

				HttpGet httpRequest = new HttpGet(hereUri);

				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String strResult = EntityUtils.toString(
							httpResponse.getEntity(), "GB2312");

					int startIndex = strResult.indexOf("currentKey");
					int startIndex2 = strResult.indexOf("password");
					
					if(startIndex > 0)
					{
						int endIndex = strResult.indexOf("\"",startIndex+13);
						int endIndex2 = strResult.indexOf("\"",startIndex2+11);
						
						String mima22 = strResult.substring(startIndex+13, endIndex);
						String[] items = mima22.split(",");
						if(items.length == 16)
						{
							for(int i = 0; i < 16; i++)
							{
								key[i] = Byte.parseByte(items[i]);
							}


							String mima33 = strResult.substring(startIndex2+11, endIndex2);
							String[] items2 = mima33.split(",");
							if(items2.length == 6)
							{
								for(int i = 0; i < 6; i++)
								{
									mima[i] = Byte.parseByte(items2[i]);
								}
								Message msg = m_myHandler.obtainMessage(2, 1, 1, "获取成功");
								m_myHandler.sendMessage(msg);
							}
							else
							{
								Message msg = m_myHandler.obtainMessage(2, 1, 1, "获取失败");
								m_myHandler.sendMessage(msg);
							}
						}
						else
						{
							Message msg = m_myHandler.obtainMessage(2, 1, 1, "获取失败");
							m_myHandler.sendMessage(msg);
						}
						

					}
					else
					{
						Message msg = m_myHandler.obtainMessage(2, 1, 1, "获取失败");
						m_myHandler.sendMessage(msg);
					}

				} else {
					Message msg = m_myHandler.obtainMessage(2, 1, 1, "获取失败");
					m_myHandler.sendMessage(msg);
				}

			} catch (Exception e) {
				Message msg = m_myHandler.obtainMessage(2, 1, 1,
						"获取失败：" + e.getMessage());
				m_myHandler.sendMessage(msg);
			}
		}
	}

	private class ImgHdr {
		short ver;
		short len;
		Character imgType;
		byte[] uid = new byte[4];
	}

	private class ProgInfo {
		int iBytes = 0; // Number of bytes programmed
		short iBlocks = 0; // Number of blocks programmed
		short nBlocks = 0; // Total number of blocks

		void reset() {
			iBytes = 0;
			iBlocks = 0;
			nBlocks = (short) (mFileImgHdr.len / (OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
		}
	}


}
