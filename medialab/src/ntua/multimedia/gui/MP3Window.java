package ntua.multimedia.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import ntua.multimedia.code.MP3Player;
import ntua.multimedia.code.Modes;
import ntua.multimedia.code.Status;
import ntua.multimedia.code.Track;
import ntua.multimedia.code.Utils;

/**
 * The Class MP3Window.
 */
public class MP3Window implements Runnable {

	/** The frame. */
	private JFrame frame;
	
	/** The file chooser. */
	private JFileChooser fileChooser = new JFileChooser();
	
	/** The list. */
	private JList<Track> list;
	
	/** The song_title. */
	private JLabel song_title;
	
	/** The artist_name. */
	private JLabel artist_name;
	
	/** The album_name. */
	private JLabel album_name;
	
	/** The cover. */
	private JLabel cover;
	
	/** The mp3 player. */
	private MP3Player mp3Player = new MP3Player();
	
	/** The selected track. */
	private Track selectedTrack;
	
	/** The modal dialog. */
	private JDialog modalDialog;
	
	/** The lyrics area. */
	private JTextArea lyricsArea;
	
	/** The progress bar. */
	private JProgressBar progressBar;
	
	/** The timer. */
	private JLabel timer;
	
	/** The play button. */
	private JButton playButton;
	
	/** The pause button. */
	private JButton pauseButton;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MP3Window window = new MP3Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MP3Window() {
		initialize();
	}
	

