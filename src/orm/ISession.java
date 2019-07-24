package orm;

import java.util.List;

public interface ISession {

    <T> int update(T item);
    <T> int updateWhere(T item, String whereSql);
    <T> int insert(T item);
    <T> int delete(T item);
    <T> List<T> getList(Class<T> tClass, String where, Object... objects);
    <T> T get(Class<T> tClass, Object id);
    <T> T get(Class<T> tClass, PairKey... pairKeys);
    Object executeScalar(String sql, Object... objects);
    void execSQL(String sql, Object... objects);
    void beginTransaction();
    void commitTransaction();
    void rollbackTransaction();
    int deleteTable(String tableName);
    int deleteTable(String tableName, String where, Object... objects);
    void close();
}
