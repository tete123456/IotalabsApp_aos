package com.iotalabs.geoar;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String UUID = "UUID";
        public static final String NAME = "name";
        public static final String  Relation= "relation";
        public static final String _TABLENAME = "friend";
        public static final String _CREATE =
                "create table "+_TABLENAME+"("
                        +_ID+" integer primary key autoincrement, "
                        +UUID+" text not null, "
                        +NAME+" text not null  );";
    }
    public static final class CreateDB2 implements BaseColumns {
        public static final String UUID = "UUID";
        public static final String str_latitude = "str_latitude";
        public static final String str_longitude = "str_longitude";
        public static final String _TABLENAME2 = "location";
        public static final String _CREATE =
                "create table " + _TABLENAME2 + "("
                        + _ID + " integer primary key autoincrement, "
                        + UUID + " text not null, "
                        + str_latitude + " text , "
                        + str_longitude + " text );";
    }
    public static final class CreateDB3 implements BaseColumns {
        public static final String name = "name";
        public static final String str_latitude = "str_latitude";
        public static final String str_longitude = "str_longitude";
        public static final String _TABLENAME3 = "friendNameLoc";
        public static final String _CREATE =
                "create table " + _TABLENAME3 + "("
                        + _ID + " integer primary key autoincrement, "
                        + name + " text not null, "
                        + str_latitude + " text , "
                        + str_longitude + " text );";
    }
}