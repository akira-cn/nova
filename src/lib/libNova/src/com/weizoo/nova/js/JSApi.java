package com.weizoo.nova.js;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JSApi {
	static final int TYPE_METHOD = 0;
	static final int TYPE_GETTER = 1;
	static final int TYPE_SETTER = 2;
	
	static final int RET_VALUE = 0;
	static final int RET_JSON = 1;
	static final int RET_FUNC = 2;
	
	int apiType() default TYPE_METHOD;
	int apiRet() default RET_VALUE;
}
