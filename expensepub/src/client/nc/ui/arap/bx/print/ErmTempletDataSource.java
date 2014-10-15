package nc.ui.arap.bx.print;

import nc.ui.pub.print.IMetaDataDataSource;

/**
 * @author 
 * 
 */
public class ErmTempletDataSource implements IMetaDataDataSource{
	
	private Object[] bxvos;
	
	public Object[] getBxvos() {
		return bxvos;
	}

	public void setBxvos(Object[] bxvos) {
		this.bxvos = bxvos;
	}

	public Object[] getMDObjects() {
        return bxvos;
	}

	@Override
	public String[] getAllDataItemExpress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAllDataItemNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getDependentItemExpressByExpress(String itemExpress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getItemValuesByExpress(String itemExpress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModuleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNumber(String itemExpress) {
		// TODO Auto-generated method stub
		return false;
	}
} 


