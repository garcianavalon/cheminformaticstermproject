package code;

public class ECFFragment {
	private int count;
	private String key;
	private Double score;

	public ECFFragment(int count, String key) {
		super();
		this.count = count;
		this.key = key;
		this.score = null;
	}

	public ECFFragment(int count, String key, Double score) {
		super();
		this.count = count;
		this.key = key;
		this.score = score;
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

}
