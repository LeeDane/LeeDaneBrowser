package com.cn.leedane.browser.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cn.leedane.browser.util.ConstantsUtil;

public class BaseSQLiteOpenHelper extends SQLiteOpenHelper {

	public static  final String TAG = "BaseSQLiteOpenHelper";

	/**
	 * 普通表创建的构造方法
	 * @param context
	 * @param name  数据库名称
	 */
	/*public BaseSQLiteOpenHelper(Context context, String name) {
		super(context, name, null, ConstantsUtil.DB_VERSION);
	}*/


	/*私有的静态对象，为整个应用程序提供一个sqlite操作的静态实例，并保证只能通过下面的静态方法getHelper(Context context)获得，
	 * 防止使用时绕过同步方法改变它*/
	private static BaseSQLiteOpenHelper instance;//这里主要解决死锁问题,是static就能解决死锁问题
	/**
	 * 私有的构造函数，只能自己使用，防止绕过同步方法生成多个实例，
	 * @param context
	 */
	private BaseSQLiteOpenHelper(Context context, String databaseName) {

		//DB_VERSION：数据库的版本号
		super(context, databaseName, null, ConstantsUtil.DB_VERSION);
	}
	/**
	 * 为应用程序提供一个单一的入口，保证应用程序使用同一个对象操作数据库，不会因为对象不同而使同步方法失效
	 * @param context 上下文
	 * @return  instance
	 */
	public static BaseSQLiteOpenHelper getHelper(Context context, String databaseName){
		if(instance==null)
			instance=new BaseSQLiteOpenHelper(context, databaseName);
		return instance;
	}
	/**
	 * 当数据库不存在或者第一次执行的时候才调用 (non-Javadoc)
	 * @see SQLiteOpenHelper#onCreate(SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(MySettingDataBase.CREATE_MY_SETTING_TABLE);
		//插入初始化设置的SQL

	}

	/**
	 * 当传进去的版本比上一版本高时调用的方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.i(TAG, "call update");
		/** 开发阶段先不执行 **/
		/*
		 * db.execSQL("DROP TABLE IF EXISTS " + ConstantUtils.TABLE_USER_ALL);
		 * //删除USER_ALL表 db.execSQL("DROP TABLE IF EXISTS " +
		 * ConstantUtils.TABLE_MONEY_IN);//删除MONEY_IN表
		 * db.execSQL("DROP TABLE IF EXISTS " +
		 * ConstantUtils.TABLE_MONEY_IN_TYPE);//删除MONEY_IN_TYPE表
		 * db.execSQL("DROP TABLE IF EXISTS " +
		 * ConstantUtils.TABLE_OPTIONS);//删除OPTIONS表
		 *
		 * this.onCreate(db); //执行创建操作!
		 */
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE);
	}

}
