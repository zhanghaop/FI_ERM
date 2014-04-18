package nc.vo.erm.settlement;


import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;

public class AuthorVO extends ValueObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 321847566538212871L;

	private String pk_account;
	
	private String accountName;
	
	private String username;
	
	private String password;
	
	private boolean isPass;
	
	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validate() throws ValidationException {
		// TODO Auto-generated method stub

	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPk_account() {
		return pk_account;
	}

	public void setPk_account(String pk_account) {
		this.pk_account = pk_account;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
