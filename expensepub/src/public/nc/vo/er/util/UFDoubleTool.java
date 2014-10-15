/*
 * �������� 2005-9-14
 *
 */
package nc.vo.er.util;

import nc.vo.pub.lang.UFDouble;

/**
 * @author zhongyue
 *
 */
public class UFDoubleTool {

	public static void main(String[] args) {
	}
	// -333.004 �� 333.004����true
	public static boolean isXiangFan(UFDouble ufd,UFDouble ufd2){
		UFDouble result = ufd.add(ufd2);
		return isZero(result);
	
	}	
	
	
	public static boolean isXiangdeng(UFDouble ufd,UFDouble ufd2){
		if(ufd==null || ufd2==null){
			return false;
		}
		UFDouble cha = ufd.sub(ufd2);
		return isZero(cha);
	
	}
	//ǰ�߾���ֵ���ڻ���ں��ߣ�����true
	public static boolean isAbsDayu(UFDouble ufd,UFDouble ufd2){
		if(ufd == null){
			return false;
		}
		if(ufd2==null){
			return true;
		}
		UFDouble abs = ufd.abs();
		UFDouble abs2 = ufd2.abs();
		
		return ArapCommonTool.isLarge(abs, abs2);
	
	}
	public static boolean isTonghao(UFDouble ufd,UFDouble ufd2){

		if((ArapCommonTool.isLargeZero(ufd)&&ArapCommonTool.isLessZero(ufd2))||(ArapCommonTool.isLessZero(ufd)&&ArapCommonTool.isLargeZero(ufd2)))
		{
			return false;
		}
		else
		{
			return true;
		}
	
	}
	public static  boolean isZero(UFDouble ufd){
		if(ufd == null){
		    return false;	
		}
		return ArapCommonTool.isZero(ufd);
	}
	public static boolean isDayu0(UFDouble ufd){
		if(ufd == null){
			return false;
		}
		return ArapCommonTool.isLargeZero(ufd);
	}
	
//	ǰ��С�ں��ߣ�����true�����򷵻�false
	public static boolean isXiaoyu(UFDouble ufd , UFDouble ufd2){
		if(ufd == null || ufd2 == null){
			return false;
		}
		UFDouble result = ufd.sub(ufd2);
		if(isDayu0(result)){
			return false;
		}else{
			return true;
		}
		
	}
	//���������о���ֵ��С���Ǹ�����ֵ
	public static UFDouble getAbsMin(UFDouble ufd,UFDouble ufd2){
		if(ufd==null || ufd2==null){
			return null;
		}
		UFDouble ufd_abs = ufd.abs();
		UFDouble ufd2_abs = ufd2.abs();
		if(isXiaoyu(ufd_abs,ufd2_abs)){
			return ufd_abs;
		}else{
			return ufd2_abs;
		}
	
	}
	
	public static UFDouble sum(UFDouble ufd,UFDouble ufd2){
		if(ufd == null){
			return ufd2;
		}
		if(ufd2 == null){
			return ufd;
		}
		UFDouble sum = ufd.add(ufd2);
		return sum;
	}
	public static UFDouble sub(UFDouble ufd,UFDouble ufd2){
		if(ufd2 == null){
			return ufd;
		}
		if(ufd == null){
			return ufd2.multiply(new UFDouble(-1));
		}
		UFDouble sub = ufd.sub(ufd2);
		return sub;
	}

	public static UFDouble getDoubleValue(Object d) {
		return d == null ? UFDouble.ZERO_DBL : (UFDouble) d;
	}
	
	/**
	 * �ж�UFDouble�Ƿ����0
	 * @param uf
	 * @return true��ʾ��Ϊ����0
	 */
	public static boolean isUFDoubleGreaterThanZero(UFDouble uf){
		if(uf == null || uf.compareTo(UFDouble.ZERO_DBL) <= 0){
			return false;
		}
		return true;
	}

	/**
	 * ��ʽ�����
	 * @param uf
	 * @param power ���ȣ�С��0ʱ������
	 */
	public static UFDouble formatUFDouble(UFDouble uf, int power){
		uf = (uf == null) ? UFDouble.ZERO_DBL :uf;
		if(power > 0){
			uf = uf.setScale(power, UFDouble.ROUND_HALF_UP);
		}

		return uf;
	}

}
