package guillermobeltran.chorusinput;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

/**
 * Created by Memo on 6/24/15.
 */
public class DataBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "chorus.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private String _task;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.DatabaseEntry.TABLE_NAME + " (" +
                    DatabaseContract.DatabaseEntry._ID + " INTEGER" + COMMA_SEP +
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_TASK + TEXT_TYPE+ " PRIMARY KEY" + COMMA_SEP +
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_SIZE + TEXT_TYPE  +
                    ") ";
    public static final String CREATE_CHAT_TABLE = " ("+
            DatabaseContract.DatabaseEntry._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE + TEXT_TYPE + COMMA_SEP +
            DatabaseContract.DatabaseEntry.COLUMN_NAME_MSG + TEXT_TYPE  + COMMA_SEP +
            DatabaseContract.DatabaseEntry.COLUMN_NAME_CHATID + TEXT_TYPE  +
            ") ";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.DatabaseEntry.TABLE_NAME;

    public DataBHelper(Context context, @Nullable String task) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _task = task;
        if(task != null){
            getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + _task + CREATE_CHAT_TABLE);
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        if(_task==null){
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        else{
            db.execSQL("CREATE TABLE IF NOT EXISTS " + _task+ CREATE_CHAT_TABLE);
        }

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}