package com.jackchance.yixun.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.jackchance.yixun.Bean.QRInfo;

import java.util.ArrayList;
import java.util.List;

public class SQLOrderDao {
    private static final String TAG = "OrdersDao";

    // 列定义
    private final String[] ORDER_COLUMNS = new String[] {"mapid", "mapname","groupid","x",
                                        "y","modelname","detail","time"};

    private Context context;
    private MyDBOpenHelper ordersDBHelper;

    public SQLOrderDao(Context context) {
        this.context = context;
        ordersDBHelper = new MyDBOpenHelper(context);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select count(Id) from Orders
            cursor = db.query(MyDBOpenHelper.TABLE_NAME, new String[]{"COUNT(modelname)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 初始化数据
     */
    public void initTable(){
        SQLiteDatabase db = null;

        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

//            db.execSQL("insert into " + MyDBOpenHelper.TABLE_NAME +
//                    " (mapid, mapname, groupid, x,y,modelname,detail,time) " +
//                    "values ('10322', '双安商场', 0, 0,0,'KFC','停车位','2018/4/11')");
//            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 执行自定义SQL语句
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")){
                Toast.makeText(context, "select", Toast.LENGTH_SHORT).show();
            }else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")){
                db = ordersDBHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 查询数据库中所有数据
     */
    public List<QRInfo> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(MyDBOpenHelper.TABLE_NAME, ORDER_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<QRInfo> orderList = new ArrayList<QRInfo>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseOrder(cursor));
                }
                return orderList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 新增一条数据
     */
    public boolean insertDate(QRInfo qrInfo){
        SQLiteDatabase db = null;
        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();
            // insert into Orders(Id, CustomName, OrderPrice, Country) values (7, "Jne", 700, "China");
            ContentValues contentValues = new ContentValues();
            contentValues.put("mapid",qrInfo.getMapid());
            contentValues.put("mapname", qrInfo.getMapname());
            contentValues.put("groupid", qrInfo.getGroupid());
            contentValues.put("x", qrInfo.getX());
            contentValues.put("y", qrInfo.getY());
            contentValues.put("modelname", qrInfo.getModelname());
            contentValues.put("detail", qrInfo.getDetail());
            contentValues.put("time", qrInfo.getImageURL());
            db.insertOrThrow(MyDBOpenHelper.TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
            return true;
        }catch (SQLiteConstraintException e){
            Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 删除一条数据  此处删除Id为7的数据
     */
    public boolean deleteOrder(String time) {
        SQLiteDatabase db = null;
        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from Orders where Id = 7
            db.delete(MyDBOpenHelper.TABLE_NAME, "time = ?", new String[]{time});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 修改一条数据  此处将Id为6的数据的OrderPrice修改了800
     */
    public boolean updateOrder(QRInfo qrInfo){
        SQLiteDatabase db = null;
        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

            // update Orders set OrderPrice = 800 where Id = 6
            ContentValues contentValues = new ContentValues();
            contentValues.put("mapid",qrInfo.getMapid());
            contentValues.put("mapname", qrInfo.getMapname());
            contentValues.put("groupid", qrInfo.getGroupid());
            contentValues.put("x", qrInfo.getX());
            contentValues.put("y", qrInfo.getY());
            contentValues.put("modelname", qrInfo.getModelname());
            contentValues.put("detail", qrInfo.getDetail());
            contentValues.put("time", qrInfo.getImageURL());
            db.update(MyDBOpenHelper.TABLE_NAME,
                    contentValues,
                    "time = ?",
                    new String[]{qrInfo.getImageURL()});
            db.setTransactionSuccessful();
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

        return false;
    }

    /**
     * 数据查询  此处将用户名为"Bor"的信息提取出来
     */
    public List<QRInfo> getBorOrder(String modelname){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();

            // select * from Orders where CustomName = 'Bor'
            cursor = db.query(MyDBOpenHelper.TABLE_NAME,
                    ORDER_COLUMNS,
                    "modelname = ?",
                    new String[] {modelname},
                    null, null, null);

            if (cursor.getCount() > 0) {
                List<QRInfo> orderList = new ArrayList<QRInfo>(cursor.getCount());
                while (cursor.moveToNext()) {
                    QRInfo order = parseOrder(cursor);
                    orderList.add(order);
                }
                return orderList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }


    /**
     * 将查找到的数据转换成QRInfo类
     */
    private QRInfo parseOrder(Cursor cursor){
        QRInfo qrInfo = new QRInfo();
        qrInfo.setMapid(cursor.getString(cursor.getColumnIndex("mapid")));
        qrInfo.setMapname(cursor.getString(cursor.getColumnIndex("mapname")));
        qrInfo.setGroupid(cursor.getInt(cursor.getColumnIndex("groupid")));
        qrInfo.setX((float)cursor.getDouble(cursor.getColumnIndex("x")));
        qrInfo.setY((float)cursor.getDouble(cursor.getColumnIndex("y")));
        qrInfo.setDirection(1);
        qrInfo.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
        qrInfo.setModelname(cursor.getString(cursor.getColumnIndex("modelname")));
        qrInfo.setModelsite("www.baidu.com");
        qrInfo.setArid("1234");
        qrInfo.setImageURL(cursor.getString(cursor.getColumnIndex("time")));
        return qrInfo;
    }
}
