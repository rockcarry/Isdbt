package kr.co.fci.tv.tvSolution;
import java.util.*;

class ScanList {
	private int		_idx;	// App Internal index
	private String	_service_name;
	private byte _segType;
	private byte _vidFormat;
	private byte _audFormat;
	private byte _isFree;

	public ScanList()
	{
		//
	}	
	
	public void SetScanList(int idx, String desc, byte segType, byte vidFormat, byte audFormat, byte isFree)
	{
		_idx = idx;
		_service_name = desc;
		_segType = segType;
		_vidFormat = vidFormat;
		_audFormat = audFormat;
		_isFree = isFree;
	}	

	
	public int GetIndex()
	{
		return _idx;
	}
	
	
	public String GetServiceName()
	{
		return _service_name;
	}	
	public byte GetServiceType()
	{
		return _segType; //1 full-seg 0 1-seg
	}
	public byte GetServiceVidFormat() {
		return _vidFormat; // 0x00 - unknown, 0x04 - H264, 0x02 - MPEG-2 video, 0x01 - MPEG-1 video
	}
	public byte GetServiceAudFormat() {
		return _audFormat; // 0x00 - unknown, 0x60 - HEAAC, 0x40 - AAC, 0x20 - MP2, 0x10 - MP1
	}
	public byte IsServiceFree() {
		return _isFree; // 0 - scramble service, 1 - free service
	}
}
	

public class ScanListManager{
	private ArrayList<ScanList>	_scanlist_collection;
	
	private int mScanListCount = 0;
	private int mSelectedIndex = 0;
	
		
	public ScanListManager()
	{
		_scanlist_collection = new ArrayList<ScanList>();
	}
	
	public int GetTotalItemCount()
	{
		return _scanlist_collection.size();
	}
	
	public ScanList getAt(short idx)
	{
		return _scanlist_collection.get(idx);
	}
	
	public boolean AddScanList(ScanList scanlist)
	{
		return _scanlist_collection.add(scanlist);
	}
	
	public boolean RemoveScanList(ScanList scanlist)
	{
		return _scanlist_collection.remove(scanlist);
	}	
	
	public void RemoveAllChannel()
	{
		ScanList tmp;
		Iterator<ScanList> it = _scanlist_collection.iterator();
		
		while(it.hasNext())
		{
			tmp = it.next();
			it.remove();
			//_scanlist_collection.remove(tmp);
		}
	}
	
	//Channel Change - setup
	public void SetupScanListChange(int ch_idx)
	{
		mSelectedIndex = ch_idx;
/*		
		int i = 0;
		ScanList tmp;
		Iterator<ScanList> it = _scanlist_collection.iterator();
		while(it.hasNext())
		{
			tmp = it.next();
			if(tmp.GetIndex() == ch_idx)
			{
				mSelectedIndex = i;
				break;
			}
			i++;
		}
*/		
		mScanListCount = GetTotalItemCount();
		
	}

	//Channel Change - previous
	public ScanList PreviousScanList(byte type)
	{
		int i = 0;
		ScanList prevList;
		if(mScanListCount == 0)
			return null;
		
		while(i++ < mScanListCount)
		{
			if(mSelectedIndex == 0)
				mSelectedIndex = mScanListCount - 1;
			else
				mSelectedIndex--;
			
			prevList = _scanlist_collection.get(mSelectedIndex);
			//if(prevList.GetType() == type)
				return prevList;
		}
		
		return null;
	}
	
	//Channel Change - next	
	public ScanList NextScanList(byte type)
	{
		int i = 0;
		ScanList nextList;
		if(mScanListCount == 0)
			return null;
		
		while(i++ < mScanListCount)
		{		
			mSelectedIndex++;
			mSelectedIndex = (mSelectedIndex % mScanListCount);
			
			nextList = _scanlist_collection.get(mSelectedIndex);
			//if(nextList.GetType() == type)
				return nextList;
		}
		
		return null;
	}
}
