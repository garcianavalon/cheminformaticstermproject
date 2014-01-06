
package code;

<<<<<<< HEAD
public class ECFFragment{
=======
public class ECFFragment implements Comparable<ECFFragment>{
>>>>>>> FragmentContribution
	private Double score;
	private int count;
	private String key;

	public ECFFragment(int count, String key, Double score) {
		super();
		this.count = count;
		this.key = key;
		this.score = score;
	}

	public ECFFragment(int count, String smile) {
		this.count = count;
		this.key = smile;
	}

	public int getCount() {
		return count;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

<<<<<<< HEAD
	
=======
	@Override
	public int compareTo(ECFFragment otherFragment) {
		return this.count > otherFragment.getCount() ? -1 : this.count < otherFragment
				.getCount() ? 1 : otherFragment.getKey().compareTo(this.key);
	}

>>>>>>> FragmentContribution
	
}


