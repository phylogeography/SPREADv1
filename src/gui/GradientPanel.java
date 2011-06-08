package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GradientPanel extends JPanel {

	public final static int HORIZONTAL = 0;
	public final static int VERTICAL = 1;
	
	private Color startColor;
	private Color endColor;
	private int direction;

	public GradientPanel(Color startColor, Color endColor, int direction) {
		super();
		this.startColor = startColor;
		this.endColor = endColor;
		this.direction = direction;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		int panelHeight = this.getHeight();
		int panelWidth = this.getWidth();

		GradientPaint gradientPaint = null;
		switch (direction) {

		case HORIZONTAL:
			gradientPaint = new GradientPaint(0, panelHeight / 2, startColor,
					panelWidth, panelHeight / 2, endColor);
			break;

		case VERTICAL:
			gradientPaint = new GradientPaint(panelWidth / 2, 0, endColor,
					panelWidth / 2, panelHeight, startColor);
			break;
		}

		if (g instanceof Graphics2D) {
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setPaint(gradientPaint);
			graphics2D.fillRect(0, 0, panelWidth, panelHeight);
		}

	}// END: paintComponent
}
