package orm;

import javafx.scene.image.Image;
import org.apache.log4j.Logger;
import org.sqlite.JDBC;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;


public class Configure implements ISession {
    private static final Logger log = Logger.getLogger(Configure.class);


    public static String CON_STR = "jdbc:sqlite:";
    public static String tName;

    static {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    private Connection connection;


    //private static MiniConnectionPoolManager poolMgr;

    public Configure(String tableName) {
        CON_STR = CON_STR + tableName.trim();
        tName = tableName;

//        SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
//        dataSource.setUrl(CON_STR);
//        dataSource.setJournalMode("WAL");
//        dataSource.getConfig().setBusyTimeout("10000");
//        poolMgr = new MiniConnectionPoolManager(dataSource, 25);


        try {
            CreatorTable.createTable();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
        // Configure.getSession().execSQL("PRAGMA journal_mode = OFF");

    }

    public Configure() {
        try {
            //connection = poolMgr.getConnection();
            connection = DriverManager.getConnection(CON_STR);
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public static ISession getSession() {

        return new Configure();
    }

    public static void createTable(Class<?> aClass) {
        CacheMetaDate date = CacheDictionary.getCacheMetaDate(aClass);
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS " + date.tableName + " (");

        for (Object f : date.keyColumns) {
            ItemField ff = (ItemField) f;
            sb.append(ff.columnName);
            sb.append(pizdaticusField(ff));
            sb.append("PRIMARY KEY, ");
        }


        for (Object f : date.listColumn) {
            ItemField ff = (ItemField) f;
            sb.append(ff.columnName);
            sb.append(pizdaticusField(ff));
        }
        String s = sb.toString().trim();
        String ss = s.substring(0, s.length() - 1);
        String sql = ss + ")";


        Configure.getSession().execSQL(sql);
        System.out.println(sql);
    }

    public static String getStringCreateTable(Class<?> aClass) {
        CacheMetaDate date = CacheDictionary.getCacheMetaDate(aClass);
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS " + date.tableName + " (");


        for (Object f : date.keyColumns) {
            ItemField ff = (ItemField) f;
            sb.append(ff.columnName).append(" ");
            sb.append(pizdaticusKey(ff));
            sb.append("PRIMARY KEY, ");
        }


        for (Object f : date.listColumn) {
            ItemField ff = (ItemField) f;
            sb.append(ff.columnName);
            sb.append(pizdaticusField(ff));
        }
        String s = sb.toString().trim();
        String ss = s.substring(0, s.length() - 1);
        String sql = ss + ")";

        return sql;
    }

    private static String pizdaticusKey(ItemField field) {
        if (field.type == float.class || field.type == Float.class) {
            return " FLOAT ";
        }
        if (field.type == Double.class || field.type == double.class) {
            return " DOUBLE ";
        }
        if (field.type == int.class || field.type == Integer.class
                || field.type == long.class || field.type == Long.class
                || field.type == short.class || field.type == Short.class
                || field.type == byte.class || field.type == Byte.class) {
            return " INTEGER ";
        }
        if (field.type == String.class) {
            return " TEXT ";
        }
        if (field.type == boolean.class) {
            return " BOOL ";
        }
        return "";
    }

    private static String pizdaticusField(ItemField field) {


        if (
                field.type == double[].class ||
                        field.type == float[].class ||
                        field.type == Double[].class ||
                        field.type == Float[].class) {
            return " TEXT, ";
        } else if (
                field.type == int[].class ||
                        field.type == BigDecimal[].class ||
                        field.type == String[].class ||
                        field.type == Enum[].class ||
                        field.type == long[].class ||
                        field.type == short[].class ||
                        field.type == byte[].class ||
                        field.type == Integer[].class ||
                        field.type == Long[].class ||
                        field.type == int[].class ||
                        field.type == Short[].class) {
            return " TEXT, ";
        } else if (
                field.type == double.class ||
                        field.type == float.class ||
                        field.type == Double.class ||
                        field.type == Float.class) {
            return " REAL DEFAULT 0, ";
        } else if (
                field.type == int.class ||
                        field.type == Enum.class ||
                        field.type == long.class ||
                        field.type == short.class ||
                        field.type == byte.class) {
            return " INTEGER DEFAULT 0, ";
        } else if (
                field.type == Integer.class ||
                        field.type == Long.class ||
                        field.type == Short.class ||
                        field.type == Byte.class) {
            return " INTEGER , ";
        } else if (field.type == String.class || field.type == BigDecimal.class) {
            return " TEXT, ";
        } else if (field.type == boolean.class || field.type == Boolean.class) {
            return " BOOL DEFAULT 0, ";
        } else if (field.type == byte[].class || field.type == Image.class) {
            return " BLOB, ";
        } else if (field.type == Date.class) {
            return " DATETIME, ";
        } else {
            return "";
        }
    }

    public static <T> void bulk(Class<T> tClass, List<T> tList) {

        List<List<T>> sd = partition(tList, 500);
        for (List<T> ts : sd) {
            InsertBulk s = Configure.getInsertBulk(tClass);
            for (T t : ts) {
                s.add(t);
            }
            String sql = s.getSql();
            if (sql != null) {
                try {
                    Configure.getSession().execSQL(sql);
                    System.out.println(sql);
                } catch (Exception ex) {
                    log.error(ex);
                    throw ex;
                }
            }
        }
    }

    public static <T> List<List<T>> partition(Collection<T> members, int maxSize) {
        List<List<T>> res = new ArrayList<>();
        List<T> internal = new ArrayList<>();
        for (T member : members) {
            internal.add(member);
            if (internal.size() == maxSize) {
                res.add(internal);
                internal = new ArrayList<>();
            }
        }
        if (!internal.isEmpty()) {
            res.add(internal);
        }
        return res;
    }

    private static InsertBulk getInsertBulk(Class aClass) {
        return new InsertBulk(aClass);
    }

    private int updateCore(Object item, String where) {
        int res = 0;
        CacheMetaDate d = CacheDictionary.getCacheMetaDate(item.getClass());
        ContentValues values = null;
        try {
            values = getContentValues(item, d);
        } catch (Exception e) {
            log.error(e);
        }

        if (d.isIAction()) {
            ((IActionOrm) item).actionBeforeUpdate(item);
        }

        String sql = d.getUpdateString();
        if (where != null) {
            sql = sql + " " + where;
        }
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {

            int i = 0;
            {
                List<ItemField> s = d.fieldList;

                for (ItemField itemField : s) {
                    Object ss = Objects.requireNonNull(values).getMap().get(itemField.columnName);
                    statement.setObject(++i, ss);
                }
            }
            {
                List<ItemField> s = d.keyColumns;

                for (ItemField itemField : s) {
                    Object ss = Objects.requireNonNull(values).getMap().get(itemField.columnName);
                    statement.setObject(++i, ss);
                }
            }


            res = statement.executeUpdate();


            statement.close();

            if (d.isIAction()) {
                ((IActionOrm) item).actionAfterUpdate(item);
            }

        } catch (Exception ex) {
            log.error(ex);
            throw new RuntimeException(ex);
        } finally {
            finalyConClose();
        }
        return res;
    }

    @Override
    public <T> int update(T item) {
        int res = updateCore(item, null);

        return res;

    }

    @Override
    public <T> int updateWhere(T item, String whereSql) {
        int res = updateCore(item, whereSql);

        return res;
    }

    @Override
    public <T> int insert(T item) {
        int res = 0;
        CacheMetaDate d = CacheDictionary.getCacheMetaDate(item.getClass());
        ContentValues values = null;
        try {
            values = getContentValues(item, d);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        if (d.isIAction()) {
            ((IActionOrm) item).actionBeforeInsert(item);
        }
        String sql = d.getInsertString();
        try (PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            List<ItemField> s = d.fieldList;
            int i = 0;
            for (ItemField itemField : s) {
                if (itemField.typeKeyField == 1) continue;
                Object ss = Objects.requireNonNull(values).getMap().get(itemField.columnName);
                statement.setObject(++i, ss);
            }
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                ItemField key_auto = null;
                List<ItemField> df = d.keyColumns;

                for (ItemField itemField : df) {
                    if (itemField.typeKeyField == 1) {
                        key_auto = itemField;
                        break;
                    }
                }
                if (generatedKeys.next()) {
                    res = generatedKeys.getInt(1);
                    Objects.requireNonNull(key_auto).field.setAccessible(true);
                    key_auto.field.set(item, res);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            statement.close();
            if (d.isIAction()) {
                ((IActionOrm) item).actionAfterInsert(item);
            }

        } catch (Exception ex) {
            log.error(ex);
            throw new RuntimeException(ex);
        } finally {
            finalyConClose();
        }
        return res;
    }

    @Override
    public <T> int delete(T item) {
        int res;
        try {
            CacheMetaDate d = CacheDictionary.getCacheMetaDate(item.getClass());
            String sql = d.getDeleteString();
            ContentValues values = null;
            try {
                values = getContentValues(item, d);
            } catch (Exception e) {
                log.error(e);
                throw new RuntimeException(e);
            }
            try (PreparedStatement statement = this.connection.prepareStatement(sql)) {

                int i = 0;
                for (Object keyColumn : d.keyColumns) {
                    ItemField s = (ItemField) keyColumn;
                    Object ss = Objects.requireNonNull(values).getMap().get(s.columnName);
                    statement.setObject(++i, ss);
                }
                statement.execute();
                res = statement.getUpdateCount();
                statement.close();

            }

        } catch (Exception ex) {
            log.error(ex);
            throw new RuntimeException(ex);
        } finally {
            finalyConClose();
        }

        return res;


    }

    @Override
    public <T> List<T> getList(Class<T> tClass, String where, Object... objects) {

        List<T> list;
        CacheMetaDate d = CacheDictionary.getCacheMetaDate(tClass);
        where = wherower(where, d);
        String sql = d.getSelectString() + " WHERE " + (where == null ? "" : where);
        if (where == null) {
            sql = d.getSelectString();
        }


        if (where == null) {
            sql = d.getSelectString();
        }

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            // В данный список будем загружать наши продукты, полученные из БД

            int i = 0;
            if (objects != null) {
                for (Object object : objects) {
                    statement.setObject(++i, object);
                }
            }

            ResultSet resultSet = statement.executeQuery();

            list = new ArrayList<>();
            while (resultSet.next()) {


                Object sd = tClass.newInstance();
                Companaund(d.fieldList, resultSet, sd);
                list.add((T) sd);

            }
            statement.close();
            resultSet.close();
            // Возвращаем наш список
            return list;

        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return null;
        } finally {
            finalyConClose();
        }
    }

    private void finalyConClose() {
        try {
            if (connection != null && connection.isClosed() == false && connection.getAutoCommit() == true) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private final String mes = "set bitnic.orm - больше чем одно значение";

    @Override
    public <T> T get(Class<T> tClass, Object id) {
        try {
            ItemField itemField = null;
            CacheMetaDate d = CacheDictionary.getCacheMetaDate(tClass);
            for (Object o : d.keyColumns) {
                ItemField s = (ItemField) o;
                if (s.typeKeyField == 1) {
                    itemField = s;
                    break;
                }
            }
            List<T> ts = getList(tClass, itemField.columnName + " = ?", id);
            if (ts.size() > 1) {
                throw new RuntimeException(mes);
            }
            if (ts.size() == 0) {
                return null;
            }
            return ts.get(0);
        } catch (Exception ex) {
            log.error(ex);
        } finally {
            finalyConClose();
        }
        return null;


    }

    @Override
    public <T> T get(Class<T> tClass, PairKey... pairKeys) {

        try {
            StringBuilder sb = new StringBuilder();
            List<Object> objects = new ArrayList<>();
            for (PairKey pairKey : pairKeys) {
                sb.append(pairKey.nameColumn + " = ? AND");
                objects.add(pairKey.value);
            }
            String s = sb.toString().substring(0, sb.length() - 3);

            List<T> ts = getList(tClass, s, objects);
            if (ts.size() > 1) {
                throw new RuntimeException(mes);
            }
            if (ts.size() == 0) {
                return null;
            }
            return ts.get(0);
        } catch (Exception ex) {
            log.error(ex);
        } finally {
            finalyConClose();
        }
        return null;

    }

    @Override
    public Object executeScalar(String sql, Object... objects) {
        Object o = null;
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            int i = 0;
            if (objects != null) {
                for (Object object : objects) {
                    statement.setObject(++i, object);
                }
            }

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            o = resultSet.getObject(1);
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            finalyConClose();
        }
        return o;
    }

    @Override
    public void execSQL(String sql, Object... objects) {

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            int i = 0;
            if (objects != null) {
                for (Object object : objects) {
                    statement.setObject(++i, object);
                }
            }

            statement.execute();


        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            finalyConClose();
        }
    }

    @Override
    public void beginTransaction() {

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commitTransaction() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollbackTransaction() {
        try {
            connection.rollback();
            connection.close();
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteTable(String tableName) {

        try (PreparedStatement statement = this.connection.prepareStatement("DELETE FROM " + tableName)) {
            statement.execute();
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            finalyConClose();
        }
        return 0;

    }

    @Override
    public int deleteTable(String tableName, String where, Object... objects) {

        try (PreparedStatement statement = this.connection.prepareStatement("DELETE FROM " + tableName + " " + where)) {
            int i = 0;
            for (Object object : objects) {
                statement.setObject(++i, object);
            }
            statement.execute();
            statement.close();
        } catch (SQLException e) {

            log.error(e);
            throw new RuntimeException(e);
        } finally {
            finalyConClose();
        }

        return 0;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private String wherower(String where, CacheMetaDate cacheMetaDate) {
        if (cacheMetaDate.where != null) {
            where = " " + cacheMetaDate.where.trim() + (where == null ? "" : " and " + where) + " ";
        }
        return where;
    }

    private void Companaund(List<ItemField> listIf, ResultSet c, Object o) throws IllegalAccessException, SQLException {
        for (ItemField str : listIf) {

            Field res = str.field;
            res.setAccessible(true);


            if (str.isUserType) {
                IUserType data = null;
                try {
                    data = (IUserType) str.aClassUserType.newInstance();
                } catch (InstantiationException e) {
                    log.error(e);
                    e.printStackTrace();
                }
                String sd = c.getString(str.columnName);
                Object sdd = Objects.requireNonNull(data).getObject(sd);
                res.set(o, sdd);
            } else {

                if (str.type == int.class) {
                    res.setInt(o, c.getInt(str.columnName));
                } else if (str.type == Date.class) {


                    Long it = c.getLong(str.columnName);
                    if (it == null) {
                        res.set(o, null);
                    } else {
                        res.set(o, new Date(it));
                    }


                } else if (str.type == BigDecimal.class) {


                    res.set(o, new BigDecimal(c.getString(str.columnName)));


                } else if (str.type == String.class) {
                    res.set(o, c.getString(str.columnName));
                } else if (str.type == double.class) {
                    res.setDouble(o, c.getDouble(str.columnName));
                } else if (str.type == float.class) {
                    res.setFloat(o, c.getFloat(str.columnName));
                } else if (str.type == long.class) {
                    res.setLong(o, c.getLong(str.columnName));
                } else if (str.type == short.class) {
                    res.setShort(o, c.getShort(str.columnName));
                } else if (str.type == byte[].class) {
                    res.set(o, c.getBlob(str.columnName));
                } else if (str.type == byte.class) {
                    res.setByte(o, (byte) c.getLong(str.columnName));
                } else if (str.type == Integer.class) {
                    if (c.getObject(str.columnName) == null) {
                        res.set(o, null);
                    } else {
                        Integer ii = c.getInt(str.columnName);
                        res.set(o, ii);
                    }
                } else if (str.type == Double.class) {
                    if (c.getObject(str.columnName) == null) {
                        res.set(o, null);
                    } else {
                        Double d = c.getDouble(str.columnName);
                        res.set(o, d);
                    }
                } else if (str.type == Float.class) {
                    if (c.getObject(str.columnName) == null) {
                        res.set(o, null);
                    } else {
                        Float f = c.getFloat(str.columnName);
                        res.set(o, f);
                    }
                } else if (str.type == Long.class) {
                    if (c.getObject(str.columnName) == null) {
                        res.set(o, null);
                    } else {
                        Long l = c.getLong(str.columnName);
                        res.set(o, l);
                    }
                } else if (str.type == Short.class) {
                    if (c.getObject(str.columnName) == null) {
                        res.set(o, null);
                    } else {
                        Short sh = c.getShort(str.columnName);
                        res.set(o, sh);
                    }
                } else if (str.type == boolean.class) {
                    boolean val;
                    val = c.getInt(str.columnName) != 0;
                    res.setBoolean(o, val);
                } else if (str.type == Boolean.class) {
                    if (c.getObject(str.columnName) == null) {
                        res.set(o, null);
                    } else {
                        boolean val;
                        val = c.getInt(str.columnName) != 0;
                        res.setBoolean(o, val);
                    }
                } else {
                    throw new RuntimeException("Error bitnic.orm set values columnName: " + str.columnName + " fieldName: " + str.fieldName + " type: " + str.aClassUserType.getName() + " type: " + str.field.getGenericType());
                }
            }
        }

    }

    private <T> ContentValues getContentValues(T item, CacheMetaDate<?> d) {
        ContentValues values = new ContentValues();
        try {
            for (ItemField str : d.fieldList) {
                Field field = str.field;//item.getClass().getDeclaredField(str.fieldName);
                field.setAccessible(true);

                if (str.isUserType) {
                    Object date = str.aClassUserType.newInstance();
                    String json = ((IUserType) date).getString(field.get(item));
                    values.put(str.columnName, json);
                } else {


                    if (str.type == BigDecimal.class) {
                        Object sd = field.get(item);
                        values.put(str.columnName, sd.toString());
                    } else if (str.type == Date.class) {
                        if (field.get(item) == null) {
                            values.put(str.columnName, 0d);
                        } else {
                            long ld = ((Date) field.get(item)).getTime();
                            values.put(str.columnName, ld);
                        }
                        continue;
                    } else if (str.type == String.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == int.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == long.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == short.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == byte.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == Short.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == Long.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == Integer.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == Double.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == Float.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == byte[].class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == double.class) {
                        values.put(str.columnName, field.get(item));
                    } else if (str.type == boolean.class) {
                        boolean val = (boolean) field.get(item);
                        if (val) {
                            values.put(str.columnName, 1);
                        } else {
                            values.put(str.columnName, 0);
                        }
                    } else if (str.type == Boolean.class) {
                        Boolean val = (Boolean) field.get(item);
                        if (val == null) {
                            values.put(str.columnName, null);
                        } else {
                            if (val) {
                                values.put(str.columnName, 1);
                            } else {
                                values.put(str.columnName, 0);
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e.getMessage());
        }
        return values;
    }

    private static class InsertBulk<F> {
        final private StringBuilder sql = new StringBuilder();
        private int it = 0;
        private CacheMetaDate metaDate;

        InsertBulk(Class<F> aClass) {
            metaDate = CacheDictionary.getCacheMetaDate(aClass);
            sql.append(" INSERT INTO ");
            sql.append(metaDate.tableName).append(" (");
            for (int i = 0; i < metaDate.listColumn.size(); i++) {
                ItemField f = (ItemField) metaDate.listColumn.get(i);
                sql.append(f.columnName);
                if (i < metaDate.listColumn.size() - 1) {
                    sql.append(", ");
                } else {
                    sql.append(") VALUES ");
                }
            }

        }

        public void add(F o) {
            it++;
            sql.append("(");
            for (int i = 0; i < metaDate.listColumn.size(); i++) {
                ItemField f = (ItemField) metaDate.listColumn.get(i);
                try {
                    Object value = f.field.get(o);
                    sql.append(getString(value, f.field.getType()));
                    if (i < metaDate.listColumn.size() - 1) {
                        sql.append(", ");
                    } else {
                    }

                } catch (IllegalAccessException e) {
                    log.error(e);
                    throw new RuntimeException("InsertBulk:" + e.getMessage());
                }
            }
            sql.append(") ,");
        }

        private String getString(Object o, Class fClass) {

            if (fClass == Date.class) {
                if (o == null) {
                    return "0";
                } else {
                    return String.valueOf(((Date) o).getTime());
                }
            }
            if (fClass == BigDecimal.class) {

                if (o == null) {
                    return "0";
                } else {
                    return o.toString();
                }

            }
            if (fClass == String.class) {
                if (o == null) {
                    return "null";
                } else {
                    return "'" + String.valueOf(o).replace("'", " ") + "'";
                }
            } else if (fClass == boolean.class) {

                if (o == null) {
                    return "0";
                } else {
                    if ((Boolean) o) {
                        return "1";
                    } else {
                        return "0";
                    }
                }
            } else if (fClass == Boolean.class) {

                if (o == null) {
                    return "null";
                } else {
                    if ((Boolean) o) {
                        return "1";
                    } else {
                        return "0";
                    }
                }
            } else if (fClass == int.class ||
                    fClass == long.class ||
                    fClass == float.class ||
                    fClass == double.class ||
                    fClass == short.class) {
                if (o == null) {
                    return "0";
                } else {
                    return String.valueOf(o);
                }
            } else if (fClass == Integer.class ||
                    fClass == Float.class ||
                    fClass == Double.class ||
                    fClass == Long.class ||
                    fClass == Short.class) {
                if (o == null) {
                    return "null";
                } else {
                    return String.valueOf(o);
                }
            } else {

                throw new RuntimeException("InsertBulk:не могу определить тип");
            }
        }

        String getSql() {
            if (it == 0) {
                return null;
            } else {
                return sql.toString().substring(0, sql.toString().lastIndexOf(",")).trim();
            }

        }


    }

}

