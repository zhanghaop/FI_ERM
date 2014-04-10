package nc.bs.erm.prealarm;
import nc.bs.pub.pa.html.IAlertMessage;
public class AlarmFormatedMsg implements IAlertMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] m_sFldName;
	private Object[][] m_oValue;
	private float[] m_fWidth;
	private String[] m_Bottom;
	private String m_sTitle;
	private String[] m_sTop;
	
	public String[] getBodyFields() {
		return m_sFldName;
	}
	
	public void setBodyFields(java.lang.String[] newBodyFields) {
		m_sFldName = newBodyFields;
	}
	
	public Object[][] getBodyValue() {
		return m_oValue;
	}
	
	public void setBodyValue(java.lang.Object[][] newBodyValue) {
		m_oValue = newBodyValue;
	}
	
	public float[] getBodyWidths() {
		return m_fWidth;
	}
	
	public void setBodyWidths(float[] newBodyWidths) {
		m_fWidth=newBodyWidths;
	}
	
	public String[] getBottom() {
		return m_Bottom;
	}

	public void setBottom(java.lang.String[] newBottom) {
		m_Bottom = newBottom;
	}

	public String getTitle() {
		return m_sTitle;
	}
	
	public void setTitle(String newTitle) {
		m_sTitle = newTitle;
	}

	public String[] getTop() {
		return m_sTop;
	}
	
	public void setTop(java.lang.String[] newTop) {
		m_sTop = newTop;
	}
}
