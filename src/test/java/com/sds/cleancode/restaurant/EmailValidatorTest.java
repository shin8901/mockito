package com.sds.cleancode.restaurant;

import org.junit.Test;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmailValidatorTest {

    @Test
    public void 이메일에_골뱅이가없으면_false를리턴한다(){
        assertThat(EmailValidator.isValid("email-without-at"), is(false));
        //then(EmailValidator.isValid("email-without-at")).isFalse();
    }

    @Test
    public void 이메일에_특수문자가있으면_false를리턴한다(){
        assertThat(EmailValidator.isValid("email-with-#"), is(false));
    }

    @Test
    public void 이메일의_골뱅이뒤에_도메인주소가없으면_false를리턴한다(){
        assertThat(EmailValidator.isValid("email-without-domain@"), is(false));
    }

    @Test
    public void 이메일이_유효하면_true를리턴한다(){
        assertThat(EmailValidator.isValid("email@email.com"), is(true));
    }
}

