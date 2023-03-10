package hr.codenamecode.tapioca.testing;

import hr.codenamecode.tapioca.Api;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(TapiocaTestExtension.class)
public @interface TapiocaTest {
  String hostname() default "localhost";

  String contextPath() default "/";

  Class<? extends Api>[] listeners() default {};

  String tapiocaSimpleHttptClientFieldName() default "http";

  String tapiocaBaseURIFieldName() default "baseURI";
}
