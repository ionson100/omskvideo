package orm;



import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CacheMetaDate<T> {
    private static final Logger log = Logger.getLogger(CacheMetaDate.class);

    private  String selectString=null;

    private  String insertString=null;

    public String getSelectString(){
        if(selectString==null){
            StringBuilder stringBuilder=new StringBuilder("SELECT ");
            for (ItemField s : fieldList) {
                stringBuilder.append(" ").append(s.columnName).append(",");
            }
            selectString=stringBuilder.toString().substring(0,stringBuilder.length()-1)+" FROM "+tableName+" ";

        }
        return  selectString;
    }

    public String getInsertString(){
        if(insertString==null){
            StringBuilder stringBuilder=new StringBuilder("INSERT INTO "+tableName+" (");
            for (ItemField s : fieldList) {
                if(s.typeKeyField==1)continue;
                stringBuilder.append(" ").append(s.columnName).append(",");
            }
            insertString=stringBuilder.toString().substring(0,stringBuilder.length()-1)+" ) VALUES( ";
            StringBuilder sb=new StringBuilder();
            for (ItemField s : fieldList) {
                if(s.typeKeyField==1)continue;
                sb.append(" ?,");
            }
            try{
                String addin=sb.toString().substring(0,sb.length()-1);
                insertString=insertString+addin+" ) ";
            }catch (Exception e){
                log.error(e);
                e.printStackTrace();
            }





        }
        return  insertString;
    }

    public List<ItemField> listColumn = null;
    public List<ItemField> keyColumns = null;
    String tableName = null;
    String where = null;
    private int isIAction = 0;
    private Class result = null;
    List<ItemField>fieldList=new ArrayList<>();


    public CacheMetaDate(Class<T> aClass) {
        SetClass(aClass);
    }

    public boolean isIAction() {
        return isIAction == 1;
    }

    private void SetClass(Class tClass) {

        if(tClass==null){
            String ttest="sdsd";
        }

        if (result == null) {
            result = tClass;
        }
        if (tableName == null) {
            tableName = AnotationOrm.GetTableName(tClass);
        }
        if (where == null) {
            where = AnotationOrm.GetWhere(tClass);
        }
        if (keyColumns == null) {

            keyColumns = AnotationOrm.GetListKkeyColumn(tClass);

        }
        if (listColumn == null) {
            listColumn = AnotationOrm.GetListColumn(tClass);
        }
        if (isIAction == 0) {
            isIAction = 2;
            for (Class aClass : tClass.getInterfaces()) {
                if (aClass == IActionOrm.class) {
                    isIAction = 1;
                }
            }
        }
        fieldList.addAll(keyColumns);
        fieldList.addAll(listColumn);
    }


    public String getUpdateString() {

        StringBuilder sb=new StringBuilder("UPDATE ");
        sb.append(tableName+" SET ");
        for (ItemField itemField : fieldList) {
            sb.append(itemField.columnName+" = ?,");
        }
        String s1=sb.toString().substring(0,sb.length()-1);
        s1=s1+" WHERE ";
        StringBuilder sb1=new StringBuilder(s1);
        for (ItemField keyColumn : keyColumns) {
            sb1.append(keyColumn.columnName+" = ? AND");
        }
        String s2=sb1.toString().substring(0,sb1.length()-3);
        return s2;
    }

    public String getDeleteString() {
        StringBuilder sb=new StringBuilder("DELETE FROM ");
        sb.append(tableName +" WHERE ");
        for (ItemField keyColumn : keyColumns) {
            sb.append(keyColumn.columnName+" = ? AND");
        }
        String s=sb.toString().substring(0,sb.length()-3);
        return s;
    }
}
