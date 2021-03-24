package io.openliberty.sample.jakarta.cdi;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.inject.Produces;

import jakarta.enterprise.context.*;

@RequestScoped
public class ManagedBean {
	public int a;
	
	public ManagedBean() {
		this.a = 10;
	}
	
	@Produces @ApplicationScoped @RequestScoped
	public List<Integer> getAllProductIds() {
		return Collections.emptyList();
	}
}
