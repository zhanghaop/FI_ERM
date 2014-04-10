package nc.bs.er.util;


public class BXPubUtil {

	/**
	 * bxvo转换成jkvo，只转表头,避免根据vo查找元数据出现错误
	 * @param vo
	 * @return
	 */
//	public static BXVO bx2jkVO(BXVO vo){
//		BXHeaderVO bxHeaderVO = vo.getParentVO();
//		//重设表头
//		vo.setParentVO(bx2jkHeadVO(bxHeaderVO));
//		return vo;
//	}
	
	/**
	 * bxvo转换成jkvo，只转表头
	 * 避免根据vo查找元数据出现错误
	 * @param head
	 * @return
	 */
//	public static BXHeaderVO bx2jkHeadVO(BXHeaderVO head){
//		if (head!=null && BXConstans.JK_DJDL.equals(head.getDjdl())) {
//			JKHeaderVO jkHeaderVO = new JKHeaderVO();
//			String[] attributeNames = head.getAttributeNames();
//			for (int i = 0; i < attributeNames.length; i++) {
//				final String key = attributeNames[i];
//				jkHeaderVO.setAttributeValue(key, head.getAttributeValue(key));
//			}
//			head = jkHeaderVO;
//		}
//		return head;
//	}
}
