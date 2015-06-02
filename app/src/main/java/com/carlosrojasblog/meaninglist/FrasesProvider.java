package com.carlosrojasblog.meaninglist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by carlosrojas on 6/1/15.
 */
public class FrasesProvider extends ContentProvider {
    //Definición del CONTENT_URI
    private static final String uri =
            "content://com.carlosrojasblog.meaninglist/frases";

    public static final Uri CONTENT_URI = Uri.parse(uri);

    //Necesario para UriMatcher
    private static final int FRASES = 1;
    private static final int FRASES_ID = 2;
    private static final UriMatcher uriMatcher;

    //Clase interna para declarar las constantes de columna
    public static final class Frases implements BaseColumns
    {
        private Frases() {}

        //Nombres de columnas
        public static final String COL_DESC = "desc";

    }

    //Base de datos
    private FrasesSqliteHelper clidbh;
    private static final String BD_NOMBRE = "DBFrases";
    private static final int BD_VERSION = 1;
    private static final String TABLA_FRASES = "Frases";

    //Inicializamos el UriMatcher
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.carlosrojasblog.meaninglist", "frases", FRASES);
        uriMatcher.addURI("com.carlosrojasblog.meaninglist", "frases/#", FRASES_ID);
    }

    @Override
    public boolean onCreate() {

        clidbh = new FrasesSqliteHelper(
                getContext(), BD_NOMBRE, null, BD_VERSION);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        //Si es una consulta a un ID concreto construimos el WHERE
        String where = selection;
        if(uriMatcher.match(uri) == FRASES_ID){
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = clidbh.getWritableDatabase();

        Cursor c = db.query(TABLA_FRASES, projection, where,
                selectionArgs, null, null, sortOrder);

        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long regId = 1;

        SQLiteDatabase db = clidbh.getWritableDatabase();

        regId = db.insert(TABLA_FRASES, null, values);

        Uri newUri = ContentUris.withAppendedId(CONTENT_URI, regId);

        Log.d("newUri=",newUri.toString());

        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        int cont;

        //Si es una consulta a un ID concreto construimos el WHERE
        String where = selection;
        if(uriMatcher.match(uri) == FRASES_ID){
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = clidbh.getWritableDatabase();

        cont = db.update(TABLA_FRASES, values, where, selectionArgs);

        return cont;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int cont;

        //Si es una consulta a un ID concreto construimos el WHERE
        String where = selection;
        if(uriMatcher.match(uri) == FRASES_ID){
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = clidbh.getWritableDatabase();

        cont = db.delete(TABLA_FRASES, where, selectionArgs);

        return cont;
    }

    @Override
    public String getType(Uri uri) {

        int match = uriMatcher.match(uri);

        switch (match)
        {
            case FRASES:
                return "vnd.android.cursor.dir/vnd.carlosrojasblog.cliente";
            case FRASES_ID:
                return "vnd.android.cursor.item/vnd.carlosrojasblog.cliente";
            default:
                return null;
        }
    }
}
