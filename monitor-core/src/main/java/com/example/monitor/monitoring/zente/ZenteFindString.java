package com.example.monitor.monitoring.zente;

public class ZenteFindString {

    public static final String ZENTE_LOG_PREFIX = "[ZENTE] : \t";

    public static final String ZENTE = "zente";

    public static final String[][] ZENTE_URL_INFO = {
            {"살로몬", "스니커즈", "https://www.jentestore.com/goods/list?keyword=%EC%82%B4%EB%A1%9C%EB%AA%AC&gender=0003&category[]=000300020001&per=24&page=1&sort=low_price&imageSize=medium&type[]=category&type[]=brand&type[]=color&type[]=size&type[]=logistics"},
            {"살로몬", "샌들", "https://www.jentestore.com/goods/list?gender=0003&category[]=0003000200050001&brand[]=0372&per=24&page=1&sort=sale&imageSize=medium&type[]=brand&type[]=color&type[]=size"},

//            {"르메르", "가방", "https://jentestore.com/goods/list?gender=0003&category[]=00030004&brand[]=0102&per=24&page=1&sort=regist&imageSize=medium&type[]=category&type[]=brand&type[]=color&type[]=size&type[]=logistics"},
//
//            {"셀린느", "토트백", "https://jentestore.com/goods/list?gender=0003&category[]=000300040001&brand[]=0036&per=24&page=1&sort=low_price&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"셀린느", "크로스백", "https://jentestore.com/goods/list?gender=0003&category[]=000300040002&brand[]=0036&per=24&page=1&sort=low_price&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"셀린느", "버킷백", "https://jentestore.com/goods/list?gender=0003&category[]=000300040008&brand[]=0036&per=24&page=1&sort=low_price&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"셀린느", "목걸이", "https://jentestore.com/goods/list?gender=0003&category[]=0003000300050002&brand[]=0036&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"셀린느", "지갑", "https://jentestore.com/goods/list?gender=0003&category[]=0003000300010001&brand[]=0036&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"셀린느", "카드지갑", "https://jentestore.com/goods/list?gender=0003&category[]=0003000300010002&brand[]=0036&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"셀린느", "동전지갑", "https://jentestore.com/goods/list?gender=0003&category[]=0003000300010004&brand[]=0036&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//
//            {"미우미우", "지갑", "https://jentestore.com/goods/list?gender=0003&category[]=0003000300010001&brand[]=0117&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"미우미우", "카드지갑", "https://jentestore.com/goods/list?gender=0003&category[]=0003000300010002&brand[]=0117&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"미우미우", "토트백", "https://jentestore.com/goods/list?gender=0003&category[]=000300040001&brand[]=0117&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"미우미우", "크로스백", "https://jentestore.com/goods/list?gender=0003&category[]=000300040002&brand[]=0117&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"미우미우", "버킷백", "https://jentestore.com/goods/list?gender=0003&category[]=000300040008&brand[]=0117&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//
//            {"스톤", "자켓", "https://jentestore.com/goods/list?keyword=%EC%8A%A4%ED%86%A4%EC%95%84%EC%9D%BC%EB%9E%9C%EB%93%9C&gender=0003&category[]=0003000100010001&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"스톤", "패딩", "https://jentestore.com/goods/list?keyword=%EC%8A%A4%ED%86%A4%EC%95%84%EC%9D%BC%EB%9E%9C%EB%93%9C&gender=0003&category[]=0003000100010008&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"스톤", "파카", "https://jentestore.com/goods/list?keyword=%EC%8A%A4%ED%86%A4%EC%95%84%EC%9D%BC%EB%9E%9C%EB%93%9C&gender=0003&category[]=0003000100010007&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"스톤", "스웨트셔츠", "https://jentestore.com/goods/list?keyword=%EC%8A%A4%ED%86%A4%EC%95%84%EC%9D%BC%EB%9E%9C%EB%93%9C&gender=0003&category[]=0003000100020002&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"스톤", "반팔", "https://jentestore.com/goods/list?keyword=%EC%8A%A4%ED%86%A4%EC%95%84%EC%9D%BC%EB%9E%9C%EB%93%9C&gender=0003&category[]=0003000100020006&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//
//            {"몽클레어", "패딩", "https://jentestore.com/goods/list?gender=0003&category[]=0003000100010008&brand[]=0118&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"몽클레어", "베스트", "https://jentestore.com/goods/list?gender=0003&category[]=0003000100010011&brand[]=0118&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//
//            {"프라다", "지갑", "https://jentestore.com/goods/list?gender=0003&category[]=0003000300010001&brand[]=0137&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"프라다", "카드지갑", "https://jentestore.com/goods/list?gender=0003&category[]=0003000300010002&brand[]=0137&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"프라다", "토트백", "https://jentestore.com/goods/list?gender=0003&category[]=000300040001&brand[]=0137&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"프라다", "백팩", "https://jentestore.com/goods/list?gender=0003&category[]=000300040003&brand[]=0137&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"프라다", "크로스", "https://jentestore.com/goods/list?gender=0003&category[]=000300040002&brand[]=0137&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//
//            {"마르지엘라", "토트백", "https://www.jentestore.com/goods/list?gender=0003&category[]=000300040001&brand[]=0106&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"마르지엘라", "크로스백", "https://www.jentestore.com/goods/list?gender=0003&category[]=000300040002&brand[]=0106&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"마르지엘라", "백팩", "https://www.jentestore.com/goods/list?gender=0003&category[]=000300040003&brand[]=0106&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"마르지엘라", "버킷백", "https://www.jentestore.com/goods/list?gender=0003&category[]=000300040008&brand[]=0106&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//
//            {"비비안", "목걸이", "https://www.jentestore.com/goods/list?gender=0003&category[]=0003000300050002&brand[]=0192&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//
//            {"생로랑", "지갑", "https://www.jentestore.com/goods/list?gender=0003&category[]=0003000300010001&brand[]=0152&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//            {"생로랑", "카드지갑", "https://www.jentestore.com/goods/list?gender=0003&category[]=0003000300010002&brand[]=0152&per=24&page=1&sort=regist&imageSize=medium&type[]=brand&type[]=color&type[]=size"},
//
//            {"보테가", "지갑", "https://www.jentestore.com/goods/list?gender=0003&category[]=0003000300010001&brand[]=0026&per=24&page=1&sort=regist&imageSize=medium&type[]=color&type[]=size"},
//            {"보테가", "카드지갑", "https://www.jentestore.com/goods/list?gender=0003&category[]=0003000300010002&brand[]=0026&per=24&page=1&sort=regist&imageSize=medium&type[]=color&type[]=size"}
    };

//    public static final String[] brandUrlList = {
//            "https://jentestore.com/goods/list?gender=0003&category[]=00030004&brand[]=0102&per=24&page=1&sort=regist&imageSize=medium&type[]=category&type[]=brand&type[]=color&type[]=size&type[]=logistics",
//            "https://www.jentestore.com/goods/list?keyword=%EC%82%B4%EB%A1%9C%EB%AA%AC&gender=0003&category[]=00030002&per=24&page=1&sort=low_price&imageSize=medium&type[]=brand&type[]=color&type[]=size",
//            "https://www.jentestore.com/goods/list?gender=0003&category[]=00030004&category[]=000300030001&brand[]=0117&per=24&page=1&sort=sale&imageSize=medium&type[]=brand&type[]=color&type[]=size",
//            "https://www.jentestore.com/goods/list?gender=0003&category[]=00030004&category[]=000300030001&category[]=0003000100010008&brand[]=0137&per=24&page=1&sort=sale&imageSize=medium&type[]=color&type[]=size",
//            "https://www.jentestore.com/goods/list?gender=0003&category[]=0003000100010008&brand[]=0163&per=24&page=1&sort=sale&imageSize=medium&type[]=color&type[]=size",
//            "https://www.jentestore.com/goods/list?gender=0003&category[]=0003000100010008&brand[]=0118&per=24&page=1&sort=sale&imageSize=medium&type[]=color&type[]=size",
//            "https://jentestore.com/goods/list?gender=0003&category[]=00030004&category[]=000300030001&brand[]=0026&per=24&page=1&sort=sale&imageSize=medium&type[]=brand&type[]=color&type[]=size",
//            "https://jentestore.com/goods/list?gender=0003&category[]=00030004&category[]=000300030001&brand[]=0036&per=24&page=1&sort=sale&imageSize=medium&type[]=color&type[]=size"
//
//    };
}
