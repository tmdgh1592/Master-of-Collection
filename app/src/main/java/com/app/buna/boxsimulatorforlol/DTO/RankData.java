package com.app.buna.boxsimulatorforlol.DTO;


public class RankData implements Comparable<RankData>{

    public Integer champCount, skinCount, totalCount;
    public String nickname;
    public Long created;


    public RankData(String nickname, int champCount, int skinCount, int totalCount, long created){
        this.nickname = nickname;
        this.champCount = champCount;
        this.skinCount = skinCount;
        this.totalCount = totalCount;
        this.created = created;
    }

    public int getChampCount() {
        return champCount;
    }

    public void setChampCount(int champCount) {
        this.champCount = champCount;
    }

    public int getSkinCount() {
        return skinCount;
    }

    public void setSkinCount(int skinCount) {
        this.skinCount = skinCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }


    @Override
    public int compareTo(RankData rankData) {
        if(totalCount == rankData.getTotalCount()) {
            /*오름차순*/
            return this.created.compareTo(rankData.created);
        }else{
            /*내림차순*/
            return rankData.totalCount.compareTo(this.totalCount);
        }
    }
}
