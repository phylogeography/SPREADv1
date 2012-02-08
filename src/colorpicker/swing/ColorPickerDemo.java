/*
 * @(#)ColorPickerDemo.java
 *
 * $Date: 2011-02-24 00:42:26 -0600 (Thu, 24 Feb 2011) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package colorpicker.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * A simple demo for the ColorPicker class.
 * 
 * @name ColorPicker
 * @title Colors: a Color Dialog
 * @release April 2007
 * @blurb This is a Photoshop-style color choosing dialog.
 *        <p>
 *        You can pull some parts of it apart for customization, but
 *        out-of-the-box it offers a great interface if you're dealing with a
 *        power user.
 *        <p>
 *        Of course: all dialogs are at least slightly evil, and they should be
 *        used with care...
 * @see <a href=
 *      "http://javagraphics.blogspot.com/2007/04/jcolorchooser-making-alternative.html"
 *      >Colors: a Color Dialog</code>
 */
public class ColorPickerDemo extends JApplet implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;

	public static BufferedImage createBlurbGraphic(Dimension preferredSize)
			throws Exception {
		
		ColorPicker picker = new ColorPicker();
		picker.setColor(new Color(0x4F63C3));
		JFrame frame = new JFrame();
		final JInternalFrame window = new JInternalFrame();
		window.getContentPane().add(picker);
		window.pack();
		frame.getContentPane().add(window);
		frame.pack();

		final BufferedImage image = new BufferedImage(window.getWidth(), window
				.getHeight(), BufferedImage.TYPE_INT_ARGB);

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				Graphics2D g = image.createGraphics();
				window.paint(g);
				g.dispose();
			}
		});

		return image;
	}

	/**
	 * This demonstrates how to customize a small <code>ColorPicker</code>
	 * component.
	 */
	public static void main(String[] args) {

		final JFrame demo = new JFrame("Demo");
		final JWindow palette = new JWindow(demo);
		final ColorPicker picker = new ColorPicker(true, false);

		final JComboBox comboBox = new JComboBox();
		final JCheckBox alphaCheckbox = new JCheckBox("Include Alpha");
		final JCheckBox hsbCheckbox = new JCheckBox("Include HSB Values");
		final JCheckBox rgbCheckbox = new JCheckBox("Include RGB Values");
		final JCheckBox modeCheckbox = new JCheckBox("Include Mode Controls",
				true);
		final JButton button = new JButton("Show Dialog");

		demo.getContentPane().setLayout(new GridBagLayout());
		palette.getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		palette.getContentPane().add(comboBox, c);
		c.gridy++;
		palette.getContentPane().add(alphaCheckbox, c);
		c.gridy++;
		palette.getContentPane().add(hsbCheckbox, c);
		c.gridy++;
		palette.getContentPane().add(rgbCheckbox, c);
		c.gridy++;
		palette.getContentPane().add(modeCheckbox, c);

		c.gridy = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		picker.setPreferredSize(new Dimension(220, 200));
		demo.getContentPane().add(picker, c);
		c.gridy++;
		c.weighty = 0;
		demo.getContentPane().add(picker.getExpertControls(), c);
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		demo.getContentPane().add(button, c);

		comboBox.addItem("Hue");
		comboBox.addItem("Saturation");
		comboBox.addItem("Brightness");
		comboBox.addItem("Red");
		comboBox.addItem("Green");
		comboBox.addItem("Blue");

		ActionListener checkboxListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				if (src == alphaCheckbox) {
					picker.setOpacityVisible(alphaCheckbox.isSelected());
				} else if (src == hsbCheckbox) {
					picker.setHSBControlsVisible(hsbCheckbox.isSelected());
				} else if (src == rgbCheckbox) {
					picker.setRGBControlsVisible(rgbCheckbox.isSelected());
				} else if (src == modeCheckbox) {
					picker.setModeControlsVisible(modeCheckbox.isSelected());
				}
				demo.pack();
			}
		};
		picker.setOpacityVisible(false);
		picker.setHSBControlsVisible(false);
		picker.setRGBControlsVisible(false);
		picker.setHexControlsVisible(false);
		picker.setPreviewSwatchVisible(false);

		picker.addPropertyChangeListener(ColorPicker.MODE_PROPERTY,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						int m = picker.getMode();
						if (m == ColorPicker.HUE) {
							comboBox.setSelectedIndex(0);
						} else if (m == ColorPicker.SAT) {
							comboBox.setSelectedIndex(1);
						} else if (m == ColorPicker.BRI) {
							comboBox.setSelectedIndex(2);
						} else if (m == ColorPicker.RED) {
							comboBox.setSelectedIndex(3);
						} else if (m == ColorPicker.GREEN) {
							comboBox.setSelectedIndex(4);
						} else if (m == ColorPicker.BLUE) {
							comboBox.setSelectedIndex(5);
						}
					}
				});

		alphaCheckbox.addActionListener(checkboxListener);
		hsbCheckbox.addActionListener(checkboxListener);
		rgbCheckbox.addActionListener(checkboxListener);
		modeCheckbox.addActionListener(checkboxListener);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = picker.getColor();
				color = ColorPicker.showDialog(demo, color, true);
				if (color != null)
					picker.setColor(color);
			}
		});

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = ((JComboBox) e.getSource()).getSelectedIndex();
				if (i == 0) {
					picker.setMode(ColorPicker.HUE);
				} else if (i == 1) {
					picker.setMode(ColorPicker.SAT);
				} else if (i == 2) {
					picker.setMode(ColorPicker.BRI);
				} else if (i == 3) {
					picker.setMode(ColorPicker.RED);
				} else if (i == 4) {
					picker.setMode(ColorPicker.GREEN);
				} else if (i == 5) {
					picker.setMode(ColorPicker.BLUE);
				}
			}
		});
		comboBox.setSelectedIndex(2);

		palette.pack();
		palette.setLocationRelativeTo(null);

		demo.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				Point p = demo.getLocation();
				palette.setLocation(new Point(p.x - palette.getWidth() - 10,
						p.y));
			}
		});
		demo.pack();
		demo.setLocationRelativeTo(null);
		demo.setVisible(true);
		palette.setVisible(true);

		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public ColorPickerDemo() {
		try {
			String lf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lf);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		ColorPicker picker = new ColorPicker();
		picker.setOpacityVisible(true);
		Random r = new Random(System.currentTimeMillis());
		picker.setColor(new Color(r.nextInt(255), r.nextInt(255), r
				.nextInt(255)));
		getContentPane().add(picker);
		picker.addPropertyChangeListener(this);

		/*
		 * If you really want to know what RGB values the mouse is over:
		 * ColorPickerPanel pickerPanel = picker.getColorPanel();
		 * pickerPanel.addMouseMotionListener(new MouseMotionAdapter() { public
		 * void mouseMoved(MouseEvent e) { ColorPickerPanel cpp =
		 * (ColorPickerPanel)e.getSource(); int[] rgb =
		 * cpp.getRGB(e.getPoint());
		 * System.out.println("indicated point: "+rgb[0
		 * ]+", "+rgb[1]+", "+rgb[2]); } });
		 */

		picker.setBackground(Color.white);
		picker.setOpaque(true);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("\"" + evt.getPropertyName() + "\" "
				+ toString(evt.getOldValue()) + "->"
				+ toString(evt.getNewValue()));
	}

	/**
	 * Because Color.toString() omits alpha information...
	 * 
	 * @param obj
	 * @return
	 */
	private static String toString(Object obj) {
		if (obj == null)
			return null;

		if (obj instanceof Color) {
			Color c = (Color) obj;
			return "Color[ r=" + c.getRed() + ", g=" + c.getGreen() + ", b="
					+ c.getBlue() + ", a=" + c.getAlpha() + "]";
		}
		return obj.toString();
	}
}
