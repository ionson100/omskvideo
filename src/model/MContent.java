package model;

import orm.Column;
import orm.PrimaryKey;
import orm.Table;

import java.util.ArrayList;
import java.util.List;

public class MContent {

   public String point_id;
   public List<MPlayList> playlist=new ArrayList<>();

}
