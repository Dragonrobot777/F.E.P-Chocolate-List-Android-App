package net.shikii.widgets;

import org.foodispower.chocolatelist.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/**
 * Applies a pressed state color filter or disabled state alpha for the button's background
 * drawable.
 *
 * @author shiki
 */
public class SAutoBgButton extends ToggleButton {

  public SAutoBgButton(Context context) {
    super(context);
  }

  public SAutoBgButton(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SAutoBgButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
	public void setChecked( boolean enabled ) {
		super.setChecked( enabled );

		int d = 0;

		if ( enabled ) {
			d = R.drawable.icon_favourite_on;
		} else {
			d = R.drawable.icon_favourite_off;
		}

		if ( 0 != d ) {
			setCompoundDrawablesWithIntrinsicBounds( d, 0, 0, 0 );
		}
	}
}
