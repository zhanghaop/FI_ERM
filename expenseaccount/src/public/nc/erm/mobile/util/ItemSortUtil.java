package nc.erm.mobile.util;

import java.util.Arrays;
import java.util.Comparator;

import nc.bs.logging.Logger;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;

public class ItemSortUtil {
	
	public static void sortBillTabVO(BillTabVO[] vos)
	{
	    sortBillTabVOByCode(vos);
	    sortBillTabVOByIndex(vos);
	}
	
	
	public static void sortBodyVOsByPos(BillTempletBodyVO[] bodys)
	  {
	    if ((bodys == null) || (bodys.length == 0))
	      return;
	    Comparator<BillTempletBodyVO> c = new Comparator<BillTempletBodyVO>()
	    {
			@Override
			public int compare(BillTempletBodyVO paramT1, BillTempletBodyVO paramT2) {
				Integer i1 = ((BillTempletBodyVO)paramT1).getPos();
		        Integer i2 = ((BillTempletBodyVO)paramT2).getPos();
		        return (i1 == null ? 0 : i1.intValue()) - (i2 == null ? 0 : i2.intValue());
			}
	      
	    };
	    Arrays.sort(bodys, c);
	  }
	
	public static void sortBodyVOsByItemkey(BillTempletBodyVO[] bodys)
	{
	    if ((bodys == null) || (bodys.length == 0))
	      return;
	    Comparator<BillTempletBodyVO> c = new Comparator<BillTempletBodyVO>()
	    {
			@Override
			public int compare(BillTempletBodyVO paramT1, BillTempletBodyVO paramT2) {
				String s1 = ((BillTempletBodyVO)paramT1).getItemkey();
		        String s2 = ((BillTempletBodyVO)paramT2).getItemkey();
		        return s1.compareTo(s2);
			}
	    };
	    Arrays.sort(bodys, c);
	}
	
	public static void sortBodyVOsByProps(BillTempletBodyVO[] bodys, String[] props)
	  {
	    if ((bodys == null) || (bodys.length == 0) || (props == null) || (props.length == 0))
	    {
	      return; }
	    for (int i = props.length - 1; i >= 0; i--) {
	      if ("pos".equals(props[i])) {
	        sortBodyVOsByPos(bodys);
	      } else if ("table_code".equals(props[i])) {
	        sortBodyVOsByTableCode(bodys);
	      } else if ("showorder".equals(props[i])) {
	        sortBodyVOsByShowOrder(bodys);
	      } else if ("itemkey".equals(props[i])) {
	        sortBodyVOsByItemkey(bodys);
	      } else {
	        Logger.debug("#####BillUtil.sortBodyVOsByProps方法不支持的属性::" + props[i] + "!!!!!");
	      }
	    }
	  }
	
	public static void sortBodyVOsByShowOrder(BillTempletBodyVO[] bodys)
	  {
	    if ((bodys == null) || (bodys.length == 0))
	      return;
	    Comparator<BillTempletBodyVO> c = new Comparator<BillTempletBodyVO>()
	    {
			@Override
			public int compare(BillTempletBodyVO paramT1, BillTempletBodyVO paramT2) {
				Integer i1 = ((BillTempletBodyVO)paramT1).getShoworder();
		        Integer i2 = ((BillTempletBodyVO)paramT2).getShoworder();
		        return (i1 == null ? 0 : i1.intValue()) - (i2 == null ? 0 : i2.intValue());
			}
	      
	    };
	    Arrays.sort(bodys, c);
	  }
	
	public static void sortBodyVOsByTableCode(BillTempletBodyVO[] bodys)
	{
	    if ((bodys == null) || (bodys.length == 0))
	      return;
	    Comparator<BillTempletBodyVO> c = new Comparator<BillTempletBodyVO>()
	    {
			@Override
			public int compare(BillTempletBodyVO paramT1, BillTempletBodyVO paramT2) {
				String s1 = ((BillTempletBodyVO)paramT1).getTableCode();
		        String s2 = ((BillTempletBodyVO)paramT2).getTableCode();
		        return s1.compareTo(s2);
			}
	    };
	    Arrays.sort(bodys, c);
	}
	
	public static void sortBillTabVOByCode(BillTabVO[] vos)
	  {
	    if ((vos == null) || (vos.length == 0)) {
	      return;
	    }
	    
	    Comparator<BillTabVO> c = new Comparator<BillTabVO>() {
			@Override
			public int compare(BillTabVO o1, BillTabVO o2) {
				String i1 = ((BillTabVO)o1).getTabcode();
		        String i2 = ((BillTabVO)o2).getTabcode();
		        return i1.compareTo(i2);
			}
	    };
	    Arrays.sort(vos, c);
	  }
	  



	public static void sortBillTabVOByIndex(BillTabVO[] vos)
	{
	    if ((vos == null) || (vos.length == 0)) {
	      return;
	    }
	    Comparator<BillTabVO> c = new Comparator<BillTabVO>() {
			@Override
			public int compare(BillTabVO o1, BillTabVO o2) {
				Integer i1 = ((BillTabVO)o1).getTabindex();
		        Integer i2 = ((BillTabVO)o2).getTabindex();
		        return (i1 == null ? 0 : i1.intValue()) - (i2 == null ? 0 : i2.intValue());
			}
	    };
	    Arrays.sort(vos, c);
	}
}
