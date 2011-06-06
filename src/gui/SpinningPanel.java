package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SpinningPanel extends JPanel {

	private Dimension dimension;
	private Component bottomComponent;
	private String label;
	private SpinWidget spinWidget;

	public SpinningPanel(Component bottomComponent, String label,
			Dimension dimension) {

		this.bottomComponent = bottomComponent;
		this.label = label;
		this.dimension = dimension;
		doMyLayout();

	}

	protected void doMyLayout() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		GridBagConstraints c = new GridBagConstraints();

		GradientPanel labelPanel = new GradientPanel(new Color(185, 195, 210),
				Color.WHITE);
		labelPanel.setMaximumSize(dimension);
		labelPanel.setLayout(new BorderLayout());

		spinWidget = new SpinWidget();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		labelPanel.add(spinWidget, BorderLayout.LINE_START);

		JLabel jlabel = new JLabel(label);
		jlabel.setHorizontalTextPosition(JLabel.LEADING);
//		jlabel.setVerticalTextPosition(JLabel.CENTER);
		labelPanel.add(jlabel, BorderLayout.CENTER);

		labelPanel.setBorder(BorderFactory.createLineBorder(new Color(154, 164,
				183)));
		add(labelPanel);

		add(bottomComponent);
		resetBottomVisibility();

	}// END: doMyLayout

	private void resetBottomVisibility() {
		if ((bottomComponent == null) || (spinWidget == null))
			return;
		bottomComponent.setVisible(spinWidget.isOpen());
		revalidate();
	}

	public void showBottom(boolean b) {
		spinWidget.setOpen(b);
	}

	public boolean isBottomShowing() {
		return spinWidget.isOpen();
	}

	public class SpinWidget extends JPanel {

		private int SPIN_WIDGET_HEIGHT = 15;
		private Dimension mySize = new Dimension(SPIN_WIDGET_HEIGHT,
				SPIN_WIDGET_HEIGHT);
		private boolean open;
		private final int HALF_HEIGHT = SPIN_WIDGET_HEIGHT / 2;
		private int[] openXPoints = { 1, HALF_HEIGHT, SPIN_WIDGET_HEIGHT - 1 };

		private int[] openYPoints = { HALF_HEIGHT, SPIN_WIDGET_HEIGHT - 1,
				HALF_HEIGHT };
		private int[] closedXPoints = { 1, 1, HALF_HEIGHT };
		private int[] closedYPoints = { 1, SPIN_WIDGET_HEIGHT - 1, HALF_HEIGHT };
		private Polygon openTriangle = new Polygon(openXPoints, openYPoints, 3);
		private Polygon closedTriangle = new Polygon(closedXPoints,
				closedYPoints, 3);

		public SpinWidget() {
			setOpen(false);
			addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					handleClick();
				}
			});
		}

		public void handleClick() {
			setOpen(!isOpen());
		}

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean o) {
			open = o;
			resetBottomVisibility();
		}

		public Dimension getMinimumSize() {
			return mySize;
		}

		public Dimension getPreferredSize() {
			return mySize;
		}

		public void paint(Graphics g) {

			g.setColor(new Color(100, 104, 111));

			if (isOpen()) {
				g.fillPolygon(openTriangle);
			} else {
				g.fillPolygon(closedTriangle);
			}

		}// END: paint

	}// END: SpinWidget class

}
