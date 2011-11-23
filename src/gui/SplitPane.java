package gui;

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
class SplitPane extends JSplitPane {

	SplitPane() {
		super();
	}

	SplitPane(int newOrientation) {
		super(newOrientation);
	}

	SplitPane(int newOrientation, boolean newContinuousLayout) {
		super(newOrientation, newContinuousLayout);
	}

	SplitPane(int newOrientation, boolean newContinuousLayout,
			Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newContinuousLayout, newLeftComponent,
				newRightComponent);
	}

	SplitPane(int newOrientation, Component newLeftComponent,
			Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
	}

	/**
	 * Override this method to prevent setting a location that violates the
	 * maximum size of either component in the splitter, if setMaximumSize() has
	 * been called.
	 */
	public void setDividerLocation(int requested) {

		int currentLoc = getDividerLocation();

		if (currentLoc == requested) {
			super.setDividerLocation(requested);
			return;
		}

		boolean growing = requested > currentLoc;

		Component maxComp = growing ? getLeftComponent() : getRightComponent();
		if (maxComp == null) {
			super.setDividerLocation(requested);
			return;
		}

		Dimension maxDim = maxComp.getMaximumSize();
		if (maxDim == null) {
			super.setDividerLocation(requested);
			return;
		}

		int maxCompSize = getSizeForPrimaryAxis(maxDim);

		if (growing) {

			if (requested > maxCompSize) {
				super.setDividerLocation(maxCompSize);
				return;
			}

		} else {

			int totalSize = getSizeForPrimaryAxis(getSize());
			int minPos = totalSize - maxCompSize - getDividerSize();
			if (requested < minPos) {
				super.setDividerLocation(minPos);
				return;
			}
		}

		super.setDividerLocation(requested);
	}

	/**
	 * If the orientation is Horizontal, the width is returned, otherwise the
	 * height.
	 */
	private int getSizeForPrimaryAxis(Dimension size) {
		return (getOrientation() == HORIZONTAL_SPLIT) ? size.width
				: size.height;
	}

}