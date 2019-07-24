package orm;



import model.ModelCollection;
import org.apache.log4j.Logger;

import java.util.List;

public final class CreatorTable {
    private static final Logger log = Logger.getLogger(CreatorTable.class);

//    public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        assert classLoader != null;
//        String path = packageName.replace('.', '/');
//        Enumeration resources = classLoader.getResources(path);
//        List dirs = new ArrayList();
//        while (resources.hasMoreElements()) {
//            URL resource = (URL) resources.nextElement();
//            dirs.add(new File(resource.getFile()));
//        }
//        ArrayList classes = new ArrayList();
//        for (Object directory : dirs) {
//            classes.addAll(findClasses((File) directory, packageName));
//        }
//        return (Class[]) classes.toArray(new Class[classes.size()]);
//    }




//    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
//        List classes = new ArrayList();
//        if (!directory.exists()) {
//            return classes;
//        }
//        File[] files = directory.listFiles();
//        for (File file : files) {
//            if (file.isDirectory()) {
//                assert !file.getName().contains(".");
//                classes.addAll(findClasses(file, packageName + "." + file.getName()));
//            } else if (file.getName().endsWith(".class")) {
//                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
//            }
//        }
//        return classes;
//    }

    public final static void createTable()  {

        StringBuilder sb=new StringBuilder();
        List<Class> classes= ModelCollection.classes;//getClasses(packetName);
        for (Class aClass : classes) {
            if(aClass.isAnnotationPresent(Table.class)){
                sb.append(Configure.getStringCreateTable(aClass));
                sb.append("; ");
            }
        }
        String[] s=sb.toString().split(";");
        for (String s1 : s) {
            String ss=s1.trim();
            if(ss.length()==0) continue;
            ISession ses=Configure.getSession();
            ses.execSQL(s1,null);
        }

    }
}
