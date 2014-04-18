package nc.ui.er.reimrule;

import javax.swing.JComponent;
import javax.swing.table.*; 
import javax.swing.plaf.basic.*; 
import java.awt.*; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List;

import nc.ui.pub.bill.BillItem;
import nc.vo.arap.utils.StringUtil;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.pub.SuperVO;
  
public class CombineTableUI extends BasicTableUI { 
	//ÿһ����ÿ�����������е���ʼλ��
	private ArrayList<HashMap<Integer, Integer>> rowPoss; 
	//ÿһ����ÿ�����������п�Խ������
    private ArrayList<HashMap<Integer, Integer>> rowCounts;
    private String[][] datas;
    /** 
     * ��ʼ����������ÿ�кϲ���Ԫ�����ʼ��λ�� ����������
     * @param vos ����д洢�����б�����׼����
     * @param bodyItem ������׼ģ�������е�������
     */
	public void process(List<SuperVO> vos,BillItem[] bodyItem) {
		//�����Ҫչʾ�ı���е�����
		datas=getTableDatas(vos,bodyItem);
        rowPoss = new ArrayList<HashMap<Integer, Integer>>(); 
        rowCounts = new ArrayList<HashMap<Integer, Integer>>(); 
        if(datas!=null && datas.length>0)
	        for (int curCol=0;curCol<datas[0].length;curCol++) { 
	            HashMap<Integer, Integer> rowPos = new HashMap<Integer, Integer>(); 
	            HashMap<Integer, Integer> rowCunt = new HashMap<Integer, Integer>(); 
	            String pre = ""; 
	            int count = 0; 
	            int startRow = 0; 
	            for (int curRow = 0; curRow < datas.length; curRow++) { 
	                String data = datas[curRow][curCol]; 
	                if (pre.equals(data)) { 
	                    count++; 
	                } else { 
	                	//�洢��ǰ����������
	                    rowCunt.put(startRow, count); 
	                    pre = data; 
	                    count = 1; 
	                    startRow = curRow; 
	                } 
	                //�洢��ǰ�е���ʼ��
	                rowPos.put(curRow, startRow); 
	            } 
	            rowCunt.put(startRow, count); 
	            rowPoss.add(rowPos); 
	            rowCounts.add(rowCunt); 
	        } 
    } 
  
	String[][] getTableDatas(List<SuperVO> vos,BillItem[] bodyItem)
	{
		//��ȡ��Ԫ������
		int rowCount = vos.size();
		//ɸѡ����Ҫչʾ���У���isShowΪ�����
		List<String> columns=new ArrayList<String>();
		for(int i=0;i<bodyItem.length;i++)
			if(bodyItem[i].isShow()){
				columns.add(bodyItem[i].getKey());
			}
		//��ȡ��Ԫ������
		int colCount=columns.size();
		//����Ҫչʾ�ĵ�Ԫ���е����ݴ洢��datas��
		String datas[][] = new String[rowCount][colCount];;
		for(int i=0;i<rowCount;i++)
		{
			ReimRulerVO vo=(ReimRulerVO)vos.get(i);
			for(int j = 0;j < colCount;j++)
			{
				try{
					datas[i][j]=vo.getAttributeValue(columns.get(j)).toString();	
					if(datas[i][j]==null)
						datas[i][j]="";
				}catch(Exception e){
					datas[i][j]="";
				}
			}
		}
		return datas;
	}
	
    /** 
     * ����table��row��column�е�Ԫ���������� 
     */
    public int span(int row, int column) { 
        if (column != -1) { 
            return rowCounts.get(column).get(rowPoss.get(column).get(row)); 
        } else { 
            return 1; 
        } 
    } 
  
    /** 
     * ����table��row��column�е�Ԫ�����ڵĺϲ���Ԫ�����ʼ��λ�� 
     */
    public int startRow(int row, int column) {
        if (column > -1 && row > -1) { 
            return rowPoss.get(column).get(row); 
        } else { 
            return row; 
        } 
    } 
   
