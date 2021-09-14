package org.processmining.hybridilpminer.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class LPMinerFilterDoubleSliderPanel extends JPanel {

	private static final long serialVersionUID = 8414175418653300000L;

	private NiceDoubleSlider slider = null;

	public LPMinerFilterDoubleSliderPanel(String title, String description, double min, double max, double defaultValue,
			boolean enabled) {
		setOpaque(false);
		if (enabled) {
			JLabel titleLabel = SlickerFactory.instance().createLabel(title);
			JLabel descLabel = SlickerFactory.instance().createLabel(description);
			slider = SlickerFactory.instance().createNiceDoubleSlider("Value", min, max, defaultValue,
					Orientation.HORIZONTAL);
			slider.setEnabled(enabled);

			slider.getSlider().setMinorTickSpacing(100);
			slider.getSlider().setMajorTickSpacing(100);
			slider.getSlider().setSnapToTicks(true);

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			// c.weightx = 1;
			// c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTH;

			c.gridx = c.gridy = 0;
			add(titleLabel, c);

			c.gridx = 0;
			c.gridy = 1;
			add(descLabel, c);

			c.gridx = 0;
			c.gridy = 2;
			add(slider, c);
			this.setPreferredSize(new Dimension(50, 150));
		}
	}

	public double getValue() {

		return slider != null ? slider.getValue() : -1;
	}

	public void setValue(double value) {
		if (slider != null) {
			this.slider.setValue(value);
		}
	}
}
