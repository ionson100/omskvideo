package model;

import orm.Column;
import orm.PrimaryKey;
import orm.Table;

@Table(MPlayList.TABLE_NAME)
public class MPlayList{

   public static  final String TABLE_NAME="playist";

   @PrimaryKey("_id")
   public int _id;

   @Column("path")
   public String path;
    @Column("description")
   public String description;
    @Column("size")
   public int size;
    @Column("id")
   public int id;
    @Column("name")
   public String name;
}
