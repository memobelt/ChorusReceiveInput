package guillermobeltran.chorusinput;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateService extends Service {
    int totalChats = 0;
    ChorusChat chat = new ChorusChat();
    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId) {

        DBHelper mDbHelper = new DBHelper(getApplicationContext(),null);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.DatabaseEntry.COLUMN_NAME_TASK,
                DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE,
                DatabaseContract.DatabaseEntry.COLUMN_NAME_SIZE
        };
        Cursor cursor = db.query(DatabaseContract.DatabaseEntry.TABLE_NAME,projection,null,null,null
                ,null,null);
        ArrayList<String> list = new ArrayList<String>();
        if(cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                String task = cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DatabaseEntry.COLUMN_NAME_TASK));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE));
                int size = cursor.getInt(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DatabaseEntry.COLUMN_NAME_SIZE));
                totalChats++;
                notify(role,task,size);
                list.add(task);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        mDbHelper.close();
    }
    public void notify(final String role, final String task, final int size){
        AQuery aq = new AQuery(getApplicationContext());
        Map<String, Object> params = chat.setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester","-1");
        params.put("role", role);
        params.put("task", task);
        aq.ajax(chat._chatUrl, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                status.getMessage();
                if (json != null) {
                    if (json.length() > size) {
                        int numNotifications = json.length() - size;
                        Intent viewIntent = new Intent(getApplicationContext(), ChorusChat.class);
                        viewIntent.putExtra("ChatNum", task);
                        viewIntent.putExtra("Role", role);
                        //viewIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Chorus").setAutoCancel(true)
                                .setWhen(System.currentTimeMillis()).setContentIntent(viewPendingIntent)
                                .setGroup("notification_group");
                        mBuilder.setContentText(Integer.toString(numNotifications) + " New Messages " +
                                "in Chat " + task);
                        NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
                        nm.notify(Integer.parseInt(task), mBuilder.build());

                        DBHelper mDbHelper = new DBHelper(getApplicationContext(),null);
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_SIZE, json.length());
                        int i = db.update(DatabaseContract.DatabaseEntry.TABLE_NAME, values, "task = " + task, null);
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "failed to update", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Json null",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
