package sk.stuba.fiit.perconik.ivda.dto;

public class ProcessDto {
	private String name;
	private int pid;
	
	public String getName() {
		return this.name;
	}

    public ProcessDto() {
    }

    public void setName(String name) {
		this.name = name;
	}

	public int getPid() {
		return this.pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
}
