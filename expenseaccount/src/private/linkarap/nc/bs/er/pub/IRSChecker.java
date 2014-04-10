package nc.bs.er.pub;

import java.sql.SQLException;

import nc.vo.pub.CircularlyAccessibleValueObject;


public interface IRSChecker {

	public boolean isReslut(Object obj)throws SQLException;
	
	public CircularlyAccessibleValueObject[] getReslut(CircularlyAccessibleValueObject[] obj)throws SQLException;
}
