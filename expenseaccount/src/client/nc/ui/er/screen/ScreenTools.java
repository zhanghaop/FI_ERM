package nc.ui.er.screen;

import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenTools {
	private static float width;

	private static float height;

	private static Dimension screen = null;

	public static Dimension getScreen() {
		if (screen == null)
			screen = Toolkit.getDefaultToolkit().getScreenSize();
		height = screen.height;
		width = screen.width;
		return screen;
	}

	public static int calW(int w) {
		float wRate = width / 1024;
		float ww = wRate * w;

		int iw = Math.round(ww);

		if (iw > width)
			iw = Math.round(width) - 10;
		return iw;
	}

	public static int calH(int h) {
		float hRate = height / 768;
		float hh = h * hRate;

		int ih = Math.round(hh);

		if (ih > width)
			ih = Math.round(height) - 10;
		return ih;
	}

	public static Dimension newDimension(int w, int h) {
		return new Dimension(calW(w), calH(h));
	}

	public static void setLocation(Object obj, int w, int h) {
		if (obj instanceof nc.ui.pub.beans.UILabel) {
			nc.ui.pub.beans.UILabel lbl = (nc.ui.pub.beans.UILabel) obj;
			lbl.setLocation(calW(w), calH(h));
		} else if (obj instanceof nc.ui.pub.beans.UIComboBox) {
			nc.ui.pub.beans.UIComboBox cbx = (nc.ui.pub.beans.UIComboBox) obj;
			cbx.setLocation(calW(w), calH(h));
		} else if (obj instanceof nc.ui.pub.beans.UITextField) {
			nc.ui.pub.beans.UITextField textfield = (nc.ui.pub.beans.UITextField) obj;
			textfield.setLocation(calW(w), calH(h));
		}
	}

	public static void setBounds(Object obj, int a, int b, int c, int d) {
		if (obj instanceof nc.ui.pub.beans.UITextField) {
			nc.ui.pub.beans.UITextField txt = (nc.ui.pub.beans.UITextField) obj;
			txt.setBounds(a, b, c, d);
		}
	}

}
