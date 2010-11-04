//stores the data to do with the patch
public class OffsetData{
	private int start;
	private int len;
	private byte[] data;
		
	public OffsetData(int start, byte[] data){
		this.start = start;
		this.len = data.length;
		this.data = data;
	}

	public int getStart() {
		return start;
	}

	public int getLength() {
		return len;
	}

	public byte[] getData() {
		return data.clone();
	}
	
	public OffsetData clone(){
		return new OffsetData(start, data.clone());
	}
}