package com.example.monitor.monitoring.gebnegozi;

public class GebenegoziProdcutFindString {

    public static final String[][] GEBE_URL_LIST = {
            {"A.P.C.", "CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=65&is=3&ir=6&ci=0&m=&s=0&n=1&cust="},
            {"A.P.C.", "BAGS",              "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=65&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"A.P.C.", "CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=65&is=1&ir=6&ci=0&m=&s=0&n=1"},
            {"A.P.C.", "BAGS",              "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=65&is=1&ir=15&ci=0&m=&s=0&n=1"},
            {"A.P.C.", "CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=65&is=2&ir=6&ci=0&m=&s=0&n=1&cust="},
            {"A.P.C.", "BAGS",              "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=65&is=2&ir=15&ci=0&m=&s=0&n=1"},

            {"ADIDAS", "SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=17&is=2&ir=8&ci=0&m=&s=0&n=1"},


            {"ALEXANDER MCQUEEN","SHOES",   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=35&is=3&ir=8&ci=0&m=&s=0&n=1"},
            {"ALEXANDER MCQUEEN","SHOES",   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=35&is=2&ir=8&ci=0&m=&s=0&n=1"},

            {"AMI", "CLOTHING",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=51&is=3&ir=-2&ci=0&m=&s=0&n=1"},
            {"AMI", "CLOTHING",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=51&is=1&ir=6&ci=0&m=&s=0&n=1"},
            {"AMI", "CLOTHING",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=51&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"ASICS",  "SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2409&is=1&ir=8&ci=0&m=&s=0&n=1"},
            {"ASICS",  "SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2409&is=2&ir=8&ci=0&m=&s=0&n=1"},


            {"BALENCIAGA","BAGS",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=96&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"BALENCIAGA","SHOES",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=96&is=3&ir=8&ci=0&m=&s=0&n=1"},
            {"BALENCIAGA","BAGS",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=96&is=2&ir=15&ci=0&m=&s=0&n=1"},
            {"BALENCIAGA","SHOES",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=96&is=2&ir=8&ci=0&m=&s=0&n=1"},

            {"BIRKENSTOCK","SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=110&is=3&ir=8&ci=0&m=&s=0&n=1"},
            {"BIRKENSTOCK","SHOES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=110&is=1&ir=-2&ci=0&m=&s=0&n=1"},


            {"BOTTEGA VENETA", "ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=131&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"BOTTEGA VENETA", "BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=131&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"BOTTEGA VENETA", "ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=131&is=1&ir=7&ci=0&m=&s=0&n=1"},
            {"BOTTEGA VENETA", "ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=131&is=2&ir=7&ci=0&m=&s=0&n=1"},
            {"BOTTEGA VENETA", "BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=131&is=2&ir=15&ci=0&m=&s=0&n=1"},

            {"BURBERRY", "ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=138&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"BURBERRY", "BAGS",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=138&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"BURBERRY", "ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=138&is=1&ir=7&ci=0&m=&s=0&n=1"},
            {"BURBERRY", "ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=138&is=2&ir=7&ci=0&m=&s=0&n=1"},
            {"BURBERRY", "BAGS",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=138&is=2&ir=15&ci=0&m=&s=0&n=1"},

            {"CARHARTT WIP","CLOTHING",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2139&is=1&ir=6&ci=0&m=&s=0&n=1"},
            {"CARHARTT WIP","CLOTHING",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2139&is=2&ir=6&ci=0&m=&s=0&n=1"},
            {"CARHARTT WIP","BAGS",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2139&is=2&ir=15&ci=0&m=&s=0&n=1"},

            {"CELINE","ACCESSORIES",                    "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=167&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"CELINE","BAGS",                           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=167&is=3&ir=15&ci=0&m=&s=0&n=1"},

            {"CHLOE", "ACCESSORIES",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=184&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"CHLOE", "BAGS",                         "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=184&is=3&ir=15&ci=0&m=&s=0&n=1"},


            {"COMME DES GARCONS PLAY","CLOTHING",   "http://93.46.41.5:1995/list-items?lsc=&lse=47,55,57,56,58,59&lde=665&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"COMME DES GARCONS PLAY","CLOTHING",   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=665&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"COURREGES","CLOTHING",                "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=1108&is=3&ir=6&ci=0&m=&s=0&n=1"},

            {"DIESEL","CLOTHING",                   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=228&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"DIESEL","BAGS",                       "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=228&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"DIESEL","CLOTHING",                   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=228&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"GOLDEN GOOSE DELUXE BRAND","SHOES",   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=330&is=2&ir=8&ci=0&m=&s=0&n=1"},

            {"GUCCI","ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=356&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"GUCCI","BAGS",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=356&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"GUCCI","ACCESSORIES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=356&is=2&ir=7&ci=0&m=&s=0&n=1"},
            {"GUCCI","BAGS",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=356&is=2&ir=15&ci=0&m=&s=0&n=1"},


            {"HOKA ONE ONE","SHOES",            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2126&is=1&ir=8&ci=0&m=&s=0&n=1"},
            {"HOKA ONE ONE","SHOES",            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2126&is=2&ir=8&ci=0&m=&s=0&n=1"},

            {"ISABEL MARANT","CLOTHING",        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=399&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"ISABEL MARANT","BAGS",            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=399&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"ISABEL MARANT","CLOTHING",        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=399&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"JACQUEMUS","ACCESSORIES",         "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=1353&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"JACQUEMUS","BAGS",                "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=1353&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"JACQUEMUS","ACCESSORIES",         "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=1353&is=2&ir=7&ci=0&m=&s=0&n=1"},
            {"JACQUEMUS","BAGS",                "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=1353&is=2&ir=15&ci=0&m=&s=0&n=1"},

            {"KENZO","CLOTHING",                "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=431&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"KENZO","CLOTHING",                "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=431&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"MAISON KITSUNE","CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=509&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"MAISON KITSUNE","CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=509&is=1&ir=6&ci=0&m=&s=0&n=1"},
            {"MAISON KITSUNE","CLOTHING",          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=509&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"MAISON MARGIELA","ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=561&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"MAISON MARGIELA","BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=561&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"MAISON MARGIELA","BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=561&is=1&ir=15&ci=0&m=&s=0&n=1"},
            {"MAISON MARGIELA","ACCESSORIES",           "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=561&is=2&ir=7&ci=0&m=&s=0&n=1"},
            {"MAISON MARGIELA","BAGS",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=561&is=2&ir=15&ci=0&m=&s=0&n=1"},
            {"MAISON MARGIELA","SHOES",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=561&is=2&ir=8&ci=0&m=&s=0&n=1"},

            {"MIU MIU","ACCESSORIES",                   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=554&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"MIU MIU","BAGS",                          "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=554&is=3&ir=15&ci=0&m=&s=0&n=1"},

            {"MONCLER","CLOTHING",                      "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=573&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"MONCLER","CLOTHING",                      "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=573&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"NEW BALANCE","SHOES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=95&is=3&ir=8&ci=0&m=&s=0&n=1"},
            {"NEW BALANCE","SHOES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=95&is=1&ir=8&ci=0&m=&s=0&n=1"},
          //  {"NEW BALANCE","SHOES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=95&is=3&ir=8&ci=0&m=&s=0&n=1"},

            {"NIKE","SHOES",                            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=608&is=3&ir=8&ci=0&m=&s=0&n=1"},

            {"OUR LEGACY","CLOTHING",                   "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2426&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"PRADA","ACCESSORIES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=675&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"PRADA","BAGS",                            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=675&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"PRADA","ACCESSORIES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=675&is=1&ir=7&ci=0&m=&s=0&n=1"},
            {"PRADA","BAGS",                            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=675&is=1&ir=15&ci=0&m=&s=0&n=1"},
            {"PRADA","ACCESSORIES",                     "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=675&is=2&ir=7&ci=0&m=&s=0&n=1"},
            {"PRADA","BAGS",                            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=675&is=2&ir=15&ci=0&m=&s=0&n=1"},


            {"SAINT LAURENT","ACCESSORIES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=908&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"SAINT LAURENT","BAGS",                    "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=908&is=3&ir=15&ci=0&m=&s=0&n=1"},
            {"SAINT LAURENT","ACCESSORIES",             "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=908&is=2&ir=7&ci=0&m=&s=0&n=1"},
            {"SAINT LAURENT","BAGS",                    "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=908&is=2&ir=15&ci=0&m=&s=0&n=1"},


            {"STONE ISLAND","CLOTHING",                 "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=791&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"STUSSY","CLOTHING",                       "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2171&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"TEN C","CLOTHING",                        "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2137&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"THE NORTH FACE","CLOTHING",               "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2334&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"THE NORTH FACE","CLOTHING",               "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=2334&is=2&ir=6&ci=0&m=&s=0&n=1"},

            {"THOM BROWNE","CLOTHING",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=813&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"THOM BROWNE","ACCESSORIES",               "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=813&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"THOM BROWNE","CLOTHING",                  "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=813&is=2&ir=6&ci=0&m=&s=0&n=1"},
            {"THOM BROWNE","ACCESSORIES",               "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=813&is=2&ir=7&ci=0&m=&s=0&n=1"},
            {"THOM BROWNE","BAGS",                      "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=813&is=2&ir=15&ci=0&m=&s=0&n=1"},

            {"VIVIENNE WESTWOOD","CLOTHING",            "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=886&is=3&ir=6&ci=0&m=&s=0&n=1"},
            {"VIVIENNE WESTWOOD","ACCESSORIES",         "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=886&is=3&ir=7&ci=0&m=&s=0&n=1"},
            {"VIVIENNE WESTWOOD","JEWELS",              "http://93.46.41.5:1995/list-items?lsc=&lse=47,59,58,57&lde=886&is=3&ir=16&ci=0&m=&s=0&n=1"},

            {"WOOYOUNGMI","CLOTHING",                   "http://93.46.41.5:1995/list-items?lsc=&lse=55,56,57,58,59&lde=2694&is=2&ir=6&ci=0&m=&s=0&n=1"},
//


    };

    public static final String GEBE = "gebe";

    public static final String SESSION_COOKIE_KEY = "JSESSIONID";
    public static final String COOKIE = "Cookie";

    public static final String GEBENE_LOG_PREFIX = "[GEBENEGOZI] : \t";
}
