package sample.table;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javassist.*;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sample.table.BuilderTableExecuter.getaClass;

/**
 * Created by ion on 14.01.2018.
 */
public class Creator {
    private static final Logger log = Logger.getLogger(Creator.class);

    /**
     * @param c          Any class which can be used to Get the class loader                          (c.getClassLoarder()) can be passed using object.getClass() methood
     * @param name       Name of the to be generated class
     * @param methods    Methods to be created as Strings
     * @param interfaces interfaces to be implemented as Strings
     * @param directory  the directory generated class files has to be written
     */
    public static void CreateClass(Class<?> c, String name, List<String> methods, List<String> interfaces, List<String> fields, String directory) {

        String temp = null;
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(c));
            CtClass cc = pool.makeClass(name);
            cc.setSuperclass(resolveCtClass(c));
            if (interfaces != null) {
                for (String s : interfaces) {
                    CtClass anInterface = pool.get(s);
                    cc.addInterface(anInterface);
                }
            }

            if (fields != null) {
                for (String s : fields) {
                    CtField m = CtField.make(s, cc);
                    cc.addField(m);
                }
            }

            if (methods != null) {
                for (String s : methods) {
                    try {
                        CtMethod m = CtNewMethod.make(s, cc);
                        cc.addMethod(m);
                    } catch (Exception ex) {
                        log.error(ex);
                    }

                }
            }


            cc.writeFile(directory);
        } catch (Exception e) {
            // TODO throw
            log.error(e);

        }
    }

    public static Class LoadClass(String className, String directory, ClassLoader loader) throws Exception {
        return getaClass(className, directory, loader);
    }


    public static List<String> GetMethodList(List<InnerFieldData> dataList) {


        List<String> sb = new ArrayList<>();
        for (InnerFieldData id : dataList) {
            String name = id.field.getName();


            if (id.field.getType()  == Long.class||id.field.getType()  == long.class) {


                sb.add("public javafx.beans.property.LongProperty " + name + "Property(){ return new javafx.beans.property.SimpleLongProperty(" + name + "); }");
            }else if (id.field.getType()  == BigDecimal.class) {
                sb.add("public javafx.beans.property.DoubleProperty " + name + "Property(){ return new javafx.beans.property.SimpleDoubleProperty(" + name + ".doubleValue()); }");
            }else if (id.field.getType() == String.class || id.field.getType() == StringProperty.class) {
                sb.add("public javafx.beans.property.StringProperty " + name + "Property(){ return new javafx.beans.property.SimpleStringProperty(" + name + "); }");
            }else if (id.field.getType() == Integer.class || id.field.getType() == int.class || id.field.getType() == IntegerProperty.class) {
                sb.add("public javafx.beans.property.IntegerProperty " + name + "Property(){return new javafx.beans.property.SimpleIntegerProperty(" + name + "); }");
            }else if (id.field.getType() == boolean.class || id.field.getType() == Boolean.class || id.field.getType() == BooleanProperty.class) {
                sb.add("public javafx.beans.property.BooleanProperty " + name + "Property(){return new javafx.beans.property.SimpleBooleanProperty(" + name + "); }");
            }else if (id.field.getType() == Double.class || id.field.getType() == double.class || id.field.getType() == DoubleProperty.class) {
                sb.add("public javafx.beans.property.DoubleProperty " + name + "Property(){ return new javafx.beans.property.SimpleDoubleProperty(" + name + "); }");
            }else if (id.field.getType() == Float.class || id.field.getType() == float.class || id.field.getType() == FloatProperty.class) {
                sb.add("public javafx.beans.property.FloatProperty " + name + "Property(){ return new javafx.beans.property.SimpleFloatProperty(" + name + "); }");
            }else  if (id.field.getType() == Date.class) {
                sb.add("public javafx.beans.property.StringProperty  " + name + "Property(){ java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(\"" + id.dateFormat + "\");if(" + name + "==null) " +
                        "return null; return new  javafx.beans.property.SimpleStringProperty(dateFormat.format(" + name + "));  }");
            }else {
                sb.add("public javafx.beans.property.StringProperty " + name + "Property(){ return new javafx.beans.property.SimpleStringProperty( String.valueOf(" + name + ")); }");
            }









        }
        return sb;
    }

    private static CtClass resolveCtClass(Class clazz) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        return pool.get(clazz.getName());
    }

    public static ObservableList Convert(List<Object> list, List<InnerFieldData> fieldDatas, Class proxy) throws IllegalAccessException, InstantiationException {

        ObservableList observableList = FXCollections.observableArrayList();


        for (Object o : list) {
            Object p = proxy.newInstance();
            for (InnerFieldData fieldData : fieldDatas) {
                try {
                    Object ob = fieldData.field.get(o);
                    fieldData.field.set(p, ob);
                } catch (Exception ex) {
                    log.error(ex);
                }
            }

            observableList.add(p);
        }
        return observableList;
    }
}

