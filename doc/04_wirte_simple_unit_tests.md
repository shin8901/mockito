# 간단한 테스트 케이스 작성

## Utit 테스트 케이스 작성


```java
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmailValidatorTest {

    @Test
    public void 이메일에_골뱅이가없으면_false를리턴한다(){
        boolean result = EmailValidator.isValid("emailgoogle.com");
        assertThat(result, is(false));
    }

    @Test
    public void 이메일에_혀용되지않는_특수문자가있으면_false를리턴한다(){
        boolean result = EmailValidator.isValid("email##@google.com");
        assertThat(result, is(false));
    }

    @Test
    public void 이메일의_골뱅이뒤에_도메인주소가_유효하지않으면_false를리턴한다(){
        boolean result = EmailValidator.isValid("email@google");
        assertThat(result, is(false));

    }

    @Test
    public void 이메일이_유효하면_true를리턴한다(){
        boolean result = EmailValidator.isValid("email@google.com");
        assertThat(result, is(true));
    }
}
```

[이전](03_domain_overview.md)  [다음](05_write_tests.md)