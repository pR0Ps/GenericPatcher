import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

public class GenericPatcher extends JFrame{

	private static final long serialVersionUID = 3108394064726135085L;

	//graphical
	public static final String PATCHER_NAME = "QueensDC ApexDC++ Patcher v3.5";
	public static final String LOGO_PATH = "rsc/logo.png"; //image (450*125) path relative to the /src/ dir.
	public static final int WINDOW_X = 500;
	public static final int WINDOW_Y = 420;
	public static final int BORDER_SIZE = 20;

	//functional
	public static final String PATCH_DATABASE = "http://queensdc.ca/patchDatabase.dat";
	public static final String APP_NAME = "ApexDC++";
	public static final String UPDATE_NAME = "QueensDC version";
	public static final String WEBSITE = "http://queensdc.ca/";
	public static final String[] CONTACTS = {"AlexiStukov", "pR0Ps"}; //make null for no support
	public static final String[] EXT_FILTER = {"exe"}; //make null to not filter files by extension
	public static final String[] FILENAMES = {"ApexDC.exe", "ApexDC-x64.exe"};
	public static final String[] FILEPATHS = {System.getenv("HOMEDRIVE") + "\\Program Files\\ApexDC++", System.getenv("HOMEDRIVE") + "\\Program Files (x86)\\ApexDC++"};
	public static final String CUSTOM_SEARCH_PATH = System.getenv("HOMEDRIVE") + "\\Program Files\\";

	//components
	JTextArea statusTextArea;
	JButton fileButton;
	JButton patchButton;
	JButton exitButton;

	//data
	File fileToPatch;
	PatchData patchData;
	JTextField fileField;

	public GenericPatcher(){
		super (PATCHER_NAME + " || pR0Ps");
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(WINDOW_X, WINDOW_Y));
		setResizable (false);
		setLayout (new BorderLayout());

		//attempt to adapt to the operating system's look
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {
			//shit son, you get the crappy Java UI ;)
		}
		//		if (JOptionPane.showConfirmDialog(null, "This application will patch your " + APP_NAME + " install to the " + UPDATE_NAME + ".\nIt will check an online database to see what to patch in your version.\nNo personal information will be sent.\n\nContinue?", "Continue?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
		//			System.exit(0);
		//		}

		//set up the text area
		statusTextArea = new JTextArea(PATCHER_NAME + " by pR0Ps\n");
		statusTextArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		statusTextArea.setEditable(false);
		statusTextArea.setWrapStyleWord(true);

		//set up the select file button
		fileButton = new JButton ("Select File");
		fileButton.setToolTipText("Select your " + FILENAMES[0] + " file");
		fileButton.addActionListener (new ActionListener(){
			public void actionPerformed (ActionEvent e){
				File f = chooseFile();
				if (!(fileToPatch != null && f == null)){
					fileToPatch = f;
				}
				if (fileToPatch != null){
					fileField.setText(fileToPatch.getAbsolutePath());
				}
			}
		});

		patchButton = new JButton ("Patch");
		patchButton.setToolTipText("Patch selected file");
		patchButton.addActionListener (new ActionListener(){
			public void actionPerformed (ActionEvent e){
				new Thread(new PatchThread()).start();
			}
		});

