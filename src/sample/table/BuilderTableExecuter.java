package sample.table;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javassist.*;
import org.apache.log4j.Logger;
import utils.Pather;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuilderTableExecuter<T> {
    private static final Logger log = Logger.getLogger(BuilderTableExecuter.class);


    public static class MyField {

        public MyField(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public final String name;
        public final Type type;
    }

    private Class<T> curclass;

    public void build(List<T> tList, TableView<T> tableView) {
        if (tList == null || tableView == null || tList.size() == 0) {
            return;
        }
        Object o = tList.get(0);
        curclass = (Class<T>) o.getClass();
        Field[] fields = curclass.getDeclaredFields();
        tableView.getItems().clear();
        tableView.getColumns().clear();
        tableView.refresh();
        for (Field field : fields) {
            createItemColumnE(o, tableView, field);
        }
        ObservableList<T> ts = FXCollections.observableArrayList(tList);
        tableView.setTableMenuButtonVisible(true);
        tableView.setStyle("-fx-font: 14px Arial;");
        tableView.setItems(ts);

    }


    private static <T, N> void createItemColumnE(T t, TableView tableView, Field field) {
        TableColumn<T, String> column = new TableColumn<>(field.getName());
        column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
        tableView.getColumns().add(column);
    }

    public Class createClasse(List<MyField> myFields, Class loador) {
        StringBuilder stringBuilder = new StringBuilder();
        for (MyField myField : myFields) {
            stringBuilder.append(myField.name);
        }
        String name = myFields.get(0) + String.valueOf(stringBuilder.toString().hashCode());
        name = name.replace(".", "_").replace("-", "_").replace("@", "_");

        Class proxyClass = null;
        try {
            proxyClass = this.loadClass(name, Pather.directoryBuilder2, loador.getClassLoader());

        } catch (Exception e) {
            if (e.getClass() == ClassNotFoundException.class) {
                this.createClass(loador, name, getFieldList(myFields), getMethodList(myFields), Pather.directoryBuilder2);
            }
        }

        try {
            proxyClass = this.loadClass(name, Pather.directoryBuilder2, loador.getClassLoader());
        } catch (Exception e) {
            log.error(e);

        }
        return proxyClass;
    }

    private Class loadClass(String className, String directory, ClassLoader loader)
            throws ClassNotFoundException, MalformedURLException {
        return getaClass(className, directory, loader);
    }

    static Class getaClass(String className, String directory, ClassLoader loader) throws MalformedURLException, ClassNotFoundException {
        File f = new File(directory);
        java.net.URL[] urls = new java.net.URL[]{f.toURI().toURL()};
        ClassLoader cl = new URLClassLoader(urls, loader);
        Class cls = cl.loadClass(className);
        return cls;
    }

    private void createClass(Class<?> loador, String name, List<String> fields, List<String> methods, String directory) {
        String temp = null;
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(loador));
            CtClass cc = pool.makeClass(name);

            if (fields != null) {
                for (String s : fields) {
                    temp = s;
                    CtField m = CtField.make(s, cc);
                    cc.addField(m);
                }
            }
            if (methods != null) {
                for (String s : methods) {
                    temp = s;
                    CtMethod m = CtNewMethod.make(s, cc);
                    cc.addMethod(m);
                }
            }


            cc.writeFile(directory);

        } catch (Exception e) {
            log.error("creator_class" + temp);
            log.error(e);
        }
    }

    private List<String> getFieldList(List<MyField> myFields) {

        List<String> res = new ArrayList<>(myFields.size());
        for (MyField mf : myFields) {
            if (mf.type == String.class) {
                res.add("public java.lang.String " + mf.name + ";");
            } else if (mf.type == Integer.class) {

                //java.lang.Integer
                res.add("public  int " + mf.name + ";");
            } else if (mf.type == int.class) {

                res.add("public int " + mf.name + ";");
            } else if (mf.type == Double.class) {

                //java.lang.Double
                res.add("public double " + mf.name + ";");
            } else if (mf.type == Float.class) {

                //java.lang.Float
                res.add("public float " + mf.name + ";");
            } else if (mf.type == double.class) {

                res.add("public double " + mf.name + ";");
            } else if (mf.type == float.class) {

                res.add("public float " + mf.name + ";");
            } else if (mf.type == Date.class) {

                res.add("public Date " + mf.name + ";");
            } else if (mf.type == BigDecimal.class) {

                res.add("public BigDecimal " + mf.name + ";");
            }
            else if (mf.type == long.class) {

                res.add("public Long " + mf.name + ";");
            }
        }
        return res;
    }

    private List<String> getMethodList(List<MyField> myFields) {
        List<String> sb = new ArrayList<>();
        for (Object id : myFields) {
            MyField mf = (MyField) id;

            if (mf.type == long.class) {
                sb.add("public javafx.beans.property.StringProperty " + mf.name + "Property(){ return new javafx.beans.property.SimpleStringProperty(String.valueOf(" + mf.name + ")); }");
            }

            if (mf.type == BigDecimal.class) {
                sb.add("public javafx.beans.property.DoubleProperty " + mf.name + "Property(){ return new javafx.beans.property.SimpleDoubleProperty(" + mf.name + ".doubleValue()); }");
            }

            if (mf.type == String.class) {
                sb.add("public javafx.beans.property.StringProperty " + mf.name + "Property(){ return new javafx.beans.property.SimpleStringProperty(" + mf.name + "); }");

            }

            if (mf.type == Integer.class || mf.type == int.class) {
                sb.add("public javafx.beans.property.StringProperty " + mf.name + "Property(){return new javafx.beans.property.SimpleStringProperty(String.valueOf(" + mf.name + " )); }");
            }
            if (mf.type == Double.class || mf.type == double.class) {
                sb.add("public javafx.beans.property.DoubleProperty " + mf.name + "Property(){ return new javafx.beans.property.SimpleDoubleProperty(" + mf.name + "); }");
            }
            if (mf.type == Float.class || mf.type == float.class) {
                sb.add("public javafx.beans.property.FloatProperty " + mf.name + "Property(){ return new javafx.beans.property.SimpleFloatProperty(" + mf.name + "); }");
            }
            if (mf.type == Date.class) {
                sb.add("public javafx.beans.property.StringProperty  " + mf.name + "Property(){ java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(\"dd.MM.yyyy HH:mm:ss\");if(" + mf.name + "==null) " +
                        "return null; return new  javafx.beans.property.SimpleStringProperty(dateFormat.format(" + mf.name + "));  }");
            }
        }
        return sb;
    }


}
