package nc.vo.erm.control;

/**
 * �˴���������������
 * �������ڣ�(2004-3-12 18:39:09)
 * @author������
 */
public class TokenTools {
    private String str;
    private String delimiters;
    private boolean retTokens;
    /**
     * TokenTools ������ע�⡣
     */
    public TokenTools() {
        super();
    }
    public TokenTools(String str) {
        this.str = str;
        this.delimiters = " \t\n\r\f";
        this.retTokens = false;
    }

    public TokenTools(String str, String delim) {
        this.str = str;
        this.delimiters = delim;
        this.retTokens = false;
    }
    /**
    �õ�ָ���ָ���ŷָ�ָ���ַ�������ַ�������   
    
    
    */
    public String[] getStringArray() {
        java.util.StringTokenizer token = getToken();
        int count = token.countTokens();
        String[] strs = new String[count];
        int i = 0;
        while (token.hasMoreTokens()) {
            strs[i] = token.nextToken();
            i++;
        }
        return strs;

    }
    public java.util.StringTokenizer getToken() {
        return new java.util.StringTokenizer(getStr(), getDelimis(), getRetTokens());
    }
    public void setStr(String s) {
        this.str = s;
    }
    public void setDelimis(String delimis) {
        this.delimiters = delimis;
    }
    public void setRetTokens(boolean b) {
        this.retTokens = b;
    }
    public String getStr() {
        return this.str;
    }
    public String getDelimis() {
        return this.delimiters;
    }
    public boolean getRetTokens() {
        return this.retTokens;
    }

    public TokenTools(String str, String delim, boolean returnTokens) {
        this.str = str;
        this.delimiters = delim;
        this.retTokens = returnTokens;
    }

public int countTokens() {
    return getToken().countTokens();
}

public boolean isOneToken() {
    return countTokens() == 1;
}
}