		//set up the exit button
		exitButton = new JButton ("Exit");
		exitButton.setToolTipText("Exit the application");
		exitButton.addActionListener (new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.exit(0);
			}
		});

		//static objects

		fileField = new JTextField("[select a file to patch]");


		//set up the borders
		JLabel topBorder = new JLabel();
		topBorder.setPreferredSize(new Dimension(WINDOW_X, BORDER_SIZE));
		topBorder.setOpaque(true);
		JLabel bottomBorder = new JLabel();
		bottomBorder.setPreferredSize(new Dimension(WINDOW_X, BORDER_SIZE));
		bottomBorder.setOpaque(true);
		JLabel leftBorder = new JLabel();
		leftBorder.setPreferredSize(new Dimension(BORDER_SIZE, WINDOW_Y));
		leftBorder.setOpaque(true);
		JLabel rightBorder = new JLabel();
		rightBorder.setPreferredSize(new Dimension(BORDER_SIZE, WINDOW_Y));
		rightBorder.setOpaque(true);
		JLabel middleSpacer = new JLabel ();
		middleSpacer.setPreferredSize(new Dimension(WINDOW_X, BORDER_SIZE));
		middleSpacer.setOpaque(true);

		//panels    	
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		((GridBagLayout)mainPanel.getLayout()).columnWidths = new int[] {451, 0};
		((GridBagLayout)mainPanel.getLayout()).rowHeights = new int[] {131, 120, 90, 0};
		((GridBagLayout)mainPanel.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
		((GridBagLayout)mainPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
		mainPanel.add(new ImagePanel(LOGO_PATH), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
		mainPanel.add(new InfoPanel(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
		mainPanel.add(new JScrollPane(statusTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		//add components to frame
		add (mainPanel, BorderLayout.CENTER);
		add (topBorder, BorderLayout.NORTH);
		add (rightBorder, BorderLayout.EAST);
		add (bottomBorder, BorderLayout.SOUTH);
		add (leftBorder, BorderLayout.WEST);

		//centre the program
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screen.width-WINDOW_X)/2,(screen.height-WINDOW_Y)/2);

		//display
		pack();
		setVisible(true);
	}

	//Displays all the information and buttons
	private class InfoPanel extends JPanel {

		private static final long serialVersionUID = -6957271624928280807L;

		public InfoPanel() {
			//set up the panel
			setLayout(new GridBagLayout());
			((GridBagLayout)getLayout()).columnWidths = new int[] {71, 228, 75, 69, 0};
			((GridBagLayout)getLayout()).rowHeights = new int[] {30, 30, 30, 25, 0};
			((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

			//application label
			JLabel appLabel = new JLabel("Application:", JLabel.RIGHT);
			add(appLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

			//application field
			JTextField appField = new JTextField (APP_NAME);
			appField.setEditable(false);
			add(appField, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

			//filename label
			JLabel filenameLabel = new JLabel("Filename:", JLabel.RIGHT);
			add(filenameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

			//filename field
			JTextField filenameField = new JTextField (arrToString(FILENAMES));
			filenameField.setEditable(false);
			add(filenameField, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

			//website label
			JLabel webLabel = new JLabel ("Website:", JLabel.RIGHT);
			add(webLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

			//website field
			JTextField webField = new JTextField (WEBSITE);
			webField.setEditable(false);
			add(webField, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

			//selected file field
			add(fileField, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

			//file select button
			add(fileButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

			//patch file
			add(patchButton, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
	}

	//Displays the 450*125 logo
	private class ImagePanel extends JPanel{

		private static final long serialVersionUID = 6021926246303488884L;
		private Image image;

		public ImagePanel(String path) {
			try{
				Toolkit tk = Toolkit.getDefaultToolkit();
				URL url = getClass().getResource(path);
				Image img = tk.createImage(url);
				tk.prepareImage(img, -1, -1, null);
				image = img;
			}
			catch (Exception e) {
				//not found, generate replacement
				image = new BufferedImage (450, 125, BufferedImage.TYPE_INT_RGB);
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

	}

	//Patches the selected file.
	//Is threaded so status messages get posed in realtime
	private class PatchThread implements Runnable{
		@Override
		public void run() {
			if (fileToPatch == null){
				JOptionPane.showMessageDialog(null, "You must select a file to patch before patching", "Select File to Patch", JOptionPane.ERROR_MESSAGE);
			}
			else{
				status("\nUsing file " + fileToPatch.getAbsolutePath() + "\nPreparing to gather patch data");
				String md5 = getMD5 (fileToPatch);
				if (md5 != null){
					patchData = getPatchData(md5);
					if (patchData != null){
						status("Done!\nPatch data loaded. Ready to start patching");
						if (!patchFile(patchData, fileToPatch)){
							JOptionPane.showMessageDialog(null, "An error occured, check the log for details", "Error Occured", JOptionPane.ERROR_MESSAGE);
						}
						else{
							JOptionPane.showMessageDialog(null, "Patching complete! Check the log for details", "Patching Complete!", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		}	
	}

	//converts an array for strings to one string separated by "/"
	private String arrToString (String[] a){
		String s = a[0];
		for (int i = 1 ; i < a.length ; i++){
			s += "/" + a[i];
		}
		return s;
	}

	private void status (String s){
		statusTextArea.append(s);
		statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
	}

	//chooses a file to patch
	//search known locations
	//manually select
	private File chooseFile(){
		ArrayList<File> found = new ArrayList<File>(2);
		File f;
		//search known locations
		status ("\nDoing preliminary scan in known locations....");
		for (int i = 0 ; i < FILEPATHS.length ; i++){
			for (int j = 0 ; j < FILENAMES.length ; j++){
				f = new File (FILEPATHS[i], FILENAMES[j]);
				if (f.exists()){
					found.add(f);
					status ("\nFile '" + f.getAbsolutePath() + "' found.");
				}
			}
		}
		if (found.size() > 0){
			Object[] choices = new Object[found.size()+1];
			for (int i = 0 ; i < found.size() ; i++){
				choices[i] = found.get(i);
			}
			choices[choices.length-1] = "Manually select file";
			Object response = JOptionPane.showInputDialog(this, "Choose the " + APP_NAME + " version to patch.", "Choose Version", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]); 
			if (response != null){
				if (response instanceof File){
					status("\nFile selected for patching\n");
					return (File) response;
				}
			}
			else{
				status("\nFile choice aborted by user\n");
				return null;
			}
		}

		//manual select
		status("\nMoving on to manual file select");
		JFileChooser chooser = new JFileChooser(CUSTOM_SEARCH_PATH);
		chooser.setDialogTitle("Choose the " + FILENAMES[0] + " file"); 
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(new CustomFileFilter(EXT_FILTER));
		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			status("\nFile selected for patching");
			return chooser.getSelectedFile();
		}
		status("\nFile choice aborted by user\n");
		return null;
	}

	//computes the MD5 checksum of a file
	private String getMD5 (File f) {
		try{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			InputStream is;
			try {
				is = new FileInputStream(f);
			} catch (FileNotFoundException e1) {
				return null;
			}				
			byte[] buffer = new byte[8192];
			int read = 0;
			try {
				while((read = is.read(buffer)) > 0) {
					digest.update(buffer, 0, read);
				}		
				byte[] md5sum = digest.digest();
				BigInteger bigInt = new BigInteger(1, md5sum);
				String output = bigInt.toString(16);
				//fix leading 0 problem, there should be 32 chars
				for (int i = 0 ; i < 32 - output.length() ; i++){
					output = "0" + output;
				}
				return output;
			}
			catch(IOException e) {
				status("\nError: Unable to process file for MD5 hashing\n" + e.getMessage());
				return null;
			}
			finally {
				try {
					is.close();
				}
				catch(IOException e) {
					status("\nError: Unable to close file for MD5 calculation\n" + e.getMessage());
					return null;
				}
			}
		}
		catch (NoSuchAlgorithmException e){
			status("\nError: Could not find the MD5 algorithm\n" + e.getMessage());
			return null;
		}
	}

	//opens the file and patches it
	private boolean patchFile (PatchData patch, File f){
		try{
			File backupFile = new File (f.getAbsolutePath() + ".BAK");
			status("\nReading in file data......");
			//read in data
			byte[] buffer = new byte[(int) f.length()];
			FileInputStream in = new FileInputStream(f);
			in.read(buffer);
			in.close();
			status("Done!\nCreating backup '" + backupFile.getName() + "'.....");
			//backup
			if (!f.renameTo(backupFile)){
				//can't rename, permissions error.
				status("Failed\nBackup creation failed, aborting.");
				return false;
			}
			status("Done!\nBackup created\nPatching data.....");
			//patch data
			ArrayList<OffsetData> offsets = patch.getOffsetData();
			for (int i=0 ; i < offsets.size() ; i++){
				byte[] patchData = offsets.get(i).getData();
				for (int j = 0 ; j < offsets.get(i).getLength() ; j++){
					buffer[offsets.get(i).getStart() + j] = patchData[j];
				}
			}
			//write data
			status("Done!\nWriting file.....");
			FileOutputStream out = new FileOutputStream(f);
			out.write(buffer);
			out.close();
			status("Done!\nChecking patched file for errors.....");
			//check patching was successful
			if (getMD5(f).equals(patch.getMD5after())){
				status("Done!\nPatching was sucessfull!\n");
				return true;
			}
			else{
				status("Error found\nError: Something went wrong, restoring the backup.....");
				if (!f.delete() || !backupFile.renameTo(f)){
					status ("Failed\nBackup could not be restored\nTo manually restore:\nRemove the '.BAK' from " + backupFile.getAbsolutePath());
				}
				else{
					status ("Done!\nBackup restored!\n");
				}
				return false;
			}

		}
		catch (FileNotFoundException e){
			status("Failed\nError: File to be patched could not be found.");
			return false;
		}
		catch (IOException e){
			status("Failed\nError: File to be patched could not be read/written to.");
			return false;
		}

	}

	//Grabs the data from the online database and returns the
	//portion that is applicable to the MD5 checksum passed to it
	//Based on the MD5 hash database downloaded from the QueensDC website return the patch data
	//Format in database is: origMD5|patchedMD5|offset|data|offset|data... with a | at the end of the last data
	//offsets are in a decimal format
	//data is a string representation of the hex values to patch with no spaces (ex. 717565656E736463 = queensdc)
	private PatchData getPatchData(String md5){
		String nextLine;
		URL url = null;
		URLConnection urlConn = null;
		InputStreamReader  inStream = null;
		BufferedReader buff = null;
		try{
			// Create the URL object that points at the data file
			status("\nConnecting to online database.....");
			url  = new URL(PATCH_DATABASE);
			urlConn = url.openConnection();
			inStream = new InputStreamReader(urlConn.getInputStream());
			buff = new BufferedReader(inStream);

			status("Connected!\nSearching database.....");
			// Read lines from the file
			while (true){
				nextLine = buff.readLine();  
				if (nextLine !=null){
					if (!(nextLine.length() == 0 || nextLine.substring(0,2).equals("--"))){ //'--' denotes a comment line
						if (nextLine.substring(0, nextLine.indexOf("|")).equals(md5)){
							status("\nEntry for selected file found");
							//md5's match
							try{
								status("\nDownloading patch data.....");
								return new PatchData(nextLine);
							}
							catch (Exception e){
								status("\nError: Online Database is corrupt" + ((CONTACTS != null) ? "\nPlease contact " + arrToString(CONTACTS) + " ASAP with details." : ""));
								return null;
							}
						}
						String temp = nextLine.substring(nextLine.indexOf("|")+1);
						if (temp.substring(0, temp.indexOf("|")).equals(md5)){
							//file is already patched
							status("\nEntry for selected file found\nFile has already been patched\n");
							return null;
						}
					}
				}
				else{
					break;
				}
			}
			//invalid file
			status("\nError: Selected file is not in the database." + ((CONTACTS != null) ? "\nIf you are sure that you are trying to patch a valid version\nof " + APP_NAME + ", please contact " + arrToString(CONTACTS) + "." : ""));
			return null;
		}
		catch(MalformedURLException e){
			status("Failed\nError: Please check the URL: " + e.getMessage());
			return null;
		}
		catch(IOException e){
			status("Failed\nError: Can't read online database file: "+ e.getClass().getName() + ".\nMake sure you are connected to the internet.");
			return null;
		}
	}

	public static void main(String[] args) {
		new GenericPatcher();
	}
}
