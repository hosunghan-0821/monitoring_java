package com.example.monitor.monitoring.gebnegozi;

public class GebenegoziProdcutFindString {

    public static final String[][] STONE_ISLAND_URL_LIST = {
            {"STONE ISLAND","CLOTHING",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=791&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

    };
    public static final String[][] GEBE_URL_LIST = {

            //2024.02.16 추가 항목 업데이트
            {"AURAREE", "CLOTHING",             "http://93.46.41.5:1995/list-items?lsc=&lse=60,59,58,47,61&lde=2297&is=2&ir=6&ci=1&m=&s=0&n=1&cust=&cg=0", "man"},

            {"DIOR HOMME", "SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=60,47,59,58,61&lde=231&is=2&ir=8&ci=1&m=&s=0&n=1&cust=&cg=0", "man"},
            {"DIOR HOMME", "ACCESSORIES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,60,61&lde=231&is=2&ir=7&ci=1&m=&s=0&n=1&cust=&cg=0", "man"},

            {"LEMAIRE", "BAGS",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,60,59,58,61&lde=2691&is=2&ir=15&ci=1&m=&s=0&n=1&cust=&cg=0", "man"},
            {"LEMAIRE", "BAGS",             "http://93.46.41.5:1995/list-items?lsc=&lse=58,59,60,47,61&lde=2691&is=3&ir=15&ci=1&m=&s=0&n=1&cust=&cg=0", "woman"},

            {"RED WING", "SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=58,59,60,47,61&lde=2691&is=3&ir=15&ci=1&m=&s=0&n=1&cust=&cg=0", "man"},

            //2024.02.16 제거한 항목 update
            {"ADIDAS", "SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=17&is=2&ir=8&ci=0&m=&s=0&n=1", "man"},

            {"AMI", "CLOTHING",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=51&is=3&ir=-2&ci=0&m=&s=0&n=1", "woman"},
            {"AMI", "CLOTHING",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=51&is=1&ir=6&ci=0&m=&s=0&n=1","unisex"},
            {"AMI", "CLOTHING",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=51&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"ASICS",  "SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2409&is=1&ir=8&ci=0&m=&s=0&n=1","unisex"},
            {"ASICS",  "SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2409&is=2&ir=8&ci=0&m=&s=0&n=1", "man"},


            {"BALENCIAGA","BAGS",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=96&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"BALENCIAGA","SHOES",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=96&is=3&ir=8&ci=0&m=&s=0&n=1", "woman"},
            {"BALENCIAGA","BAGS",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=96&is=2&ir=15&ci=0&m=&s=0&n=1", "man"},
            {"BALENCIAGA","SHOES",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=96&is=2&ir=8&ci=0&m=&s=0&n=1", "man"},

            {"BIRKENSTOCK","SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=110&is=3&ir=8&ci=0&m=&s=0&n=1", "woman"},
            {"BIRKENSTOCK","SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=110&is=1&ir=-2&ci=0&m=&s=0&n=1","unisex"},


            {"BOTTEGA VENETA", "ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=131&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"BOTTEGA VENETA", "BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=131&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"BOTTEGA VENETA", "ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=131&is=1&ir=7&ci=0&m=&s=0&n=1","unisex"},
            {"BOTTEGA VENETA", "ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=131&is=2&ir=7&ci=0&m=&s=0&n=1", "man"},
            {"BOTTEGA VENETA", "BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=131&is=2&ir=15&ci=0&m=&s=0&n=1", "man"},

            {"BURBERRY", "ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=138&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"BURBERRY", "BAGS",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=138&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"BURBERRY", "ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=138&is=1&ir=7&ci=0&m=&s=0&n=1","unisex"},
            {"BURBERRY", "ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=138&is=2&ir=7&ci=0&m=&s=0&n=1", "man"},
            {"BURBERRY", "BAGS",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=138&is=2&ir=15&ci=0&m=&s=0&n=1", "man"},

            {"CARHARTT WIP","CLOTHING",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2139&is=1&ir=6&ci=0&m=&s=0&n=1","unisex"},
            {"CARHARTT WIP","CLOTHING",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2139&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},
            {"CARHARTT WIP","BAGS",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2139&is=2&ir=15&ci=0&m=&s=0&n=1", "man"},

            {"CELINE","ACCESSORIES",                    "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=167&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"CELINE","BAGS",                           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=167&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},

            {"CHLOE", "ACCESSORIES",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=184&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"CHLOE", "BAGS",                         "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=184&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},


            {"COMME DES GARCONS PLAY","CLOTHING",   "http://93.46.41.5:1995/list-items?lsc=&lse=47,55,57,56,58,59,60,61&lde=665&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},
            {"COMME DES GARCONS PLAY","CLOTHING",   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=665&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"COURREGES","CLOTHING",                "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=1108&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},

            {"DIESEL","CLOTHING",                   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=228&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},
            {"DIESEL","BAGS",                       "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=228&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"DIESEL","CLOTHING",                   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=228&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},


            {"GUCCI","ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=356&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"GUCCI","BAGS",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=356&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"GUCCI","ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=356&is=2&ir=7&ci=0&m=&s=0&n=1", "man"},
            {"GUCCI","BAGS",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=356&is=2&ir=15&ci=0&m=&s=0&n=1", "man"},


            {"HOKA ONE ONE","SHOES",            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2126&is=1&ir=8&ci=0&m=&s=0&n=1","unisex"},
            {"HOKA ONE ONE","SHOES",            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2126&is=2&ir=8&ci=0&m=&s=0&n=1", "man"},

            {"ISABEL MARANT","CLOTHING",        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=399&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},
            {"ISABEL MARANT","BAGS",            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=399&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"ISABEL MARANT","CLOTHING",        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=399&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"KENZO","CLOTHING",                "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=431&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},
            {"KENZO","CLOTHING",                "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=431&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"MAISON KITSUNE","CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=509&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},
            {"MAISON KITSUNE","CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=509&is=1&ir=6&ci=0&m=&s=0&n=1","unisex"},
            {"MAISON KITSUNE","CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=509&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"MAISON MARGIELA","ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=561&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"MAISON MARGIELA","BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=561&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"MAISON MARGIELA","BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=561&is=1&ir=15&ci=0&m=&s=0&n=1","unisex"},
            {"MAISON MARGIELA","ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=561&is=2&ir=7&ci=0&m=&s=0&n=1", "man"},
            {"MAISON MARGIELA","BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=561&is=2&ir=15&ci=0&m=&s=0&n=1", "man"},

            {"MIU MIU","ACCESSORIES",                   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=554&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"MIU MIU","BAGS",                          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=554&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},

            {"MONCLER","CLOTHING",                      "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=573&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},
            {"MONCLER","CLOTHING",                      "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=573&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"NEW BALANCE","SHOES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=95&is=3&ir=8&ci=0&m=&s=0&n=1", "woman"},
            {"NEW BALANCE","SHOES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=95&is=1&ir=8&ci=0&m=&s=0&n=1","unisex"},
            {"NEW BALANCE","SHOES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=95&is=2&ir=8&ci=0&m=&s=0&n=1", "man"},

            {"NIKE","SHOES",                            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=608&is=3&ir=8&ci=0&m=&s=0&n=1", "woman"},

            {"OUR LEGACY","CLOTHING",                   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2426&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"PRADA","ACCESSORIES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=675&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"PRADA","BAGS",                            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=675&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"PRADA","ACCESSORIES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=675&is=1&ir=7&ci=0&m=&s=0&n=1","unisex"},
            {"PRADA","BAGS",                            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=675&is=1&ir=15&ci=0&m=&s=0&n=1","unisex"},
            {"PRADA","ACCESSORIES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=675&is=2&ir=7&ci=0&m=&s=0&n=1", "man"},
            {"PRADA","BAGS",                            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=675&is=2&ir=15&ci=0&m=&s=0&n=1", "man"},


            {"SAINT LAURENT","ACCESSORIES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=908&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"SAINT LAURENT","BAGS",                    "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=908&is=3&ir=15&ci=0&m=&s=0&n=1", "woman"},
            {"SAINT LAURENT","ACCESSORIES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=908&is=2&ir=7&ci=0&m=&s=0&n=1", "man"},
            {"SAINT LAURENT","BAGS",                    "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=908&is=2&ir=15&ci=0&m=&s=0&n=1", "man"},



            {"STUSSY","CLOTHING",                       "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2171&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"TEN C","CLOTHING",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2137&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"THE NORTH FACE","CLOTHING",               "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2334&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},
            {"THE NORTH FACE","CLOTHING",               "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=2334&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"THOM BROWNE","CLOTHING",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=813&is=3&ir=6&ci=0&m=&s=0&n=1", "woman"},
            {"THOM BROWNE","ACCESSORIES",               "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=813&is=3&ir=7&ci=0&m=&s=0&n=1", "woman"},
            {"THOM BROWNE","CLOTHING",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=813&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},
            {"THOM BROWNE","ACCESSORIES",               "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=813&is=2&ir=7&ci=0&m=&s=0&n=1", "man"},

            {"VIVIENNE WESTWOOD","JEWELS",              "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57,60,61&lde=886&is=3&ir=16&ci=0&m=&s=0&n=1", "woman"},

            {"WOOYOUNGMI","CLOTHING",                   "http://93.46.41.5:1995/list-items?lsc=&lse=55,56,57,58,59,60,61&lde=2694&is=2&ir=6&ci=0&m=&s=0&n=1", "man"},

            {"LONGCHAMP","BAGS",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,60,58,59,61&lde=474&is=3&ir=15&ci=1&m=&s=0&n=1&cust=&cg=0", "woman"},
            {"LONGCHAMP","BAGS",               "http://93.46.41.5:1995/list-items?lsc=&lse=58,47,60,59,61&lde=474&is=2&ir=15&ci=1&m=&s=0&n=1&cust=&cg=0", "man"},
////


    };

    public static final String GNB = "GNB";

    public static final String GNB_STONE_ISLAND = "GNB_STONE_ISLAND";
    public static final String SESSION_COOKIE_KEY = "JSESSIONID";
    public static final String COOKIE = "Cookie";

    public static final String GEBENE_LOG_PREFIX = "[GEBENEGOZI] : \t";

    public static final String GEBENE_MAIN_URL = "http://93.46.41.5:1995/login";

}
