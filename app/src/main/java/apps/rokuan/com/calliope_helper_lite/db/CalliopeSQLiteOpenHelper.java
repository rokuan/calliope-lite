package apps.rokuan.com.calliope_helper_lite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import apps.rokuan.com.calliope_helper_lite.db.model.Server;

/**
 * Created by LEBEAU Christophe on 16/12/2016.
 */

public class CalliopeSQLiteOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "calliope";
    private static final int DATABASE_VERSION = 1;

    public CalliopeSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Server.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public <T> boolean upsert(T o){
        try {
            Dao<T, String> dao = DaoManager.createDao(this.getConnectionSource(), o.getClass());
            dao.createOrUpdate(o);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public <T> List<T> queryAll(Class<T> clazz){
        try {
            Dao<T, String> dao = DaoManager.createDao(this.getConnectionSource(), clazz);
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<T>();
        }
    }
}
