package nc.ui.erm.accountage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class SequenceRender extends JTable implements TableCellRenderer {
	private static final long serialVersionUID = 1;
	private final JLabel numLabel;

	/** �߽������ӵ�н��㡢û�н��� */
	protected Border noFocusBorder = new EmptyBorder(1, 2, 1, 2);
	protected Border hasFocusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");

	public SequenceRender() {
		super();
		setBorder(noFocusBorder);

		// ���á���ʾ��
		numLabel = new JLabel();
		numLabel.setName("number");
		numLabel.setText("");
		numLabel.setHorizontalAlignment(SwingConstants.CENTER);
		numLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		// ���������
		setName("RenderPanel");
		setLayout(new BorderLayout());
		add(numLabel, "Center");
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			numLabel.setForeground(Color.blue);
		} else {
			numLabel.setForeground(Color.black);
		}
		if (hasFocus) {
			numLabel.setForeground(Color.red);
		}
		numLabel.setText(value == null ? "" : value.toString());
		return this;
	}

}


