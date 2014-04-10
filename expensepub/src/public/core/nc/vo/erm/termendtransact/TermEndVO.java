package nc.vo.erm.termendtransact;

import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;

/**
 * @author twei
 *
 */
public class TermEndVO extends ValueObject {

	private static final long serialVersionUID = 166932548933070782L;
	private String pkCorp;
	private String year;
	private String month;
	
	public TermEndVO() {
	}
	public TermEndVO(String pkCorp,String year,String month) {
		this.pkCorp=pkCorp;
		this.year=year;
		this.month=month;
	}

	public String getPkCorp() {
		return pkCorp;
	}
	public void setPkCorp(String pkCorp) {
		this.pkCorp = pkCorp;
	}
	
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public void validate() throws ValidationException {
	}

}
