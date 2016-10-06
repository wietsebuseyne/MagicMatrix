package whitetea.magicmatrix.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jssc.SerialPortException;
import whitetea.magicmatrix.communication.packet.Mode;
import whitetea.magicmatrix.communication.packet.SetModePacket;
import whitetea.magicmatrix.model.Frame;
import whitetea.magicmatrix.model.MagicMatrix;
import whitetea.magicmatrix.model.animation.Animation;
import whitetea.magicmatrix.model.animation.AnimationType;
import whitetea.magicmatrix.model.animation.CustomFramesAnimation;
import whitetea.magicmatrix.model.observer.Observer;

import com.bric.plaf.SimpleColorPaletteUI;
import com.bric.swing.ColorPalette;

public class MagicMatrixFrame extends JFrame implements Observer {

	private static final long serialVersionUID = -506094509602640695L;
	private JPanel contentPane;
	private JTextField txtCurrentColor;
	private JMenu mnLoad;
	private FramePanel framePanel;
	private MagicMatrix model;
	private static final int WIDTH = 8, HEIGHT = 8;
	private String fileName = null;
	private List<JComponent> alteringComponents = new ArrayList<>();
	private boolean saved = true, selectingColor = false;
    private Robot robot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MagicMatrixFrame frame = new MagicMatrixFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	//TODO new thread for performance ==> add frame one by one
	private void refreshLoadableImages() {
		mnLoad.removeAll();
		//TODO enkel files met juist afmetingen
    	File[] pngFiles = new File(System.getProperty("user.dir")).listFiles(new FilenameFilter() { 
	         public boolean accept(File dir, String filename)
	              { return filename.toLowerCase().endsWith(".png"); }
		} );
		if(pngFiles.length == 0)
			mnLoad.add(new JMenuItem("No png files in current directory"));
		//TODO performance improvement int ipv color http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
		for(int i = 0; i < pngFiles.length; i++) {
			final File file = pngFiles[i];
			JMenuItem mntmLoad = new JMenuItem(file.getName());
			mntmLoad.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(discardChanges()) {
						try {
							setFileName(file.getName());
							BufferedImage image = ImageIO.read(file);
							model.removeAllFrames();
							int imgHeight = image.getHeight(),
									imgWidth = image.getWidth();
							List<Frame> frames = new ArrayList<>();
							for(int r = 0; r < imgHeight; r += HEIGHT) {
								for(int c = 0; c < imgWidth; c += WIDTH) {
									Frame frame = new Frame(HEIGHT, WIDTH);
									for (int row = 0; row < HEIGHT; row++) {
										for (int col = 0; col < HEIGHT; col++) {
											frame.setPixelColor(row, col, new Color(image.getRGB(col+c, row+r)));
										}
									}
									frames.add(frame);
								}
							}
							model.addFrames(frames);
							model.removeFrame(0);
							saved = true;
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}					
				}
			});
			mnLoad.add(mntmLoad);
		}
		//TODO mnLoad.addSeparator();
		//TODO mnLoad.add(new JMenuItem("Other"));
	}
	
	private void disableAlteringComponents() {
		for(JComponent c : alteringComponents)
			c.setEnabled(false);
	}
	
	private void enableAlteringComponents() {
		for(JComponent c : alteringComponents)
			c.setEnabled(true);
	}
	
	private boolean discardChanges() {
		if(!isSaved()) {
			Object[] options = {"Yes", "No"};
			if (JOptionPane
					.showOptionDialog(null, "The changes to the file are not saved."
							+ "\nAre you sure you want to continue and discard the changes?",
					"Unsaved changes",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1])
					== JOptionPane.NO_OPTION)
				return false;
		}
		return true;
	}

	/**
	 * Create the frame.
	 */
	public MagicMatrixFrame() {
		setTitle("MagicMatrix\r\n");
		model = new MagicMatrix(HEIGHT, WIDTH);
		model.addObserver(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(792, 662);
		setMinimumSize(new Dimension(600, 475));
		setLocationRelativeTo(null);
		
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(discardChanges()) {
					setFileName(null);
				    model.removeAllFrames();
				}
			}
		});
		mnFile.add(mntmNew);
		
		mnLoad = new JMenu("Load");
		mnLoad.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				refreshLoadableImages();
			}
		});
		refreshLoadableImages();
		mnFile.add(mnLoad);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveToFile();
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as");
		mntmSaveAs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = "";
				while(newName != null && (newName.isEmpty() || !isValidPath(newName))) {
					newName = JOptionPane.showInputDialog("Please enter a valid filename");
				}
				saveToFile(newName);
			}
		});
		mnFile.add(mntmSaveAs);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {500, 250};
		gbl_contentPane.rowHeights = new int[] {500, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
		
		framePanel = new FramePanel(model);
		GridBagConstraints gbc_framePanel = new GridBagConstraints();
		gbc_framePanel.weighty = 1.0;
		gbc_framePanel.weightx = 1.0;
		gbc_framePanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_framePanel.insets = new Insets(0, 0, 5, 5);
		gbc_framePanel.gridx = 0;
		gbc_framePanel.gridy = 0;
		contentPane.add(framePanel, gbc_framePanel);
		
		JPanel pnlConfigurations = new JPanel();
		GridBagConstraints gbc_pnlConfigurations = new GridBagConstraints();
		gbc_pnlConfigurations.fill = GridBagConstraints.BOTH;
		gbc_pnlConfigurations.weighty = 1;
		gbc_pnlConfigurations.gridx = 1;
		gbc_pnlConfigurations.gridy = 0;
		contentPane.add(pnlConfigurations, gbc_pnlConfigurations);
		
		JPanel pnlControls = new JPanel();
		GridLayout grid = new GridLayout(8, 2, 0, 0);
		grid.setHgap(5);
		grid.setVgap(5);
		pnlControls.setLayout(grid);
		
		JButton btnNew = new JButton("Add");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addFrame();
			}
		});
		
		JLabel lblCurrentColor = new JLabel("Current Color:");
		pnlControls.add(lblCurrentColor);
		
		txtCurrentColor = new JTextField();
		txtCurrentColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					model.setCurrentColor(Color.decode("#" + txtCurrentColor.getText()));
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
				}
			}
		});
		pnlControls.add(txtCurrentColor);
		
		JLabel lblAnimation = new JLabel("Mode:");
		pnlControls.add(lblAnimation);
		
		JComboBox<AnimationType> cmbMode = new JComboBox<AnimationType>();
		pnlControls.add(cmbMode);
		cmbMode.setModel(new DefaultComboBoxModel<AnimationType>(AnimationType.values()));
		cmbMode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Animation animation = ((AnimationType)cmbMode.getSelectedItem()).getAnimation();
				model.stopAnimation();
				if((AnimationType)cmbMode.getSelectedItem() == AnimationType.PLASMA) {
					try {
						model.getColorduinoCommunicator().sendPacket(new SetModePacket(Mode.PLASMA));
					} catch (SerialPortException | IllegalStateException ex) {}
				}
				if(animation == null) {
					model.stopAnimation();
					enableAlteringComponents();
				} else if(animation instanceof CustomFramesAnimation) { //TODO remove dirty fix for custom frames
					disableAlteringComponents();
					((CustomFramesAnimation)animation).setFrames(model.getFrames());
					((CustomFramesAnimation)animation).setStartFrame(model.getCurrentFrameIndex());
					model.startAnimation(animation);
				} else {
					disableAlteringComponents();
					model.startAnimation(animation);
				}
			}
		});
		
		JLabel lblSpeed = new JLabel("Interval:");
		pnlControls.add(lblSpeed);
		
		JSpinner spinner = new JSpinner();
		pnlControls.add(spinner);
		spinner.setModel(new SpinnerNumberModel(model.getAnimationSpeed(), new Long(1), null, new Long(50)));
		spinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				model.setAnimationSpeed((Long)spinner.getValue());
			}
		});
		pnlControls.add(btnNew);
		
		JButton btnRemove = new JButton("Delete");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.removeFrame();
			}
		});
		pnlControls.add(btnRemove);
		
		JButton btnMoveleft = new JButton("\u2190");
		btnMoveleft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveFrameLeft();
			}
		});
		
		JButton btnCopy = new JButton("Copy");
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addCopy();
			}
		});
		pnlControls.add(btnCopy);
		
		JPanel panel = new JPanel();
		pnlControls.add(panel);
		pnlControls.add(btnMoveleft);
		
		JButton btnMoveright = new JButton("\u2192");
		btnMoveright.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveFrameRight();
			}
		});
		pnlControls.add(btnMoveright);
		
		JButton btnShiftLeft = new JButton("Shift Left");
		btnShiftLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.shiftLeft();
			}
		});
		pnlControls.add(btnShiftLeft);
		
		JButton btnShiftRight = new JButton("Shift Right");
		btnShiftRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.shiftRight();
			}
		});
		pnlControls.add(btnShiftRight);
		
		JButton btnShiftUp = new JButton("Shift Up");
		btnShiftUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.shiftUp();
			}
		});
		pnlControls.add(btnShiftUp);
		
		JButton btnShiftDown = new JButton("Shift Down");
		btnShiftDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.shiftDown();
			}
		});
		pnlControls.add(btnShiftDown);
		pnlConfigurations.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		alteringComponents.add(btnCopy);
		alteringComponents.add(btnMoveleft);
		alteringComponents.add(btnMoveright);
		alteringComponents.add(btnNew);
		alteringComponents.add(btnRemove);
		alteringComponents.add(btnShiftDown);
		alteringComponents.add(btnShiftLeft);
		alteringComponents.add(btnShiftRight);
		alteringComponents.add(btnShiftUp);
		//TODO disable frame itself
		
		ColorPalette colorPalette = new ColorPalette();
		colorPalette.setUI(new SimpleColorPaletteUI());
		colorPalette.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				setColor(colorPalette.getColor());
			}
		});
		colorPalette.setColor(Color.RED);
		pnlConfigurations.setPreferredSize(colorPalette.getPreferredSize());
		pnlConfigurations.add(colorPalette);
		pnlConfigurations.add(pnlControls);

		FramePicker framePicker = new FramePicker(model);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		//scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)(framePicker.getPreferredSize().getHeight() + scrollPane.getHorizontalScrollBar().getPreferredSize().getHeight()+200)));
		contentPane.add(scrollPane, gbc_scrollPane);
		
		scrollPane.setViewportView(framePicker);
	}
	
	public boolean isSaved() {
		return fileName != null && saved;
	}
	
	public void saveToFile() {
		if(fileName == null)
			fileName = "";
		while(fileName != null && (fileName.isEmpty() || !isValidPath(fileName))) {
			fileName = JOptionPane.showInputDialog("Please enter a valid filename");
		}
		saveToFile(fileName);
	}
	
	public void saveToFile(String fileName) {
		try {
			if(fileName != null) {
				if(fileName.length() < 4 || !fileName.substring(fileName.length()-4).equals(".png"))
					fileName += ".png";
				setFileName(fileName);
			    BufferedImage bi = model.getImage();
			    File outputfile = new File(fileName);
			    if(ImageIO.write(bi, "png", outputfile))
			    	saved = true;
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
		if(fileName == null)
			setTitle("MagicMatrix");
		else
			setTitle("MagicMatrix - " + fileName);
	}

	@Override
	public void update(MagicMatrix updatedModel) {
		saved = false;
		Color c = updatedModel.getCurrentColor();
		txtCurrentColor.setBackground(c);
		txtCurrentColor.setText(String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
		txtCurrentColor.setForeground((perceivedBrightness(c) > 130 ? Color.BLACK : Color.WHITE));

	}
	
	private int perceivedBrightness(Color c) {
	    return (int)Math.sqrt(
	    c.getRed() * c.getRed() * .299 +
	    c.getGreen() * c.getGreen() * .587 +
	    c.getBlue() * c.getBlue() * .114);
	}
	
    public boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException |  NullPointerException ex) {
            return false;
        }
        return true;
    }
    
    public void setColor(Color c) {
		model.setCurrentColor(c);
    }
    /*
 // update the selected color on mouse press, dragged, and release
    public void mousePressed(MouseEvent evt) {
    	Point p = evt.getLocationOnScreen();
    	setSelectedColor(robot.getPixelColor(p.x, p.y));
    }
    public void mouseDragged(MouseEvent evt) {
    	Point p = evt.getLocationOnScreen();
    	setSelectedColor(robot.getPixelColor(p.x, p.y));
    }
    // for released we want to hide the frame as well
    public void mouseReleased(MouseEvent evt) {
    	Point p = evt.getLocationOnScreen();
    	setSelectedColor(robot.getPixelColor(p.x, p.y));
    }
    // update both the display label and the component that was passed in
    public void setSelectedColor(Color color) {
    	model.setCurrentColor(color);
    	btnCurrentColor.setBackground(color);
    }

    // no-ops for the rest of the mouse-event listener
    public void mouseClicked(MouseEvent evt) { }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }
    public void mouseMoved(MouseEvent evt) { }*/

}
