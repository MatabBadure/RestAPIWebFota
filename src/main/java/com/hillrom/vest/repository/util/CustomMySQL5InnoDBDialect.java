package com.hillrom.vest.repository.util;

public class CustomMySQL5InnoDBDialect extends org.hibernate.dialect.MySQL5InnoDBDialect {

	  public CustomMySQL5InnoDBDialect() {
	    super();
	    this.registerFunction("group_concat", new GroupConcatFunction());
	  }
	}
