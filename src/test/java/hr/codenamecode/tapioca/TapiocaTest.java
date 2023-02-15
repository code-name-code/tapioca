package hr.codenamecode.tapioca;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(TapiocaTomcatTestExtension.class)
public @interface TapiocaTest {
  int port() default 11111;

  String hostname() default "localhost";

  String contextPath() default "/";

  Class<? extends Api>[] listeners() default {};

  String tapiocaSimpleHttptClientFieldName() default "http";

  String tapiocaBaseURIFieldName() default "baseURI";
}
