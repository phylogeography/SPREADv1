package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GradientPanel extends JPanel {

	private Color startColor;
	private Color endColor;

	private enum directionEnum {
		HORIZONTAL, VERTICAL
	}

	private directionEnum directionSwitcher;

	public GradientPanel(Color startColor, Color endColor) {
		super();
		this.startColor = startColor;
		this.endColor = endColor;
		directionSwitcher = directionEnum.VERTICAL;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		int panelHeight = this.getHeight();
		int panelWidth = this.getWidth();

		GradientPaint gradientPaint = null;
		switch (directionSwitcher) {

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
