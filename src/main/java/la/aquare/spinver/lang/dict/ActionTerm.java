package la.aquare.spinver.lang.dict;

public class ActionTerm {
	public String term;
	public boolean hasBlockingBefore;
	public String negativeRegex = "";
	
	public ActionTerm(String aTerm) {
		term = aTerm;
	}
	
	public ActionTerm(String aTerm, boolean blockingBefore) {
		term = aTerm;
		hasBlockingBefore = blockingBefore;		
	}

	public ActionTerm(String aTerm, boolean blockingBefore, String negRegex) {
		term = aTerm;
		hasBlockingBefore = blockingBefore;
		negativeRegex = negRegex;
	}
	
}
