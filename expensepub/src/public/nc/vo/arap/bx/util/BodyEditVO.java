package nc.vo.arap.bx.util;

public class BodyEditVO {
	
	private Object value;
	private int row;
	private int pos;
	private String itemkey;
	private String tablecode;
	
	public String getItemkey() {
		return itemkey;
	}
	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getTablecode() {
		return tablecode;
	}
	public void setTablecode(String tablecode) {
		this.tablecode = tablecode;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	
	public String toString()
    {
        return (new StringBuilder()).append(value).append(row).append(pos).append(itemkey).append(tablecode).toString();
    }
	

}
