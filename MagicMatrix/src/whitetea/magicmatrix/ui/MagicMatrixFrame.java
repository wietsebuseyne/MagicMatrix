package whitetea.magicmatrix.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import whitetea.magicmatrix.model.Frame;
import whitetea.magicmatrix.model.MagicMatrix;
import whitetea.magicmatrix.model.animation.Animation;
import whitetea.magicmatrix.model.animation.AnimationType;
import whitetea.magicmatrix.model.animation.CustomFramesAnimation;

import com.bric.plaf.SimpleColorPaletteUI;
import com.bric.swing.ColorPalette;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MagicMatrixFrame extends JFrame {

	private static final long serialVersionUID = -506094509602640695L;
	private JPanel contentPane;
	private JMenu mnLoad;
	private FramePanel framePanel;
	private MagicMatrix model;
	private static final int width = 8, height = 8;
	private static String fileName = "output.png";

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
					try {
						fileName = file.getName();
						BufferedImage image = ImageIO.read(file);
						model.removeAllFrames();
						int imgHeight = image.getHeight(),
								imgWidth = image.getWidth();
						for(int r = 0; r < imgHeight; r += height) {
							for(int c = 0; c < imgWidth; c += width) {
								Frame frame = new Frame(height, width);
								for (int row = 0; row < height; row++) {
									for (int col = 0; col < height; col++) {
										frame.setPixelColor(row, col, new Color(image.getRGB(col+c, row+r)));
									}
								}
								model.addFrame(frame);
							}
						}
						model.removeFrame(0);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					
				}
			});
			mnLoad.add(mntmLoad);
		}
		mnLoad.addSeparator();
		mnLoad.add(new JMenuItem("Other"));
	}

	/**
	 * Create the frame.
	 */
	public MagicMatrixFrame() {
		model = new MagicMatrix(height, width);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1257, 648);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
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
				System.out.println("test");
				try {
				    BufferedImage bi = model.getImage();
				    File outputfile = new File(fileName);
				    ImageIO.write(bi, "png", outputfile);
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as");
		mnFile.add(mntmSaveAs);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{510, 167, 0};
		gbl_contentPane.rowHeights = new int[]{510, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		framePanel = new FramePanel(model);
		GridBagConstraints gbc_framePanel = new GridBagConstraints();
		gbc_framePanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_framePanel.insets = new Insets(0, 0, 5, 5);
		gbc_framePanel.gridx = 0;
		gbc_framePanel.gridy = 0;
		contentPane.add(framePanel, gbc_framePanel);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 0;
		contentPane.add(panel_1, gbc_panel_1);
		
		ColorPalette colorPalette = new ColorPalette();
		panel_1.add(colorPalette);
		colorPalette.setUI(new SimpleColorPaletteUI());
		colorPalette.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				model.setCurrentColor(colorPalette.getColor());
			}
		});
		colorPalette.setColor(Color.RED);
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new GridLayout(1, 0, 0, 0));
		
		JLabel lblAnimation = new JLabel("Mode:");
		panel_2.add(lblAnimation);
		
		JComboBox<AnimationType> comboBox = new JComboBox<AnimationType>();
		comboBox.setModel(new DefaultComboBoxModel<AnimationType>(AnimationType.values()));
		comboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Animation animation = ((AnimationType)comboBox.getSelectedItem()).getAnimation();
				if(animation == null)
					model.stopAnimation();
				else if(animation instanceof CustomFramesAnimation) { //TODO remove dirty fix for custom frames
					((CustomFramesAnimation)animation).setFrames(model.getFrames());
					model.startAnimation(animation);
				} else
					model.startAnimation(animation);
			}
		});
		panel_2.add(comboBox);
		
		JLabel lblSpeed = new JLabel("Interval:");
		panel_1.add(lblSpeed);
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(model.getAnimationSpeed(), new Long(1), null, new Long(50)));
		spinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				model.setAnimationSpeed((Long)spinner.getValue());
			}
		});
		panel_1.add(spinner);

		FramePicker framePicker = new FramePicker(model);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		scrollPane.setPreferredSize(new Dimension((int)scrollPane.getPreferredSize().getWidth(), (int)(framePicker.getPreferredSize().getHeight() + scrollPane.getHorizontalScrollBar().getPreferredSize().getHeight())));
		contentPane.add(scrollPane, gbc_scrollPane);
		
		scrollPane.setViewportView(framePicker);
		//scrollPane.setPreferredSize(framePicker.getPreferredSize());
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		contentPane.add(panel, gbc_panel);
		
		JButton btnNew = new JButton("Add");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addFrame();
			}
		});
		panel.add(btnNew);
		
		JButton btnRemove = new JButton("Delete");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.removeFrame();
			}
		});
		panel.add(btnRemove);
		
		JButton btnMoveleft = new JButton("\u2190");
		btnMoveleft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveFrameLeft();
			}
		});
		panel.add(btnMoveleft);
		
		JButton btnMoveright = new JButton("\u2192");
		btnMoveright.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveFrameRight();
			}
		});
		panel.add(btnMoveright);
		
		JButton btnCopy = new JButton("Copy");
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addCopy();
			}
		});
		panel.add(btnCopy);
		
		JButton btnShiftRight = new JButton("Shift Right");
		btnShiftRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.shiftRight();
			}
		});
		panel.add(btnShiftRight);
		
		JButton btnShiftLeft = new JButton("Shift Left");
		btnShiftLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.shiftLeft();
			}
		});
		panel.add(btnShiftLeft);
		
		JButton btnShiftUp = new JButton("Shift Up");
		btnShiftUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.shiftUp();
			}
		});
		panel.add(btnShiftUp);
		
		JButton btnShiftDown = new JButton("Shift Down");
		btnShiftDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.shiftDown();
			}
		});
		panel.add(btnShiftDown);
	}

}
