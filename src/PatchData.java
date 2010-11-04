import java.util.ArrayList;

//holds all the data pertaining to the file patch
public class PatchData {
	
	private String MD5before;
	private String MD5after;
	private ArrayList<OffsetData> offsets = new ArrayList<OffsetData>(2);
	
	//main constructor
	//parse the line and organize the data
	public PatchData(String data) throws Exception{
		try {
			String temp = data;
			int offset;
			String patch;
			int index = temp.indexOf("|");

			MD5before = temp.substring(0, index);

			temp = temp.substring(index+1);
			index = temp.indexOf("|");

			MD5after = temp.substring (0, index);

			while (true){
				temp = temp.substring(index+1);
				index = temp.indexOf("|");
				if (index == -1) break; //end of data

				offset = Integer.parseInt(temp.substring(0, index));
				temp = temp.substring(index+1);
				index = temp.indexOf("|");

				patch = temp.substring(0, index);

				offsets.add(new OffsetData(offset, hexStringToByteArray(patch)));
			}
			offsets.trimToSize();
		}
		catch (Exception e){
			throw new Exception ();
		}
	}
	
	//converts a string representation of bytes to an array of bytes
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	public String getMD5before() {
		return MD5before;
	}

	public String getMD5after() {
		return MD5after;
	}
	
	public ArrayList<OffsetData> getOffsetData(){
		//TODO: information hiding
		ArrayList<OffsetData> temp = new ArrayList<OffsetData>(offsets.size());
		for (OffsetData d : offsets){
			temp.add(d.clone());
		}
		return temp;
	}
}