    @Override
    public void paint(Graphics g, JComponent c) { 
        Rectangle r = g.getClipBounds(); 
        rendererPane.removeAll(); 
        int firstCol = table.columnAtPoint(new Point(r.x, 0)); 
        int lastCol = table.columnAtPoint(new Point(r.x + r.width, 0)); 
        // -1 ��ʾ���һ�г����˱�����ʾ��Χ 
        if (lastCol < 0) { 
            lastCol = table.getColumnCount() - 1; 
        } 
        for (int i = firstCol; i <= lastCol; i++) { 
        	//���չʾ�ַ�
            paintCol(i, g); 
        } 
        //���߿�
        paintGrid(g, 0, table.getRowCount() - 1, 0, table.getColumnCount() - 1); 
    } 
    /**
     * ���ָ���е��ַ�
     * @param col ָ����
     */
    private void paintCol(int col, Graphics g) { 
        Rectangle r = g.getClipBounds(); 
        for (int row = 0; row < table.getRowCount(); row++) { 
            Rectangle r1 = table.getCellRect(row, col, true); 
            if (r1.intersects(r)) 
            { 
                int starkRow = startRow(row, col);
                int spanRow = span(starkRow, col);
                if(spanRow > 1)
                	r1.x=(int)(r1.x+r1.getWidth()/2-datas[row][col].length());
                if((spanRow&1) == 1)
                	r1.y=(int)(r1.y+r1.getHeight()*(spanRow/2));
                else
                	r1.y=(int)(r1.y+r1.getHeight()*spanRow/2.0-r1.getHeight()/2);
                paintCell(starkRow, col, g, r1); 
                // �����һ�հ׸�-1����Ϊforѭ������row++ 
                row += spanRow - 1; 
            } 
        } 
    } 
  
    private void paintCell(int row, int column, Graphics g, Rectangle area) { 
        int verticalMargin = table.getRowMargin(); 
        int horizontalMargin = table.getColumnModel().getColumnMargin(); 
  
        area.setBounds(area.x + horizontalMargin / 2, area.y + verticalMargin / 2, area.width - horizontalMargin, area.height - verticalMargin); 
  
        if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) { 
            Component component = table.getEditorComponent(); 
            component.setBounds(area); 
            component.validate(); 
        } else { 
            TableCellRenderer renderer = table.getCellRenderer(row, column); 
            Component component = table.prepareRenderer(renderer, row, column); 
            if (component.getParent() == null) { 
                rendererPane.add(component); 
            } 
            rendererPane.paintComponent(g, component, table, area.x, area.y, 
                    area.width, area.height, true); 
        } 
    } 
  
    /**
     * �����������������
     * @param rMin ��ʼ��
     * @param rMax ��ֹ��
     */
    private void paintGrid(Graphics g, int rMin, int rMax, int cMin, int cMax) { 
        g.setColor(table.getGridColor()); 
  
        Rectangle minCell = table.getCellRect(rMin, cMin, true); 
        Rectangle maxCell = table.getCellRect(rMax, cMax, true); 
        Rectangle damagedArea = minCell.union(maxCell); 
  
        if (table.getShowHorizontalLines()) { 
            for (int row = rMin; row <= rMax; row++) { 
                for (int column = cMin; column <= cMax; column++) { 
                    Rectangle cellRect = table.getCellRect(row, column, true); 
                    int visibleCell = startRow(row, column); 
                    int span = span(row, column); 
                    if (span > 1 && row < visibleCell + span - 1 && !StringUtil.isNullWithTrim(datas[row][column])) { 
                    } else { 
                        g.drawLine(cellRect.x, cellRect.y + cellRect.height - 1, cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height - 1); 
                    } 
                } 
            } 
        } 
        if (table.getShowVerticalLines()) { 
            TableColumnModel cm = table.getColumnModel(); 
            int tableHeight = damagedArea.y + damagedArea.height; 
            int x; 
            if (table.getComponentOrientation().isLeftToRight()) { 
                x = damagedArea.x; 
                for (int column = cMin; column <= cMax; column++) { 
                    int w = cm.getColumn(column).getWidth(); 
                    x += w; 
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1); 
                } 
            } else { 
                x = damagedArea.x; 
                for (int column = cMax; column >= cMin; column--) { 
                    int w = cm.getColumn(column).getWidth(); 
                    x += w; 
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1); 
                } 
            } 
        } 
    } 
}

