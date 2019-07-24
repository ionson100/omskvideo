package orm;

import java.util.HashMap;
import java.util.Map;

public class ContentValues {

   static class PairValue{
        public String name;
        public Object value;
    }
   Map<String,Object> map=new HashMap<>();

    public void put(String s,Object o){
        map.put(s,o);
    }
    public Map<String,Object> getMap(){
        return map;
    }

}
