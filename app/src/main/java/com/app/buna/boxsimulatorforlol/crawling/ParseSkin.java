package com.app.buna.boxsimulatorforlol.crawling;

import android.content.Context;

import java.util.Random;

public class ParseSkin {

    private Context context;

    public ParseSkin() {

    }

    public ParseSkin(Context context){
        this.context = context;
    }

    public String getRandomSkinUri(){

        Random rnd = new Random();
        int sNum = rnd.nextInt(3)+1; //스킨 넘버 : 0은 기본스킨이기 때문에 1부터
        int cNum = rnd.nextInt(116); //챔피언 넘버
        String[] cname = {"Garen","Galio","Gangplank","Gragas","Graves","Gnar","Nami","Nasus","Nautilus","Nocturne","Nunu","Nidalee","Darius","Diana","Draven","Ryze","Rammus","Lux"
                ,"Rumble","Renekton","Leona","Rek'Sai","Rengar","Lucian","Lulu","LeBlanc","LeeSin","Riven","Lissandra","MasterYi","Maokai","Malzahar","Malphite","Mordekaiser"
                ,"Morgana","Dr. Mundo","Miss Fortune","Bard","Varus","Vi","Veigar","Vayne","Vel'Koz","Volibear","Braum","Brand","Vladimir","Blitzcrank","Viktor","Poppy","Sion","Shaco"
                ,"Sejuani","Sona","Soraka","Shen","Shyvana","Swain","Skarner","Sivir","XinZhao","Syndra","Singed","Thresh","Ahri","Amumu","Azir","Akali","Aatrox","Alistar"
                ,"Annie","Anivia","Ashe","Yasuo","Ekko","Elise","Wukong","Orianna","Olaf","Yorick","Udyr","Urgot","Warwick","Irelia","Evelynn","Ezreal","JarvanIV","Zyra"
                ,"Zac","Janna","Jax","Zed","Xerath","Jayce","Ziggs","Zilean","Jinx","Cho'Gath","Karma","Kassadin","Karthus","Cassiopeia","Kha'Zix","Katarina","Kalista","kennen"
                ,"Caitlyn","Kayle","Kog'Maw","Corki","Quinn","Taric","Talon","Tahm Kench","Trundle","Tristana","Tryndamere","TwistedFate","Twitch","Pantheon"
                ,"Fiddlesticks","Fiora","Fizz","Heimerdinger","Hecarim"};  //2015년 기준이어서 없는 챔피언 추가해야함

        String Rcname = cname[cNum];    //랜덤 챔피언 name

        String imageUrl = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/"+Rcname+"_"+sNum+".jpg";

        return imageUrl;
    }

}
