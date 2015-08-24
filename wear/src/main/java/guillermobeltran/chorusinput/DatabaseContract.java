package guillermobeltran.chorusinput;

import android.provider.BaseColumns;

public final class DatabaseContract {
    public DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class DatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_chats";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_ROLE1 = "role";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_MSG = "message";
        public static final String COLUMN_NAME_CHATID = "chatid";
        public static final String COLUMN_NAME_TIME = "time";
    }
}