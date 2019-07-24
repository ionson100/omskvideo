package orm;

import java.util.Dictionary;
import java.util.Hashtable;

public class CacheDictionary {

   private static final Object lock = new Object();

   private static final Dictionary<String, CacheMetaDate> dic = new Hashtable();

   public static CacheMetaDate getCacheMetaDate(Class aClass) {
       if (dic.get(aClass.getName()) == null) {
           synchronized (lock) {
               if (dic.get(aClass.getName()) == null) {
                   dic.put(aClass.getName(), new CacheMetaDate<>(aClass));
               }
           }
       }
       return dic.get(aClass.getName());
   }
}