	/**
	 * Open action.
	 *
	 * @param parent the parent
	 */
	private void openAction(Component parent){
		int returnVal = fileChooser.showOpenDialog(parent);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	for(File file: fileChooser.getSelectedFiles()) {
	    		if(Utils.isMP3(file))
	    			mp3Player.addToPlayList(file);
	    		else 
	    			JOptionPane.showMessageDialog(frame, "MP3 Player cannot play file "+file.getName()+". \n"
	    					+ "The Player might not support the file type or might not support the \n"
	    					+ "codec that was used to compress the file.");
	    	}
	    } 
	    
	}
	
	/**
	 * Close action.
	 */
	private void closeAction(){
		mp3Player.close(selectedTrack);
	}
	
	/**
	 * Exit action.
	 */
	private void exitAction(){
		mp3Player.stop();
		frame.dispose();
	}
	
	/**
	 * About action.
	 */
	private void aboutAction(){
        modalDialog.setVisible(true);
	}
	
	/**
	 * Play action.
	 */
	private void playAction(){
		if(selectedTrack != null){
			if(mp3Player.getStatus() != Status.PAUSED) {
				mp3Player.setStatus(Status.PLAYING);
				Thread play = new Thread(this,"play");
				play.start();
			} else {
				mp3Player.resume();
			}
			updateDetails();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(Thread.currentThread().getName().equals("progress"))
			updateProgressBar();
		else
			play();
	}
	
	/**
	 * Play.
	 */
	public void play(){
		Track nextTrack = null;
		while(mp3Player.getStatus() != Status.STOPPED) {
			list.setSelectedIndex(mp3Player.getIndex(selectedTrack));
			mp3Player.init(selectedTrack);
			updateDetails();
			Thread progress = new Thread(this,"progress");
			progress.start();
			mp3Player.play(selectedTrack);
			nextTrack = mp3Player.getNext(selectedTrack);
			if (nextTrack == null) {		
				break;
			}
			else {
				selectedTrack = nextTrack;
			}
			updateDetails();
		}
		updateDetails();
	}
	
	/**
	 * Update progress bar.
	 */
	public void updateProgressBar(){
		while(true){
			int position = mp3Player.getPosition();
			if(position < 0)
				return;
			progressBar.setValue(position);
			timer.setText(Utils.getTimeFromFrames(position, selectedTrack.getFramerate()));
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Pause action.
	 */
	private void pauseAction(){
		mp3Player.pause();
		updateDetails();
	}
	
	/**
	 * Stop action.
	 */
	private void stopAction(){
		mp3Player.stop();
		updateDetails();
	}
	
	/**
	 * Ff action.
	 */
	private void ffAction(){
		mp3Player.fastForward();
	}
	
	/**
	 * Update header.
	 */
	public void updateHeader(){
		switch(mp3Player.getStatus()){
		case NULL:
			frame.setTitle("MP3 Player");
			break;
		case PAUSED:
			frame.setTitle("MP3 Player PAUSED: " + selectedTrack.getName());
			break;
		case PLAYING:
			frame.setTitle("MP3 Player PLAYING: " + selectedTrack.getName());
			break;
		case STOPPED:
			frame.setTitle("MP3 Player STOPPED");
			break;
		}
	}
	
	
	/**
	 * Update details.
	 */
	private void updateDetails() {
		
		if(mp3Player.getStatus() == Status.PLAYING) {
			playButton.setVisible(false);
			pauseButton.setVisible(true);
		} else if(mp3Player.getStatus() == Status.PAUSED){
			playButton.setVisible(true);
			pauseButton.setVisible(false);
		} else if(mp3Player.getStatus() == Status.STOPPED){
			playButton.setVisible(true);
			pauseButton.setVisible(false);
			progressBar.setValue(0);
			timer.setText("0:00");
		} else if(selectedTrack != null){
			song_title.setText(Utils.parseValue(selectedTrack.getName()));
			artist_name.setText(Utils.parseValue(selectedTrack.getArtist()));
			album_name.setText(Utils.parseValue(selectedTrack.getAlbumName()));
			lyricsArea.setText(selectedTrack.getLyrics());
			if(selectedTrack.getCover() != null)
				cover.setIcon(new ImageIcon(selectedTrack.getCover()));
			else
				cover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("empty_cover.png")));
			progressBar.setMaximum(selectedTrack.getFrames());
			progressBar.setValue(0);
			timer.setText("0:00");
			playButton.setVisible(true);
			pauseButton.setVisible(false);
		}  else {
			song_title.setText("");
			artist_name.setText("");
			album_name.setText("");
			lyricsArea.setText("");
			cover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("empty_cover.png")));
			progressBar.setValue(0);
			timer.setText("0:00");
			playButton.setVisible(true);
			pauseButton.setVisible(false);
		}
		updateHeader();
		
	}
	
	/**
	 * Play list value changed.
	 */
	private void playListValueChanged(){
		stopAction();
		mp3Player.setStatus(Status.NULL);
		int index = list.getSelectedIndex();
		if(index >= 0)
			selectedTrack = mp3Player.getPlayList().get(index);
		else
			selectedTrack = null;
		updateDetails();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ImageIcon image = new ImageIcon(getClass().getClassLoader().getResource("window_icon.gif"));
		frame = new JFrame("MP3 Player");
		frame.setIconImage(image.getImage());
		frame.setMinimumSize(new Dimension(900,600));
		frame.setBounds(100, 100, 950, 750);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		Window topWindow = SwingUtilities.getWindowAncestor(frame);
		modalDialog = new JDialog(topWindow, "About", ModalityType.APPLICATION_MODAL);
        modalDialog.setSize(new Dimension(300, 350));
        modalDialog.setLocationRelativeTo(topWindow);
        modalDialog.setResizable(false);
        
        modalDialog.setIconImage(image.getImage());
        
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setAlignmentY(SwingConstants.CENTER);
        JLabel aboutIcon = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("MP3_File.png")));
        aboutIcon.setAlignmentY(SwingConstants.CENTER);
        aboutIcon.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogPanel.add(aboutIcon, BorderLayout.NORTH);
        JLabel headerLabel = new JLabel("MP3 Player", SwingConstants.CENTER);
        headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        
        JPanel textPanel = new JPanel();
        
        textPanel.add(headerLabel);
        JLabel version = new JLabel("------ Version: 1.0.0 ------", SwingConstants.CENTER);
        version.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        textPanel.add(version);
        
        textPanel.add(new JLabel("Semester project for Multimedia Technology", SwingConstants.CENTER));
        textPanel.add(new JLabel("Created by Valmas Nikolaos 03111116", SwingConstants.CENTER));
        textPanel.add(new JLabel("Email: nick_valmas@windowslive.com", SwingConstants.CENTER));
        
        
        dialogPanel.add(textPanel, BorderLayout.CENTER);
        
        JButton ok_btn = new JButton("OK");
        //ok_btn.setPreferredSize(new Dimension(180, 30));
        ok_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modalDialog.dispose();
				
			}
		});
        dialogPanel.add(ok_btn, BorderLayout.SOUTH);
        modalDialog.getContentPane().add(dialogPanel);
        
		
		final JMenuItem openMenu = new JMenuItem("Open");
		openMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openAction(openMenu);
			}
		});
		fileMenu.add(openMenu);
		
		JMenuItem closeMenu = new JMenuItem("Close");
		closeMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeAction();
			}
		});
		fileMenu.add(closeMenu);
		
		JMenuItem exitMenu = new JMenuItem("Exit");
		exitMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitAction();
			}
		});
		fileMenu.add(exitMenu);
		
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		JMenuItem aboutMenu = new JMenuItem("About");
		aboutMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aboutAction();
			}
		});
		helpMenu.add(aboutMenu);
		
		FileFilter filter = new FileNameExtensionFilter("MP3 File","mp3");
		fileChooser.setFileFilter(filter);
		fileChooser.setMultiSelectionEnabled(true);
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel cover_panel = new JPanel();
		cover_panel.setBorder(new EmptyBorder(10, 10, 10, 0));
		panel_2.add(cover_panel, BorderLayout.WEST);
		
		cover = new JLabel("");
		cover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("empty_cover.png")));
		//cover.setIcon(new ImageIcon(GetAlbumDetailsService.getAlbumCoverAndName(new Track("Demons", "Imagine Dragons", "Night Visions (Deluxe)"), CoverDimensions.x300)));
		cover_panel.add(cover);
		
		JPanel top_panel = new JPanel();
		top_panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel_2.add(top_panel);
		top_panel.setLayout(new BoxLayout(top_panel, BoxLayout.Y_AXIS));
		
		JPanel details_timer_panel = new JPanel();
		top_panel.add(details_timer_panel);
		
		JPanel details_panel = new JPanel();
		details_panel.setLayout(new GridLayout(3, 1, 0, 0));
		
		song_title = new JLabel("");
		details_panel.add(song_title);
		song_title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
		
		artist_name = new JLabel("");
		details_panel.add(artist_name);
		artist_name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		
		album_name = new JLabel("");
		details_panel.add(album_name);
		album_name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		details_timer_panel.setLayout(new BoxLayout(details_timer_panel, BoxLayout.X_AXIS));
		details_timer_panel.add(details_panel);
		
		JPanel panel = new JPanel();
		details_timer_panel.add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		timer = new JLabel("0:00", SwingConstants.RIGHT);
		timer.setPreferredSize(new Dimension(50, 155));
		panel.add(timer);
		timer.setVerticalAlignment(SwingConstants.BOTTOM);
		timer.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
		
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(146, 50));
		progressBar.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		top_panel.add(progressBar);
		progressBar.setForeground(SystemColor.textHighlight);
		
		JPanel button_Panel = new JPanel();
		button_Panel.setBorder(new EmptyBorder(10, 0, 0, 0));
		top_panel.add(button_Panel);
		button_Panel.setLayout(new BorderLayout());
		
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		actionPanel.setBorder(new EmptyBorder(14, 0, 0, 0));
		button_Panel.add(actionPanel,BorderLayout.WEST);
		
		playButton = new JButton("Play");
		actionPanel.add(playButton);
		playButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		
		pauseButton = new JButton("Pause");
		actionPanel.add(pauseButton);
		pauseButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		
		JButton stopButton = new JButton("Stop");
		actionPanel.add(stopButton);
		stopButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		
		JButton ffButton = new JButton("Fast Forward");
		actionPanel.add(ffButton);
		ffButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		ffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ffAction();
			}
		});
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playButton.setVisible(true);
				pauseButton.setVisible(false);
				stopAction();
			}
		});
		pauseButton.setVisible(false);
		
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pauseAction();
			}
		});
		
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playAction();
			}
		});
		
		Border empty = BorderFactory.createEmptyBorder();
		
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		button_Panel.add(rightPanel, BorderLayout.EAST);
		
		JPanel modePanel = new JPanel();
		rightPanel.add(modePanel);
		modePanel.setBorder(BorderFactory.createTitledBorder(empty, "Modes", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
		modePanel.setLayout(new GridLayout(2, 2, 5, 0));
		
		ButtonGroup btnGroup = new ButtonGroup();
		
		final JToggleButton normal = new JToggleButton("Normal");
		normal.setPreferredSize(new Dimension(100, 20));
		normal.setSelected(true);
		modePanel.add(normal);
		btnGroup.add(normal);
		
		normal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mp3Player.setMode(Modes.NORMAL);
				normal.setSelected(true);
			}
		});
		
		final JToggleButton repeatOne = new JToggleButton("Repeat One");
		repeatOne.setPreferredSize(new Dimension(100, 20));
		modePanel.add(repeatOne);
		btnGroup.add(repeatOne);
		
		repeatOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mp3Player.setMode(Modes.REPEAT_ONE);
				repeatOne.setSelected(true);
			}
		});
		
		final JToggleButton repeatAll = new JToggleButton("Repeat All");
		repeatAll.setPreferredSize(new Dimension(100, 20));
		modePanel.add(repeatAll);
		btnGroup.add(repeatAll);
		
		repeatAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mp3Player.setMode(Modes.REPEAT_ALL);
				repeatAll.setSelected(true);
			}
		});
		
		final JToggleButton random = new JToggleButton("Random");
		random.setPreferredSize(new Dimension(100, 20));
		modePanel.add(random);
		btnGroup.add(random);
		
		random.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mp3Player.setMode(Modes.RANDOM);
				random.setSelected(true);
			}
		});
		
		JPanel bottom_panel = new JPanel();
		frame.getContentPane().add(bottom_panel, BorderLayout.CENTER);
		bottom_panel.setBorder(new EmptyBorder(0, 10, 10, 10));
		//bottom_panel.setLayout(new BoxLayout(bottom_panel, BoxLayout.X_AXIS));
		bottom_panel.setLayout(new BorderLayout(10,10));
		
		list = new JList<Track>(mp3Player.getPlayList());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				playListValueChanged();
			}
		});
		
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		lyricsArea = new JTextArea();
		lyricsArea.setEditable(false);
		JScrollPane lyricsScroller = new JScrollPane(lyricsArea);
		lyricsScroller.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				listScroller, lyricsScroller);
		
		bottom_panel.add(splitPane, BorderLayout.CENTER);
		
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);

		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(300, 0);
		listScroller.setMinimumSize(minimumSize);
		
		minimumSize = new Dimension(0, 0);
		lyricsScroller.setMinimumSize(minimumSize);
	}

}
