package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
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
	private Color gradientPanelStartColor;
	private Color gradientPanelEndColor;
	private Color gradientPanelBorderColor;
	private Color SpinWidgetColor;

	public SpinningPanel(Component bottomComponent, String label,
			Dimension dimension) {

		this.bottomComponent = bottomComponent;
		this.label = label;
		this.dimension = dimension;

		gradientPanelStartColor = new Color(182, 192, 207);
		gradientPanelEndColor = new Color(202, 209, 220);
		gradientPanelBorderColor = new Color(154, 164, 183);
		SpinWidgetColor = new Color(100, 104, 111);

		doMyLayout();

	}

	protected void doMyLayout() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		GradientPanel labelPanel = new GradientPanel(gradientPanelStartColor,
				gradientPanelEndColor);
		labelPanel.setMaximumSize(dimension);
		labelPanel.setLayout(new BorderLayout());

		spinWidget = new SpinWidget();
		labelPanel.add(spinWidget, BorderLayout.LINE_START);

		JLabel jlabel = new JLabel(label);
		labelPanel.add(jlabel, BorderLayout.CENTER);

		labelPanel.setBorder(BorderFactory
				.createLineBorder(gradientPanelBorderColor));
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

		private final int SPIN_WIDGET_HEIGHT = 15;
		private final int HALF_HEIGHT = SPIN_WIDGET_HEIGHT / 2;
		private Dimension mySize = new Dimension(SPIN_WIDGET_HEIGHT,
				SPIN_WIDGET_HEIGHT);
		private boolean open;

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

			g.setColor(SpinWidgetColor);

			if (isOpen()) {
				g.fillPolygon(openTriangle);
			} else {
				g.fillPolygon(closedTriangle);
			}

		}// END: paint

	}// END: SpinWidget class

}
