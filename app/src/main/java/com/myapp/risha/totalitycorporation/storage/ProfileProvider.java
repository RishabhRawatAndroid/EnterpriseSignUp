package com.myapp.risha.totalitycorporation.storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.DATABASE_CREATE;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.DATABASE_NAME;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.DATABASE_VERSION;

public class ProfileProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.myapp.risha.totalitycorporation.ProfileProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/userinfo";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String NAME = "Name";
    public static final String EMAIL = "Email";
    public static final String PHONENO = "PhoneNo";
    public static final String IMAGE = "Image";
    public static final String CLOUD = "Cloud";
    public static final String GOOGLE = "Google";
    public static final String FACEBOOK = "Facebook";
    public static final String ID = "id";


    static final int user = 1;
    static final int userid = 2;


    //Database name and its version
    static final String DATABASE_NAME = "MyUserDetailsDB";
    static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "userinfoDB";
    //create table query
    static final String DATABASE_CREATE = "create table userinfoDB(id integer primary key autoincrement , Name text ,Email text , PhoneNo text , Image text , Cloud text , Google text, Facebook text);";

    //Create context and Sqlite database
    Context context;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelperClass databasehelperclass;

    private static final UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(PROVIDER_NAME, "userinfo", user);
        matcher.addURI(PROVIDER_NAME, "userinfo/#", userid);

    }

    public ProfileProvider()
    {

    }
//    public ProfileProvider(Context context) {
//        this.context=context;
//        databasehelperclass=new DatabaseHelperClass(context);
//    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (matcher.match(uri)) {
            case user:
                count = sqLiteDatabase.delete(DATABASE_TABLE, selection, selectionArgs);
                break;
            case userid:
                String id = uri.getPathSegments().get(1);
                count = sqLiteDatabase.delete(DATABASE_TABLE, ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + selection);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {

            case user:
                return "vnd.android.cursor.dir/userinfo";
            case userid:
                return "vnd.android.cursor.item/userinfo";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //add the user info like name ,email google account email or facebook account name
        long rowid = sqLiteDatabase.insert(DATABASE_TABLE, "", values);
        if (rowid > 0) {
            Uri uri1 = ContentUris.withAppendedId(CONTENT_URI, rowid);
            getContext().getContentResolver().notifyChange(uri1, null);
            return uri1;
        }
        throw new SQLException("Failed to insert data " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelperClass dbhelper = new DatabaseHelperClass(context);
        sqLiteDatabase = dbhelper.getWritableDatabase();
        return (sqLiteDatabase == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(DATABASE_TABLE);
        if (matcher.match(uri) == userid)
            //if getting a particular userid
            sqlBuilder.appendWhere(
                    ID + " = " + uri.getPathSegments().get(1));
        if (sortOrder == null || sortOrder == "")
            sortOrder = ID;
        Cursor c = sqlBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        //register to watch a content URI for changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int count = 0;
        switch (matcher.match(uri)) {
            case user:
                count = sqLiteDatabase.update(DATABASE_TABLE, values, selection, selectionArgs);
                break;
            case userid:
                count = sqLiteDatabase.update(DATABASE_TABLE, values, ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    class DatabaseHelperClass extends SQLiteOpenHelper {
        Context context;

        public DatabaseHelperClass(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (Exception ex) {
                Log.d("RISHABH CON", "No create the database " + ex.toString());
                Toast.makeText(context, "SOME ERROR IS OCCURED WHILE CREATING DATABASE", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS userinfoDB");
            onCreate(db);
        }
    }


//    public ProfileProvider opendatabase() throws SQLException
//    {
//        sqLiteDatabase=databasehelperclass.getWritableDatabase();
//        return this;
//    }
//
//    public void closedatabase()
//    {
//        databasehelperclass.close();
//    }

    public long InsertData(String name,String email,String phoneno,String image,String cloud,String google,String facebook)
    {
        //when we inserting data in the sqlite database we use a ContentValue class
        ContentValues values=new ContentValues();
        //values.put("ID",count);
        values.put("NAME",name);
        values.put("EMAIL",email);
        values.put("PHONENO",phoneno);
        values.put("IMAGE",image);
        values.put("CLOUD",cloud);
        values.put("GOOGLE",google);
        values.put("FACEBOOK",facebook);
        //if some occur are found then it return -1
        return sqLiteDatabase.insertOrThrow(DATABASE_TABLE,null,values);
    }

//    //Delete a particular data
//    public Boolean DeleteParticularData(int id)
//    {
//        return sqLiteDatabase.delete(DATABASE_TABLE,ID+"="+id,null)>0;
//    }
//
//    public Cursor RetriveParticularContact(int Phone)
//    {
//        Cursor cursor=sqLiteDatabase.query(true,DATABASE_TABLE,new String[]{NAME,EMAIL,PHONENO,IMAGE,CLOUD,GOOGLE,FACEBOOK},Phone+"="+Phone,null,null,null,null,null);
//        return cursor;
//    }
//
//    //Updating the data
//    public boolean updateContact(String id,String name, String email,String phoneno,String image,String cloud,String google,String facebook)
//    {
//        ContentValues args = new ContentValues();
//        args.put(NAME, name);
//        args.put(EMAIL, email);
//        args.put(PHONENO,phoneno);
//        args.put(IMAGE,image);
//        args.put(CLOUD,cloud);
//        args.put(GOOGLE,google);
//        args.put(FACEBOOK,facebook);
//        return sqLiteDatabase.update(DATABASE_TABLE, args, ID + "=" + id, null) > 0;
//    }


}



