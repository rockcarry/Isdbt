package kr.co.fci.tv.channelList;

/**
 * Created by live.kim on 2015-09-14.
 */
public class Channel {

    private int index;
    private String name;
    private int type;
    private int free;
    private int remoteKey;
    private int svcID;
    private int freqKHz;

    public Channel() {
        super();
    }

    public Channel(int _index, String _name, int _type, int _free, int _remoteKey, int _svcID, int _freqKHz) {
        super();
        this.index = _index;
        this.name = _name;
        this.type = _type;
        this.free = _free;
        this.remoteKey = _remoteKey;
        this.svcID = _svcID;
        this.freqKHz = _freqKHz;
    }



    public int getFreqKHz() {
        return freqKHz;
    }

    public int getindex() {
        return index;
    }

    public void setindex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {

        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFree() {

        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getRemoteKey() {
        return remoteKey;
    }

    public void setRemoteKey(int _remoteKey) {
        this.remoteKey = _remoteKey;
    }

    public int getSvcID() {
        return svcID;
    }

    public void setSvcID(int _svcID) {
        this.svcID = _svcID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Channel other = (Channel) obj;
        if (index != other.index)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Channel change to [ " + name + " ]";
    }



}
