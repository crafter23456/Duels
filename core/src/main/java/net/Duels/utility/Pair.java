package net.Duels.utility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<A, B> {

	private A a;

	private B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
}
