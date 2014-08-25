
public class RankingSkeleton {

	String undeclaredVariable;
	String candidateDeclaredVariable;
	int similarity;
	
	public RankingSkeleton(String undeclaredVariable,
			String candidateDeclaredVariable, int similarity) {
		super();
		this.undeclaredVariable = undeclaredVariable;
		this.candidateDeclaredVariable = candidateDeclaredVariable;
		this.similarity = similarity;
	}
}
