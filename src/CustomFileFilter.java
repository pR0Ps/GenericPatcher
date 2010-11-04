import java.io.File;
import javax.swing.filechooser.FileFilter;


public class CustomFileFilter extends FileFilter {

	private String[] extFilter;
	
	public CustomFileFilter(String[] extFilter){
		this.extFilter = extFilter;
	}
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		if (extFilter == null){
			return true;
		}
		String extension = getExtension(f);
		if (extension != null) {
			for (int i = 0 ; i < extFilter.length ; i++){
				if (extension.equals(extFilter[i])){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Executable Files";
	}
	
	/*
     * Get the extension of a file, in lower case.
     * @param f the file whose extension to get.  Must not be null.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
 
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

}
